package com.example.android.cookings.viewmodels;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.cookings.data.requests.responses.Recipe;
import com.example.android.cookings.repositories.RecipeRepository;

/**
 * LiveData ViewModel for MainActivity.
 */
public class MainActivityViewModel extends ViewModel {

    private RecipeRepository recipeRepository;
    private LiveData<List<Recipe>> recipes;

    public MainActivityViewModel() {
        this.recipeRepository = RecipeRepository.getInstance();
    }

    /**
     * Get the Recipe LiveData (List).
     */
    public LiveData<List<Recipe>> getRecipes() {
        return recipes;
    }

    /**
     * Set the Recipe LiveData (List), but only if null to prevent network recalling.
     */
    public void setRecipeLiveData() {
        if(recipes == null) {
            recipes = recipeRepository.getRecipes();
        }
    }
}
