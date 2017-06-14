package com.kaushalmandayam.djroomba.screens.base;

/**
 * The base presenter class that all presenters in this project must inherit from.
 * <p/>
 * Created on 03/26/16.
 *
 * @author Kaushal Mandayam
 */
public abstract class BasePresenter<T>
{
    protected T view;

    public void attachView(T view)
    {
        this.view = view;
    }

    public void detachView()
    {
        this.view = null;
    }

}

