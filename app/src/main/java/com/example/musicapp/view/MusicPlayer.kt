package com.example.musicapp.view

import android.content.*
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.musicapp.services.CloseNotification
import com.example.musicapp.services.MusicService
import com.example.musicapp.R
import com.example.musicapp.databinding.ActivityMainBinding
import com.example.musicapp.view.MenuInicial.Companion.SHARED_LIST_MUSIC_ACTIVE
import com.example.musicapp.view.MenuInicial.Companion.SHARED_MUSIC_ACTIVE
import com.example.musicapp.viewmodel.MainViewModel
import com.example.musicapp.model.Music
import com.example.musicapp.model.singleton.MusicSingleton.index
import com.example.musicapp.model.singleton.MusicSingleton.playlistAtual
import com.example.musicapp.model.singleton.MusicSingleton.repeatMusic
import com.example.musicapp.model.singleton.MusicSingleton.shuffleOn
import com.example.musicapp.model.singleton.MusicSingleton.tempoPause
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MusicPlayer : AppCompatActivity(), ServiceConnection {

    lateinit var mMainViewModel: MainViewModel
    lateinit var SHARED_PREFERENCES_MUSIC: SharedPreferences
    lateinit var SHARED_PREFERENCES_MUSIC_EDITOR: SharedPreferences.Editor

    var handler: Handler = Handler()
    var musicService: MusicService? = null

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
        startService(Intent(this, CloseNotification::class.java))
        startService(Intent(this, MusicService::class.java))

        SHARED_PREFERENCES_MUSIC = getSharedPreferences(
            MenuInicial.SHARED_MAIN,
            MODE_PRIVATE
        )
        SHARED_PREFERENCES_MUSIC_EDITOR = SHARED_PREFERENCES_MUSIC.edit()

        mMainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        initDados()
        setClicks()
        initToolbar("Music Player")

        runOnUiThread(object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val curretPosition = mediaPlayer.currentPosition / 1000
                    binding.seekMusicDuration.progress = curretPosition
                    binding.txtTimeRunning.text = formatarTime(curretPosition)

                    mediaPlayer.setOnCompletionListener {
                        if (!it.isPlaying) {
                            musicService?.nextMusic()
                            mMainViewModel.trocarMusica()
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

            musicService?.clickPlayer()
        }

        mMainViewModel.setDados(playlistAtual)
        mMainViewModel.musicaSelecionada.observe(this, Observer {
            binding.txtArtista.text = it.nomeArtista
            binding.txtMusica.text = it.nomeMusica

            binding.imageMusic.setImageURI(Uri.parse(it.imagem))
            if(binding.imageMusic.drawable == null) binding.imageMusic.setImageResource(R.drawable.img_music)

            binding.seekMusicDuration.max = (it.duration / 1000).toInt()
            binding.txtTimeFinish.text = formatarTime((it.duration / 1000).toInt())
        })

        trocarIconeIniciar()

        binding.seekMusicDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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


        binding.btnIniciaMusica.setOnClickListener {
            musicService?.clickPlayer()
            mMainViewModel.trocarMusica()
            trocarIconeIniciar()
        }

        binding.btnProximaMusica.setOnClickListener {

            animationTransition(animationTransitionRight){
                musicService?.nextMusic()
                mMainViewModel.trocarMusica()
                trocarIconeIniciar()
            }
        }

        binding.btnVoltarMusica.setOnClickListener {

            animationTransition(animationTransitionLeft){
                musicService?.previousMusic()
                mMainViewModel.trocarMusica()
                trocarIconeIniciar()
            }
        }

        binding.btnShuffleMusic.setOnClickListener {
            shuffleOn = !shuffleOn
            trocarIconeIniciar()
        }

        binding.btnRepeatMusic.setOnClickListener {
            repeatMusic = !repeatMusic
            trocarIconeIniciar()
        }
    }
    private fun animationTransition(
        animationTransition: Animation,
        function: () -> Unit
    ) {
        binding.btnProximaMusica.isClickable = false
        binding.btnVoltarMusica.isClickable = false
        binding.btnIniciaMusica.isClickable = false

        binding.llnPlayer.startAnimation(animationTransition)

        animationTransition.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
                TODO("Not yet implemented")
            }

            override fun onAnimationEnd(animation: Animation?) {
                function()
                binding.btnProximaMusica.isClickable = true
                binding.btnVoltarMusica.isClickable = true
                binding.btnIniciaMusica.isClickable = true
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
            binding.btnIniciaMusica.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24)
        } else {
            binding.btnIniciaMusica.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
        }

        if(shuffleOn){
            binding.btnShuffleMusic.setImageResource(R.drawable.ic_baseline_shuffle_clicked_24)
        }else{
            binding.btnShuffleMusic.setImageResource(R.drawable.ic_baseline_shuffle_24)
        }

        if(repeatMusic){
            binding.btnRepeatMusic.setImageResource(R.drawable.ic_baseline_repeat_clicked)
        }else{
            binding.btnRepeatMusic.setImageResource(R.drawable.ic_baseline_repeat_24)
        }
    }
    private fun initToolbar(title : String) {
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        mMainViewModel.trocarMusica()
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

    companion object {
        var mediaPlayer = MediaPlayer()
    }
}