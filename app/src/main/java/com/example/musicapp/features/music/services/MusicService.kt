package com.example.musicapp.features.music.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.musicapp.application.MusicApplication
import com.example.musicapp.model.ActionClick

class MusicService() : Service(){
    val mBinder : IBinder = MyBinder()
    var actionClick: ActionClick? = null

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    fun setCallback(actionClick: ActionClick){
        this.actionClick = actionClick
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
                    if(actionClick != null){
                        actionClick?.clickPlayer()
                    }
                }
                MusicApplication.ACTION_NEXT -> {
                    if(actionClick != null){
                        actionClick?.nextMusic()
                    }
                }
                MusicApplication.ACTION_PREVIOUS -> {
                    if(actionClick != null){
                        actionClick?.previousMusic()
                    }
                }
            }
            return START_STICKY
        }
        return super.onStartCommand(intent, flags, startId)
    }
}


