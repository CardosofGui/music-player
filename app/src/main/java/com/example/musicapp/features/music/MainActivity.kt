package com.example.musicapp.features.music

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.musicapp.R
import com.example.musicapp.singleton.MusicSingleton
import kotlinx.android.synthetic.main.activity_list__musics.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar


class MainActivity : AppCompatActivity() {

    lateinit var mMainViewModel: MainViewModel
    var handler: Handler = Handler()
    var tempoPause: Int = 0
    var musicaEstado = MUSICA_NAO_INICIADA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                            mMainViewModel.proximaMusica()
                            musicaEstado = TROCAR_MUSICA
                            iniciarMusica(mMainViewModel.index)
                        }
                    }
                }
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun initDados() {
        mMainViewModel.index = intent.getIntExtra("MUSICA_SELECIONADA", 0)

        mMainViewModel.nomeArtista.observe(this, Observer {
            txtArtista.text = it
        })
        mMainViewModel.nomeMusica.observe(this, Observer {
            txtMusica.text = it
        })
        mMainViewModel.fotoMusica.observe(this, Observer {
            if (it != null) {
                Glide.with(this).asBitmap().load(it).into(imageMusic)
            } else {
                Glide.with(this).load(R.drawable.img_music).into(imageMusic)
            }
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
            iniciarMusica(mMainViewModel.index)
            btnIniciaMusica.startAnimation(animationClickStart)
        }

        btnProximaMusica.setOnClickListener {

            animationTransition(animationTransitionRight) {
                musicaEstado = TROCAR_MUSICA
                mMainViewModel.proximaMusica()
                iniciarMusica(mMainViewModel.index)
            }
        }

        btnVoltarMusica.setOnClickListener {

            animationTransition(animationTransitionLeft) {
                musicaEstado = TROCAR_MUSICA
                mMainViewModel.musicaAnterior()
                iniciarMusica(mMainViewModel.index)
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
            }

            override fun onAnimationStart(animation: Animation?) {

            }
        })
    }

    private fun iniciarMusica(index: Int) {
        if (musicaEstado == MUSICA_NAO_INICIADA || musicaEstado == TROCAR_MUSICA) {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                mediaPlayer.release()
            }

            val uri = Uri.parse(MusicSingleton.listaMusicas[index].diretorio)
            mediaPlayer = MediaPlayer.create(this, uri)
            mediaPlayer.start()
            musicaEstado = MUSICA_INICIADA
            MusicSingleton.musicaTocando = true
        } else if (musicaEstado == MUSICA_INICIADA) {
            mediaPlayer.pause()
            tempoPause = mediaPlayer.currentPosition
            musicaEstado = MUSICA_PAUSADA
            MusicSingleton.musicaTocando = false
        } else if (musicaEstado == MUSICA_PAUSADA) {
            mediaPlayer.seekTo(tempoPause)
            mediaPlayer.start()
            musicaEstado = MUSICA_INICIADA
            MusicSingleton.musicaTocando = true
        }

        trocarIconeIniciar()
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
        if (MusicSingleton.musicaTocando) {
            btnIniciaMusica.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24)
        } else {
            btnIniciaMusica.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
        }
    }

    private fun initToolbar(title : String) {
        toolbar.title = title
        setSupportActionBar(toolbar)
    }

    companion object {
        var mediaPlayer: MediaPlayer = MediaPlayer()

        const val MUSICA_INICIADA = "MUSICA_INICIADA"
        const val MUSICA_NAO_INICIADA = "MUSICA_NAO_INICIADA"
        const val MUSICA_PAUSADA = "MUSICA_PAUSADA"
        const val TROCAR_MUSICA = "TROCAR_MUSICA"
    }
}