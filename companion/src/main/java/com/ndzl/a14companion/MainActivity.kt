package com.ndzl.a14companion

import android.app.ActivityOptions
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager

class MainActivity : AppCompatActivity()  {

    private lateinit var credentialManager: CredentialManager

    private lateinit var ctx: Context



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        ctx = this@MainActivity


        //register a broadcast receiver to receive the broadcast
        val broadcastReceiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            override fun onReceive(context: Context?, intent: Intent?) {

                if (intent != null) {
                    if(intent.action == "com.ndzl.a14companion") {

                        val data = intent?.getStringExtra("data")

                        Log.i("com.ndzl.a14companion / BroadcastReceiver", "Data received: $data")


                        //sleep 10 seconds
                        Thread.sleep(10000)

                        try {
                            val pendingIntentFromChallenger = intent?.getParcelableExtra<PendingIntent>("pending_intent")!!
                            val ao = ActivityOptions.makeBasic()

                            pendingIntentFromChallenger.send( ao.setPendingIntentBackgroundActivityStartMode(ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED).toBundle() )
                            //pendingIntentFromChallenger.send(  )

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
            }
        }
        val filter = IntentFilter("com.ndzl.a14companion")
        filter.addCategory(Intent.CATEGORY_DEFAULT)
        registerReceiver(broadcastReceiver, filter, RECEIVER_EXPORTED)

    }



}