package com.example.android.popularmovies.sync;

import android.content.Context;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;

/**
 * Created by Soham on 12-Jul-17.
 */

public class JobTasks {
    private static boolean sInitialized;
    private static final String JOB_TAG = "job_check_internet_status_task";
    synchronized public static void scheduleJob(Context context){
        if(sInitialized)return;
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher firebaseJobDispatcher = new FirebaseJobDispatcher(driver);
        Job job = firebaseJobDispatcher.newJobBuilder()
                .setService(CheckConnectivityStatusFirebaseJobDispatcher.class)
                .setTag(JOB_TAG)
                .setLifetime(Lifetime.FOREVER)
                .build();

    }
}
