package com.example.android.cookings.repositories;

import android.content.Context;
import android.util.Log;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android.cookings.AppExecutor;
import com.example.android.cookings.R;
import com.example.android.cookings.data.requests.BakingApi;
import com.example.android.cookings.data.requests.BakingRetrofit;
import com.example.android.cookings.data.requests.responses.Recipe;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for Recipes, fetches data from the API.
 */
public class RecipeRepository {

    private static final String TAG = RecipeRepository.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static RecipeRepository instance;
    private MutableLiveData<List<Recipe>> mutableRecipes;

    public static RecipeRepository getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new RecipeRepository();
            }
        }
        return instance;
    }

    private RecipeRepository() {
        mutableRecipes = new MutableLiveData<>();
    }

    public LiveData<List<Recipe>> getRecipes() {
        Log.d(TAG, "Retrieving recipes..");
        AppExecutor.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                BakingApi bakingApi = BakingRetrofit.getRetrofit().create(BakingApi.class);
                Call<List<Recipe>> call = bakingApi.getRecipes();
                call.enqueue(new Callback<List<Recipe>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Recipe>> call,
                                           @NonNull Response<List<Recipe>> response) {
                        if (response.code() != HttpURLConnection.HTTP_OK) {
                            Log.d(TAG, "Response code: " + response.code());
                        } else if (response.body() != null) {
                            mutableRecipes.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });
        return mutableRecipes;
    }

    /**
     * Mock Recipe used for testing.
     */
    public static Recipe getMockRecipe(Context context) {
        ArrayList<Recipe.Ingredient> ingredients = new ArrayList<>();
        ArrayList<Recipe.Step> steps = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ingredients.add(new Recipe.Ingredient(
                    i + 1,
                    context.getString(R.string.mock_measure),
                    context.getString(R.string.mock_ingredient, String.valueOf(i + 1))));
            steps.add(new Recipe.Step(
                    i,
                    context.getString(R.string.mock_description_short),
                    context.getString(R.string.mock_description, String.valueOf(i + 1)),
                    null,
                    null));
        }
        return new Recipe(999, 8, context.getString(R.string.mock_recipe),
                null, ingredients, steps);
    }
}
