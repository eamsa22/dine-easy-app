package org.m2sdl.dineeasy.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import org.m2sdl.dineeasy.R;
import org.m2sdl.dineeasy.model.Restaurant;
import org.m2sdl.dineeasy.viewModel.RestaurantViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


    }

    public void homeActivity(View view){
        Intent intent = new Intent(this , HomeActivity.class);
        startActivity(intent);
    }

    public void ConsultationActivity(View view){
        Intent intent = new Intent(this , ConsultationActivity.class);
        startActivity(intent);
    }

}