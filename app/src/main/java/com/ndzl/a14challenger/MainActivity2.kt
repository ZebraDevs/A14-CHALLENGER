package com.ndzl.a14challenger

import android.app.Activity
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class MainActivity2 : AppCompatActivity()  {

    private lateinit var credentialManager: CredentialManager

    private lateinit var ctx: Context

    companion object {
        const val TAG = "MainActivity2"
        lateinit var metrics: DisplayMetrics
        var density by Delegates.notNull<Int>()

        lateinit var mediaProjection: MediaProjection
        lateinit var imageReader: ImageReader
        lateinit var virtualDisplay: VirtualDisplay
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        ctx = this@MainActivity2



        metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        density = metrics.densityDpi


    }


    fun onClickbtn_TYPED_FGS(v: View?) {
        try {
            //TO PLAY WITH https://github.com/NDZL/-blog-A14/wiki/1-%E2%80%90-Min-Installable-Target-API
            //CHANGE TARGET API TO 29, THEN TEST THE COMPATIBILITY FRAMEWORK TOOL
            val serviceIntent = Intent(this, MyForegroundService::class.java)
            startForegroundService(serviceIntent)
        } catch (e: Exception) {
            Log.e("TAG", "onClickbtn_TYPED_FGS " + e.message)
        }
    }

    //https://developer.android.com/identity/sign-in/credential-provider#handle-password-queries
    //https://medium.com/novumlogic/using-androids-new-credential-manager-api-14a661cca66f
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun  onClickbtn_CREDENTIAL_MANAGER_STORE_PWD(v: View?) {


            try {
                //CREDENTIAL MANAGER NEEDS THIS APP TO TARGET API 34
                //HERE WE ARE GOING TEST THE SIMPLE USERNAME/PASSWORD STORAGE AND RETRIEVAL
                credentialManager = CredentialManager.create( applicationContext )

                val createPasswordRequest = CreatePasswordRequest(id = "ndzl", password = "Zebra123")
                GlobalScope.launch {
                    try {
                        val result = credentialManager.createCredential(ctx, createPasswordRequest)
                        //handleRegisterPasswordResult(result)
                        Log.d("onClickbtn_CREDENTIAL_MANAGER", "create result ${result.type} ${result.data}")
                    } catch (e: CreateCredentialCancellationException) {
                            //do nothing, the user chose not to save the credential
                            Log.v("onClickbtn_CREDENTIAL_MANAGER", "User cancelled the save")
                    } catch (e: CreateCredentialException) {
                        Log.d("onClickbtn_CREDENTIAL_MANAGER", "CreateCredentialException ${e.message}")
                    }
                }

            } catch (e: Exception) {
                Log.e("TAG", "onClickbtn_CREDENTIAL_MANAGER " + e.message)
            }


    }

    //result  get result android.credentials.TYPE_PASSWORD_CREDENTIAL Bundle[{androidx.credentials.BUNDLE_KEY_ID=ndzl, androidx.credentials.BUNDLE_KEY_PASSWORD=Zebra123}]
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun  onClickbtn_CREDENTIAL_MANAGER_RETRIEVE_PWD(v: View?) {
        try {
            if(!this::credentialManager.isInitialized)
                return

            val getCredRequest = GetCredentialRequest(listOf(GetPasswordOption()))

            GlobalScope.launch {

                    try {
                        val result = credentialManager.getCredential(ctx, getCredRequest)
                        //handleGetPasswordResult(result)
                        Log.d("onClickbtn_CREDENTIAL_MANAGER_RETRIEVE_PWD", "get result ${result.credential.type} ${result.credential.data}")
                    } catch (e: Exception) {
                        Log.d("onClickbtn_CREDENTIAL_MANAGER_RETRIEVE_PWD", "GetCredentialException ${e.message}")
                    }

            }

        } catch (e: Exception) {
            Log.e("TAG", "onClickbtn_CREDENTIAL_MANAGER " + e.message)
        }


    }


    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun  onClickbtn_CREDENTIAL_MANAGER_GOOGLEFLOW(v: View?) {
        try {

            if(!this::credentialManager.isInitialized)
                credentialManager = CredentialManager.create( applicationContext )

            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("125566413388-7uolsjhotn185bvqe2ffu0i47rjqljlj.apps.googleusercontent.com")  //replace with your Web application type client ID is your backend server's OAuth 2.0 client ID
                .setAutoSelectEnabled(false)
                .setNonce("NOPE! NDZL")
            .build()

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption( googleIdOption )
                .build()

            GlobalScope.launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = this@MainActivity2,
                    )
                    handleSignIn(result)
                } catch (e: GetCredentialException) {
                    Log.e("onClickbtn_CREDENTIAL_MANAGER_GOOGLEFLOW", "#1 " + e.message)
                }
            }


        } catch (e: Exception) {
            Log.e("onClickbtn_CREDENTIAL_MANAGER_GOOGLEFLOW", "#2 " + e.message)
        }
    }

    fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        val credential = result.credential

        when (credential) {

            // Passkey credential
            is PublicKeyCredential -> {
                // Share responseJson such as a GetCredentialResponse on your server to
                // validate and authenticate
                val responseJson = credential.authenticationResponseJson
            }

            // Password credential
            is PasswordCredential -> {
                // Send ID and password to your server to validate and authenticate.
                val username = credential.id
                val password = credential.password
            }

            // GoogleIdToken credential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        val idToken = googleIdTokenCredential.idToken
                        Log.i("handleSignIn", "Received Google ID token: $idToken ")
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("handleSignIn", "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e("handleSignIn", "Unexpected#1 type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e("handleSignIn", "Unexpected#2 type of credential")
            }
        }
    }


    fun onClickbtn_TAKESCREENSHOT(v: View?) {
        try {

            startScreenCapture()

        } catch (e: Exception) {
            Log.e("TAG", "onClickbtn_TAKESCREENSHOT " + e.message)
        }
    }


    private fun startScreenCapture() {
        val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), 3003)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 3003 && resultCode == RESULT_OK) {

            val intentFGS: Intent = ScreenCaptureService.newIntent(this, resultCode, data!!)
            startForegroundService( intentFGS )
        }
    }

    val screenCaptureCallback = Activity.ScreenCaptureCallback {
        //show a Toast message
        Log.d("TAG", "Screen capture started")
        Toast.makeText(this, "Screen capture started", Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onStart() {
        super.onStart()
        // Pass in the callback created in the previous step
        // and the intended callback executor (e.g. Activity's mainExecutor).
        registerScreenCaptureCallback(mainExecutor, screenCaptureCallback)
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onStop() {
        super.onStop()
        unregisterScreenCaptureCallback(screenCaptureCallback)

        Log.i("MainActivity2", "onStop was called. This is relevant to learn when the app enters a cached state.")
    }


    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter("android.intent.action.SCREEN_ON")
        intentFilter.addAction("android.intent.action.SCREEN_OFF")
        intentFilter.addAction("A14CHALLENGER_BROADCAST_ACTION")
        registerReceiver(receiverForQueuedBroadcasts, intentFilter, RECEIVER_EXPORTED)
        Log.d("A14Challenger queued broadcasts", "Context-based receiver registered  at ${java.text.SimpleDateFormat("HH:mm:ss:SSS").format(java.util.Date(System.currentTimeMillis()))}")

    }

    override fun onPause() {
        super.onPause()

        try {
            //unregisterReceiver( receiverForQueuedBroadcasts )  // had to comment this out to receive the queued broadcasts, although this is not a goog programming practice!
        } catch (e: Exception) { }
    }



    fun onClickbtn_PENDINGINTENTS(v: View?) {
        try {
            // to showcase https://developer.android.com/about/versions/14/behavior-changes-14#safer-intents
            // https://developer.android.com/privacy-and-security/risks/pending-intent#mitigations

            val implicitIntent= Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.zebra.com") // Example data
                // Add the following line to the intent to make it explicit and prevent a runtime exception to be raised on API 34+
             //   setClassName("com.ndzl.a14challenger", "com.ndzl.a14challenger.MainActivity2")
            }
            val  pendingIntent =
            PendingIntent.getActivity(
                this ,
                /* requestCode = */ 0,
                implicitIntent, /* flags = */ PendingIntent.FLAG_MUTABLE
            )



        } catch (e: Exception) {
            Log.e("TAG", "onClickbtn_PENDINGINTENTS " + e.message)

            //GETTING New mutable implicit PendingIntent: pkg=com.ndzl.a14challenger, action=android.intent.action.VIEW, featureId=null. This will be blocked once the app targets U+ for security reasons.

        }
    }

    fun onClickbtn_STARTACTVIAPENDINT(v: View?) {
        try {

            //directly start a local activity startActivity( Intent(this, PendingIntentsActivity::class.java)  )

            val thisappFooActivity = Intent(this, PendingIntentsActivity::class.java)
            val pendingIntentForThisappFooActivity = PendingIntent.getActivity(this, 12345, thisappFooActivity, PendingIntent.FLAG_IMMUTABLE)

            //send an explicit intent to another app
            val intentShipper = Intent()
           // intentShipper.setClassName("com.ndzl.a14companion", "com.ndzl.a14companion.MainActivity")
            intentShipper.action = "com.ndzl.a14companion"
            intentShipper.putExtra("pending_intent", pendingIntentForThisappFooActivity)
            intentShipper.putExtra("data", "Hello from Challenger")

            sendBroadcast(intentShipper)

        } catch (e: Exception) {
            Log.e("TAG", "onClickbtn_STARTACTVIAPENDINT " + e.message)

        }
    }


    fun onClickbtn_BACKGRDCOROUTINES(v: View?) {
        try {


            startRepeatingTaskAsRunnable(1000)

            startRepeatingTaskWithCoroutines(1000)
  
            //startRepeatingTaskWithWorkmanager(1000) //no less than 15 minute period!  Interval duration lesser than minimum allowed value; Changed to 900000

            //startRepeatingTaskWithJobScheduler(15 * 60 * 1000L) //no less than 15 minute period!

            startRepeatingTaskWithJavaThread(1000)

        } catch (e: Exception) {
            Log.e("TAG", "onClickbtn_BACKGRDCOROUTINES " + e.message)

        }
    }
    @RequiresApi(Build.VERSION_CODES.S)
    fun onClickbtn_EXACTALARMS(v: View?) {
        try {

            //schedule an exact alarm to go off in X seconds
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            val alarmIntent = Intent(this, ExactAlarmReceiver::class.java)
            alarmIntent.action = "com.ndzl.a14challenger.EXACT_ALARM"
            val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE )
            alarmManager.canScheduleExactAlarms()
            alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 8000, pendingIntent)

            Log.i( "onClickbtn_EXACTALARMS", "Alarm scheduled now ${System.currentTimeMillis()}")

        } catch (e: Exception) {
            Log.e("TAG", "onClickbtn_EXACTALARMS " + e.message)

        }
    }

    val receiverForQueuedBroadcasts = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("A14Challenger queued broadcasts", "Broadcast received at ${java.text.SimpleDateFormat("HH:mm:ss:SSS").format(java.util.Date(System.currentTimeMillis()))} as ${intent.action}")
        }
    }

    fun onClickbtn_QUEUEDBROADCASTS(v: View?) {
        try {


            /*No code has been developed in this callback. Please see the broadcast receiver registered in the onResume and the unregister in onPause*/


        } catch (e: Exception) {
            Log.e("TAG", "onClickbtn_QUEUEDBROADCASTS " + e.message)

        }
    }



    var javathreadCounter =0
    private fun startRepeatingTaskWithJavaThread(timeInterval: Long) {
        val one = object : Thread() {
            override fun run() {
                try {
                    while (true) {

                        println("JavaThreadCounter=${javathreadCounter++}")
                        sleep(timeInterval)
                    }

                } catch (v: InterruptedException) {
                    println(v)
                }
            }
        }
        one.priority = Thread.MAX_PRIORITY
        one.isDaemon = true
        one.start()
    }

    private fun startRepeatingTaskWithWorkmanager(timeInterval: Long) {
        val workRequest = PeriodicWorkRequestBuilder<MyWorkerExerciser>(timeInterval, TimeUnit.MILLISECONDS)
            // Additional constraints if needed
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "a14_work_name", // Unique name for the work
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, // Policy for handling existing work
            workRequest
        )
    }

    var jobschedulerCounter = 0
    private fun startRepeatingTaskWithJobScheduler(timeInterval:  Long) {

        val componentName = ComponentName(this, JobServiceExerciser::class.java)

        val jobInfo = JobInfo.Builder(123, componentName )
            .setPeriodic(timeInterval) ///* Minimum interval for a periodic job, in milliseconds. */private static final long MIN_PERIOD_MILLIS = 15 * 60 * 1000L;   // 15 minutes
            .setPersisted(false) // Persist across device reboots
            .build()

        val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        val result = jobScheduler.schedule(jobInfo)
        if (result == JobScheduler.RESULT_SUCCESS) {
            Log.i("MainActivity2", "Job scheduled successfully")
        } else {
            Log.i("MainActivity2", "Job scheduling failed")
        }
    }

    var runnableCounter=0
    //adding a timer to repeat a task at a certain interval without Coruotines
    private fun startRepeatingTaskAsRunnable(timeInterval: Long) {
        val handler = android.os.Handler()
        val runnable = object : Runnable {
            override fun run() {
                try {
                    Log.i("startRepeatingTaskAsRunnable", "runnableCounter=${runnableCounter++}")
                } catch (e: Exception) {
                    Log.e("TAG", "startRepeatingTaskAsRunnable " + e.message)
                }
                handler.postDelayed(this, timeInterval)
            }
        }
        handler.postDelayed(runnable, timeInterval)
    }

    var couroutineCounter=0
    //adding a timer to repeat a task at a certain interval with Coruotines
    private fun startRepeatingTaskWithCoroutines(timeInterval: Long) {
        GlobalScope.launch {
            while (true) {
                try {
                    Log.i("startRepeatingTaskWithCoroutines", "couroutineCounter=${couroutineCounter++}")
                } catch (e: Exception) {
                    Log.e("TAG", "startRepeatingTaskWithCoroutines " + e.message)
                }
                kotlinx.coroutines.delay(timeInterval)
            }
        }
    }

}

var workmanagerCounter=0
class MyWorkerExerciser(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.i("MyWorkerExerciser", "workmanagerCounter=${workmanagerCounter++}")
        return Result.success()
        //return Result.retry()
    }

}
