package com.kaushalmandayam.djroomba.screens.partyList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.kaushalmandayam.djroomba.screens.partyList.PartyListPresenter.PartyListView;
import com.konifar.fab_transformation.FabTransformation;


import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import kaaes.spotify.webapi.android.models.Track;


/**
 * Created by kaushalmandayam on 5/7/17.
 */

public class PartyListActivity extends BaseActivity<PartyListPresenter> implements PartyListView
{

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
    @BindView(R.id.tracksRecyclerView)
    RecyclerView tracksRecyclerView;

    private TracksAdapter tracksAdapter;

    //==============================================================================================
    // static Methods
    //==============================================================================================

    public static void start(Context context)
    {
        Intent starter = new Intent(context, PartyListActivity.class);
        context.startActivity(starter);
    }

    //==============================================================================================
    // Life-cycle Methods
    //==============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_list);
        sheet.setVisibility(View.GONE);
        attachPresenter(new PartyListPresenter(), this);
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

    @Override
    public void onBackPressed()
    {
        if (fab.getVisibility() != View.VISIBLE)
        {
            FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet);
            return;
        }
        super.onBackPressed();
    }

    @OnClick(R.id.overlay)
    void onClickBackground()
    {
        if (fab.getVisibility() != View.VISIBLE)
        {
            FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet);
        }
    }

    @OnClick(R.id.fab)
    void onClickFab()
    {
        if (fab.getVisibility() == View.VISIBLE)
        {
            FabTransformation.with(fab).setOverlay(overlay).transformTo(sheet);
        }
    }

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
