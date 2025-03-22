package org.m2sdl.dineeasy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import org.m2sdl.dineeasy.R;
import org.m2sdl.dineeasy.model.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final List<Review> reviews;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewerName, tvReviewTitle, tvReviewComment;
        RatingBar ratingBar;
        ViewPager2 photosPager;
        ImageView ivArrowLeft, ivArrowRight;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewerName = itemView.findViewById(R.id.tv_reviewer_name);
            tvReviewTitle = itemView.findViewById(R.id.tv_review_title);
            tvReviewComment = itemView.findViewById(R.id.tv_review_comment);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            photosPager = itemView.findViewById(R.id.photos_pager);
            ivArrowLeft = itemView.findViewById(R.id.iv_arrow_left);
            ivArrowRight = itemView.findViewById(R.id.iv_arrow_right);
        }

        public void bind(Review review) {
            tvReviewerName.setText(review.getName());
            tvReviewTitle.setText(review.getTitle());
            tvReviewComment.setText(review.getComment());
            ratingBar.setRating(review.getRating());

            if (review.getImagePathStrings() != null && !review.getImagePathStrings().isEmpty()) {
                ImagePagerAdapter adapter = new ImagePagerAdapter(review.getImagePathStrings());
                photosPager.setAdapter(adapter);
                photosPager.setVisibility(View.VISIBLE);
                ivArrowLeft.setVisibility(View.VISIBLE);
                ivArrowRight.setVisibility(View.VISIBLE);

                ivArrowLeft.setOnClickListener(v -> {
                    if (photosPager.getCurrentItem() > 0) {
                        photosPager.setCurrentItem(photosPager.getCurrentItem() - 1);
                    }
                });

                ivArrowRight.setOnClickListener(v -> {
                    if (photosPager.getCurrentItem() < review.getImagePathStrings().size() - 1) {
                        photosPager.setCurrentItem(photosPager.getCurrentItem() + 1);
                    }
                });
            } else {
                photosPager.setVisibility(View.GONE);
                ivArrowLeft.setVisibility(View.GONE);
                ivArrowRight.setVisibility(View.GONE);
            }
        }
    }
}

