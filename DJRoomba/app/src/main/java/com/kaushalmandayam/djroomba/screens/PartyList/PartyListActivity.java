package com.kaushalmandayam.djroomba.screens.PartyList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.kaushalmandayam.djroomba.R;
import com.kaushalmandayam.djroomba.screens.PartyList.PartyListPresenter.PartyListView;
import com.kaushalmandayam.djroomba.screens.TrackList.TrackListActivity;
import com.kaushalmandayam.djroomba.screens.base.BaseActivity;
import com.konifar.fab_transformation.FabTransformation;

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
        presenter.onSubmitButtonClicked(partyNameEditText.toString(),
                                        partyDescriptionEditText.toString(),
                                        passwordCheckBox.isSelected());
    }


    //==============================================================================================
    // Life-cycle Methods
    //==============================================================================================

    @Override
    public void showPartyAdded()
    {
        Log.d(TAG, "showPartyAdded: Success");
    }
}
