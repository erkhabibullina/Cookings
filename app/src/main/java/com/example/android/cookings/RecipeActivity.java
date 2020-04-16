package com.example.android.cookings;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

import org.parceler.Parcels;

import java.math.BigDecimal;
import java.util.Set;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;
import androidx.fragment.app.FragmentManager;

import com.example.android.cookings.data.requests.responses.Recipe;

public class RecipeActivity extends AppCompatActivity implements
        StepListFragment.OnStepClickListener,
        StepDetailFragment.OnButtonClickListener {

    private static final String TAG = RecipeActivity.class.getSimpleName();
    private static final String DETAIL_FRAGMENT = "detail_fragment";
    private FragmentManager fragmentManager;
    private Recipe recipe;
    private boolean isTablet;
    private int stepIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // Restore index value
        if (savedInstanceState != null) {
            stepIndex = savedInstanceState.getInt(Constants.SELECTED_ITEM);
        }

        // Check if tablet
        isTablet = getResources().getBoolean(R.bool.isTablet);

        // Get Recipe from parcel
        recipe = Parcels.unwrap(getIntent().getParcelableExtra(Constants.PARCEL_RECIPE));

        // Create Set of ingredients for easy extraction in widget
        Set<String> ingredientSet = new ArraySet<>();
        for(Recipe.Ingredient ingredient: recipe.getIngredients()) {
            ingredientSet.add(ingredientString(ingredient));
        }

        // Store name and ingredients into SharedPreferences for easy fetching by WidgetProvider
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString(getString(R.string.recipe_key), recipe.getName()).apply();
        sharedPreferences.edit().putStringSet(getString(R.string.ingredients_key), ingredientSet).
                apply();

        // Update Widget
        Intent intent = new Intent(this, BakingWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int ids[] = AppWidgetManager.getInstance(getApplication()).
                getAppWidgetIds(new ComponentName(getApplication(), BakingWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);

        // Display Toast to inform the user that the Widget has updated
        Toast.makeText(this, getString(R.string.widget_updated), Toast.LENGTH_SHORT).show();

        // Set title
        setTitle(recipe.getName());

        // Set FragmentManager
        fragmentManager = getSupportFragmentManager();

        /* Set StepListFragment, but only if there are no fragments in the backstack,
         * to prevent StepListFragment from overwriting the current fragment on rotation. */
        if (fragmentManager.getBackStackEntryCount() == 0) {
            StepListFragment stepListFragment = createStepList();
            fragmentManager.beginTransaction()
                    .replace(R.id.recipe_steps_container, stepListFragment)
                    .commit();
        }

        // Set StepDetailFragment
        if (isTablet) {
            loadStepDetailForTablet(createStepDetail(recipe.getSteps().get(stepIndex)));
        }
    }

    /**
     * Create a String from Ingredient which includes quantity, measure and ingredient.
     */
    private String ingredientString(Recipe.Ingredient ingredient) {
        StringBuilder sb = new StringBuilder();
        BigDecimal quantity = BigDecimal.valueOf(ingredient.getQuantity()).stripTrailingZeros();
        sb.append(quantity.toPlainString())
                .append(ingredient.getMeasure())
                .append(" ")
                .append(ingredient.getIngredient());
        return sb.toString();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(Constants.SELECTED_ITEM, stepIndex);
    }

    /**
     * Handles click event from StepListFragment.
     */
    @Override
    public void onStepSelected(int index) {
        stepIndex = index;
        StepDetailFragment stepDetailFragment = createStepDetail(recipe.getSteps().get(index));
        if (isTablet) {
            loadStepDetailForTablet(stepDetailFragment);
        } else {
            loadStepDetailForPhone(stepDetailFragment);
        }
    }

    /**
     * Handles click event from StepDetailFragment.
     */
    @Override
    public void OnButtonClick(int button) {
        switch (button) {
            case Constants.BUTTON_PREV:
                if (--stepIndex < 0) {
                    stepIndex = recipe.getSteps().size() - 1;
                }
                break;
            case Constants.BUTTON_NEXT:
                if (++stepIndex > recipe.getSteps().size() - 1) {
                    stepIndex = 0;
                }
                break;
        }

        // Create StepDetailFragment
        StepDetailFragment stepDetailFragment =
                createStepDetail(recipe.getSteps().get(stepIndex));

        // Load fragment into container
        if (isTablet) {
            loadStepDetailForTablet(stepDetailFragment);
        } else {
            loadStepDetailForPhone(stepDetailFragment);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (fragmentManager.getBackStackEntryCount() > 0 && !isTablet) {
                    fragmentManager.popBackStackImmediate();
                } else {
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0 && !isTablet) {
            fragmentManager.popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Loads StepDetailFragment into container (Phone)
     */
    private void loadStepDetailForPhone(StepDetailFragment stepDetailFragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.recipe_steps_container, stepDetailFragment, DETAIL_FRAGMENT)
                .addToBackStack(DETAIL_FRAGMENT)
                .commit();
    }

    private void loadStepDetailForTablet(StepDetailFragment stepDetailFragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.recipe_stepdetail_container, stepDetailFragment, DETAIL_FRAGMENT)
                .commit();
    }

    /**
     * Generate StepListFragment with bundle.
     */
    private StepListFragment createStepList() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.PARCEL_RECIPE, Parcels.wrap(recipe));
        StepListFragment stepListFragment = new StepListFragment();
        stepListFragment.setArguments(bundle);
        return stepListFragment;
    }

    /**
     * Generate StepDetailFragment with bundle.
     */
    private StepDetailFragment createStepDetail(Recipe.Step step) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.SELECTED_ITEM, stepIndex);
        bundle.putParcelable(Constants.PARCEL_STEP, Parcels.wrap(step));
        StepDetailFragment stepDetailFragment = new StepDetailFragment();
        stepDetailFragment.setArguments(bundle);
        return stepDetailFragment;
    }
}
