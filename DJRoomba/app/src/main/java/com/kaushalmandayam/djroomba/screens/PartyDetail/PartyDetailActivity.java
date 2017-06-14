package com.kaushalmandayam.djroomba.screens.PartyDetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.kaushalmandayam.djroomba.R;
import com.google.gson.Gson;
import com.kaushalmandayam.djroomba.models.Party;
import com.kaushalmandayam.djroomba.screens.TrackList.TrackListActivity;
import com.kaushalmandayam.djroomba.screens.base.BaseActivity;

import butterknife.OnClick;

/**
 * Screen to display a party
 * <p>
 * Created on 6/14/17.
 *
 * @author Kaushal
 */

public class PartyDetailActivity extends BaseActivity<PartyDetailPresenter>
                                    implements PartyDetailPresenter.PartyDetailView
{
    //==============================================================================================
    // class properties
    //==============================================================================================

    private static final String PARTY_KEY = "PARTY_KEY";
    private Party party;

    //==============================================================================================
    // static Methods
    //==============================================================================================

    public static void start(Context context, Party party)
    {
        Gson gson = new Gson();
        Intent starter = new Intent(context, PartyDetailActivity.class);
        starter.putExtra(PARTY_KEY, gson.toJson(party));
        context.startActivity(starter);
    }

    //==============================================================================================
    // Life-cycle Methods
    //==============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_detail);

        Bundle bundle = getIntent().getExtras();
        Gson gson = new Gson();
        party = gson.fromJson(bundle.getString(PARTY_KEY), Party.class);
    }

    //==============================================================================================
    // onClick methods
    //==============================================================================================

    @OnClick(R.id.searchTextView)
    void onClickToolbar()
    {
        TrackListActivity.start(this, party);
    }

}
