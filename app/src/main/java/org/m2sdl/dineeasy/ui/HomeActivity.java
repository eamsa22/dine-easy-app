package org.m2sdl.dineeasy.ui;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.m2sdl.dineeasy.BuildConfig;
import org.m2sdl.dineeasy.R;
import org.m2sdl.dineeasy.adapter.RecyclerViewAdapter;
import org.m2sdl.dineeasy.model.Restaurant;
import org.m2sdl.dineeasy.viewModel.RestaurantViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class HomeActivity extends BaseActivity {

    private RecyclerView restaurantRecyclerView ;
    private RecyclerViewAdapter adapter ;

    private ArrayAdapter<String> placeSuggestionsAdapter;
    private AutoCompleteTextView locationInput;

    private CheckBox vegetarianCheckBox ;
    private CheckBox veganCheckBox ;

    private HashMap<String, String> placeIdMap = new HashMap<>();

    private RestaurantViewModel viewModel ;

    private ProgressBar progressBar;
    private PlacesClient placesClient;

    private double selectedLatitude = 0.0;
    private double selectedLongitude = 0.0;
    String includedType ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home_activity);
        setupToolbar();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        viewModel =  new ViewModelProvider(this).get(RestaurantViewModel.class);

        progressBar = findViewById(R.id.progressBar) ;

        locationInput = findViewById(R.id.location_input);
        placeSuggestionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        locationInput.setAdapter(placeSuggestionsAdapter);

        vegetarianCheckBox = findViewById(R.id.checkbox_vegetarian);
        veganCheckBox = findViewById(R.id.checkBox_vegan);



        progressBar = findViewById(R.id.progressBar) ;
        viewModel =  new ViewModelProvider(this).get(RestaurantViewModel.class);

       String apiKey = BuildConfig.PLACES_API_KEY;

        if (TextUtils.isEmpty(apiKey) || apiKey.equals("DEFAULT_API_KEY")) {
            Log.e("Places", "No api key");
            finish();
            return;
        }
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(getApplicationContext(), apiKey);
        }
        placesClient = Places.createClient(this);



        restaurantRecyclerView = findViewById(R.id.restaurant_recycler_view);
        ArrayList<Restaurant> list = new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);
        restaurantRecyclerView.setVisibility(View.GONE);

        getFilters();
        viewModel.getRestaurants(placesClient,includedType,selectedLatitude,selectedLongitude).observe(this,restaurants -> {
            list.clear();
            if (restaurants != null) {
                progressBar.setVisibility(View.GONE);
                restaurantRecyclerView.setVisibility(View.VISIBLE);
                list.addAll(restaurants);


            } else {
                Log.e("LiveData", "Erreur : liste des restaurants est null");
                Toast.makeText(this, "Erreur de chargement des restaurants", Toast.LENGTH_SHORT).show();
            }}  );

        RestaurantViewModel viewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);

        viewModel.getUpdatedList().observe(this, updatedRestaurants -> {
            if (updatedRestaurants != null) {
                updateUI(updatedRestaurants);
            }
        });

        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager((this)));

        adapter  = new RecyclerViewAdapter(this, list, Places.createClient(this));
        restaurantRecyclerView.setAdapter(adapter);

        locationInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 1) {
                    getPlaceSuggestions(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        locationInput.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPlace = (String) parent.getItemAtPosition(position);

            String placeId = placeIdMap.get(selectedPlace);
            if (placeId != null) {
                fetchPlaceCoordinates(placeId);
            }
        });

        Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFilters();
                viewModel.getRestaurants(placesClient,includedType,selectedLatitude,selectedLongitude).observe(HomeActivity.this, restaurants -> {
                    progressBar.setVisibility(View.VISIBLE);
                    restaurantRecyclerView.setVisibility(View.GONE);
                    list.clear();
                    if (restaurants != null) {
                        progressBar.setVisibility(View.GONE);
                        restaurantRecyclerView.setVisibility(View.VISIBLE);
                        list.addAll(restaurants);
                        updateUI(restaurants);

                    } else {
                        Log.e("LiveData", "Erreur : liste des restaurants est null");
                    }}  );
            }
        });

    }

    private void updateUI(List<Restaurant> restaurants){
        adapter.setRestaurants(restaurants);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!isFinishing() && !isDestroyed()) {
            restaurantRecyclerView.setAdapter(adapter);
        }
    }

    private void getPlaceSuggestions(String query) {
        FindAutocompletePredictionsRequest predictionsRequest =
                FindAutocompletePredictionsRequest.builder()
                        .setQuery(query)
                        .build();

        placesClient.findAutocompletePredictions(predictionsRequest)
                .addOnSuccessListener((FindAutocompletePredictionsResponse response) -> {
                    List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
                    List<String> result = new ArrayList<>();
                    placeIdMap.clear();
                    for (AutocompletePrediction prediction : predictions) {
                        String placeName = prediction.getFullText(null).toString();
                        String placeId = prediction.getPlaceId();

                        result.add(placeName);
                        placeIdMap.put(placeName, placeId);
                    }
                    updatePlaceSuggestionsAdapter(result);
                })
                .addOnFailureListener((exception) -> {
                    Log.e("Google Places API", "Place prediction error: " + exception.getMessage());
                });
    }

    private void updatePlaceSuggestionsAdapter(List<String> suggestions) {
        placeSuggestionsAdapter.clear();
        placeSuggestionsAdapter.addAll(suggestions);
        placeSuggestionsAdapter.notifyDataSetChanged();
    }

    public void getFilters() {
        boolean isVegetarian = vegetarianCheckBox != null && vegetarianCheckBox.isChecked();
        boolean isVegan = veganCheckBox != null && veganCheckBox.isChecked();

        includedType = isVegan ? "vegan_restaurant" : isVegetarian ? "vegetarian_restaurant" : "restaurant";

    }

    private void fetchPlaceCoordinates(String placeId) {
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId,
                Arrays.asList(Place.Field.LAT_LNG)).build();

        placesClient.fetchPlace(request)
                .addOnSuccessListener(response -> {
                    LatLng latLng = response.getPlace().getLatLng();
                    if (latLng != null) {
                        selectedLatitude = latLng.latitude;
                        selectedLongitude = latLng.longitude;
                    }
                })
                .addOnFailureListener(exception -> {
                    Log.e("Google Places API", "Error fetching place details: " + exception.getMessage());
                });
    }



}