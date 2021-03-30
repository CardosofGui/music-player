package com.example.musicapp.application

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.musicapp.R
import com.example.musicapp.model.Music
import java.io.ByteArrayOutputStream
import java.lang.Exception

class MusicApplication : Application(){

    companion object{
        const val CHANNEL_ID_1 = "CHANNEL_1"
        const val CHANNEL_ID_2 = "CHANNEL_2"
        const val ACTION_NEXT = "ACTION_NEXT"
        const val ACTION_PREVIOUS = "ACTION_PREVIOUS"
        const val ACTION_PLAY = "ACTION_PLAY"

        lateinit var instance : MusicApplication
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        createNotifyChannel()
    }


    private fun createNotifyChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            var notificationChannel = NotificationChannel(CHANNEL_ID_1, "Channel (1)", NotificationManager.IMPORTANCE_LOW)
            notificationChannel.description = "Channel 1 Description"
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            val notificationChannel2 = NotificationChannel(CHANNEL_ID_2, "Channel (2)", NotificationManager.IMPORTANCE_LOW)
            notificationChannel2.description = "Channel 2 Description"
            notificationChannel2.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
            notificationManager.createNotificationChannel(notificationChannel2)
        }
    }
}