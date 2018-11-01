package com.sih.justonce;

public class Event {
    private String name;
    private String description;
    private int imageResourceId;

    private Event(String name, String description, int imageResourceId){
        this.name = name;
        this.description = description;
        this.imageResourceId = imageResourceId;
    }

    public static Event[] getEvents(){

        Event[] events = new Event[2];

        // imageResourceId should be some imgae in drawable i.e R.drawable.IRC
        events[0] = new Event("IRC", "The zonal qualifying rounds for the competition will be held in various countries," +
                " whose winning entries will be invited to compete in the IRC Grand Finale to be held at" +
                " Techfest 2018-19, IIT Bombay. ", 1);

        events[1] = new Event("Meshmerize", "When the time compression stops, he finds himself in a technologically advanced" +
                " mechanical era where robots have built a mesh-like bridge for traversing into different time" +
                " zones. Help Nova in solving the maze in shortest path to reach to his machine.",2);

        return events;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

}
