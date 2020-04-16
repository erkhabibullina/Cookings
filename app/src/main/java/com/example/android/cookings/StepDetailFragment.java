package com.example.android.cookings;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.cookings.data.requests.responses.Recipe;
import com.example.android.cookings.databinding.FragmentStepdetailBinding;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.parceler.Parcels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

public class StepDetailFragment extends Fragment {

    private static final String TAG = StepDetailFragment.class.getSimpleName();
    private FragmentStepdetailBinding dataBinding;
    private OnButtonClickListener callback;
    private SimpleExoPlayer player;
    private long exoPosition;
    private boolean exoState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_stepdetail, container, false);
        View rootView = dataBinding.getRoot();

        if (getArguments() != null) {
            Recipe.Step step = Parcels.unwrap(getArguments().getParcelable(Constants.PARCEL_STEP));

            // Set description
            dataBinding.description.setText(step.getDescription());

            // Set callback for prev/next button
            dataBinding.prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.OnButtonClick(Constants.BUTTON_PREV);
                }
            });
            dataBinding.nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.OnButtonClick(Constants.BUTTON_NEXT);
                }
            });

            // Restore saved values
            if (savedInstanceState != null) {
                exoPosition = savedInstanceState.getLong(Constants.EXOPLAYER_POSITION);
                exoState = savedInstanceState.getBoolean(Constants.EXOPLAYER_STATE);
            }

            // Set ExoPlayer
            if (step.getVideoURL() != null && !step.getVideoURL().isEmpty()) {
                initializePlayer(Uri.parse(step.getVideoURL()));
            } else {
                dataBinding.exoPlayerview.setVisibility(View.GONE);
            }
        }

        return rootView;
    }

    private void initializePlayer(Uri mediaUri) {

        // Create components
        TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();
        RenderersFactory renderersFactory = new DefaultRenderersFactory(getActivity());
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(),
                Util.getUserAgent(getActivity(), getString(R.string.app_name)));

        // Create ExoPlayer
        player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
        dataBinding.exoPlayerview.setPlayer(player);

        // The media which to be played
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaUri);

        // Prepare player with source
        player.prepare(videoSource);

        // Restore ExoPlayer position and state
        if (exoPosition != 0) {
            player.setPlayWhenReady(exoState);
            player.seekTo(exoPosition);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save ExoPlayer position and state
        if(player != null) {
            outState.putLong(Constants.EXOPLAYER_POSITION, player.getCurrentPosition());
            outState.putBoolean(Constants.EXOPLAYER_STATE, player.getPlayWhenReady());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Ensure that host activity has implemented the callback interface
        try {
            callback = (OnButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement OnButtonClickListener");
        }
    }

    public interface OnButtonClickListener {
        void OnButtonClick(int button);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23 && player != null) {
            player.stop();
            player.release();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23 && player != null) {
            player.stop();
            player.release();
        }
    }

}
