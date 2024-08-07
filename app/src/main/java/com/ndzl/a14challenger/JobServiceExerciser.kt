package com.ndzl.a14challenger

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.IBinder
import android.util.Log

class JobServiceExerciser : JobService() {


    companion object{
        private var jobschedulerCounter = 0
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.i("AnonymousJobService", "Job started")
        // Return true if the job needs to continue running

        Log.i("AnonymousJobService", "jobschedulerCounter=${jobschedulerCounter++}")
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.i("AnonymousJobService", "Job stopped")
        // Return true to reschedule the job
        return true
    }
}