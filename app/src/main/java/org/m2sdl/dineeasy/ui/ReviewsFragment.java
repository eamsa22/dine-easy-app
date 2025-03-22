package org.m2sdl.dineeasy.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayout;

import org.m2sdl.dineeasy.CameraHelper;
import org.m2sdl.dineeasy.ImageFilterHelper;
import org.m2sdl.dineeasy.R;
import org.m2sdl.dineeasy.adapter.ReviewAdapter;
import org.m2sdl.dineeasy.model.Restaurant;
import org.m2sdl.dineeasy.model.Review;
import org.m2sdl.dineeasy.viewModel.ReviewViewModel;
import java.util.ArrayList;
import java.util.List;

public class ReviewsFragment extends Fragment {

    private ReviewViewModel reviewViewModel;
    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    private CameraHelper cameraHelper;

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String TAG = "ReviewsFragment";

    private TextureView textureView;
    private Button btnLeaveReview, btnConfirm, btnTry, btnGrayscale ,btnSepia, btnOriginal;
    private ImageView btnAddPhoto, imgPreview, btnCapture;
    private ImageView[] stars = new ImageView[5];
    private int rating = 1;
    private LinearLayout reviewForm;
    private EditText editReviewTitle, editComment, editName;
    private Button btnSubmitReview;
    private FlexboxLayout photoContainer;

    private Bitmap capturedBitmap;
    Bitmap originalBitmap ;
    private ArrayList<Bitmap> imageList = new ArrayList<>();

