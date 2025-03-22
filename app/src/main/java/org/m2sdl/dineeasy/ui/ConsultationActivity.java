package org.m2sdl.dineeasy.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.m2sdl.dineeasy.R;
import org.m2sdl.dineeasy.model.Reservation;

public class ConsultationActivity extends BaseActivity {

    private EditText etSearch;
    private TextView tvErrorMessage;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_consultation);
        setupToolbar();
        setupInsets();
        initUI();
    }

    /**
     * Initializes the UI components.
     */
    private void initUI() {
        etSearch = findViewById(R.id.et_search);
        tvErrorMessage = findViewById(R.id.tv_error_message);
        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching...");
        progressDialog.setCancelable(false);
    }

    /**
     * Adjusts view padding to avoid overlap with system bars.
     */
    private void setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Initiates a search for a reservation in Firestore.
     */
    public void searchReservation(View view) {
        String reference = etSearch.getText().toString().trim();

        if (reference.isEmpty()) {
            showErrorMessage("Please enter a reservation number");
            return;
        }

        progressDialog.show();
        tvErrorMessage.setVisibility(View.GONE);

        db.collection("reservations").document(reference).get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();

                    if (task.isSuccessful() && task.getResult() != null) {
                        handleReservationResult(task.getResult(), reference);
                    } else {
                        showErrorMessage("Error while searching");
                    }
                });
    }

    /**
     * Handles the result of the Firestore query.
     */
    private void handleReservationResult(DocumentSnapshot document, String reference) {
        if (document.exists()) {
            Reservation reservation = document.toObject(Reservation.class);

            if (reservation != null) {
                reservation.setReservationId(reference);
                navigateToConfirmation(reservation);
            } else {
                showErrorMessage("An issue occurred while retrieving the reservation");
            }
        } else {
            showErrorMessage("Reservation not found");
        }
    }

    /**
     * Displays an error message in the TextView.
     */
    private void showErrorMessage(String message) {
        tvErrorMessage.setText(message);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }

    /**
     * Navigates to the ConfirmationActivity with reservation data.
     */
    private void navigateToConfirmation(Reservation reservation) {
        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra("reservation", reservation);
        intent.putExtra("typePage", "consultation");
        startActivity(intent);
    }
}
