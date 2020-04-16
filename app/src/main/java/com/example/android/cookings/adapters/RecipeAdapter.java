package com.example.android.cookings.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.cookings.R;
import com.example.android.cookings.data.requests.responses.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private static final String TAG = RecipeAdapter.class.getSimpleName();
    private List<Recipe> recipes;
    private OnRecipeClickListener onRecipeClickListener;
    private Context context;

    public RecipeAdapter(Context context, List<Recipe> recipes, OnRecipeClickListener onRecipeClickListener) {
        this.onRecipeClickListener = onRecipeClickListener;
        this.recipes = recipes;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Recipe currentRecipe = recipes.get(position);

        // Set Recipe name
        holder.recipeTv.setText(currentRecipe.getName());
        holder.servingsTv.setText(context.getString(R.string.servings,
                String.valueOf(currentRecipe.getServings())));

        // Set Recipe ingredients
        StringBuilder ingredients = new StringBuilder();
        for(int i = 0; i < currentRecipe.getIngredients().size(); i++) {
            ingredients.append(currentRecipe.getIngredients().get(i).getIngredient());
            if(i < currentRecipe.getIngredients().size() - 1) {
                ingredients.append(", ");
            }
        }
        holder.ingredientSummaryTv.setText(ingredients.toString());

        // Set Recipe image
        if(currentRecipe.getImageURL() != null && !currentRecipe.getImageURL().isEmpty()) {
            Picasso.get().load(currentRecipe.getImageURL()).into(holder.recipeIv,
                    new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            holder.recipeIv.setVisibility(View.VISIBLE);
                            Log.d(TAG,"Image for recipe '" + currentRecipe.getName() +
                                    "' successfully loaded.");
                        }

                        @Override
                        public void onError(Exception e) {
                            holder.recipeIv.setVisibility(View.GONE);
                            Log.d(TAG, "Error loading Recipe image.");
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView recipeTv, servingsTv, ingredientSummaryTv;
        private ImageView recipeIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.recipeIv = itemView.findViewById(R.id.recipe_iv);
            this.recipeTv = itemView.findViewById(R.id.recipe_tv);
            this.servingsTv = itemView.findViewById(R.id.recipe_servings_tv);
            this.ingredientSummaryTv = itemView.findViewById(R.id.ingredients_summary_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRecipeClickListener.onRecipeClick(getAdapterPosition());
        }
    }

    public interface OnRecipeClickListener {
        void onRecipeClick(int index);
    }

    public List<Recipe> getRecipes() {
        return new ArrayList<>(recipes);
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }
}
