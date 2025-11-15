package com.example.hike;

public class Hike {
    public long id;
    public String name;
    public String location;
    public String date;
    public String parking;
    public String difficulty;
    public String description;

    public Hike() {}

    public Hike(long id, String name, String location, String date, String parking, String difficulty, String description) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.date = date;
        this.parking = parking;
        this.difficulty = difficulty;
        this.description = description;
    }

    @Override
    public String toString() {
        String n = name == null ? "" : name;
        String loc = location == null ? "" : location;
        if (n.isEmpty() && loc.isEmpty()) return "(Unnamed hike)";
        if (loc.isEmpty()) return n;
        return n + " (" + loc + ")";
    }
}
