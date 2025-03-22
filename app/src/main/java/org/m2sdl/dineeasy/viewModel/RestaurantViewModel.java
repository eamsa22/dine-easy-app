package org.m2sdl.dineeasy.viewModel;


import android.util.Log;
import android.widget.ImageView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.libraries.places.api.net.PlacesClient;

import org.m2sdl.dineeasy.R;
import org.m2sdl.dineeasy.model.Restaurant;
import org.m2sdl.dineeasy.repository.RestaurantRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestaurantViewModel extends ViewModel {
    private RestaurantRepository repository ;
    private MutableLiveData<List<Restaurant>> restaurants  ;

    private MutableLiveData<List<Restaurant>> updatedRestaurants = new MutableLiveData<>();

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    private boolean isFetchingData = false ;

    public RestaurantViewModel(){
        repository = new RestaurantRepository();
        restaurants = new MutableLiveData<>();
    }

    public LiveData<List<Restaurant>> getRestaurants(PlacesClient placesClient, String includedType , double selectedLatitude , double selectedLongitude) {
        if(!isFetchingData) {
            isFetchingData = true;
            repository.getRestaurants(placesClient,includedType,selectedLatitude,selectedLongitude ).observeForever(newRestaurants -> {
                if (newRestaurants != null) {
                    restaurants.postValue(newRestaurants);
                }
                isFetchingData = false ;
            });
        }
        return restaurants;
    }
    public void getRestaurantImage(String photoReference, PlacesClient placesClient, ImageView imageView) {
        if ( photoReference != null && !photoReference.isEmpty()) {
            repository.getRestaurantImage(photoReference, placesClient, imageView);
        } else {
            Log.d("RecyclerViewAdapter", "Image not found, using default.");
            imageView.setImageResource(R.drawable.default_restaurant);
        }
    }
        @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
    public MutableLiveData<List<Restaurant>> getUpdatedList() {
        return updatedRestaurants;
    }
}
