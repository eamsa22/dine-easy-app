package org.m2sdl.dineeasy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.m2sdl.dineeasy.R;
import org.m2sdl.dineeasy.databinding.ActivityConfirmationBinding;
import org.m2sdl.dineeasy.model.Reservation;
import org.m2sdl.dineeasy.viewModel.RestaurantViewModel;

import java.util.Objects;

public class ConfirmationActivity extends BaseActivity {

    private Reservation reservation;
    private String typePage;
    private RestaurantViewModel restaurantViewModel ;
    private PlacesClient placesClient;

    private ActivityConfirmationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupToolbar();

        restaurantViewModel = new RestaurantViewModel();
        placesClient = Places.createClient(this);
        reservation = (Reservation) getIntent().getSerializableExtra("reservation");
        typePage = (String) getIntent().getSerializableExtra("typePage");

        initializeReservationDetails();

    }

    /**
     * Function to initialize do view with the reservation details
     */
    private void initializeReservationDetails() {
        if(reservation != null){
            if(Objects.equals(typePage, "confirmation")){
                binding.setTitleConsultOrConfirm(getString(R.string.confirmation_reservation));
                binding.setConfirmationMessage(getString(R.string.confirmation_text, reservation.getFullName()));
            }
            else{
                binding.setTitleConsultOrConfirm(getString(R.string.consultation_reservation));
                binding.setConfirmationMessage(getString(R.string.consultation_text, reservation.getFullName()));
            }
            binding.setIconInfo(R.drawable.icons_info);
            binding.setLabelReservationNumber(getString(R.string.reservation_number));
            binding.setFieldReservationNumber(reservation.getReservationId());
            binding.setIconEmail(R.drawable.email_icon);
            binding.setLabelEmail(getString(R.string.contact_email));
            binding.setFieldEmail(reservation.getEmail());
            binding.setIconPhone(R.drawable.baseline_contact_phone_24);
            binding.setLabelPhone(getString(R.string.contact_phone));
            binding.setFieldPhone(reservation.getPhone());
            binding.setIconGuests(R.drawable.guests_icon);
            binding.setLabelGuests(getString(R.string.numberOfGuests));
            binding.setFieldGuests(reservation.getGuestsCount() + " personne(s)");
            binding.setRestaurantAddress(reservation.getRestaurantAddress());
            binding.setRestaurantName(reservation.getRestaurantName());
            binding.setReservationDate(reservation.getDateTime());
            binding.setReservationDateLabel(getString(R.string.date));
            binding.setIconCalendar(R.drawable.baseline_calendar_month_24);
            ImageView ivRestaurantImage = findViewById(R.id.iv_restaurant_image);
            restaurantViewModel.getRestaurantImage(reservation.getRestaurantImgUrl(),placesClient,ivRestaurantImage);

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        super.onBackPressed();
        if (Objects.equals(typePage, "confirmation")) {
            intent = new Intent(this, HomeActivity.class);
        }
        else{
            intent = new Intent(this, ConsultationActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }
}