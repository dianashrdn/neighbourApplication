package com.example.neighbourapplication.model;

public class WatchProgramme {
    String watchId;
    String programmeName;
    String username;
    String description;
    String dateStart;
    String dateEnd;


    public String getUsername() {
        return username;
    }

    public String getWatchId() {
        return watchId;
    }

    public String getProgrammeName() {
        return programmeName;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public String getDateStart() {
        return dateStart;
    }

    public String getDescription() {
        return description;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProgrammeName(String programmeName) {
        this.programmeName = programmeName;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}




