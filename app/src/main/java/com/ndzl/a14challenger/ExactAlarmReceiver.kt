package com.ndzl.a14challenger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ExactAlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("ExactAlarmReceiver", "Alarm received now  ${System.currentTimeMillis()}")
    }
}