package org.m2sdl.dineeasy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.net.PlacesClient;

import org.m2sdl.dineeasy.R;
import org.m2sdl.dineeasy.ui.RestaurantDetailsActivity;
import org.m2sdl.dineeasy.model.Restaurant;
import org.m2sdl.dineeasy.viewModel.RestaurantViewModel;

import java.util.HashSet;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private final List<Restaurant> restaurantList;
    private final PlacesClient placesClient;
    private final RestaurantViewModel restaurantViewModel = new RestaurantViewModel() ;

    public RecyclerViewAdapter(Context context, List<Restaurant> restaurantList, PlacesClient placesClient) {
        this.context = context;
        this.restaurantList = restaurantList;
        this.placesClient = placesClient;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRestaurants(List<Restaurant> list) {
        this.restaurantList.clear();
        this.restaurantList.addAll(new HashSet<>(list));
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.restaurant_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Restaurant restaurant = restaurantList.get(position);
        if(restaurant != null ){
            holder.nameTextView.setText(restaurant.getName());
            holder.addressTextView.setText(restaurant.getAddress());
            restaurantViewModel.getRestaurantImage(restaurant.getMainPhotoReference(), placesClient , holder.imageView);
            setOnCardClickListener(holder.itemView, restaurant);
        }
    }

    private void setOnCardClickListener(View itemView, Restaurant restaurant) {
        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RestaurantDetailsActivity.class);
            intent.putExtra("restaurant", restaurant);
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return restaurantList != null ? restaurantList.size() : 0;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView addressTextView,nameTextView ;
        ImageView imageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            addressTextView = itemView.findViewById(R.id.address);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
