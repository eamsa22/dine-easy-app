package org.m2sdl.dineeasy.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Restaurant implements Parcelable {
    private String id;
    private String name;
    private String address;
    private String phoneNumber;
    private String websiteUrl;
    private boolean reservable;
    private double rating;
    private int userRatingsTotal;
    private int priceLevel;
    private boolean delivery;
    private boolean dineIn;
    private boolean takeout;
    private boolean servesBreakfast;
    private boolean servesBrunch;
    private boolean servesLunch;
    private boolean servesDinner;
    private boolean servesVegetarianFood;
    private boolean servesWine;
    private boolean isOpen;
    private List<String> photoReferences;
    private String mainPhotoReference;
    private ArrayList<String> openingHours;
    private List<String> types;
    private List<Review> reviews = new ArrayList<>();

    private String type ;

    public Restaurant(String id, String name, String address, String phoneNumber, String websiteUrl,
                      boolean reservable, double rating, int userRatingsTotal, int priceLevel,
                      boolean delivery, boolean dineIn, boolean takeout, boolean servesBreakfast,
                      boolean servesBrunch, boolean servesLunch, boolean servesDinner,
                      boolean servesVegetarianFood, boolean servesWine, List<String> photoReferences,
                      String mainPhotoReference, ArrayList<String> openingHours, List<String> types, List<Review> reviews) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.websiteUrl = websiteUrl;
        this.reservable = reservable;
        this.rating = rating;
        this.userRatingsTotal = userRatingsTotal;
        this.priceLevel = priceLevel;
        this.delivery = delivery;
        this.dineIn = dineIn;
        this.takeout = takeout;
        this.servesBreakfast = servesBreakfast;
        this.servesBrunch = servesBrunch;
        this.servesLunch = servesLunch;
        this.servesDinner = servesDinner;
        this.servesVegetarianFood = servesVegetarianFood;
        this.servesWine = servesWine;
        this.photoReferences = photoReferences;
        this.mainPhotoReference = mainPhotoReference;
        this.openingHours = openingHours;
        this.types = types;
        this.reviews = reviews;
    }

    protected Restaurant(Parcel in) {
        id = in.readString();
        name = in.readString();
        address = in.readString();
        phoneNumber = in.readString();
        websiteUrl = in.readString();
        reservable = in.readByte() != 0;
        rating = in.readDouble();
        userRatingsTotal = in.readInt();
        priceLevel = in.readInt();
        delivery = in.readByte() != 0;
        dineIn = in.readByte() != 0;
        takeout = in.readByte() != 0;
        servesBreakfast = in.readByte() != 0;
        servesBrunch = in.readByte() != 0;
        servesLunch = in.readByte() != 0;
        servesDinner = in.readByte() != 0;
        servesVegetarianFood = in.readByte() != 0;
        servesWine = in.readByte() != 0;
        photoReferences = in.createStringArrayList();
        mainPhotoReference = in.readString();
        openingHours = in.createStringArrayList();
        types = in.createStringArrayList();
        in.readList(reviews, Review.class.getClassLoader());  // Read reviews list
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(phoneNumber);
        dest.writeString(websiteUrl);
        dest.writeByte((byte) (reservable ? 1 : 0));
        dest.writeDouble(rating);
        dest.writeInt(userRatingsTotal);
        dest.writeInt(priceLevel);
        dest.writeByte((byte) (delivery ? 1 : 0));
        dest.writeByte((byte) (dineIn ? 1 : 0));
        dest.writeByte((byte) (takeout ? 1 : 0));
        dest.writeByte((byte) (servesBreakfast ? 1 : 0));
        dest.writeByte((byte) (servesBrunch ? 1 : 0));
        dest.writeByte((byte) (servesLunch ? 1 : 0));
        dest.writeByte((byte) (servesDinner ? 1 : 0));
        dest.writeByte((byte) (servesVegetarianFood ? 1 : 0));
        dest.writeByte((byte) (servesWine ? 1 : 0));
        dest.writeStringList(photoReferences);
        dest.writeString(mainPhotoReference);
        dest.writeStringList(openingHours);
        dest.writeStringList(types);
        dest.writeList(reviews); // Write reviews list
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    // Getters and Setters
    public List<String> getPhotoReferences() {
        return photoReferences;
    }

    public void setPhotoReferences(List<String> photoReferences) {
        this.photoReferences = photoReferences;
    }

    public String getMainPhotoReference() {
        return mainPhotoReference;
    }

    public void setMainPhotoReference(String mainPhotoReference) {
        this.mainPhotoReference = mainPhotoReference;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public boolean isReservable() {
        return reservable;
    }

    public double getRating() {
        return rating;
    }

    public int getUserRatingsTotal() {
        return userRatingsTotal;
    }

    public int getPriceLevel() {
        return priceLevel;
    }

    public boolean isServesVegetarianFood() {
        return servesVegetarianFood;
    }

    public ArrayList<String> getOpening() {
        return openingHours;
    }

    public List<Review> getReviews() {
        return reviews;
    }
}
