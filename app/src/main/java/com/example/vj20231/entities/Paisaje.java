package com.example.vj20231.entities;

public class Paisaje {
    public int id;
    public String namePaisaje;
    public String imgPaisaje;
    public Double latitude;
    public Double longitude;

    public Paisaje(int id, String namePaisaje, String imgPaisaje, Double latitude, Double longitude) {
        this.id = id;
        this.namePaisaje = namePaisaje;
        this.imgPaisaje = imgPaisaje;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Paisaje() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamePaisaje() {
        return namePaisaje;
    }

    public void setNamePaisaje(String namePaisaje) {
        this.namePaisaje = namePaisaje;
    }

    public String getImgPaisaje() {
        return imgPaisaje;
    }

    public void setImgPaisaje(String imgPaisaje) {
        this.imgPaisaje = imgPaisaje;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}


