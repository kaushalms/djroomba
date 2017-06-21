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
import com.kaushalmandayam.djroomba.managers.PartyManager;
import com.kaushalmandayam.djroomba.models.Party;
import com.kaushalmandayam.djroomba.models.TrackViewModel;
import com.kaushalmandayam.djroomba.screens.PartyList.PartyListActivity;
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
    private static final String TRACK_ADDED_KEY = "TRACK_ADDED_KEY";
    private Party party;
    private Track addedTrack;
    private PlayListAdapter playListAdapter;
    private int lastClickedPosition;
    private boolean isTrackAdded;

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

    public static void start(Context context, Party party, Track track)
    {
        Gson gson = new Gson();
        Intent starter = new Intent(context, PartyDetailActivity.class);
        starter.putExtra(PARTY_KEY, gson.toJson(party));
        starter.putExtra(TRACK_ADDED_KEY, gson.toJson(track));
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
        if (party != null)
        {
            PartyManager.INSTANCE.setParty(party);
        }

        addedTrack = gson.fromJson(bundle.getString(TRACK_ADDED_KEY), Track.class);
        if (addedTrack != null)
        {
            TrackViewModel addedTrackViewModel = new TrackViewModel();
            addedTrackViewModel.setTrack(addedTrack);
            addedTrackViewModel.setPlaying(false);
            AudioPlayerManager.INSTANCE.getTracks().add(addedTrack);
            AudioPlayerManager.INSTANCE.getTrackViewModels().add(addedTrackViewModel);
        }

        setupTrackAdapter();

        if (addedTrack != null)
        {
            playListAdapter.setTrackViewModels(AudioPlayerManager.INSTANCE.getTrackViewModels());
        }
        else
        {
            LoginManager.INSTANCE.setAccesstokenListener(this);
            LoginManager.INSTANCE.fetchAccessToken(PreferenceUtils.getRefreshToken());
        }


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
                if (trackViewModel.isPlaying())
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

            @Override
            public void showPauseButton()
            {
                playMediaImageView.setVisibility(View.GONE);
                pauseMediaImageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void savelastClickedPosition(int lastClickedPosition)
            {
                saveLastPlayedPosition(lastClickedPosition);
            }
        });
        playlistRecyclerView.setAdapter(playListAdapter);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        PartyListActivity.start(this);
        finish();
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

    @OnClick(R.id.previousImageView)
    void onPreviousButtonClicked()
    {
        if (lastClickedPosition > 0)
        {
            playMediaImageView.setVisibility(View.VISIBLE);
            pauseMediaImageView.setVisibility(View.GONE);

            playListAdapter.playPreviousTrack(lastClickedPosition);
            lastClickedPosition--;
            presenter.onPlayClicked(AudioPlayerManager.INSTANCE.getTracks().get(lastClickedPosition));
        }

    }

    @OnClick(R.id.nextImageView)
    void onNextButtonClicked()
    {
        if (lastClickedPosition < AudioPlayerManager.INSTANCE.getTracks().size() - 1)
        {
            playMediaImageView.setVisibility(View.VISIBLE);
            pauseMediaImageView.setVisibility(View.GONE);

            playListAdapter.playNextTrack(lastClickedPosition);
            lastClickedPosition++;
            presenter.onPlayClicked(AudioPlayerManager.INSTANCE.getTracks().get(lastClickedPosition));
        }

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
        if(playerEvent == PlayerEvent.kSpPlaybackNotifyTrackDelivered)
        {
            if (lastClickedPosition < AudioPlayerManager.INSTANCE.getTracks().size() - 1)
            {
                playMediaImageView.setVisibility(View.VISIBLE);
                pauseMediaImageView.setVisibility(View.GONE);

                playListAdapter.playNextTrack(lastClickedPosition);
                lastClickedPosition++;
                presenter.onPlayClicked(AudioPlayerManager.INSTANCE.getTracks().get(lastClickedPosition));
            }
        }
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
            playListAdapter.setTrackViewModels(AudioPlayerManager.INSTANCE.getTrackViewModels());
        }
    }

}
