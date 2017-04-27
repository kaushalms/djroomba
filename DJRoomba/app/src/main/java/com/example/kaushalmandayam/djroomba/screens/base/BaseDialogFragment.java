package com.example.kaushalmandayam.djroomba.screens.base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kaushalmandayam.djroomba.Constants;
import com.example.kaushalmandayam.djroomba.R;

import butterknife.ButterKnife;

/**
 * All dialog fragments should inherit from this class.
 * <p/>
 * Created on 3/26/16.
 *
 * @author Kaushal Mandayam
 */
public abstract class BaseDialogFragment<T> extends DialogFragment
{
    //==============================================================================================
    // Class Properties
    //==============================================================================================

    protected T presenter;
    protected ProgressDialog progressDialog;
    protected Toast toast;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFragment);
        return dialog;
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

    protected void showProgressDialog(String message)
    {
        View view = null;

        if (progressDialog == null)
        {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setIndeterminate(true);
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_progressbar, null, false);

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
