package com.kaushalmandayam.djroomba.screens.PartyDetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.kaushalmandayam.djroomba.R;
import com.google.gson.Gson;
import com.kaushalmandayam.djroomba.Utils.PreferenceUtils;
import com.kaushalmandayam.djroomba.managers.AudioPlayerManager;
import com.kaushalmandayam.djroomba.managers.LoginManager;
import com.kaushalmandayam.djroomba.managers.PartyManager;
import com.kaushalmandayam.djroomba.models.Party;
import com.kaushalmandayam.djroomba.models.PartyTrack;
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

public class PartyDetailActivity extends BaseActivity<PartyDetailPresenter> implements
        PartyDetailPresenter.PartyDetailView,
        SpotifyPlayer.NotificationCallback,
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
    @BindView(R.id.songProgressBar)
    ProgressBar songProgressBar;

    private static final String PARTY_KEY = "PARTY_KEY";
    private static final String TRACK_ADDED_KEY = "TRACK_ADDED_KEY";
    private Party party;
    public TrackViewModel trackViewModel;
    private PlayListAdapter playListAdapter;
    private int lastClickedPosition;
    private CountDownTimer countDownTimer;
    private int startTime;
    private PartyTrack track;
    public static final int MILLISECONDS_PER_SECOND = 1000;

    //==============================================================================================
    // static Methods
    //==============================================================================================

    public static void start(Context context, Party party)
    {
        Gson gson = new Gson();
        Intent starter = new Intent(context, PartyDetailActivity.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
        showProgressDialog(getString(R.string.loading_playlist));
        party = gson.fromJson(bundle.getString(PARTY_KEY), Party.class);
        setupTrackAdapter();

        // Get a fresh access token and update the view;
        // Set Adapter after fetching access token
        LoginManager.INSTANCE.subscribeAccessTokenListener(this);
        LoginManager.INSTANCE.fetchAccessToken(PreferenceUtils.getRefreshToken());

        // Reset current song if you join a new party
        if (PartyManager.INSTANCE.getParty() != null
            && !party.getPartyId().equals(PartyManager.INSTANCE.getParty().getPartyId()))
        {
            AudioPlayerManager.INSTANCE.clearTracks();
            AudioPlayerManager.INSTANCE.setCurrentTrackViewModel(null);
        }

        // Save party to manager
        if (party != null)
        {
            PartyManager.INSTANCE.setParty(party);
        }

        // Restore progress
        if (AudioPlayerManager.INSTANCE.getCurrentTrackViewModel() != null)
        {
            setupProgressBar(AudioPlayerManager.INSTANCE.getProgess());
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onBackPressed()
    {
        PartyListActivity.start(this);
        finish();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        LoginManager.INSTANCE.unSubscribeAccessTokenListener(this);
    }

    //==============================================================================================
    // Class instance Methods
    //==============================================================================================

    private void setupTrackAdapter()
    {
        dismissProgressDialog();
        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        playListAdapter = new PlayListAdapter(PartyDetailActivity.this, new PlayListAdapter.PlaylistAdapterListener()
        {
            @Override
            public void onPlayClicked(TrackViewModel trackViewModel, int lastClickedPosition)
            {
                saveTrackViewModel(trackViewModel);
                if (trackViewModel.isPlaying())
                {
                    presenter.onPlayerResumed();
                }
                else
                {
                    presenter.onPlayClicked(trackViewModel.getTrack());
                }
                resetTimer();
                setupProgressBar(startTime);
                saveLastPlayedPosition(lastClickedPosition);
                playMediaImageView.setVisibility(View.GONE);
                pauseMediaImageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPauseClicked()
            {
                if (countDownTimer != null)
                {
                    countDownTimer.cancel();
                }
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

            @Override
            public void resetCounter()
            {
                startTime = 0;
            }

            @Override
            public void upVoteTrack(TrackViewModel trackViewModel)
            {
                int votes = trackViewModel.getVotes();
                votes++;
                trackViewModel.setVotes(votes);
                PartyManager.INSTANCE.updateVotes(track);
            }

            @Override
            public void downVoteTrack(TrackViewModel trackViewModel)
            {
                int votes = trackViewModel.getVotes();
                if (votes > 0)
                {
                    votes--;
                }
                trackViewModel.setVotes(votes);
                PartyManager.INSTANCE.updateVotes(track);
            }
        });
        playlistRecyclerView.setAdapter(playListAdapter);
    }


    private void setupProgressBar(final int timerStartTime)
    {
        final int maxSongLength = (int) (AudioPlayerManager.INSTANCE.getCurrentTrackViewModel()
                .getTrack().duration_ms / MILLISECONDS_PER_SECOND);
        int counterMaxTime = (maxSongLength - timerStartTime);
        if (timerStartTime != 0)
        {
            songProgressBar.setMax(counterMaxTime);
            songProgressBar.setProgress(timerStartTime);
        }
        else
        {
            songProgressBar.setMax(maxSongLength);
            songProgressBar.setProgress(0);
        }

        countDownTimer = new CountDownTimer(counterMaxTime * MILLISECONDS_PER_SECOND, MILLISECONDS_PER_SECOND)
        {
            int i = timerStartTime;

            @Override
            public void onTick(long millisUntilFinished)
            {
                Log.v("Log_tag", "Tick of Progress" + i + millisUntilFinished);
                i++;
                startTime = i;
                songProgressBar.setProgress(i);
                AudioPlayerManager.INSTANCE.setProgress(i);
            }

            @Override
            public void onFinish()
            {
                songProgressBar.setProgress(maxSongLength);
                onNextButtonClicked();
            }
        };
        countDownTimer.start();

    }

    private void saveTrackViewModel(TrackViewModel trackViewModel)
    {
        this.trackViewModel = trackViewModel;
    }

    private void saveLastPlayedPosition(int lastClickedPosition)
    {
        this.lastClickedPosition = lastClickedPosition;
    }

    private void resetTimer()
    {
        if (countDownTimer != null)
        {
            countDownTimer.cancel();
            countDownTimer = null;
        }
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
        if (trackViewModel != null)
        {
            presenter.onPlayerResumed();
            playListAdapter.playTrack(lastClickedPosition);
        }
        else
        {
            playListAdapter.playTrack(0);
            presenter.onPlayClicked(AudioPlayerManager.INSTANCE.getCurrentTrackViewModel().getTrack());
        }

        resetTimer();
        setupProgressBar(startTime);
        playMediaImageView.setVisibility(View.GONE);
        pauseMediaImageView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.pauseMediaImageView)
    void onMediaPauseClicked()
    {
        if (countDownTimer != null)
        {
            countDownTimer.cancel();
        }

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
            resetTimer();
            setupProgressBar(startTime);
        }
    }

    @OnClick(R.id.nextImageView)
    void onNextButtonClicked()
    {
        if (lastClickedPosition < AudioPlayerManager.INSTANCE.getTrackViewModels().size() - 1)
        {
            playMediaImageView.setVisibility(View.VISIBLE);
            pauseMediaImageView.setVisibility(View.GONE);

            playListAdapter.playNextTrack(lastClickedPosition);
            lastClickedPosition++;
            if (presenter != null)
            {
                presenter.onPlayClicked(AudioPlayerManager.INSTANCE.getTracks().get(lastClickedPosition));
            }
            else
            {
                // play in background, when you are on a different activity
                AudioPlayerManager.INSTANCE.playTrack(lastClickedPosition);
            }

            resetTimer();
            setupProgressBar(startTime);
        }
    }

    //==============================================================================================
    // view Interface methods
    //==============================================================================================

    @Override
    public void showTracks(final List<TrackViewModel> trackViewModels)
    {
        dismissProgressDialog();
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                playListAdapter.setData(trackViewModels);
            }
        });
    }

    //==============================================================================================
    // Spotify player callback implementation
    //==============================================================================================

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent)
    {
        // todo move to next song on finish

        if (playerEvent == PlayerEvent.kSpPlaybackNotifyTrackDelivered)
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
        // do nothing
    }

    //==============================================================================================
    // Access token listener
    //==============================================================================================

    @Override
    public void setAccessToken(String userToken)
    {
        presenter.onAccessTokenReceived(userToken);
        if (AudioPlayerManager.INSTANCE.getTrackViewModels() == null)
        {
            presenter.getPartyTracks(party);
        }
        else
        {
            playListAdapter.setTrackViewModels(AudioPlayerManager.INSTANCE.getTrackViewModels());
        }
    }
}
