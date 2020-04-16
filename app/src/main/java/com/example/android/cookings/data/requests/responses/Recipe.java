package com.example.android.cookings.data.requests.responses;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Recipe which are fetched from the API.
 */
@Parcel
public class Recipe {

    int id, servings;
    String name, image;
    List<Ingredient> ingredients;
    List<Step> steps;

    /**
     * Constructor for Parcel.
     */
    public Recipe() {
    }

    public Recipe(int id, int servings, String name, String image, List<Ingredient> ingredients,
                  List<Step> steps) {
        this.id = id;
        this.servings = servings;
        this.name = name;
        this.image = image;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public int getId() {
        return id;
    }

    public int getServings() {
        return servings;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return image;
    }

    public List<Ingredient> getIngredients() {
        return new ArrayList<>(ingredients);
    }

    public List<Step> getSteps() {
        return new ArrayList<>(steps);
    }

    @Parcel
    public static class Ingredient {

        double quantity;
        String measure, ingredient;

        /**
         * Constructor for Parcel.
         */
        public Ingredient() {
        }

        public Ingredient(double quantity, String measure, String ingredient) {
            this.quantity = quantity;
            this.measure = measure;
            this.ingredient = ingredient;
        }

        public double getQuantity() {
            return quantity;
        }

        public String getMeasure() {
            return measure;
        }

        public String getIngredient() {
            return ingredient;
        }
    }

    @Parcel
    public static class Step {

        int id;
        String shortDescription, description, videoURL, thumbnailURL;

        /**
         * Constructor for Parcel.
         */
        public Step() {
        }

        public Step(int id, String shortDescription, String description, String videoURL, String thumbnailURL) {
            this.id = id;
            this.shortDescription = shortDescription;
            this.description = description;
            this.videoURL = videoURL;
            this.thumbnailURL = thumbnailURL;
        }

        public int getId() {
            return id;
        }

        public String getShortDescription() {
            return shortDescription;
        }

        public String getDescription() {
            return description;
        }

        public String getVideoURL() {
            return videoURL;
        }

        public String getThumbnailURL() {
            return thumbnailURL;
        }
    }
}
