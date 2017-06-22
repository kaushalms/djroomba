package com.kaushalmandayam.djroomba.screens.PartyDetail;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kaushalmandayam.djroomba.R;
import com.kaushalmandayam.djroomba.managers.AudioPlayerManager;
import com.kaushalmandayam.djroomba.models.TrackViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Adapter to display a list of songs
 * <p>
 * Created on 6/14/17.
 *
 * @author Kaushal
 */

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.PlaylistViewHolder>
{

    //==============================================================================================
    // Class Properties
    //==============================================================================================

    private PlayListAdapter.PlaylistAdapterListener listener;
    private List<Track> tracks = new ArrayList<>();
    private List<TrackViewModel> trackViewModels = new ArrayList<>();
    private int lastClickedPosition;
    private Context context;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    public PlayListAdapter(Context context, PlayListAdapter.PlaylistAdapterListener listener)
    {
        this.listener = listener;
        this.context = context;
    }

    public void setData(List<Track> tracks)
    {
        this.tracks = new ArrayList<>();
        this.tracks.addAll(tracks);
        for (Track track : tracks)
        {
            TrackViewModel trackViewModel = new TrackViewModel();
            trackViewModel.setTrack(track);
            trackViewModels.add(trackViewModel);
        }
        AudioPlayerManager.INSTANCE.setTrackViewModels(trackViewModels);
        this.notifyDataSetChanged();
    }

    public void setTrackViewModels(List<TrackViewModel> trackViewModels)
    {
        this.tracks.clear();
        this.trackViewModels.clear();
        this.trackViewModels.addAll(trackViewModels);
        this.notifyDataSetChanged();
    }

    //==============================================================================================
    // Adapter methods
    //==============================================================================================

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_track, parent, false);
        return new PlayListAdapter.PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position)
    {
        final TrackViewModel trackViewModel = trackViewModels.get(position);
        holder.load(trackViewModel);
    }

    @Override
    public int getItemCount()
    {
        return trackViewModels.size();
    }

    //==============================================================================================
    // Instance methods
    //==============================================================================================

    public void pauseTrack(int lastClickedPosition)
    {
        trackViewModels.get(lastClickedPosition).setPlaying(false);
        AudioPlayerManager.INSTANCE.setTrackViewModels(trackViewModels);
        notifyItemChanged(lastClickedPosition);
    }

    public void playTrack(int lastClickedPosition)
    {
        trackViewModels.get(lastClickedPosition).setPlaying(true);
        AudioPlayerManager.INSTANCE.setTrackViewModels(trackViewModels);
        notifyItemChanged(lastClickedPosition);
    }

    public void playPreviousTrack(int lastClickedPosition)
    {
        trackViewModels.get(lastClickedPosition).setPlaying(false);
        lastClickedPosition--;
        trackViewModels.get(lastClickedPosition).setPlaying(true);
        AudioPlayerManager.INSTANCE.setTrackViewModels(trackViewModels);
        notifyDataSetChanged();
    }

    public void playNextTrack(int lastClickedPosition)
    {
        trackViewModels.get(lastClickedPosition).setPlaying(false);
        lastClickedPosition++;
        trackViewModels.get(lastClickedPosition).setPlaying(true);
        AudioPlayerManager.INSTANCE.setTrackViewModels(trackViewModels);
        notifyDataSetChanged();
    }

    //==============================================================================================
    // View Holder
    //==============================================================================================

    public class PlaylistViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.trackTextView)
        TextView trackTextView;
        @BindView(R.id.artistTextView)
        TextView artistTextView;
        @BindView(R.id.pauseImageView)
        ImageView pauseImageView;
        @BindView(R.id.playImageView)
        ImageView playImageView;

        private TrackViewModel trackViewModel;

        public PlaylistViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void load(TrackViewModel trackViewModel)
        {
            this.trackViewModel = trackViewModel;
            if (trackViewModel.isPlaying())
            {
                lastClickedPosition = trackViewModels.lastIndexOf(trackViewModel);
                listener.savelastClickedPosition(lastClickedPosition);
                playImageView.setVisibility(View.GONE);
                pauseImageView.setVisibility(View.VISIBLE);
                trackTextView.setTextColor(ContextCompat.getColor(context, R.color.primary));
                listener.showPauseButton();
            }
            else
            {
                trackTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
                playImageView.setVisibility(View.VISIBLE);
                pauseImageView.setVisibility(View.GONE);
            }
            trackTextView.setText(getFormattedString(trackViewModel.getTrack().name));
            artistTextView.setText(getArtistNames(trackViewModel.getTrack()));
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

        @OnClick(R.id.playImageView)
        void onPlayButtonClicked()
        {
            if (!(lastClickedPosition == trackViewModels.indexOf(trackViewModel)))
            {
                trackViewModels.get(lastClickedPosition).setPlaying(false);
                notifyItemChanged(lastClickedPosition);
            }
            playImageView.setVisibility(View.GONE);
            pauseImageView.setVisibility(View.VISIBLE);
            trackTextView.setTextColor(ContextCompat.getColor(context, R.color.primary));
            lastClickedPosition = trackViewModels.indexOf(trackViewModel);
            listener.onPlayClicked(trackViewModel,lastClickedPosition);
            trackViewModel.setPlaying(true);
        }

        @OnClick(R.id.pauseImageView)
        void onPauseClicked()
        {
            playImageView.setVisibility(View.VISIBLE);
            pauseImageView.setVisibility(View.GONE);
            listener.onPauseClicked();
        }
    }

    //==============================================================================================
    // Adapter listener Interface
    //==============================================================================================

    public interface PlaylistAdapterListener
    {
        void onPlayClicked(TrackViewModel trackViewModel, int lastClickedPosition);

        void onPauseClicked();

        void showPauseButton();

        void savelastClickedPosition(int lastClickedPosition);
    }
}
