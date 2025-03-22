package org.m2sdl.dineeasy.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

import org.m2sdl.dineeasy.R;
import org.m2sdl.dineeasy.databinding.ActivityRestaurantDetailsBinding;
import org.m2sdl.dineeasy.model.Restaurant;

public class RestaurantDetailsActivity extends BaseActivity {

    private Restaurant restaurant;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityRestaurantDetailsBinding binding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupToolbar();

        restaurant = getIntent().getParcelableExtra("restaurant");
        tabLayout = findViewById(R.id.tab_layout);


        loadFragment(RestaurantDetailsFragment.newInstance(restaurant));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment = null;
                if (tab.getPosition() == 0) {
                    selectedFragment = RestaurantDetailsFragment.newInstance(restaurant);
                } else {
                    selectedFragment = new ReviewsFragment();
                }
                loadFragment(selectedFragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    /**
     * function to load the details or reviews fragment
     */
    private void loadFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("restaurant", restaurant);

        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }


}