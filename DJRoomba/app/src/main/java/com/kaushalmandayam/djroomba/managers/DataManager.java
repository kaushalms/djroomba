package com.kaushalmandayam.djroomba.managers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by kaushalmandayam on 5/4/17.
 */

public enum DataManager
{
    INSTANCE;

    public DatabaseReference getmDatabase()
    {
        return mDatabase;
    }

    public void setmDatabase(DatabaseReference mDatabase)
    {
        this.mDatabase = mDatabase;
    }

    private DatabaseReference mDatabase;

}
