package com.example.musicapp.features.music

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
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
import com.example.musicapp.features.list_music.List_Musics.Companion.SHARED_LIST_MUSIC_ACTIVE
import com.example.musicapp.features.list_music.List_Musics.Companion.SHARED_MUSIC_ACTIVE
import com.example.musicapp.features.music.services.NotificationReceiver
import com.example.musicapp.features.music.viewModel.MainViewModel
import com.example.musicapp.model.Music
import com.example.musicapp.singleton.MusicSingleton.index
import com.example.musicapp.singleton.MusicSingleton.playlistAtual
import com.example.musicapp.singleton.MusicSingleton.repeatMusic
import com.example.musicapp.singleton.MusicSingleton.shuffleOn
import com.example.musicapp.singleton.MusicSingleton.tempoPause
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception


class MainActivity : AppCompatActivity(), ServiceConnection {

    lateinit var mMainViewModel: MainViewModel
    lateinit var SHARED_PREFERENCES_MUSIC: SharedPreferences
    lateinit var SHARED_PREFERENCES_MUSIC_EDITOR: SharedPreferences.Editor

    var handler: Handler = Handler()
    var musicService: MusicService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startService(Intent(this, CloseNotification::class.java))
        startService(Intent(this, MusicService::class.java))

        SHARED_PREFERENCES_MUSIC = getSharedPreferences(
            List_Musics.SHARED_MAIN,
            MODE_PRIVATE
        )
        SHARED_PREFERENCES_MUSIC_EDITOR = SHARED_PREFERENCES_MUSIC.edit()


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
                            musicService?.nextMusic()
                            mMainViewModel.atualizarDadosMusica()
                            trocarIconeIniciar()
                        }
                    }
                }
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun initDados() {
        val gson = Gson()
        playlistAtual = gson.fromJson(SHARED_PREFERENCES_MUSIC.getString(SHARED_LIST_MUSIC_ACTIVE, ""), object : TypeToken<ArrayList<Music>>(){}.type)

        if(index != SHARED_PREFERENCES_MUSIC.getInt(SHARED_MUSIC_ACTIVE, -1)){
            index = SHARED_PREFERENCES_MUSIC.getInt(SHARED_MUSIC_ACTIVE, -1)
        }

        mMainViewModel.setListaMusica(playlistAtual)
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
        val animationTransitionRight =
            AnimationUtils.loadAnimation(this, R.anim.animation_transition_right)
        val animationTransitionLeft =
            AnimationUtils.loadAnimation(this, R.anim.animation_transition_left)


        btnIniciaMusica.setOnClickListener {
            musicService?.clickPlayer()
            mMainViewModel.atualizarDadosMusica()
            trocarIconeIniciar()
        }

        btnProximaMusica.setOnClickListener {

            animationTransition(animationTransitionRight){
                musicService?.nextMusic()
                mMainViewModel.atualizarDadosMusica()
                trocarIconeIniciar()
            }
        }

        btnVoltarMusica.setOnClickListener {

            animationTransition(animationTransitionLeft){
                musicService?.previousMusic()
                mMainViewModel.atualizarDadosMusica()
                trocarIconeIniciar()
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