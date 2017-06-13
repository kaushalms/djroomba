package com.kaushalmandayam.djroomba.screens.TrackList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kaushalmandayam.djroomba.R;
import com.kaushalmandayam.djroomba.screens.base.BaseActivity;
import com.kaushalmandayam.djroomba.screens.TrackList.TrackListPresenter.TrackListView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import kaaes.spotify.webapi.android.models.Track;


/**
 * Created by kaushalmandayam on 5/7/17.
 */

public class TrackListActivity extends BaseActivity<TrackListPresenter> implements TrackListView
{

    @BindView(R.id.searchTextView)
    TextView searchTextView;
    @BindView(R.id.searchEditText)
    EditText searchEditText;
    @BindView(R.id.partyListLayout)
    RelativeLayout partyListLayout;
    @BindView(R.id.musicNoteImageView)
    ImageView musicNoteImageView;
    @BindView(R.id.searchImageView)
    ImageView searchImageView;
    @BindView(R.id.clearImageView)
    ImageView clearImageView;
    @BindView(R.id.tracksRecyclerView)
    RecyclerView tracksRecyclerView;

    private TracksAdapter tracksAdapter;

    //==============================================================================================
    // static Methods
    //==============================================================================================

    public static void start(Context context)
    {
        Intent starter = new Intent(context, TrackListActivity.class);
        context.startActivity(starter);
    }

    //==============================================================================================
    // Life-cycle Methods
    //==============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);
        attachPresenter(new TrackListPresenter(), this);
        setupSearchBarView();
        setupTrackAdapter();
        searchEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // Do nothing
            }

            @Override
            public void afterTextChanged(final Editable searchParameter)
            {
                if (!searchParameter.toString().isEmpty())
                {
                    presenter.onTrackSearched(searchParameter.toString());
                }
                else
                {
                    tracksAdapter.clearDataList();
                }
            }
        });
    }

    //==============================================================================================
    // Instance methods
    //==============================================================================================

    private void setupTrackAdapter()
    {
        tracksRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        tracksAdapter = new TracksAdapter();
        tracksRecyclerView.setAdapter(tracksAdapter);
    }

    private void setupSearchBarView()
    {
        searchTextView.setVisibility(View.GONE);
        searchEditText.setVisibility(View.VISIBLE);
        searchEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        searchImageView.setVisibility(View.GONE);
        clearImageView.setVisibility(View.VISIBLE);
        musicNoteImageView.setVisibility(View.VISIBLE);
    }

    //==============================================================================================
    // onClick methods
    //==============================================================================================

    @OnClick(R.id.searchTextView)
    void onClickToolbar()
    {
        setupSearchBarView();
    }

    @OnClick(R.id.clearImageView)
    void onClickClear()
    {
        searchEditText.setText("");
    }


    //==============================================================================================
    // View method implementation
    //==============================================================================================

    @Override
    public void populateTrack(final List<Track> tracks)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                tracksAdapter.setData(tracks);
            }
        });

    }

}