package com.example.musicapp.features.music

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RemoteViews
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.musicapp.features.music.services.CloseNotification
import com.example.musicapp.features.music.services.MusicService
import com.example.musicapp.R
import com.example.musicapp.application.MusicApplication
import com.example.musicapp.features.list_music.List_Musics
import com.example.musicapp.features.list_music.Splash_Screen
import com.example.musicapp.features.music.services.NotificationReceiver
import com.example.musicapp.features.music.viewModel.MainViewModel
import com.example.musicapp.model.ActionClick
import com.example.musicapp.singleton.MusicSingleton
import com.example.musicapp.singleton.MusicSingleton.index
import com.example.musicapp.singleton.MusicSingleton.listaMusicas
import com.example.musicapp.singleton.MusicSingleton.repeatMusic
import com.example.musicapp.singleton.MusicSingleton.shuffleOn
import com.example.musicapp.singleton.MusicSingleton.tempoPause
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception


class MainActivity : AppCompatActivity(), ActionClick, ServiceConnection {

    lateinit var mMainViewModel: MainViewModel
    lateinit var mediaSession : MediaSessionCompat

    var handler: Handler = Handler()
    var musicService: MusicService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaSession = MediaSessionCompat(this, "PlayerAudio")

        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
        mMainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        initDados()
        setClicks()
        initToolbar("Music Player")

