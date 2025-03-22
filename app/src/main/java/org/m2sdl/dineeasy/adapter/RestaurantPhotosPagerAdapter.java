package org.m2sdl.dineeasy.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.model.PhotoMetadata;

import org.m2sdl.dineeasy.R;
import org.m2sdl.dineeasy.repository.RestaurantRepository;

import java.util.List;

public class RestaurantPhotosPagerAdapter extends RecyclerView.Adapter<RestaurantPhotosPagerAdapter.ViewHolder> {
    private Context context;
    private List<String> menuPhotoReferences;
    private PlacesClient placesClient;

    private RestaurantRepository restaurantRepository ;


    public RestaurantPhotosPagerAdapter(Context context, List<String> menuPhotoReferences, PlacesClient placesClient) {
        this.context = context;
        this.menuPhotoReferences = menuPhotoReferences;
        this.placesClient = placesClient;
        this.restaurantRepository = new RestaurantRepository() ;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo_pager, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String photoReference = menuPhotoReferences.get(position);
        this.restaurantRepository.getRestaurantImage(photoReference,placesClient,holder.imageView);
    }

    @Override
    public int getItemCount() {
        return menuPhotoReferences.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_photo_pager);
        }
    }

}
