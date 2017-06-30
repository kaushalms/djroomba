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
import com.kaushalmandayam.djroomba.Utils.MapUtils;
import com.kaushalmandayam.djroomba.managers.AudioPlayerManager;
import com.kaushalmandayam.djroomba.managers.PartyManager;
import com.kaushalmandayam.djroomba.models.TrackViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<TrackViewModel, Integer> tracksMap = new HashMap<>();
    private int lastClickedPosition;
    private Context context;
    private TrackViewModel currentTrackViewModel;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    public PlayListAdapter(Context context, PlayListAdapter.PlaylistAdapterListener listener)
    {
        this.listener = listener;
        this.context = context;
        currentTrackViewModel = AudioPlayerManager.INSTANCE.getCurrentTrackViewModel();
    }

    public void setData(List<TrackViewModel> trackViewModels)
    {
        this.tracks.clear();
        this.trackViewModels.clear();
        this.trackViewModels.addAll(trackViewModels);
        for (TrackViewModel trackViewModel: trackViewModels)
        {
            tracksMap.put(trackViewModel, trackViewModel.getVotes());
        }

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
        if (currentTrackViewModel != null)
        {
            if (trackViewModel.getTrack().id.equals(AudioPlayerManager.INSTANCE.getCurrentTrackViewModel().getTrack().id))
            {
                trackViewModel.setPlaying(true);
                lastClickedPosition = holder.getAdapterPosition();
            }
            else
            {
                trackViewModel.setPlaying(false);
            }
        }

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
        AudioPlayerManager.INSTANCE.getCurrentTrackViewModel().setPlaying(false);
        AudioPlayerManager.INSTANCE.setTrackViewModels(trackViewModels);
        notifyItemChanged(lastClickedPosition);
    }

    public void playTrack(int lastClickedPosition)
    {
        trackViewModels.get(lastClickedPosition).setPlaying(true);
        AudioPlayerManager.INSTANCE.getCurrentTrackViewModel().setPlaying(true);
        AudioPlayerManager.INSTANCE.setTrackViewModels(trackViewModels);
        AudioPlayerManager.INSTANCE.setCurrentTrackViewModel(trackViewModels.get(lastClickedPosition));
        notifyItemChanged(lastClickedPosition);
        listener.onPlayClicked(trackViewModels.get(lastClickedPosition), lastClickedPosition);
    }


    public void playPreviousTrack(int lastClickedPosition)
    {
        trackViewModels.get(lastClickedPosition).setPlaying(false);
        lastClickedPosition--;
        trackViewModels.get(lastClickedPosition).setPlaying(true);
        AudioPlayerManager.INSTANCE.setCurrentTrackViewModel(trackViewModels.get(lastClickedPosition));
        AudioPlayerManager.INSTANCE.setTrackViewModels(trackViewModels);
        notifyDataSetChanged();
    }

    public void playNextTrack(int lastClickedPosition)
    {
        trackViewModels.get(lastClickedPosition).setPlaying(false);
        lastClickedPosition++;
        trackViewModels.get(lastClickedPosition).setPlaying(true);
        AudioPlayerManager.INSTANCE.setCurrentTrackViewModel(trackViewModels.get(lastClickedPosition));
        AudioPlayerManager.INSTANCE.setTrackViewModels(trackViewModels);
        notifyDataSetChanged();
    }

    public void playFirstTrack()
    {
        resetVotes();
        rearrangeItems();
        listener.onPlayClicked(trackViewModels.get(lastClickedPosition), lastClickedPosition);
        trackViewModels.get(0).setPlaying(true);
        AudioPlayerManager.INSTANCE.setCurrentTrackViewModel(trackViewModels.get(0));
    }

    private void rearrangeItems()
    {
        tracksMap = MapUtils.sortByValue(tracksMap);
        AudioPlayerManager.INSTANCE.saveTracksMap(tracksMap);
        trackViewModels = new ArrayList<>(tracksMap.keySet());
        notifyDataSetChanged();
    }

    private void resetVotes()
    {
        trackViewModels.get(lastClickedPosition).setPlaying(false);
        TrackViewModel trackViewModel = trackViewModels.get(lastClickedPosition);
        trackViewModel.setVotes(0);
        PartyManager.INSTANCE.updateVotes(trackViewModel);
        lastClickedPosition = 0;
        listener.savelastClickedPosition(lastClickedPosition);
        listener.resetCounter();
        tracksMap.put(trackViewModel, trackViewModel.getVotes());
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
        @BindView(R.id.voteTextView)
        TextView voteTextView;

        private TrackViewModel trackViewModel;
        private int votes;

        public PlaylistViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void load(TrackViewModel trackViewModel)
        {
            this.trackViewModel = trackViewModel;
            votes = trackViewModel.getVotes();
            voteTextView.setText(String.valueOf(trackViewModel.getVotes()));
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
                trackTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
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
            if (!(lastClickedPosition == trackViewModels.indexOf(trackViewModel))
                    || trackViewModels.indexOf(trackViewModel) == 0)
            {
                AudioPlayerManager.INSTANCE.setCurrentTrackViewModel(trackViewModel);
                trackViewModels.get(lastClickedPosition).setPlaying(false);
                listener.resetCounter();
                notifyItemChanged(lastClickedPosition);
            }
            else
            {
                trackViewModel.setPlaying(true);
            }

            playImageView.setVisibility(View.GONE);
            pauseImageView.setVisibility(View.VISIBLE);
            trackTextView.setTextColor(ContextCompat.getColor(context, R.color.primary));
            lastClickedPosition = trackViewModels.indexOf(trackViewModel);
            listener.onPlayClicked(trackViewModel, lastClickedPosition);
            trackViewModel.setPlaying(true);
        }

        @OnClick(R.id.pauseImageView)
        void onPauseClicked()
        {
            AudioPlayerManager.INSTANCE.getCurrentTrackViewModel().setPlaying(false);
            trackViewModels.get(lastClickedPosition).setPlaying(false);
            playImageView.setVisibility(View.VISIBLE);
            pauseImageView.setVisibility(View.GONE);
            listener.onPauseClicked();
        }

        @OnClick(R.id.thumbsUpImageView)
        void onThumbsUpClicked()
        {
            votes++;
            trackViewModels.get(getAdapterPosition()).setVotes(votes);
            tracksMap.put(trackViewModel, votes);
            voteTextView.setText(String.valueOf(votes));
            listener.upVoteTrack(trackViewModels.get(getAdapterPosition()));
            rearrangeItems();
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

        void resetCounter();

        void upVoteTrack(TrackViewModel trackViewModel);

        void downVoteTrack(TrackViewModel trackViewModel);
    }
}