        runOnUiThread(object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val curretPosition = mediaPlayer.currentPosition / 1000
                    seekMusicDuration.progress = curretPosition
                    txtTimeRunning.text = formatarTime(curretPosition)

                    mediaPlayer.setOnCompletionListener {
                        if (!it.isPlaying) {
                            nextMusic()
                        }
                    }
                }
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun initDados() {
        index = intent.getIntExtra("MUSICA_SELECIONADA", index)

        mMainViewModel.nomeArtista.observe(this, Observer {
            txtArtista.text = it
        })
        mMainViewModel.nomeMusica.observe(this, Observer {
            txtMusica.text = it
        })
        mMainViewModel.fotoMusica.observe(this, Observer {
            imageMusic.setImageURI(it)
            if(imageMusic.drawable == null) imageMusic.setImageResource(R.drawable.img_music)
        })
        mMainViewModel.musicaDuration.observe(this, Observer {
            seekMusicDuration.max = it / 1000
            txtTimeFinish.text = formatarTime(it / 1000)
        })

        trocarIconeIniciar()
        mMainViewModel.atualizarDadosMusica()


        seekMusicDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (mediaPlayer.isPlaying && fromUser) {
                    mediaPlayer.seekTo(progress * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                tempoPause = seekBar!!.progress

            }
        })
    }

    private fun setClicks() {
        val animationClickStart = AnimationUtils.loadAnimation(this, R.anim.animation)
        val animationTransitionRight =
            AnimationUtils.loadAnimation(this, R.anim.animation_transition_right)
        val animationTransitionLeft =
            AnimationUtils.loadAnimation(this, R.anim.animation_transition_left)


        btnIniciaMusica.setOnClickListener {
            clickPlayer()
        }

        btnProximaMusica.setOnClickListener {

            animationTransition(animationTransitionRight){
                nextMusic()
            }
        }

        btnVoltarMusica.setOnClickListener {

            animationTransition(animationTransitionLeft){
                previousMusic()
            }
        }

        btnShuffleMusic.setOnClickListener {
            shuffleOn = !shuffleOn
            trocarIconeIniciar()
        }

        btnRepeatMusic.setOnClickListener {
            repeatMusic = !repeatMusic
            trocarIconeIniciar()
        }
    }

    private fun animationTransition(
        animationTransition: Animation,
        function: () -> Unit
    ) {
        btnProximaMusica.isClickable = false
        btnVoltarMusica.isClickable = false
        btnIniciaMusica.isClickable = false

        llnPlayer.startAnimation(animationTransition)

        animationTransition.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
                TODO("Not yet implemented")
            }

            override fun onAnimationEnd(animation: Animation?) {
                function()
                btnProximaMusica.isClickable = true
                btnVoltarMusica.isClickable = true
                btnIniciaMusica.isClickable = true
            }

            override fun onAnimationStart(animation: Animation?) {

            }
        })
    }

    private fun formatarTime(time: Int): String {
        var totalOut = ""
        var totalNew = ""
        val seconds = (time % 60).toString()
        val minutes = (time / 60).toString()
        totalOut = "$minutes:$seconds"
        totalNew = "$minutes:0$seconds"

        if (seconds.length == 1) {
            return totalNew
        } else {
            return totalOut
        }
    }

    private fun trocarIconeIniciar() {
        if (mediaPlayer.isPlaying) {
            btnIniciaMusica.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24)
        } else {
            btnIniciaMusica.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
        }

        if(shuffleOn){
            btnShuffleMusic.setImageResource(R.drawable.ic_baseline_shuffle_clicked_24)
        }else{
            btnShuffleMusic.setImageResource(R.drawable.ic_baseline_shuffle_24)
        }

        if(repeatMusic){
            btnRepeatMusic.setImageResource(R.drawable.ic_baseline_repeat_clicked)
        }else{
            btnRepeatMusic.setImageResource(R.drawable.ic_baseline_repeat_24)
        }
    }

    private fun initToolbar(title : String) {
        toolbar.title = title
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun clickPlayer() {
        if(mediaPlayer.isPlaying){
            mediaPlayer.pause()
            showNotifications(R.drawable.ic_baseline_play_circle_filled_24)
        }else{
            val uri = Uri.parse(listaMusicas[index].assetFileStringUri)
            mediaPlayer = MediaPlayer.create(baseContext, uri)
            mediaPlayer.seekTo(tempoPause*1000)
            mediaPlayer.start()
            showNotifications(R.drawable.ic_baseline_pause_circle_filled_24)
        }

        mMainViewModel.atualizarDadosMusica()
        trocarIconeIniciar()
    }
    override fun nextMusic() {
        if(!repeatMusic){
            if(shuffleOn){
                index = (0 until listaMusicas.size).random()
            }else{
                index++

                if(index >= listaMusicas.size){
                    index = 0
                }
            }
        }

        if(mediaPlayer.isPlaying){
            mediaPlayer.pause()
            mediaPlayer.release()
        }

        val uri = Uri.parse(listaMusicas[index].assetFileStringUri)
        mediaPlayer = MediaPlayer.create(this, uri)
        mediaPlayer.start()
        showNotifications(R.drawable.ic_baseline_pause_circle_filled_24)

        mMainViewModel.atualizarDadosMusica()
        trocarIconeIniciar()
    }
    override fun previousMusic() {
        if(!repeatMusic){
            if(shuffleOn){
                index = (0 until listaMusicas.size).random()
            }else{
                index++

                if(index < 0){
                    index = 0
                }
            }
        }


        if(mediaPlayer.isPlaying){
            mediaPlayer.pause()
            mediaPlayer.release()
        }

        val uri = Uri.parse(listaMusicas[index].assetFileStringUri)
        mediaPlayer = MediaPlayer.create(this, uri)
        mediaPlayer.start()
        showNotifications(R.drawable.ic_baseline_pause_circle_filled_24)

        mMainViewModel.atualizarDadosMusica()
        trocarIconeIniciar()
    }

    @SuppressLint("UseCompatLoadingForDrawables", "WrongConstant")
    fun showNotifications(playPauseBtn : Int){
        val customNotificationStyle = RemoteViews(packageName, R.layout.custom_notification)
        val musicaTocando = listaMusicas[index]

        val intent = Intent(this, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ACTIVITY_CLEAR_TOP)

        val prevIntent = Intent(this, NotificationReceiver::class.java).setAction(MusicApplication.ACTION_PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(this, NotificationReceiver::class.java).setAction(MusicApplication.ACTION_NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent(this, NotificationReceiver::class.java).setAction(MusicApplication.ACTION_PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val bitmap: Bitmap?
        bitmap = try {
            val picture = listaMusicas[index].art_uri
            MediaStore.Images.Media.getBitmap(this.contentResolver, picture)
        }catch (e : Exception){
            val defaultPicture : Drawable? = getDrawable(R.drawable.img_music)
            val bitmapDraw = defaultPicture?.toBitmap()
            bitmapDraw
        }

        customNotificationStyle.setTextViewText(R.id.txtTitleMusic, musicaTocando.title)
        customNotificationStyle.setTextViewText(R.id.txtTitleArtist, musicaTocando.artist)
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
        notificationManager.notify(0, notification)
        startService(Intent(this, CloseNotification::class.java))
    }

    companion object {
        var mediaPlayer = MediaPlayer()
    }

    override fun onResume() {
        super.onResume()
        mMainViewModel.atualizarDadosMusica()
    }
    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
        Log.e("conexao", "coneceted")
    }
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.getService()
        musicService!!.setCallback(this)
        Log.e("conexao", "coneceted")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
                return false
            }
            else -> return false
        }
    }
}