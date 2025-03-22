package org.m2sdl.dineeasy.model;

import java.io.Serializable;
import java.util.List;

public class Reservation implements Serializable {
    private String reservationId;
    private String restaurantId;
    private String fullName;
    private String email;
    private String phone;
    private int guestsCount;
    private String dateTime;
    private List<String> specialRequests;

    private String restaurantAddress;
    private String restaurantName;
    private String restaurantImgUrl;

    public Reservation() {}

    public Reservation(String restaurantId, String fullName, String email, String phone, int guestsCount, String dateTime, List<String> specialRequests, String restaurantAddress, String restaurantName, String restaurantImgUrl) {
        this.restaurantId = restaurantId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.guestsCount = guestsCount;
        this.dateTime = dateTime;
        this.specialRequests = specialRequests;
        this.restaurantAddress = restaurantAddress;
        this.restaurantName = restaurantName;
        this.restaurantImgUrl = restaurantImgUrl;
    }


// Getters et Setters

    public String getReservationId() {
        return reservationId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public int getGuestsCount() {
        return guestsCount;
    }

    public String getDateTime() {
        return dateTime;
    }

    public List<String> getSpecialRequests() {
        return specialRequests;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getRestaurantImgUrl() {
        return restaurantImgUrl;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setGuestsCount(int guestsCount) {
        this.guestsCount = guestsCount;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setSpecialRequests(List<String> specialRequests) {
        this.specialRequests = specialRequests;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public void setRestaurantImgUrl(String restaurantImgUrl) {
        this.restaurantImgUrl = restaurantImgUrl;
    }
}
