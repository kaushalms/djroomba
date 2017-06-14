package com.kaushalmandayam.djroomba.screens.PartyList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.kaushalmandayam.djroomba.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaushalmandayam.djroomba.models.Party;
import com.kaushalmandayam.djroomba.screens.PartyList.PartyListPresenter.PartyListView;
import com.kaushalmandayam.djroomba.screens.TrackList.TrackListActivity;
import com.kaushalmandayam.djroomba.screens.base.BaseActivity;
import com.konifar.fab_transformation.FabTransformation;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Screen to display list of parties and create new parties
 * <p>
 * Created on 6/13/17.
 *
 * @author Kaushal
 */

public class PartyListActivity extends BaseActivity<PartyListPresenter> implements PartyListView
{
    private static final String TAG = "PartyListActivity";
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.overlay)
    View overlay;
    @BindView(R.id.sheet)
    View sheet;
    @BindView(R.id.partyNameEditText)
    EditText partyNameEditText;
    @BindView(R.id.partyDescriptionEditText)
    EditText partyDescriptionEditText;
    @BindView(R.id.passwordCheckBox)
    CheckBox passwordCheckBox;
    @BindView(R.id.partyListRecyclerView)
    RecyclerView partyListRecyclerView;

    private PartyListAdapter partyListAdapter;
    private DatabaseReference partyDatabaseReference;

    //==============================================================================================
    // static Methods
    //==============================================================================================

    public static void start(Context context)
    {
        Intent starter = new Intent(context, PartyListActivity.class);
        context.startActivity(starter);
    }

    //==============================================================================================
    // Life-cycle Methods
    //==============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_list);
        sheet.setVisibility(View.GONE);
        attachPresenter(new PartyListPresenter(), this);
        partyDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("parties");

        partyDatabaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                presenter.savePartyMatadata(dataSnapshot);
                setupPartyAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }


    @Override
    public void onBackPressed()
    {
        if (fab.getVisibility() != View.VISIBLE)
        {
            FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet);
            return;
        }
        super.onBackPressed();
    }

    @OnClick(R.id.fab)
    void onClickFab()
    {
        if (fab.getVisibility() == View.VISIBLE)
        {
            FabTransformation.with(fab).setOverlay(overlay).transformTo(sheet);
        }
    }

    @OnClick(R.id.cancelButton)
    void onCancelButtonClicked()
    {
        if (fab.getVisibility() != View.VISIBLE)
        {
            FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet);
        }
    }

    @OnClick(R.id.searchTextView)
    void onSearchButtonClicked()
    {
        TrackListActivity.start(this);
    }

    @OnClick(R.id.submitButton)
    void onSubmitButtonClicked()
    {
        presenter.onSubmitButtonClicked(partyNameEditText.getText().toString(),
                                        partyDescriptionEditText.getText().toString(),
                                        passwordCheckBox.isSelected());
    }


    //==============================================================================================
    // view implementations
    //==============================================================================================

    private void setupPartyAdapter()
    {
        partyListRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        partyListAdapter = new PartyListAdapter();
        partyListRecyclerView.setAdapter(partyListAdapter);
        presenter.onAdapterViewSet();
    }

    //==============================================================================================
    // view implementations
    //==============================================================================================

    @Override
    public void showPartyAdded()
    {
        Log.d(TAG, "showPartyAdded: Success");
    }

    @Override
    public void showPartyList(List<Party> parties)
    {
        partyListAdapter.setData(parties);
    }
}
