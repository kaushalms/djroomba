package com.kaushalmandayam.djroomba.screens.PartyDetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.kaushalmandayam.djroomba.R;
import com.google.gson.Gson;
import com.kaushalmandayam.djroomba.Utils.PreferenceUtils;
import com.kaushalmandayam.djroomba.managers.AudioPlayerManager;
import com.kaushalmandayam.djroomba.managers.LoginManager;
import com.kaushalmandayam.djroomba.managers.UserManager;
import com.kaushalmandayam.djroomba.models.Party;
import com.kaushalmandayam.djroomba.models.TrackViewModel;
import com.kaushalmandayam.djroomba.screens.TrackList.TrackListActivity;
import com.kaushalmandayam.djroomba.screens.base.BaseActivity;

import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Screen to display a party
 * <p>
 * Created on 6/14/17.
 *
 * @author Kaushal
 */

public class PartyDetailActivity extends BaseActivity<PartyDetailPresenter>
        implements PartyDetailPresenter.PartyDetailView, SpotifyPlayer.NotificationCallback,
        LoginManager.AccessTokenListener

{
    //==============================================================================================
    // class properties
    //==============================================================================================

    @BindView(R.id.playlistRecyclerView)
    RecyclerView playlistRecyclerView;
    @BindView(R.id.pauseMediaImageView)
    ImageView pauseMediaImageView;
    @BindView(R.id.playMediaImageView)
    ImageView playMediaImageView;

    private static final String PARTY_KEY = "PARTY_KEY";
    private Party party;
    private PlayListAdapter playListAdapter;
    private int lastClickedPosition;

    //==============================================================================================
    // static Methods
    //==============================================================================================

    public static void start(Context context, Party party)
    {
        Gson gson = new Gson();
        Intent starter = new Intent(context, PartyDetailActivity.class);
        starter.putExtra(PARTY_KEY, gson.toJson(party));
        context.startActivity(starter);
    }

    //==============================================================================================
    // Life-cycle Methods
    //==============================================================================================

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_detail);
        attachPresenter(new PartyDetailPresenter(), this);

        Bundle bundle = getIntent().getExtras();
        Gson gson = new Gson();
        party = gson.fromJson(bundle.getString(PARTY_KEY), Party.class);

        LoginManager.INSTANCE.setAccesstokenListener(this);
        LoginManager.INSTANCE.fetchAccessToken(PreferenceUtils.getRefreshToken());

        setupTrackAdapter();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    private void setupTrackAdapter()
    {
        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        playListAdapter = new PlayListAdapter(PartyDetailActivity.this, new PlayListAdapter.PlaylistAdapterListener()
        {
            @Override
            public void onPlayClicked(TrackViewModel trackViewModel, int lastClickedPosition)
            {
                if(trackViewModel.isPlaying())
                {
                    presenter.onPlayerResumed();
                }
                else
                {
                    presenter.onPlayClicked(trackViewModel.getTrack());
                }
                saveLastPlayedPosition(lastClickedPosition);
                playMediaImageView.setVisibility(View.GONE);
                pauseMediaImageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPauseClicked()
            {
                presenter.onPauseClicked();
                playMediaImageView.setVisibility(View.VISIBLE);
                pauseMediaImageView.setVisibility(View.GONE);
            }
        });
        playlistRecyclerView.setAdapter(playListAdapter);
    }

    private void saveLastPlayedPosition(int lastClickedPosition)
    {
        this.lastClickedPosition = lastClickedPosition;
    }

    //==============================================================================================
    // onClick methods
    //==============================================================================================

    @OnClick(R.id.searchTextView)
    void onClickToolbar()
    {
        TrackListActivity.start(this, party);
    }

    @OnClick(R.id.playMediaImageView)
    void onMediaPlayClicked()
    {
        playMediaImageView.setVisibility(View.GONE);
        pauseMediaImageView.setVisibility(View.VISIBLE);
        playListAdapter.playTrack(lastClickedPosition);
        presenter.onPlayerResumed();
    }

    @OnClick(R.id.pauseMediaImageView)
    void onMediaPauseClicked()
    {
        playMediaImageView.setVisibility(View.VISIBLE);
        pauseMediaImageView.setVisibility(View.GONE);
        playListAdapter.pauseTrack(lastClickedPosition);
        presenter.onPauseClicked();
    }

    //==============================================================================================
    // view Interface methods
    //==============================================================================================

    @Override
    public void showTracks(final List<Track> tracks)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                playListAdapter.setData(tracks);
            }
        });
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent)
    {

    }

    @Override
    public void onPlaybackError(Error error)
    {

    }

    @Override
    public void setAccessToken(String userToken)
    {
        presenter.onAccessTokenReceived(userToken);
        if (AudioPlayerManager.INSTANCE.getTracks() == null)
        {
            presenter.getTracks(party);
        }
        else if (AudioPlayerManager.INSTANCE.getTrackViewModels() != null)
        {
            setupTrackAdapter();
            playListAdapter.setTrackViewModels(AudioPlayerManager.INSTANCE.getTrackViewModels());
            playListAdapter.notifyDataSetChanged();
        }
    }
}
