package com.kaushalmandayam.djroomba.managers;

import com.kaushalmandayam.djroomba.models.TrackViewModel;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Singleton to manage Spotify Audio Player
 * <p>
 * Created by kaushalmandayam on 4/29/17.
 */

public enum AudioPlayerManager
{
    //==============================================================================================
    // Class Properties
    //==============================================================================================

    INSTANCE;

    private Player player;
    private List<Track> tracks;
    private TrackViewModel currentTrackViewModel;
    private List<TrackViewModel> trackViewModels;
    private Map<TrackViewModel, Integer> tracksMap;

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

    public List<TrackViewModel> getTrackViewModels()
    {
        return trackViewModels;
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public void setTrackViewModels(List<TrackViewModel> trackViewModels)
    {
        this.trackViewModels = trackViewModels;
    }

    public List<Track> getTracks()
    {
        return tracks;
    }

    public void clearTracks()
    {
        trackViewModels = null;
        tracks = null;
    }

    public TrackViewModel getCurrentTrackViewModel()
    {
        return currentTrackViewModel;
    }

    public void setCurrentTrackViewModel(TrackViewModel currentTrackViewModel)
    {
        this.currentTrackViewModel = currentTrackViewModel;
    }

    public void playTrack(int lastClickedPosition)
    {
        player.playUri(new Player.OperationCallback()
        {
            @Override
            public void onSuccess()
            {
                // nothing
            }

            @Override
            public void onError(Error error)
            {
                // nothing
            }
        }, tracks.get(lastClickedPosition).uri, 0, 0);
    }


    public void saveTracksMap(Map<TrackViewModel, Integer> tracksMap)
    {
        this.tracksMap = tracksMap;
        trackViewModels = new ArrayList<>(tracksMap.keySet());
    }
}
