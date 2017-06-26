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
import com.google.gson.Gson;
import com.kaushalmandayam.djroomba.managers.PartyManager;
import com.kaushalmandayam.djroomba.models.Party;
import com.kaushalmandayam.djroomba.models.PartyTrack;
import com.kaushalmandayam.djroomba.screens.PartyDetail.PartyDetailActivity;
import com.kaushalmandayam.djroomba.screens.PartyList.PartyListActivity;
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

    private static final String PARTY_KEY = "PARTY_KEY";

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
    private Party party;

    //==============================================================================================
    // static Methods
    //==============================================================================================

    public static void start(Context context, Party party)
    {
        Gson gson = new Gson();
        Intent starter = new Intent(context, TrackListActivity.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        starter.putExtra(PARTY_KEY, gson.toJson(party));
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

        Bundle bundle = getIntent().getExtras();
        Gson gson = new Gson();
        party = gson.fromJson(bundle.getString(PARTY_KEY), Party.class);

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
                    showProgressDialog("loading");
                    presenter.onTrackSearched(searchParameter.toString());
                }
                else
                {
                    tracksAdapter.clearDataList();
                }
            }
        });
    }


    @Override
    public void onBackPressed()
    {
        PartyDetailActivity.start(TrackListActivity.this, PartyManager.INSTANCE.getParty());
        finish();
    }

    //==============================================================================================
    // Instance methods
    //==============================================================================================

    private void setupTrackAdapter()
    {
        tracksRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        tracksAdapter = new TracksAdapter(party, new TracksAdapter.TrackListAdapterListener()
        {
            @Override
            public void onAddSongClicked(Party party, Track track)
            {
                presenter.updatePlaylist(track);
            }
        });
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
        if (!searchEditText.getText().toString().isEmpty())
        {
            searchEditText.setText("");
        }
        else
        {
            PartyDetailActivity.start(this, PartyManager.INSTANCE.getParty());
            finish();
        }
    }

    //==============================================================================================
    // View method implementation
    //==============================================================================================

    @Override
    public void populateTrack(final List<Track> tracks)
    {
        dismissProgressDialog();
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                tracksAdapter.setData(tracks);
            }
        });

    }

    @Override
    public void startPartyDetailActivity()
    {
        PartyDetailActivity.start(TrackListActivity.this, PartyManager.INSTANCE.getParty());
        finish();
    }
}
