package com.sabbir.andorid.apps.myids.brain;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.sabbir.andorid.apps.myids.activities.RegisterActivity;

/**
 * Created by sabbir on 11/29/17.
 *
 * @author sabbir (sabbir@mpowe-social.com)
 */

public class DumbApp extends Application {

    private static DumbApp instance;
    private FirebaseUser mFirebaseUser;

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;

        /*Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.d("TAG",""+e);
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });*/
    }

    public static DumbApp getInstance(){
        return instance;
    }

    public FirebaseUser getFirebaseUser(){
        return mFirebaseUser;
    }

    public void setFirebaseUser(FirebaseUser user){
        this.mFirebaseUser=user;
    }
}
