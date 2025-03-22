package org.m2sdl.dineeasy.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Review implements Parcelable {
    private String reviewId;
    private String restaurantId;
    private String name;
    private String title;
    private String comment;
    private int rating;
    private List<String> imagePathStrings;

    // Constructeur sans arguments
    public Review() {
    }

    // Constructeur avec tous les paramètres
    public Review(String restaurantId, String name, String title, String comment, int rating, List<String> imagePathStrings) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.title = title;
        this.comment = comment;
        this.rating = rating;
        this.imagePathStrings = imagePathStrings;
    }

    // Constructeur utilisé pour lire à partir d'un Parcel
    protected Review(Parcel in) {
        reviewId = in.readString();
        restaurantId = in.readString();
        name = in.readString();
        title = in.readString();
        comment = in.readString();
        rating = in.readInt();
        imagePathStrings = in.createStringArrayList();
    }

    // Méthode pour écrire l'objet dans le Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reviewId);
        dest.writeString(restaurantId);
        dest.writeString(name);
        dest.writeString(title);
        dest.writeString(comment);
        dest.writeInt(rating);
        dest.writeStringList(imagePathStrings);
    }

    // Méthode pour décrire le contenu (pas souvent utilisée)
    @Override
    public int describeContents() {
        return 0;
    }

    // Créateur permettant de recréer un objet Review à partir du Parcel
    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    // Getters et setters
    public String getReviewId() {
        return reviewId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getComment() {
        return comment;
    }

    public int getRating() {
        return rating;
    }

    public List<String> getImagePathStrings() {
        return imagePathStrings;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setImagePathStrings(List<String> imagePathStrings) {
        this.imagePathStrings = imagePathStrings;
    }
}
