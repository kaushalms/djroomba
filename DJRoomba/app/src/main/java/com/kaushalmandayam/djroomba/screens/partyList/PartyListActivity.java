package com.kaushalmandayam.djroomba.screens.partyList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.kaushalmandayam.djroomba.R;
import com.kaushalmandayam.djroomba.screens.base.BaseActivity;
import com.kaushalmandayam.djroomba.screens.partyList.PartyListPresenter.PartyListView;
import com.konifar.fab_transformation.FabTransformation;

import butterknife.BindView;
import butterknife.OnClick;
/**
 * Created by kaushalmandayam on 5/7/17.
 */

public class PartyListActivity extends BaseActivity<PartyListPresenter> implements PartyListView
{

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.overlay)
    View overlay;
    @BindView(R.id.sheet)
    View sheet;
    @BindView(R.id.partyListLayout)
    RelativeLayout partyListLayout;

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
       // FabTransformation.with(fab).setOverlay(overlay).tr(sheet);
        attachPresenter(new PartyListPresenter(), this);
    }

    @Override
    public void onBackPressed() {
        if (fab.getVisibility() != View.VISIBLE) {
            FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet);
            return;
        }
        super.onBackPressed();
    }

    @OnClick(R.id.overlay)
    void onClickBackground() {
        if (fab.getVisibility() != View.VISIBLE) {
            FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet);
        }
    }

    @OnClick(R.id.fab)
    void onClickFab() {
        if (fab.getVisibility() == View.VISIBLE) {
            FabTransformation.with(fab).setOverlay(overlay).transformTo(sheet);
        }
    }
}