    private Restaurant restaurant;
    private LinearLayout reviewListContainer;
    private List<Review> reviews= new ArrayList<>();
    private final TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            Log.d("texture listener,", "opening camera ");
           cameraHelper.openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {}

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            cameraHelper.closeCamera();
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {}
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);
        if (getArguments() != null) {
            restaurant = getArguments().getParcelable("restaurant");
        }
        reviewViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);

        initViews(view);

        cameraHelper = new CameraHelper(requireContext(), textureView, btnCapture , bitmap -> {
            requireActivity().runOnUiThread(() -> {
                capturedBitmap = bitmap;
                originalBitmap = bitmap;
                imgPreview.setImageBitmap(capturedBitmap);
                imgPreview.setVisibility(View.VISIBLE);
                btnConfirm.setVisibility(View.VISIBLE);
                btnTry.setVisibility(View.VISIBLE);
                btnGrayscale.setVisibility(View.VISIBLE);
                btnSepia.setVisibility(View.VISIBLE);
                btnOriginal.setVisibility(View.VISIBLE);
                textureView.setVisibility(View.GONE);
                btnCapture.setVisibility(View.GONE);
            });
        });

        updateRating(rating);

        fetchReviews();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraHelper.closeCamera();
    }

    private void initViews(View view) {
        reviewRecyclerView = view.findViewById(R.id.reviewsRecyclerView);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        reviewAdapter = new ReviewAdapter(reviews);

        reviewRecyclerView.setAdapter(reviewAdapter);

        textureView = view.findViewById(R.id.texture_view);
        btnLeaveReview = view.findViewById(R.id.btn_leave_review);
        reviewForm = view.findViewById(R.id.review_form);
        btnAddPhoto = view.findViewById(R.id.btn_add_photo);
        btnCapture = view.findViewById(R.id.btn_capture);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnGrayscale = view.findViewById(R.id.btn_grayscale);
        btnSepia = view.findViewById(R.id.btn_sepia);
        btnOriginal = view.findViewById(R.id.btn_original);
        reviewListContainer = view.findViewById(R.id.review_list_container);

        btnTry = view.findViewById(R.id.btn_try);
        imgPreview = view.findViewById(R.id.img_preview);

        editReviewTitle = view.findViewById(R.id.edit_review_title);
        editComment = view.findViewById(R.id.edit_comment);
        editName = view.findViewById(R.id.edit_name);
        btnSubmitReview = view.findViewById(R.id.btn_submit_review);
        photoContainer = view.findViewById(R.id.photo_container);

        stars[0] = view.findViewById(R.id.star1);
        stars[1] = view.findViewById(R.id.star2);
        stars[2] = view.findViewById(R.id.star3);
        stars[3] = view.findViewById(R.id.star4);
        stars[4] = view.findViewById(R.id.star5);
        for (int i = 0; i < stars.length; i++) {
            final int index = i + 1;
            stars[i].setOnClickListener(v -> updateRating(index));
        }

        textureView.setSurfaceTextureListener(textureListener);
        btnLeaveReview.setOnClickListener(v -> toggleReviewForm());
        btnAddPhoto.setOnClickListener(v -> cameraHelper.openCamera());
        btnCapture.setOnClickListener(v -> cameraHelper.captureImage());

        btnConfirm.setOnClickListener(v -> {
            addPhoto(capturedBitmap);
            confirmImage();
        });
        btnSubmitReview.setOnClickListener(v -> submitReview());
        btnTry.setOnClickListener(v -> retryCapture());
        btnGrayscale.setOnClickListener(v -> {
            if (capturedBitmap != null) {
                capturedBitmap = ImageFilterHelper.applyGrayscale(capturedBitmap);
                imgPreview.setImageBitmap(capturedBitmap);
            }
        });

        btnSepia.setOnClickListener(v -> {
            if (capturedBitmap != null) {
                capturedBitmap = ImageFilterHelper.applySepia(capturedBitmap);
                imgPreview.setImageBitmap(capturedBitmap);
            }
        });

        btnOriginal.setOnClickListener(v -> {
            imgPreview.setColorFilter(null);
            imgPreview.setImageBitmap(originalBitmap);
            capturedBitmap = originalBitmap;
        });
    }



    private void toggleReviewForm() {
        if (reviewForm.getVisibility() == View.GONE) {
            reviewForm.setVisibility(View.VISIBLE);
            btnLeaveReview.setVisibility(View.GONE);
            reviewListContainer.setVisibility(View.GONE);
        } else {
            reviewForm.setVisibility(View.GONE);
            btnLeaveReview.setVisibility(View.VISIBLE);
            reviewListContainer.setVisibility(View.VISIBLE);
        }
    }

    private void updateRating(int selectedRating) {
        rating = selectedRating;
        for (int i = 0; i < stars.length; i++) {
            stars[i].setImageResource(i < rating ? R.drawable.filled_star : R.drawable.empty_star_icon);
        }
    }
    private void confirmImage() {
        imgPreview.setVisibility(View.GONE);
        btnConfirm.setVisibility(View.GONE);
        textureView.setVisibility(View.GONE);
        btnGrayscale.setVisibility(View.GONE);
        btnSepia.setVisibility(View.GONE);
        btnTry.setVisibility(View.GONE);
        btnOriginal.setVisibility(View.GONE);
        reviewForm.setVisibility(View.VISIBLE);
    }

    private void addPhoto(Bitmap bitmap) {
        imageList.add(bitmap);
        FrameLayout frameLayout = new FrameLayout(requireContext());
        FlexboxLayout.LayoutParams frameParams = new FlexboxLayout.LayoutParams(300, 300);
        frameParams.setMargins(10, 10, 10, 10);
        frameLayout.setLayoutParams(frameParams);

        ImageView imageView = new ImageView(requireContext());
        imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setOnClickListener(v -> showFullScreenImage(bitmap));

        ImageView deleteButton = new ImageView(requireContext());
        FrameLayout.LayoutParams deleteParams = new FrameLayout.LayoutParams(80, 80);
        deleteParams.setMargins(0, 0, 0, 0);
        deleteParams.gravity = android.view.Gravity.END | android.view.Gravity.TOP;
        deleteButton.setLayoutParams(deleteParams);
        deleteButton.setImageResource(android.R.drawable.ic_delete);
        deleteButton.setColorFilter(android.graphics.Color.BLACK);
        deleteButton.setBackgroundColor(0xAAFFFFFF);
        deleteButton.setPadding(20, 20, 20, 20);
        deleteButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        deleteButton.setOnClickListener(v -> {
            imageList.remove(bitmap);
            photoContainer.removeView(frameLayout);
        });

        frameLayout.addView(imageView);
        frameLayout.addView(deleteButton);
        photoContainer.addView(frameLayout);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraHelper.openCamera();
            } else {
                Log.e(TAG, "Permission pour la caméra refusée");
            }
        }
    }
    private void showFullScreenImage(Bitmap bitmap) {
        Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_fullscreen_image);

        ImageView fullScreenImage = dialog.findViewById(R.id.fullscreen_image);
        fullScreenImage.setImageBitmap(bitmap);

        ImageButton closeButton = dialog.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void retryCapture() {
        imgPreview.setVisibility(View.GONE);
        btnConfirm.setVisibility(View.GONE);
        btnTry.setVisibility(View.GONE);
        btnOriginal.setVisibility(View.GONE);
        btnSepia.setVisibility(View.GONE);
        btnGrayscale.setVisibility(View.GONE);
        textureView.setVisibility(View.VISIBLE);
        capturedBitmap = null;
        cameraHelper.openCamera();
    }
    private void clearForm() {
        editReviewTitle.setText("");
        editComment.setText("");
        editName.setText("");
        rating =1;
        imageList= new ArrayList<>();
        updateRating(rating);
        reviewForm.setVisibility(View.GONE);
        btnLeaveReview.setVisibility(View.VISIBLE);
        reviewListContainer.setVisibility(View.VISIBLE);
        photoContainer.removeAllViews();
        reviews.clear();
        fetchReviews();
    }
    private void submitReview() {
        String reviewTitle = editReviewTitle.getText().toString().trim();
        String reviewComment = editComment.getText().toString().trim();
        String reviewName = editName.getText().toString().trim();
        List<String> uploadedImageUrls = new ArrayList<>();

        Review review = new Review(restaurant.getId(), reviewName, reviewTitle, reviewComment, rating, uploadedImageUrls);

        if (reviewTitle.isEmpty() || reviewComment.isEmpty() || reviewName.isEmpty()) {
            Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Enregistrement de l'avis...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        reviewViewModel.submitReview(review, imageList);

        reviewViewModel.getReviewSubmissionLiveData().observe(getViewLifecycleOwner(), isSuccess -> {
            progressDialog.dismiss();
            if (isSuccess) {
                Toast.makeText(requireContext(), "Avis ajouté avec succès", Toast.LENGTH_SHORT).show();
                clearForm();
            } else {
                Toast.makeText(requireContext(), "Erreur lors de l'ajout de l'avis", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchReviews() {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Chargement des avis...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        reviewViewModel.fetchReviews(restaurant.getId()).observe(getViewLifecycleOwner(), reviewsList -> {

            Log.d("fetching reviews", String.valueOf(reviews.size()));
            reviews.clear();

            if (reviewsList != null) {
                reviews.addAll(reviewsList);
                reviews.addAll(restaurant.getReviews());
                reviewAdapter.notifyDataSetChanged();
            } else {
                Log.e("LiveData", "Error: reviews list is null");
                Toast.makeText(requireContext(), "Erreur lors du chargement des avis", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        });
    }

}
