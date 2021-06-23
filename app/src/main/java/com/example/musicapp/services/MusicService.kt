package com.example.musicapp.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.provider.MediaStore
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.musicapp.R
import com.example.musicapp.application.MusicApplication
import com.example.musicapp.model.ActionClick
import com.example.musicapp.view.MenuInicial
import com.example.musicapp.view.MusicPlayer
import com.example.musicapp.model.singleton.MusicSingleton
import com.example.musicapp.model.singleton.MusicSingleton.playlistAtual
import com.example.musicapp.view.MusicPlayer.Companion.mediaPlayer
import java.lang.Exception

class MusicService() : Service(){
    val mBinder : IBinder = MyBinder()

    lateinit var SHARED_PREFERENCES_MUSIC: SharedPreferences
    lateinit var SHARED_PREFERENCES_MUSIC_EDITOR: SharedPreferences.Editor

    var actionClick : ActionClick? = null

    override fun onCreate() {
        super.onCreate()

        SHARED_PREFERENCES_MUSIC = getSharedPreferences(
            MenuInicial.SHARED_MAIN,
            MODE_PRIVATE
        )
        SHARED_PREFERENCES_MUSIC_EDITOR = SHARED_PREFERENCES_MUSIC.edit()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    inner class MyBinder : Binder() {
        fun getService() : MusicService? {
            return this@MusicService
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val actionName = intent?.getStringExtra("myAction")

        if(actionName != null){
            when(actionName){
                MusicApplication.ACTION_PLAY -> {
                    actionClick?.clickPlayer()
                }
                MusicApplication.ACTION_NEXT -> {
                    actionClick?.nextMusic()
                }
                MusicApplication.ACTION_PREVIOUS -> {
                    actionClick?.previousMusic()
                }
            }
            return START_STICKY
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("UseCompatLoadingForDrawables", "WrongConstant")
    fun showNotifications(playPauseBtn : Int){
        val customNotificationStyle = RemoteViews(packageName, R.layout.custom_notification)
        val musicaTocando = playlistAtual[MusicSingleton.index]

        val intent = Intent(this, MusicPlayer::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, intent,
            Intent.FLAG_ACTIVITY_CLEAR_TOP
        )

        val prevIntent = Intent(this, NotificationReceiver::class.java).setAction(MusicApplication.ACTION_PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(this, NotificationReceiver::class.java).setAction(MusicApplication.ACTION_NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent(this, NotificationReceiver::class.java).setAction(MusicApplication.ACTION_PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val bitmap: Bitmap? = try {
            val picture = Uri.parse(playlistAtual[MusicSingleton.index].imagem)
            MediaStore.Images.Media.getBitmap(this.contentResolver, picture)
        }catch (e : Exception){
            val defaultPicture : Drawable? = getDrawable(R.drawable.img_music)
            val bitmapDraw = defaultPicture?.toBitmap()
            bitmapDraw
        }

        customNotificationStyle.setTextViewText(R.id.txtTitleMusic, musicaTocando.nomeMusica)
        customNotificationStyle.setTextViewText(R.id.txtTitleArtist, musicaTocando.nomeArtista)
        customNotificationStyle.setImageViewBitmap(R.id.imgMusicImageNotification, bitmap)

        customNotificationStyle.setOnClickPendingIntent(R.id.btnPreviusMusic, prevPendingIntent)
        customNotificationStyle.setOnClickPendingIntent(R.id.btnPlayMusic, playPendingIntent)
        customNotificationStyle.setOnClickPendingIntent(R.id.btnNextMusic, nextPendingIntent)
        customNotificationStyle.setOnClickPendingIntent(R.id.llnNotification, contentIntent)

        customNotificationStyle.setImageViewResource(R.id.btnPlayMusic, playPauseBtn)

        val notification = NotificationCompat.Builder(this, MusicApplication.CHANNEL_ID_2)
            .setSmallIcon(playPauseBtn)
            .setCustomContentView(customNotificationStyle)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(false)
            .build()

        val notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
        startForeground(1, notification)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        stopForeground(true)
        mediaPlayer.release()
        stopSelf()
    }
}


