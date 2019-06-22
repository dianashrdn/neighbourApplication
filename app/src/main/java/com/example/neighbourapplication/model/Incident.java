package com.example.neighbourapplication.model;


import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class Incident implements Serializable {
    private String incidentId;
    private String incidentName, photo, description;
    private String date, time;
    private GeoPoint location;

    public Incident(){}

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setDate(String date) {
        this.date = date;

    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    public void setIncidentName(String incidentName) {
        this.incidentName = incidentName;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public String getPhoto() {
        return photo;
    }

    public String getIncidentName() {
        return incidentName;
    }

    public String getDate() {
        return date;
    }



}
