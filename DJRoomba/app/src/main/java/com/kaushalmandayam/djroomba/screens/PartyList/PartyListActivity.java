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
import com.kaushalmandayam.djroomba.managers.LoginManager;
import com.kaushalmandayam.djroomba.models.Party;
import com.kaushalmandayam.djroomba.screens.PartyDetail.PartyDetailActivity;
import com.kaushalmandayam.djroomba.screens.PartyList.PartyListPresenter.PartyListView;
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

public class PartyListActivity extends BaseActivity<PartyListPresenter> implements PartyListView,
        LoginManager.AccessTokenListener
{
    //==============================================================================================
    // Class Properties
    //==============================================================================================

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

    //==============================================================================================
    // static Methods
    //==============================================================================================

    public static void start(Context context)
    {
        Intent intent = new Intent(context, PartyListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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
        presenter.onCreate();
    }

    @Override
    public void onBackPressed()
    {
        if (fab.getVisibility() != View.VISIBLE)
        {
            FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet);
            return;
        }
        finish();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        LoginManager.INSTANCE.unSubscribeAccessTokenListener(this);
    }

    //==============================================================================================
    // Click Methods
    //==============================================================================================

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

    @OnClick(R.id.submitButton)
    void onSubmitButtonClicked()
    {
        LoginManager.INSTANCE.subscribeAccessTokenListener(this);
        if (!partyNameEditText.getText().toString().isEmpty())
        {
            presenter.onSubmitButtonClicked(partyNameEditText.getText().toString(),
                    partyDescriptionEditText.getText().toString(),
                    passwordCheckBox.isSelected());

            if (fab.getVisibility() != View.VISIBLE)
            {
                FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet);
            }
        }
        else
        {
            showToast(getString(R.string.party_name_empty));
        }

    }

    //==============================================================================================
    // view implementations
    //==============================================================================================

    @Override
    public void setupPartyAdapter()
    {
        partyListRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        partyListAdapter = new PartyListAdapter(new PartyListAdapter.PartyListAdapterListener()
        {
            @Override
            public void onJoinButtonClicked(Party party)
            {
                presenter.onJoinButtonClicked(party);
                LoginManager.INSTANCE.subscribeAccessTokenListener(PartyListActivity.this);
            }
        });
        partyListRecyclerView.setAdapter(partyListAdapter);
        presenter.onAdapterViewSet();
    }

    @Override
    public void startPartyDetailActivity(Party party)
    {
        PartyDetailActivity.start(PartyListActivity.this, party);
    }

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

    @Override
    public void setAccessToken(String userToken)
    {
        presenter.onAccessTokenReceived(userToken);
    }
}
