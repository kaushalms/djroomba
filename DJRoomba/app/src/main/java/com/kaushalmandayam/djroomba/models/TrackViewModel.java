package com.kaushalmandayam.djroomba.models;

import kaaes.spotify.webapi.android.models.Track;

/**
 * View model for track cell in recycelrView
 * <p>
 * Created on 6/20/17.
 *
 * @author Kaushal
 */

public class TrackViewModel
{
    private Track track;
    private boolean isPlaying;
    private int votes;

    public Track getTrack()
    {
        return track;
    }

    public void setTrack(Track track)
    {
        this.track = track;
    }

    public boolean isPlaying()
    {
        return isPlaying;
    }

    public void setPlaying(boolean playing)
    {
        isPlaying = playing;
    }

    public int getVotes()
    {
        return votes;
    }

    public void setVotes(int votes)
    {
        this.votes = votes;
    }
}
