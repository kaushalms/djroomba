package com.kaushalmandayam.djroomba.screens.TrackList;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kaushalmandayam.djroomba.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Adapter to display a list of tracks from search results
 * <p>
 * Created on 6/13/17.
 *
 * @author Kaushal
 */

public class TracksAdapter extends RecyclerView.Adapter<TracksAdapter.TrackViewHolder>
{

    //==============================================================================================
    // Class Properties
    //==============================================================================================

    private List<Track> tracks = new ArrayList<>();

    //==============================================================================================
    // Constructor
    //==============================================================================================

    public TracksAdapter(List<Track> tracks)
    {
        this.tracks.clear();
        this.tracks = tracks;
    }

    public TracksAdapter()
    {
        // empty constructor
    }

    public void setData(List<Track> tracks)
    {
        this.tracks = new ArrayList<>();
        this.tracks.addAll(tracks);
        this.notifyDataSetChanged();
    }

    public void clearDataList()
    {
        tracks.clear();
        notifyDataSetChanged();
    }


    //==============================================================================================
    // Adapter Methods
    //==============================================================================================

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrackViewHolder holder, int position)
    {
        final Track track = tracks.get(position);
        holder.load(track);
    }

    @Override
    public int getItemCount()
    {
        return tracks.size();
    }

    //==============================================================================================
    // View Holder
    //==============================================================================================

    public class TrackViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.trackTextView)
        TextView trackTextView;
        @BindView(R.id.artistTextView)
        TextView artistTextView;

        public TrackViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void load(Track track)
        {
            trackTextView.setText(getFormattedString(track.name));
            artistTextView.setText(getArtistNames(track));
        }

        private String getFormattedString(String trackName)
        {
            return trackName.split("\\(")[0];
        }


        private String getArtistNames(Track track)
        {
            String artistName = track.artists.get(0).name;

            if (track.artists.size() > 1)
            {
                for (int i = 1; i < track.artists.size(); i++)
                {
                    artistName = artistName + ", " + track.artists.get(i).name;
                }
            }

            return artistName;
        }
    }
}
