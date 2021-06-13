package com.example.musicapp.services

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.annotation.Nullable


class CloseNotification : Service() {
    override fun onTaskRemoved(rootIntent: Intent) {
        //Toast.makeText(this, “service called: “, Toast.LENGTH_LONG).show();
        super.onTaskRemoved(rootIntent)
        val ns = Context.NOTIFICATION_SERVICE
        val nMgr = getSystemService(ns) as NotificationManager
        nMgr.cancelAll()
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}