package com.sih.justonce;

public class Category {
    private String name;
    private String description;


    private Category(String name ,String description){
        this.name = name;
        this.description = description;
    }

    public static Category[] getCategories(){

        Category [] categories = new Category[2];
        categories[0] = new Category("Robotics","Awesome technology showdown");
        categories[1] = new Category("Fun","Bored from tech ? take a chill pill");

        return categories;
    }

    public String toString(){
        return this.name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
