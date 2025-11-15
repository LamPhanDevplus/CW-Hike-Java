package com.example.hike;

public class HikeObservation {
    public long id;
    public long hikeId;
    public String description;
    public String time;

    public HikeObservation() {}

    public HikeObservation(long id, long hikeId, String description, String time) {
        this.id = id;
        this.hikeId = hikeId;
        this.description = description;
        this.time = time;
    }
}

