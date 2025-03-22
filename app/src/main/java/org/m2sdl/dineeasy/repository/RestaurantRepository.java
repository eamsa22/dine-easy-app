package org.m2sdl.dineeasy.repository;


import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AuthorAttribution;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.Review;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.SearchByTextRequest;

import org.m2sdl.dineeasy.R;
import org.m2sdl.dineeasy.model.Restaurant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class RestaurantRepository {

    public RestaurantRepository() {
    }

    public MutableLiveData<List<Restaurant>> getRestaurants(PlacesClient placesClient , String includedType , double selectedLatitude , double selectedLongitude  ) {
        MutableLiveData<List<Restaurant>> restaurantsLiveData = new MutableLiveData<>();

        final List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER,
                Place.Field.WEBSITE_URI,
                Place.Field.CURRENT_OPENING_HOURS,
                Place.Field.OPENING_HOURS,
                Place.Field.RESERVABLE,
                Place.Field.RATING,
                Place.Field.USER_RATINGS_TOTAL,
                Place.Field.PRICE_LEVEL,
                Place.Field.DELIVERY,
                Place.Field.DINE_IN,
                Place.Field.TAKEOUT,
                Place.Field.SERVES_BREAKFAST,
                Place.Field.SERVES_BRUNCH,
                Place.Field.SERVES_LUNCH,
                Place.Field.SERVES_DINNER,
                Place.Field.SERVES_VEGETARIAN_FOOD,
                Place.Field.SERVES_WINE,
                Place.Field.PHOTO_METADATAS,
                Place.Field.TYPES,
                Place.Field.REVIEWS

        );

        final SearchByTextRequest searchByTextRequest ;

        if (selectedLatitude != 0.0 && selectedLongitude != 0.0) {
            searchByTextRequest = SearchByTextRequest.builder("restaurant", placeFields)
                    .setIncludedType(includedType)
                    .setLocationRestriction(RectangularBounds.newInstance(
                            new LatLng(selectedLatitude - 0.01, selectedLongitude - 0.01),
                            new LatLng(selectedLatitude + 0.01, selectedLongitude + 0.01)
                    ))
                    .build();
            }else{
            searchByTextRequest = SearchByTextRequest.builder("restaurant", placeFields).setIncludedType(includedType)
                    .build();
        }




        placesClient.searchByText(searchByTextRequest)
                .addOnSuccessListener(response -> {
                    List<Place> places = response.getPlaces();
                    List<Restaurant> restaurantList = new ArrayList<>();

                    for (Place place : places) {

                        List<String> photoReferences = new ArrayList<>();
                        List<org.m2sdl.dineeasy.model.Review> reviews = new ArrayList<>();

                        String mainPhotoReference = null;
                        if (place.getPhotoMetadatas() != null) {
                            for (PhotoMetadata metadata : place.getPhotoMetadatas()) {
                                photoReferences.add(metadata.zza());
                                if (metadata.getAuthorAttributions() != null && mainPhotoReference == null) {
                                    for (AuthorAttribution attribution : metadata.getAuthorAttributions().asList()) {
                                        if (attribution.getName().equalsIgnoreCase(place.getName())) {
                                            mainPhotoReference = metadata.zza();
                                            break;
                                        }
                                    }
                                }
                            }
                            Log.d("Photos : ", place.getPhotoMetadatas().toString());

                        }

                        if (place.getReviews() != null && (!place.getReviews().isEmpty())) {
                            for(Review review : place.getReviews()){
                                Double reviewRating = review.getRating();
                                String reviewText = review.getText();
                                String authorName = review.getAuthorAttribution().getName();
                                String reviewTitle = "Avis "+ review.getRelativePublishTimeDescription();
                                org.m2sdl.dineeasy.model.Review reviewModel = new org.m2sdl.dineeasy.model.Review(place.getId(),authorName,reviewTitle,reviewText,  (int) reviewRating.doubleValue(),List.of());
                                reviews.add(reviewModel);
                            }
                        }
                        Restaurant restaurant = new Restaurant(
                                place.getId(),
                                place.getName(),
                                place.getAddress(),
                                place.getPhoneNumber(),
                                place.getWebsiteUri() != null ? place.getWebsiteUri().toString() : null,
                                place.getReservable() == Place.BooleanPlaceAttributeValue.TRUE,
                                place.getRating() != null ? place.getRating() : 0.0,
                                place.getUserRatingsTotal() != null ? place.getUserRatingsTotal() : 0,
                                place.getPriceLevel() != null ? place.getPriceLevel() : -1,
                                place.getDelivery() == Place.BooleanPlaceAttributeValue.TRUE,
                                place.getDineIn() == Place.BooleanPlaceAttributeValue.TRUE,
                                place.getTakeout() == Place.BooleanPlaceAttributeValue.TRUE,
                                place.getServesBreakfast() == Place.BooleanPlaceAttributeValue.TRUE,
                                place.getServesBrunch() == Place.BooleanPlaceAttributeValue.TRUE,
                                place.getServesLunch() == Place.BooleanPlaceAttributeValue.TRUE,
                                place.getServesDinner() == Place.BooleanPlaceAttributeValue.TRUE,
                                place.getServesVegetarianFood() == Place.BooleanPlaceAttributeValue.TRUE,
                                place.getServesWine() == Place.BooleanPlaceAttributeValue.TRUE,
                                photoReferences,
                                mainPhotoReference,
                                place.getOpeningHours() != null ? new ArrayList<>(place.getOpeningHours().getWeekdayText()) : null,
                                place.getPlaceTypes(),
                                reviews
                        );


                        restaurantList.add(restaurant);
                    }

                    restaurantsLiveData.setValue(restaurantList);
                    Log.d("Places API", "Restaurants retrieved successfully.");

                })
                .addOnFailureListener(e -> Log.e("Places API", "Error fetching restaurants", e));

        return restaurantsLiveData;
    }


    public void getRestaurantImage(String photoReference, PlacesClient placesClient, ImageView imageView) {
        if (photoReference == null || photoReference.isEmpty()) {
            imageView.setImageResource(R.drawable.default_restaurant);
            return;
        }
        PhotoMetadata photoMetadata = PhotoMetadata.builder(photoReference).build();

        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(500)
                .setMaxHeight(500)
                .build();

        placesClient.fetchPhoto(photoRequest).addOnSuccessListener(fetchPhotoResponse -> {
            Bitmap bitmap = fetchPhotoResponse.getBitmap();
            imageView.setImageBitmap(bitmap);
        }).addOnFailureListener(e -> {
            imageView.setImageResource(R.drawable.default_restaurant);
        });
    }

}
