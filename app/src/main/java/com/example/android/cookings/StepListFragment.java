package com.example.android.cookings;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.parceler.Parcels;

import java.math.BigDecimal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.android.cookings.adapters.StepAdapter;
import com.example.android.cookings.data.requests.responses.Recipe;
import com.example.android.cookings.databinding.FragmentSteplistBinding;

public class StepListFragment extends Fragment implements StepAdapter.OnStepClickListener {

    private static final String TAG = StepListFragment.class.getSimpleName();
    private OnStepClickListener callback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentSteplistBinding dataBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_steplist, container, false);
        View rootView = dataBinding.getRoot();

        if (getArguments() != null) {
            Recipe recipe = Parcels.unwrap(getArguments().getParcelable(Constants.PARCEL_RECIPE));

            // Set servings
            dataBinding.servingsTv.setText(getString(R.string.servings,
                    String.valueOf(recipe.getServings())));

            // Set ingredients
            for (Recipe.Ingredient i : recipe.getIngredients()) {
                BigDecimal quantityVal = BigDecimal.valueOf(i.getQuantity()).stripTrailingZeros();
                View ingredientLayout = getLayoutInflater().inflate(R.layout.list_ingredient, null);
                TextView quantity = ingredientLayout.findViewById(R.id.quantity_tv),
                        measure = ingredientLayout.findViewById(R.id.measure_tv),
                        ingredient = ingredientLayout.findViewById(R.id.ingredient_tv);
                quantity.setText(String.valueOf(quantityVal.toPlainString()));
                measure.setText(i.getMeasure());
                ingredient.setText(i.getIngredient());
                dataBinding.ingredientsLayout.addView(ingredientLayout);
            }

            // Set Adapter
            StepAdapter stepAdapter = new StepAdapter(getActivity(), recipe.getSteps(), this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            DividerItemDecoration divider = new DividerItemDecoration(dataBinding.stepRecyclerview.
                    getContext(), layoutManager.getOrientation());
            dataBinding.stepRecyclerview.addItemDecoration(divider);
            dataBinding.stepRecyclerview.setLayoutManager(layoutManager);
            dataBinding.stepRecyclerview.setAdapter(stepAdapter);

            // Prevent screen from scrolling down to RecyclerView
            dataBinding.stepRecyclerview.setFocusable(false);
        } else {
            Log.d(TAG, "Error getting recipe from getArguments()");
        }

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Ensure that host activity has implemented the callback interface
        try {
            callback = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement OnStepClickListener");
        }
    }

    @Override
    public void onStepClick(int index) {
        callback.onStepSelected(index);
    }

    /**
     * Interface for callback to Activity.
     */
    public interface OnStepClickListener {
        void onStepSelected(int index);
    }
}
