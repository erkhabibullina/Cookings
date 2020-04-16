package com.example.android.cookings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.idling.CountingIdlingResource;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.example.android.cookings.R;
import com.example.android.cookings.adapters.RecipeAdapter;
import com.example.android.cookings.data.requests.responses.Recipe;
import com.example.android.cookings.viewmodels.MainActivityViewModel;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecipeAdapter recipeAdapter;
    private MainActivityViewModel viewModel;
    private CountingIdlingResource idlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create idlingResource
        idlingResource = new CountingIdlingResource(MainActivity.class.getSimpleName());

        // Find statuses
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        int orientation = getResources().getConfiguration().orientation;

        // Set Adapter
        RecyclerView recyclerView = findViewById(R.id.main_recyclerview);
        recipeAdapter = new RecipeAdapter(this, new ArrayList<Recipe>(), this);

        if(!isTablet) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(),
                    layoutManager.getOrientation());
            recyclerView.addItemDecoration(divider);
            recyclerView.setLayoutManager(layoutManager);
        } else {
            int spanCount = orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 3;
            GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
            recyclerView.setLayoutManager(layoutManager);
        }

        // Set Adapter
        recyclerView.setAdapter(recipeAdapter);

        // Set ViewModel
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        viewModel.setRecipeLiveData();
        subscribeObservers();

        // Set testing to wait
        idlingResource.increment();
    }

    @Override
    public void onRecipeClick(int index) {
        Recipe recipe = recipeAdapter.getRecipes().get(index);
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra(Constants.PARCEL_RECIPE, Parcels.wrap(recipe));
        startActivity(intent);
    }

    /**
     * Set ViewModel Observers.
     */
    private void subscribeObservers() {
        viewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                recipeAdapter.setRecipes(recipes);
                // Set testing to resume
                idlingResource.decrement();
            }
        });
    }

    public CountingIdlingResource getIdlingResource() {
        return idlingResource;
    }
}
