package com.ndzl.a14challenger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log

class ExactAlarmReceiver: BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("ExactAlarmReceiver", "Alarm received now  ${System.currentTimeMillis()}")


        for (i in 0 until 128) {
            val random = (50..500).random()
            playPCMData(generateNote(random.toDouble(), 1.0, 1000))
        }

    }

    fun generateNote(frequency: Double, durationInSeconds: Double, sampleRate: Int): ByteArray {

        val numSamples = (durationInSeconds * sampleRate).toInt()
        val samples = ByteArray(numSamples)

        for (i in 0 until numSamples) {
            val time = i.toDouble()/100.0
            samples[i] = ((Math.sin( frequency * time) * 127.0).toInt() + 0).toByte()
        }

        return samples
    }

    fun playPCMData(data: ByteArray) {

            try {
                val audioTrack = AudioTrack(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_8BIT)
                        .setSampleRate(11025)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                        .build(),
                    /*data.size*/1000000,
                    AudioTrack.MODE_STATIC,
                    AudioManager.AUDIO_SESSION_ID_GENERATE
                )


                audioTrack.write(data, 0, data.size)
                audioTrack.play()
            } catch (e: Exception) {

            }

    }

}