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

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.ViewHolder> {

    private static final String TAG = StepAdapter.class.getSimpleName();
    private OnStepClickListener onStepClickListener;
    private List<Recipe.Step> steps;
    private Context context;

    public StepAdapter(Context context, List<Recipe.Step> steps, OnStepClickListener onStepClickListener) {
        this.onStepClickListener = onStepClickListener;
        this.steps = steps;
        this.context = context;
    }

    @NonNull
    @Override
    public StepAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_step, parent, false);
        return new StepAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Recipe.Step currentStep = steps.get(position);

        // Set Step id
        holder.stepIdTv.setText(context.getString(R.string.step_id,
                String.valueOf(currentStep.getId())));

        // Set Step description
        holder.stepDescriptionTv.setText(currentStep.getShortDescription());

        // Set Step image
        if(currentStep.getThumbnailURL() != null && !currentStep.getThumbnailURL().isEmpty()) {
            Picasso.get().load(currentStep.getThumbnailURL()).into(holder.thumbnailTv,
                    new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            holder.thumbnailTv.setVisibility(View.VISIBLE);
                            Log.d(TAG,"Image for step '" + currentStep.getDescription() +
                                    "' successfully loaded.");
                        }

                        @Override
                        public void onError(Exception e) {
                            holder.thumbnailTv.setVisibility(View.GONE);
                            Log.d(TAG, "Error loading Step image.");
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView stepIdTv, stepDescriptionTv;
        private ImageView thumbnailTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.stepIdTv = itemView.findViewById(R.id.step_id_tv);
            this.stepDescriptionTv = itemView.findViewById(R.id.step_description_tv);
            this.thumbnailTv = itemView.findViewById(R.id.step_iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onStepClickListener.onStepClick(getAdapterPosition());
        }
    }

    public interface OnStepClickListener {
        void onStepClick(int index);
    }

    public List<Recipe.Step> getSteps() {
        return new ArrayList<>(steps);
    }

    public void setSteps(List<Recipe.Step> steps) {
        this.steps = steps;
        notifyDataSetChanged();
    }
}
