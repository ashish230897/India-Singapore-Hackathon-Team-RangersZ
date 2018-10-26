package com.example.ashish.firstversionapp;

public class Events {
    private String name;
    private String description;
    private int imageResourceId;

    public static final Events[] events = {
            new Events("IRC", "The zonal qualifying rounds for the competition will be held in various countries," +
                    " whose winning entries will be invited to compete in the IRC Grand Finale to be held at" +
                    " Techfest 2018-19, IIT Bombay. ", R.drawable.irc),
            new Events("RowBoatics", "Design and make a wired controlled or wirelessly controlled yacht, powered" +
                    " only by batteries, which has to navigate through obstacle and perform task course on water" +
                    " while caring a payload, in the shortest time possible.", R.drawable.rowboatics),
            new Events("Meshmerize", "When the time compression stops, he finds himself in a technologically advanced" +
                    " mechanical era where robots have built a mesh-like bridge for traversing into different time" +
                    " zones. Help Nova in solving the maze in shortest path to reach to his machine.", R.drawable.meshmerize)
    };

    private Events(String name, String description, int imageResourceId)
    {
        this.description = description;
        this.imageResourceId = imageResourceId;
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String toString()
    {
        return this.name;
    }
}
