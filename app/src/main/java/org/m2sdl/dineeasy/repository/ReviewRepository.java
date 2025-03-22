package org.m2sdl.dineeasy.repository;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.m2sdl.dineeasy.model.Review;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ReviewRepository {

    private final FirebaseFirestore firestore;
    private final FirebaseStorage storage;
    private final MutableLiveData<Boolean> reviewSubmissionLiveData = new MutableLiveData<>();

    public ReviewRepository() {
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }


    public MutableLiveData<List<Review>> getReviews(String restaurantId) {
        MutableLiveData<List<Review>> reviewsLiveData = new MutableLiveData<>();

        firestore.collection("Reviews")
                .whereEqualTo("restaurantId", restaurantId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        List<Review> reviews = snapshot.toObjects(Review.class);
                        reviewsLiveData.setValue(reviews);
                    } else {
                        reviewsLiveData.setValue(null);
                    }
                });

        return reviewsLiveData;
    }



    public void submitReview(Review review, List<Bitmap> imageList) {
        if (imageList == null || imageList.isEmpty()) {
            saveReviewToFirebase(review);
            return;
        }

        List<String> uploadedImageUrls = new ArrayList<>();
        StorageReference storageRef = storage.getReference();

        for (Bitmap bitmap : imageList) {
            byte[] imageBytes = convertBitmapToByteArray(bitmap);
            StorageReference imageRef = storageRef.child("images/" + System.currentTimeMillis() + ".png");
            UploadTask uploadTask = imageRef.putBytes(imageBytes);

            uploadTask.addOnSuccessListener(taskSnapshot ->
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        uploadedImageUrls.add(uri.toString());
                        if (uploadedImageUrls.size() == imageList.size()) {
                            review.setImagePathStrings(uploadedImageUrls);
                            saveReviewToFirebase(review);
                        }
                    })
            ).addOnFailureListener(e -> reviewSubmissionLiveData.postValue(false));
        }
    }

    private void saveReviewToFirebase(final Review review) {
        firestore.collection("Reviews").add(review)
                .addOnSuccessListener(documentReference -> {
                    review.setReviewId(documentReference.getId());
                    documentReference.update("reviewId", review.getReviewId())
                            .addOnSuccessListener(aVoid -> reviewSubmissionLiveData.postValue(true))
                            .addOnFailureListener(e -> reviewSubmissionLiveData.postValue(false));
                })
                .addOnFailureListener(e -> reviewSubmissionLiveData.postValue(false));
    }

    public LiveData<Boolean> getReviewSubmissionLiveData() {
        return reviewSubmissionLiveData;
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}
