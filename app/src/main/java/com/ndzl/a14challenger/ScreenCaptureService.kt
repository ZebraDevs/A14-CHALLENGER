package com.ndzl.a14challenger

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.NotificationCompat
import com.ndzl.a14challenger.MainActivity2.Companion.imageReader
import com.ndzl.a14challenger.MainActivity2.Companion.mediaProjection
import com.ndzl.a14challenger.MainActivity2.Companion.virtualDisplay
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ScreenCaptureService : Service() {


    companion object {
        val EXTRA_RESULT_CODE = "resultCode"
        val EXTRA_DATA = "resultData"
        fun newIntent(context: Context, resultCode: Int, data: Intent): Intent {
            val intent = Intent(context, ScreenCaptureService::class.java)
            intent.putExtra(EXTRA_RESULT_CODE, resultCode)
            intent.putExtra(EXTRA_DATA, data)
            return intent
        }
    }

    override fun onBind(intent: Intent): IBinder? = null

    private var resultCode = 0
    private var resultData: Intent? = null
    private lateinit var mediaProjectionManager: MediaProjectionManager
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        resultCode = intent!!.getIntExtra(EXTRA_RESULT_CODE, 1);
        resultData = intent.getParcelableExtra(EXTRA_DATA);
        val notification = createNotification()
        startForeground(222, notification)
        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, resultData!!)

        imageReader = ImageReader.newInstance(MainActivity2.metrics.widthPixels, MainActivity2.metrics.heightPixels, PixelFormat.RGBA_8888, 3)
        imageReader.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage()
            if (image != null) {
                val planes = image.planes
                val buffer = planes[0].buffer
                val pixelStride = planes[0].pixelStride
                val rowStride = planes[0].rowStride
                val rowPadding = rowStride - pixelStride * MainActivity2.metrics.widthPixels

                val bitmap = Bitmap.createBitmap(
                    MainActivity2.metrics.widthPixels + rowPadding / pixelStride,
                    MainActivity2.metrics.heightPixels,
                    Bitmap.Config.ARGB_8888
                )
                bitmap.copyPixelsFromBuffer(buffer)
                image.close()// Use the bitmap (e.g., save to file, display, etc.)
                saveBitmapToAppExtStorage(this, bitmap, "screenshotA14_${System.currentTimeMillis()}.jpg")
                stopSelf() //needed to get just one screenshot
            }
        }, null)

        val mpcallback = MyMediaProjectionCallback()
        mediaProjection.registerCallback(mpcallback, null)


        //wait 500ms seconds before starting the virtual display
        Thread.sleep(500)


        virtualDisplay = mediaProjection.createVirtualDisplay(
            "ScreenCapture",
            MainActivity2.metrics.widthPixels,
            MainActivity2.metrics.heightPixels,
            MainActivity2.density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.surface,
            null,
            null
        )
        return START_STICKY
    }


    class MyMediaProjectionCallback : MediaProjection.Callback() {
        fun onStart() {
            // Handle projection start (e.g., start recording)
        }

        override fun onStop() {
            // Handle projection stop (e.g., release resources)

            virtualDisplay?.release()
            imageReader?.close()
            mediaProjection?.stop()

        }

        fun onError(errorCode: Int, message: String) {
            // Handle projection errors
        }
    }

    private fun createNotification(): Notification {
        val channel = NotificationChannel(
            "SCREEN_CAPTURE_SERVICE_ID",
            "sCREEN_CAPTURE_SERVICE CHANNEL_NAME",
            NotificationManager.IMPORTANCE_LOW // Or higher
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, "SCREEN_CAPTURE_SERVICE_ID")
            .setContentTitle("Service Running")
            .setContentText("Your service is running in the foreground.")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this,MainActivity2::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()
    }

    fun saveBitmapToAppExtStorage(context: Context, bitmap: Bitmap, filename: String, quality: Int = 80) : String{ // 80% quality=400kb, 50% quality=200kb   <100ms to save
        val file = File(context.externalCacheDir, filename)//externalCacheDir saves to /storage/emulated/0/Android/data/com.ndzl.cv.ocr/cache/ocr_1720099489030.jpg
        try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            }
            Log.i("SCREENSHOT", "saveBitmapToAppExtStorage / saved to "+file.absolutePath)
        } catch (e: IOException) {
            Log.e("SCREENSHOT", "saveBitmapToAppExtStorage / external storage exception "+e.message)
        }
        return file.absolutePath
    }

/*    private fun setOutputtextInMainThread( txt: String){
        runOnUiThread {
            viewBinding.tvOCRout.text = txt
        }
    }*/


}
