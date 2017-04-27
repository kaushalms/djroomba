package com.example.kaushalmandayam.djroomba.screens.base;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.kaushalmandayam.djroomba.Constants;
import com.example.kaushalmandayam.djroomba.R;

import butterknife.ButterKnife;

/**
 * All fragments should inherit from this class.
 * <p/>
 * Created on 03/27/16.
 *
 * @author Kaushal Mandayam
 */
public abstract class BaseFragment<T> extends Fragment
{
    //==============================================================================================
    // Class Properties
    //==============================================================================================

    protected T presenter;
    protected Toast toast;
    protected ProgressDialog progressDialog;
    protected View progressView;

    //==============================================================================================
    // Lifecycle Methods
    //==============================================================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(getFragmentLayout(), container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
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
    // Layout Methods
    //==============================================================================================

    /**
     * Every fragment has to inflate a layout in the onCreateView method. We have added this method to
     * avoid duplicate all the inflate code in every fragment. You only have to return the layout to
     * inflate in this method when extends BaseFragment.
     */
    protected abstract int getFragmentLayout();

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

    protected void showToast(String message)
    {
        if (toast != null)
        {
            toast.cancel();
        }
        toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
        toast.show();
    }

    protected void showProgressDialogWithNoText()
    {
        if (progressDialog == null)
        {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIndeterminate(true);
            progressView = LayoutInflater.from(getActivity()).inflate(R.layout.item_progressbar_no_text, null, false);
            progressDialog.setCancelable(false);
        }

        progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        progressDialog.show();
        progressDialog.setContentView(progressView);
    }

    protected void dismissProgressDialog()
    {
        if (progressDialog != null)
        {
            progressDialog.dismiss();
        }
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
