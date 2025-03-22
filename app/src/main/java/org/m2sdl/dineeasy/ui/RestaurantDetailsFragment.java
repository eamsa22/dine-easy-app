package org.m2sdl.dineeasy.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.IsOpenRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.m2sdl.dineeasy.R;
import org.m2sdl.dineeasy.adapter.RestaurantPhotosPagerAdapter;
import org.m2sdl.dineeasy.model.Restaurant;

import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RestaurantDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantDetailsFragment extends Fragment {
    private static final String ARG_RESTAURANT = "restaurant";
    private Restaurant restaurant ;
    private ImageView arrowLeft, arrowRight;

    private PlacesClient placesClient ;

    public RestaurantDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param restaurant Parameter 1.
     * @return A new instance of fragment RestaurantDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RestaurantDetailsFragment newInstance(Restaurant restaurant) {
        RestaurantDetailsFragment fragment = new RestaurantDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RESTAURANT, restaurant);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurant = getArguments().getParcelable(ARG_RESTAURANT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        placesClient = Places.createClient(requireActivity());

        Calendar isOpenCalendar = Calendar.getInstance();

        IsOpenRequest isOpenRequest = IsOpenRequest.newInstance(restaurant.getId(), isOpenCalendar.getTimeInMillis());


        View view = inflater.inflate(R.layout.fragment_restaurant_details, container, false);

        arrowLeft = view.findViewById(R.id.iv_arrow_left);
        arrowRight = view.findViewById(R.id.iv_arrow_right);
        
        TextView nameTextView = view.findViewById(R.id.details_restaurant_name);
        nameTextView.setText(restaurant.getName());
        TextView addressTextView = view.findViewById(R.id.details_restaurant_address);
        addressTextView.setText(restaurant.getAddress());
        TextView ratingTextView = view.findViewById(R.id.details_restaurant_rating);
        ratingTextView.setText(String.valueOf(restaurant.getRating()));
        TextView phoneNumberTextView = view.findViewById(R.id.details_restaurant_phone_number);
        phoneNumberTextView.setText(String.valueOf(restaurant.getPhoneNumber()));
        TextView webTextView = view.findViewById(R.id.details_restaurant_web);
        webTextView.setText(String.valueOf(restaurant.getWebsiteUrl() != null ? restaurant.getWebsiteUrl() : getString(R.string.restaurant_no_web_Site) )  );
        TextView scheduleTextView = view.findViewById(R.id.details_restaurant_schedule);
        TextView vegeterianTextView = view.findViewById(R.id.details_restaurant_vegeterian);
        vegeterianTextView.setText(restaurant.isServesVegetarianFood() ? getString(R.string.serves_vegeterian): getString(R.string.serves_no_vegeterian));
        StringBuilder formattedText = new StringBuilder();
        for (String day : restaurant.getOpening()) {
            formattedText.append(day).append("\n \n");
        }
        scheduleTextView.setText(formattedText);

        TextView openTextView = view.findViewById(R.id.details_restaurant_open);

        placesClient.isOpen(isOpenRequest).addOnSuccessListener(response -> {
            boolean isOpen = Boolean.TRUE.equals(response.isOpen());
            if(isOpen){
                openTextView.setText(getString(R.string.open));
                openTextView.setTextColor(ContextCompat.getColor(requireContext(),R.color.green));
            }else{
                openTextView.setText(R.string.closed);
                openTextView.setTextColor(ContextCompat.getColor(requireContext(),R.color.red));
            }

        });



        ViewPager2 viewPager = view.findViewById(R.id.photos_pager);

        if(restaurant != null && restaurant.getPhotoReferences() != null ){
            List<String> references = restaurant.getPhotoReferences() ;
            RestaurantPhotosPagerAdapter adapter = new RestaurantPhotosPagerAdapter(getContext(),references , Places.createClient(requireContext()));
            viewPager.setAdapter(adapter);

            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                  updateArrows(position, adapter.getItemCount());
                }
            });


            arrowLeft.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true));

            arrowRight.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true));
            updateArrows(0, adapter.getItemCount());

        }

        Button bookingButton = view.findViewById(R.id.booking_button);
        bookingButton.setOnClickListener(v -> bookingActivity());




        return view ;
    }

    private void updateArrows(int position, int itemCount) {
        arrowLeft.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        arrowRight.setVisibility(position == itemCount - 1 ? View.GONE : View.VISIBLE);
    }

    public void bookingActivity() {
        Intent intent = new Intent(getContext(),BookingActivity.class);
        intent.putExtra("restaurant",restaurant) ;
        startActivity(intent);
    }


}