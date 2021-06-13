package com.example.musicapp.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.musicapp.application.MusicApplication.Companion.ACTION_NEXT
import com.example.musicapp.application.MusicApplication.Companion.ACTION_PLAY
import com.example.musicapp.application.MusicApplication.Companion.ACTION_PREVIOUS

class NotificationReceiver() : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val intent2 = Intent(context, MusicService::class.java)

        if(intent?.action != null){
            when(intent.action){
                ACTION_PLAY -> {
                    intent2.putExtra("myAction", intent.action)
                    context?.startService(intent2)
                }
                ACTION_NEXT -> {
                    intent2.putExtra("myAction", intent.action)
                    context?.startService(intent2)
                }
                ACTION_PREVIOUS -> {
                    intent2.putExtra("myAction", intent.action)
                    context?.startService(intent2)
                }
            }
        }
    }
}
