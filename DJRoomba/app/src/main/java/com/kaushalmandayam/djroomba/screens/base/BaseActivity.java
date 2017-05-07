package com.kaushalmandayam.djroomba.screens.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kaushalmandayam.djroomba.R;
import com.kaushalmandayam.djroomba.Constants;

import butterknife.ButterKnife;

/**
 * The base class that all activities inherit from. Shared functionality can be done here.
 * <p/>
 * Created on 04/29/2017
 *
 * @author Kaushal Mandayam
 */
public abstract class BaseActivity<T> extends AppCompatActivity
{
    //==============================================================================================
    // Class Properties
    //==============================================================================================

    protected T presenter;
    public Toolbar toolbar;
    protected ActionBar actionBar;
    protected Toast toast;
    protected ProgressDialog progressDialog;

    //==============================================================================================
    // Lifecycle Methods
    //==============================================================================================

    @Override
    public void setContentView(@LayoutRes int layoutResID)
    {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//
//        if (toolbar != null)
//        {
//            setSupportActionBar(toolbar);
//            actionBar = getSupportActionBar();
//
//            if (actionBar != null)
//            {
//                actionBar.setDisplayHomeAsUpEnabled(true);
//                actionBar.setHomeButtonEnabled(true);
//                actionBar.setDisplayShowTitleEnabled(false);
//            }
//
//            toolbar.setNavigationOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    onBackPressed();
//                }
//            });
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy()
    {
        if (presenter instanceof BasePresenter)
        {
            BasePresenter basePresenter = (BasePresenter) presenter;
            basePresenter.detachView();
        }

        presenter = null;

        super.onDestroy();
    }

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

    protected void showToast(String message)
    {
        if (toast != null)
        {
            toast.cancel();
        }
        toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }

    protected void showProgressDialog(String message)
    {
        View view = null;

        if (progressDialog == null)
        {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            view = LayoutInflater.from(this).inflate(R.layout.item_progressbar, null, false);

            if (message == null)
            {
                message = getString(R.string.please_wait);
            }

            TextView messageTextView = (TextView) view.findViewById(R.id.progressBarTextView);
            messageTextView.setText(message);
            progressDialog.setCancelable(false);
        }

        if (view != null)
        {
            progressDialog.show();
            progressDialog.setContentView(view);
        }
    }

    protected void dismissProgressDialog()
    {
        if (progressDialog != null)
        {
            progressDialog.dismiss();
        }
    }

    protected void hideStatusBar()
    {
        View decorView = getWindow().getDecorView();
        int options = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(options);
    }

    //==============================================================================================
    // Presenter Methods
    //==============================================================================================

    protected void attachPresenter(T presenter, BaseView view)
    {
        this.presenter = presenter;

        if (presenter instanceof BasePresenter)
        {
            BasePresenter basePresenter = (BasePresenter) presenter;
            basePresenter.attachView(view);
        }
        else
        {
            Log.w(Constants.TAG, "Presenter is not a base presenter!!");
        }
    }
}