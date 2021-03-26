package com.example.musicapp.features.music

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
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
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.musicapp.features.music.services.CloseNotification
import com.example.musicapp.features.music.services.MusicService
import com.example.musicapp.R
import com.example.musicapp.application.MusicApplication.Companion.ACTION_NEXT
import com.example.musicapp.application.MusicApplication.Companion.ACTION_PLAY
import com.example.musicapp.application.MusicApplication.Companion.ACTION_PREVIOUS
import com.example.musicapp.application.MusicApplication.Companion.CHANNEL_ID_2
import com.example.musicapp.features.list_music.List_Musics
import com.example.musicapp.features.music.services.NotificationReceiver
import com.example.musicapp.features.music.viewModel.MainViewModel
import com.example.musicapp.model.ActionClick
import com.example.musicapp.singleton.MusicSingleton
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception


class MainActivity : AppCompatActivity(), ServiceConnection,
    ActionClick {

    lateinit var mMainViewModel: MainViewModel
    var handler: Handler = Handler()
    var tempoPause: Int = 0
    lateinit var mediaSession : MediaSessionCompat
    var musicService: MusicService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaSession = MediaSessionCompat(this, "PlayerAudio")
        mMainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
        startService(Intent(this, CloseNotification::class.java))

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
        MusicSingleton.index = intent.getIntExtra("MUSICA_SELECIONADA", MusicSingleton.index)

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
            btnIniciaMusica.startAnimation(animationClickStart)
        }

        btnProximaMusica.setOnClickListener {

            animationTransition(animationTransitionRight) {
                nextMusic()
            }
        }

        btnVoltarMusica.setOnClickListener {

            animationTransition(animationTransitionLeft) {
                previousMusic()
            }
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
                mMainViewModel.atualizarDadosMusica()
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
    }

    private fun initToolbar(title : String) {
        toolbar.title = title
        setSupportActionBar(toolbar)
    }

    @SuppressLint("UseCompatLoadingForDrawables", "WrongConstant")
    fun showNotifications(playPauseBtn : Int){
        val intent = Intent(this, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val prevIntent = Intent(this, NotificationReceiver::class.java).setAction(ACTION_PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(this, NotificationReceiver::class.java).setAction(ACTION_NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent(this, NotificationReceiver::class.java).setAction(ACTION_PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val bitmap: Bitmap?
        bitmap = try {
            val picture = MusicSingleton.listaMusicas[MusicSingleton.index].art_uri
            MediaStore.Images.Media.getBitmap(this.contentResolver, picture)
        }catch (e : Exception){
            val defaultPicture : Drawable? = getDrawable(R.drawable.img_music)
            val bitmapDraw = defaultPicture?.toBitmap()
            bitmapDraw
        }


        val notification = NotificationCompat.Builder(this, CHANNEL_ID_2)
            .setSmallIcon(playPauseBtn)
            .setLargeIcon(bitmap)
            .setContentTitle(MusicSingleton.listaMusicas[MusicSingleton.index].title)
            .setContentText(MusicSingleton.listaMusicas[MusicSingleton.index].artist)
            .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", prevPendingIntent)
            .addAction(playPauseBtn, "Play", playPendingIntent)
            .addAction(R.drawable.ic_baseline_skip_next_24, "Next", nextPendingIntent)
            .setFullScreenIntent(prevPendingIntent, true)
            .setFullScreenIntent(playPendingIntent, true)
            .setFullScreenIntent(nextPendingIntent, true)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentIntent)
            .setOnlyAlertOnce(true)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .build()

        val notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)
    }

    companion object {
        var mediaPlayer = MediaPlayer()
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

    override fun clickPlayer() {
        if(mediaPlayer.isPlaying){
            mediaPlayer.pause()
            showNotifications(R.drawable.ic_baseline_play_circle_filled_24)
        }else{

            val uri = Uri.parse(MusicSingleton.listaMusicas[MusicSingleton.index].assetFileStringUri)
            mediaPlayer = MediaPlayer.create(baseContext, uri)
            mediaPlayer.seekTo(tempoPause*1000)
            mediaPlayer.start()
            showNotifications(R.drawable.ic_baseline_pause_circle_filled_24)

        }

        trocarIconeIniciar()
        mMainViewModel.atualizarDadosMusica()
    }

    override fun nextMusic() {
        MusicSingleton.index++

        if(MusicSingleton.index >= MusicSingleton.listaMusicas.size){
            MusicSingleton.index = 0
        }

        if(mediaPlayer.isPlaying){
            mediaPlayer.pause()
            mediaPlayer.release()
        }

        val uri = Uri.parse(MusicSingleton.listaMusicas[MusicSingleton.index].assetFileStringUri)
        mediaPlayer = MediaPlayer.create(this, uri)
        mediaPlayer.start()

        trocarIconeIniciar()
        showNotifications(R.drawable.ic_baseline_pause_circle_filled_24)
        mMainViewModel.atualizarDadosMusica()
    }

    override fun previousMusic() {

        MusicSingleton.index--

        if(MusicSingleton.index < 0){
            MusicSingleton.index = MusicSingleton.listaMusicas.size-1
        }

        if(mediaPlayer.isPlaying){
            mediaPlayer.pause()
            mediaPlayer.release()
        }

        val uri = Uri.parse(MusicSingleton.listaMusicas[MusicSingleton.index].assetFileStringUri)
        mediaPlayer = MediaPlayer.create(this, uri)
        mediaPlayer.start()


            trocarIconeIniciar()
            showNotifications(R.drawable.ic_baseline_pause_circle_filled_24)
            mMainViewModel.atualizarDadosMusica()

    }

    override fun onResume() {
        super.onResume()

        mMainViewModel.atualizarDadosMusica()
    }
}