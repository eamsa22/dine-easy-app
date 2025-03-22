package org.m2sdl.dineeasy.ui;

import static android.widget.Toast.LENGTH_SHORT;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.m2sdl.dineeasy.R;
import org.m2sdl.dineeasy.databinding.ActivityBookingBinding;
import org.m2sdl.dineeasy.model.Reservation;
import org.m2sdl.dineeasy.model.Restaurant;
import org.m2sdl.dineeasy.viewModel.RestaurantViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingActivity extends BaseActivity {
    private EditText etCustomGuests;
    private EditText dateTimeText;
    private EditText fullNameText;
    private EditText emailText;
    private EditText phoneText;
    private EditText editTextOtherRequest;

    private ImageView viewCalendar;
    private CheckBox checkboxOther;
    private RadioGroup radioGroupGuests;
    private Restaurant restaurant;
    private FirebaseFirestore db;
    private Toast currentToast ;
    private PlacesClient placesClient;
    private RestaurantViewModel restaurantViewModel;

    @SuppressLint({"CutPasteId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityBookingBinding binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupToolbar();
        restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");
        placesClient = Places.createClient(this);
        restaurantViewModel = new RestaurantViewModel();

        restaurant = getIntent().getParcelableExtra("restaurant");
        db = FirebaseFirestore.getInstance();
        etCustomGuests = findViewById(R.id.et_customGuests);
        dateTimeText = findViewById(R.id.et_date);
        fullNameText = findViewById(R.id.et_name);;
        emailText = findViewById(R.id.et_email);
        phoneText = findViewById(R.id.et_phone);
        editTextOtherRequest = findViewById(R.id.editText_other_request);
        viewCalendar = findViewById(R.id.iv_calendar);
        checkboxOther = findViewById(R.id.checkbox_other);
        radioGroupGuests = findViewById(R.id.radioGroupGuests);
        etCustomGuests = findViewById(R.id.et_customGuests);

        initializeRestaurantDetails();

        binding.setNumberOfGuests(getString(R.string.numberOfGuests));
        binding.setStarRequired(getString(R.string.star_requeried));
        binding.setFullName(getString(R.string.fullName));
        binding.setEmail(getString(R.string.email));
        binding.setPhone(getString(R.string.phone));
        binding.setSpecialRequests(getString(R.string.special_requests));
        binding.setReservationDate(getString(R.string.date));

        fullNameText.addTextChangedListener(createTextWatcher(R.id.et_name));
        emailText.addTextChangedListener(createTextWatcher(R.id.et_email));
        phoneText.addTextChangedListener(createTextWatcher(R.id.et_phone));
        dateTimeText.addTextChangedListener(createTextWatcher(R.id.et_date));
        etCustomGuests.addTextChangedListener(createTextWatcher(R.id.et_customGuests));

        dateTimeText.setOnClickListener(view -> openDateTimePicker());
        viewCalendar.setOnClickListener(view -> openDateTimePicker());

        checkboxOther.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editTextOtherRequest.setVisibility(View.VISIBLE);
            } else {
                editTextOtherRequest.setVisibility(View.GONE);
            }
        });

        radioGroupGuests.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio6) {
                    etCustomGuests.setVisibility(View.VISIBLE);
                } else {
                    etCustomGuests.setVisibility(View.GONE);
                }
            }
        });

        NestedScrollView scrollView = findViewById(R.id.nested_scroll_view);
        scrollView.setOnTouchListener((v, event) -> {
            editTextOtherRequest.getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        });

        editTextOtherRequest.setOnTouchListener((v, event) -> {
            editTextOtherRequest.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

    }


    /***
     * function to initialize the details of the restaurant on the view
     */
    private void initializeRestaurantDetails() {
        if (restaurant != null) {
            TextView tvRestaurantName = findViewById(R.id.tv_restaurant_name);
            TextView tvRestaurantAddress = findViewById(R.id.tv_restaurant_address);
            ImageView ivRestaurantImage = findViewById(R.id.iv_restaurant_image);

            tvRestaurantName.setText(restaurant.getName());
            tvRestaurantAddress.setText(restaurant.getAddress());

            restaurantViewModel.getRestaurantImage(restaurant.getMainPhotoReference(),placesClient,ivRestaurantImage);
        }
    }

    /**
     * function that creates watchers for text fields to validate the input
     * @param fieldId id of the text field
     */
    private TextWatcher createTextWatcher(int fieldId) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                if (fieldId == R.id.et_name) {
                    validateFullName();
                } else if (fieldId == R.id.et_email) {
                    validateEmail();
                } else if (fieldId == R.id.et_phone) {
                    validatePhone();
                } else if (fieldId == R.id.et_date) {
                    validateDate();
                }
                else if (fieldId == R.id.et_customGuests) {
                    validateGuestsCount();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };
    }

    /**
     * function to handle on calendar click
     */
    private void openDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        int dateTimePickerTheme = R.style.CustomDatePickerStyle;

        new DatePickerDialog(this, dateTimePickerTheme, (datePicker, selectedYear, selectedMonth, selectedDay) -> {
            selectedMonth += 1;
            int finalSelectedMonth = selectedMonth;

            new TimePickerDialog(this, dateTimePickerTheme, (timePicker, selectedHour, selectedMinute) -> {
                @SuppressLint("DefaultLocale") String dateTime =String.format("%02d-%02d-%04d  ", selectedDay, finalSelectedMonth, selectedYear) +
                        String.format("%02d:%02d", selectedHour, selectedMinute);
                dateTimeText.setText(dateTime);
            }, hour, minute, true).show();

        }, year, month, day).show();
    }

    /**
     * function to navigate to homeActivityView
     */
    public void homeActivity(View view){
        Intent intent = new Intent(this , HomeActivity.class);
        startActivity(intent);
    }

    /**
     * function to create a new reservation in firebase
     * @param view View
     */
    public void reserveRestaurant(View view) {
        if (isFormValid()) {

            String fullName = fullNameText.getText().toString().trim();
            String email = emailText.getText().toString().trim();
            String phone = phoneText.getText().toString().trim();
            String dateTime = dateTimeText.getText().toString().trim();
            int guestsCount = getSelectedGuestsCount();
            List<String> specialRequests = getSpecialRequests();

            if (restaurant == null) {
                Toast.makeText(this, "Erreur : Informations du restaurant introuvables", Toast.LENGTH_LONG).show();
                return;
            }

            String restaurantName = restaurant.getName() != null ? restaurant.getName() : "Nom inconnu";
            String restaurantAddress = restaurant.getAddress();
            String imgUrl = restaurant.getMainPhotoReference();

            Reservation reservation = new Reservation(
                    restaurant.getId(),
                    fullName,
                    email,
                    phone,
                    guestsCount,
                    dateTime,
                    specialRequests,
                    restaurantAddress,
                    restaurantName,
                    imgUrl
            );

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Enregistrement de la réservation...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            addReservationToFirebase(reservation, progressDialog);
        } else {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * function to submit the creation of the reservation
     * @param reservation Reservation
     */
    private void addReservationToFirebase(final Reservation reservation, final ProgressDialog progressDialog) {
        CollectionReference reservationsRef = db.collection("reservations");

        reservationsRef.add(reservation)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Reservation", "Réservation ajoutée avec l'ID: " + documentReference.getId());
                        reservation.setReservationId(documentReference.getId());
                        progressDialog.dismiss();

                        Toast.makeText(BookingActivity.this, "Réservation confirmée!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(BookingActivity.this, ConfirmationActivity.class);
                        intent.putExtra("reservation", reservation);
                        intent.putExtra("typePage","confirmation");
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Reservation", "Erreur lors de l'ajout de la réservation", e);
                        progressDialog.dismiss();  // Fermer le dialogue en cas d'échec
                        Toast.makeText(BookingActivity.this, "Erreur lors de la réservation. Essayez à nouveau.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * function to get the number of selected
     * guests from the radio button group
     */
    private int getSelectedGuestsCount() {
        int selectedRadioId = radioGroupGuests.getCheckedRadioButtonId();

        if (selectedRadioId == R.id.radio6) {
            try {
                return Integer.parseInt(etCustomGuests.getText().toString());
            } catch (NumberFormatException e) {
                return 1;
            }
        } else if (selectedRadioId == R.id.radio1) {
            return 1;
        } else if (selectedRadioId == R.id.radio2) {
            return 2;
        } else if (selectedRadioId == R.id.radio3) {
            return 3;
        } else if (selectedRadioId == R.id.radio4) {
            return 4;
        } else if (selectedRadioId == R.id.radio5) {
            return 5;
        } else {
            return 1;
        }
    }

    /**
     * function to retrieve the requests checked and in the customized field
     * @return List of special requests
     */
    private List<String> getSpecialRequests() {
        List<String> requests = new ArrayList<>();
        CheckBox checkbox1 = findViewById(R.id.checkbox_table_near_window); ;
        CheckBox checkbox2 = findViewById(R.id.checkbox_vegetarian);;
        if (checkbox1.isChecked()) {
            requests.add(getString(R.string.table_near_window));
        }
        if (checkbox2.isChecked()) {
            requests.add(getString(R.string.vegetarian));
        }
        String customRequest = editTextOtherRequest.getText().toString().trim();
        if (!customRequest.isEmpty()) {
            requests.add(customRequest);
        }
        return requests;
    }

    /**
     * Function to validate the form of reservation
     */
    public boolean isFormValid() {
        return validateGuestsCount() && validateDate() && validateFullName() && validateEmail() && validatePhone();
    }

    /***
     * function to display an error message when the form is invalid
     * @param message Error message
     */
    private void showError(String message) {
        if (currentToast != null) {
            currentToast.cancel();
        }

        currentToast = Toast.makeText(this,message, LENGTH_SHORT);
        currentToast.setDuration(LENGTH_SHORT);
        currentToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0,0);
        currentToast.show();
    }


    /**
     * setter of text editor border's color in error case
     * @param field EditText
     */
    private void setErrorBackground(EditText field) {
        field.setBackgroundResource(R.drawable.border_error);
    }

    /**
     * setter of text editor border's color in default case
     * @param field EditText
     */
    private void resetFieldBackground(EditText field) {
        field.setBackgroundResource(R.drawable.border);
    }

    /***
     * Guests-Number Validator
     */
    private boolean validateGuestsCount() {
        int selectedRadioId = radioGroupGuests.getCheckedRadioButtonId();

        if (selectedRadioId == -1) {
            showError("Veuillez sélectionner le nombre de convives");
            return false;
        }

        if (selectedRadioId == R.id.radio6) {
            String customGuestsCount = etCustomGuests.getText().toString().trim();
            try {
                int customCount = Integer.parseInt(customGuestsCount);
                if (customCount < 1) {
                    showError("Le nombre de convives doit être supérieur à zéro");
                    setErrorBackground(etCustomGuests);
                    return false;
                } else {
                    resetFieldBackground(etCustomGuests);
                    if (currentToast != null) {
                        currentToast.cancel();
                    }
                    return true;
                }
            } catch (NumberFormatException e) {
                showError("Veuillez entrer un nombre valide pour le nombre de convives");
                setErrorBackground(etCustomGuests);
                return false;
            }
        } else {
            resetFieldBackground(etCustomGuests);
            if (currentToast != null) {
                currentToast.cancel();
            }
            return true;
        }
    }

    /***
     * Date-Time Validator
     */
    private boolean validateDate() {
        String reservationDate = dateTimeText.getText().toString().trim();
        if (reservationDate.isEmpty()) {
            setErrorBackground(dateTimeText);
            showError("La date est obligatoire");
            return false;
        }

        try {
            String[] dateParts = reservationDate.split(" {2}")[0].split("-");
            String[] timeParts = reservationDate.split(" {2}")[1].split(":");

            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1;
            int year = Integer.parseInt(dateParts[2]);

            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, day, hour, minute);

            Calendar currentDate = Calendar.getInstance();

            if (selectedDate.before(currentDate)) {
                setErrorBackground(dateTimeText);
                showError("La date ne peut pas être dans le passé");
                return false;
            }

            long diffInMillis = selectedDate.getTimeInMillis() - currentDate.getTimeInMillis();
            if (diffInMillis < 15 * 60 * 1000) {
                setErrorBackground(dateTimeText);
                showError("La réservation doit être dans au moins 15 minutes");
                return false;
            }

            resetFieldBackground(dateTimeText);
            if (currentToast != null) {
                currentToast.cancel();
            }
            return true;

        } catch (Exception e) {
            setErrorBackground(dateTimeText);
            showError("Format de date invalide");
            return false;
        }
    }

    /***
     * Full-name Validator
     */
    private boolean validateFullName() {
        String fullName = fullNameText.getText().toString().trim();

        String[] words = fullName.split("\\s+");
        if (words.length < 2) {
            setErrorBackground(fullNameText);
            showError("Le nom complet doit contenir au moins deux mots");
            return false;
        } else {
            resetFieldBackground(fullNameText);
            if (currentToast != null) {
                currentToast.cancel();
            }
            return true;
        }
    }

    /***
     * Email Validator
     */
    private boolean validateEmail() {
        String email = emailText.getText().toString().trim();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setErrorBackground(emailText);
            showError("Email invalide");
            return false;
        } else {
            resetFieldBackground(emailText);
            if (currentToast != null) {
                currentToast.cancel();
            }
            return true;
        }
    }

    /***
     * Phone Validator
     */
    private boolean validatePhone() {
        String phone = phoneText.getText().toString().trim();

        if (phone.isEmpty() || phone.length() < 10) {
            setErrorBackground(phoneText);
            showError("Numéro de téléphone invalide");
            return false;
        } else {
            resetFieldBackground(phoneText);
            if (currentToast != null) {
                currentToast.cancel();
            }
            return true;
        }
    }

}