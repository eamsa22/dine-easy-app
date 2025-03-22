package org.m2sdl.dineeasy.viewModel;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.m2sdl.dineeasy.model.Review;
import org.m2sdl.dineeasy.repository.ReviewRepository;

import java.util.ArrayList;
import java.util.List;

public class ReviewViewModel extends ViewModel {

    private MutableLiveData<List<Review>> reviewsLiveData;
    private ReviewRepository reviewRepository;
    private boolean isFetchingData = false ;

    public ReviewViewModel() {
        reviewRepository = new ReviewRepository();
        reviewsLiveData = new MutableLiveData<>();
    }


    public LiveData<List<Review>> fetchReviews(String restaurantId) {
        if(!isFetchingData) {
            isFetchingData = true;
            reviewRepository.getReviews(restaurantId).observeForever(newReviews -> {
                if (newReviews != null) {
                    reviewsLiveData.postValue(newReviews);
                }
                isFetchingData = false ;
            });
        }
        return reviewsLiveData;
    }

    public LiveData<Boolean> getReviewSubmissionLiveData() {
        return reviewRepository.getReviewSubmissionLiveData();
    }

    public void submitReview(Review review, List<Bitmap> imageList) {
        reviewRepository.submitReview(review, imageList);
    }
}
