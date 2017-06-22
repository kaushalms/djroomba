package com.kaushalmandayam.djroomba.screens.PartyList;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
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
    //==============================================================================================\

    private final Context context;
    private List<Party> parties = new ArrayList<>();
    private PartyListAdapterListener listener;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    public PartyListAdapter(Context context, PartyListAdapter.PartyListAdapterListener listener)
    {
        this.context = context;
        this.listener = listener;
    }
    //==============================================================================================
    // Instance methods
    //==============================================================================================

    public void setData(List<Party> parties)
    {
        this.parties = new ArrayList<>();
        this.parties.addAll(parties);
        this.notifyDataSetChanged();
    }

    //==============================================================================================
    // Adapter methods
    //==============================================================================================

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
            holder.joinPartyButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    listener.onJoinButtonClicked(party);
                }
            });
        }
    }

    @Override
    public int getItemCount()
    {
        return parties.size();
    }

    //==============================================================================================
    // View holder class
    //==============================================================================================

    public class PartyViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.partyImageView)
        ImageView partyImageView;
        @BindView(R.id.partyNameTextView)
        TextView partyNameTextView;
        @BindView(R.id.joinPartyButton)
        Button joinPartyButton;

        public PartyViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void load(Party party)
        {
            Glide.with(partyImageView.getContext())
                    .load(party.getImageUrl())
                    .asBitmap()
                    .placeholder(R.drawable.ic_avatar_empty_40_px)
                    .dontAnimate()
                    .into(new BitmapImageViewTarget(partyImageView)
                    {
                        @Override
                        protected void setResource(Bitmap resource)
                        {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            partyImageView.setImageDrawable(circularBitmapDrawable);
                        }
                    });

            partyNameTextView.setText(party.getPartyName());
        }


    }

//==============================================================================================
// Adapter listener Interface
//==============================================================================================

    public interface PartyListAdapterListener
    {
        void onJoinButtonClicked(Party party);
    }
}
