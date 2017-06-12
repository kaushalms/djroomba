package com.kaushalmandayam.djroomba.screens.partyList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kaushalmandayam.djroomba.R;
import com.kaushalmandayam.djroomba.managers.UserManager;
import com.kaushalmandayam.djroomba.screens.base.BaseActivity;
import com.kaushalmandayam.djroomba.screens.partyList.PartyListPresenter.PartyListView;
import com.konifar.fab_transformation.FabTransformation;


import butterknife.BindView;
import butterknife.OnClick;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit2.Call;
import retrofit2.Response;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;


/**
 * Created by kaushalmandayam on 5/7/17.
 */

public class PartyListActivity extends BaseActivity<PartyListPresenter> implements PartyListView {

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.searchTextView)
    TextView searchTextView;
    @BindView(R.id.searchEditText)
    EditText searchEditText;
    @BindView(R.id.overlay)
    View overlay;
    @BindView(R.id.sheet)
    View sheet;
    @BindView(R.id.partyListLayout)
    RelativeLayout partyListLayout;
    @BindView(R.id.musicNoteImageView)
    ImageView musicNoteImageView;
    @BindView(R.id.searchImageView)
    ImageView searchImageView;
    @BindView(R.id.clearImageView)
    ImageView clearImageView;
    @BindView(R.id.trackTextView)
    TextView trackTextView;


    private AnimatedVectorDrawable searchToBar;
    private AnimatedVectorDrawable barToSearch;
    private float offset;
    private Interpolator interp;
    private int duration;
    private boolean expanded = false;


    //==============================================================================================
    // static Methods
    //==============================================================================================

    public static void start(Context context) {
        Intent starter = new Intent(context, PartyListActivity.class);
        context.startActivity(starter);
    }

    //==============================================================================================
    // Life-cycle Methods
    //==============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_list);
        sheet.setVisibility(View.GONE);
        // FabTransformation.with(fab).setOverlay(overlay).tr(sheet);
        attachPresenter(new PartyListPresenter(), this);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                final SpotifyApi api = new SpotifyApi();
                api.setAccessToken(UserManager.INSTANCE.getUserToken());
                if (!s.toString().isEmpty())
                {
                    try{
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                SpotifyService spotifyService = api.getService();
                                // trackTextView.setText(spotifyService.searchTracks(s.toString()).toString(), retrofit.Callback < TracksPager > callback);
                                TracksPager tracksPager = spotifyService.searchTracks(s.toString());
                                int tracks = tracksPager.tracks.items.size();
                                Log.d("TRACKS", "afterTextChanged: " + tracks);

                                spotifyService.getAlbum("2dIGnmEIy1WZIcZCFSj6i8", new retrofit.Callback<Album>() {
                                    @Override
                                    public void onResponse(Call<Album> call, Response<Album> response) {

                                    }

                                    @Override
                                    public void onFailure(Call<Album> call, Throwable t) {

                                    }

                                });
                            }
                        });
                    }
                    catch (RetrofitError error) {
                        SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                        // handle error
                    }
                }


            }
        });
        // setupToolbarAnimation();
    }

    private void setupToolbarAnimation() {
        //   searchToBar = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.anim_search_to_bar);
        //   barToSearch = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.anim_bar_to_search);

        searchToBar = (AnimatedVectorDrawable) ContextCompat.getDrawable(this, R.drawable.anim_search_to_bar);
        barToSearch = (AnimatedVectorDrawable) ContextCompat.getDrawable(this, R.drawable.anim_bar_to_search);
        interp = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
        duration = getResources().getInteger(R.integer.duration_bar);
        // iv is sized to hold the search+bar so when only showing the search icon, translate the
        // whole view left by half the difference to keep it centered
        offset = -71f * (int) getResources().getDisplayMetrics().scaledDensity;
        searchImageView.setTranslationX(offset);
    }


    @Override
    public void onBackPressed() {
        if (fab.getVisibility() != View.VISIBLE) {
            FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet);
            return;
        }
        super.onBackPressed();
    }

    @OnClick(R.id.overlay)
    void onClickBackground() {
        if (fab.getVisibility() != View.VISIBLE) {
            FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet);
        }
    }

    @OnClick(R.id.fab)
    void onClickFab() {
        if (fab.getVisibility() == View.VISIBLE) {
            FabTransformation.with(fab).setOverlay(overlay).transformTo(sheet);
        }
    }

    @OnClick(R.id.searchTextView)
    void onClickToolbar() {
        setupSearchBarView();
    }

    @OnClick(R.id.clearImageView)
    void onClickClear() {
        searchEditText.setText("");
    }

    private void setupSearchBarView() {
        searchTextView.setVisibility(View.GONE);
        searchEditText.setVisibility(View.VISIBLE);
        searchEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        searchImageView.setVisibility(View.GONE);
        clearImageView.setVisibility(View.VISIBLE);
        musicNoteImageView.setVisibility(View.VISIBLE);
    }

//    public void animate() {
//
//        if (!expanded) {
//            searchTextView.setVisibility(View.VISIBLE);
//            searchImageView.setImageDrawable(searchToBar);
//            searchToBar.start();
//            searchImageView.animate().translationX(0f).setDuration(duration).setInterpolator(interp);
//            searchEditText.animate().alpha(1f).setStartDelay(duration - 100).setDuration(100).setInterpolator(interp);
//        } else {
//            searchImageView.setImageDrawable(barToSearch);
//            barToSearch.start();
//            searchImageView.animate().translationX(offset).setDuration(duration).setInterpolator(interp);
//            searchEditText.setAlpha(0f);
//
//            searchTextView.setVisibility(View.GONE);
//            searchEditText.setVisibility(View.VISIBLE);
//            searchEditText.requestFocus();
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//            clearImageView.setVisibility(View.VISIBLE);
//            musicNoteImageView.setVisibility(View.VISIBLE);
//        }
//        expanded = !expanded;
//    }
}
