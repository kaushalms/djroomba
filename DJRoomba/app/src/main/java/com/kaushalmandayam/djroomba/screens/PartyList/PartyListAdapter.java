package com.kaushalmandayam.djroomba.screens.PartyList;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kaushalmandayam.djroomba.R;
import com.kaushalmandayam.djroomba.models.Party;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter to display a list of parties
 * <p>
 * Created on 6/14/17.
 *
 * @author Kaushal
 */

public class PartyListAdapter extends RecyclerView.Adapter<PartyListAdapter.PartyViewHolder>
{
    //==============================================================================================
    // Class Properties
    //==============================================================================================

    private List<Party> parties = new ArrayList<>();
    private PartyListAdapterListener listener;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    public PartyListAdapter(PartyListAdapter.PartyListAdapterListener listener)
    {
        this.listener = listener;
    }

    public void setData(List<Party> parties)
    {
        this.parties = new ArrayList<>();
        this.parties.addAll(parties);
        this.notifyDataSetChanged();
    }

    @Override
    public PartyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_party, parent, false);
        return new PartyListAdapter.PartyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PartyViewHolder holder, int position)
    {
        final Party party = parties.get(position);
        holder.load(party);

        if (listener != null)
        {
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    listener.onPartyClicked(party);
                }
            });
        }
    }

    @Override
    public int getItemCount()
    {
        return parties.size();
    }

    public class PartyViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.partyImageView)
        ImageView partyImageView;
        @BindView(R.id.partyNameTextView)
        TextView partyNameTextView;

        public PartyViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void load(Party party)
        {
            Glide.with(partyImageView.getContext())
                    .load(party.getImageUrl())
                    .placeholder(R.drawable.ic_avatar_empty_40_px)
                    .dontAnimate()
                    .into(partyImageView);

            partyNameTextView.setText(party.getPartyName());
        }
    }

    //==============================================================================================
    // Adapter listener Interface
    //==============================================================================================

    public interface PartyListAdapterListener
    {
        void onPartyClicked(Party party);
    }
}
