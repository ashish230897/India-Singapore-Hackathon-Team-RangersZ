package com.example.ashish.managermainactivity;

public class Events {
    private String name;

    public static final Events[] events = {
            new Events("IRC"),
            new Events("Meshmerize"),
            new Events("RowBoatics")
    };

    private Events(String name)
    {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString()
    {
        return this.name;
    }
}
