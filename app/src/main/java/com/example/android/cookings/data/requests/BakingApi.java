package com.example.android.cookings.data.requests;

import com.example.android.cookings.data.requests.responses.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Retrofit caller, returns correlating response object.
 */
public interface BakingApi {

    // Get recipes
    @GET(Api.RECIPES)
    Call<List<Recipe>> getRecipes();

    abstract class Api {
        private static final String RECIPES = "topher/2017/May/59121517_baking/baking.json";
    }
}
