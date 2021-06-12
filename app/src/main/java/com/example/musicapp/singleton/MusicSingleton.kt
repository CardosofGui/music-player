package com.example.musicapp.singleton

import com.CodeBoy.MediaFacer.mediaHolders.audioContent
import com.example.musicapp.model.Music

object MusicSingleton{
    var listaMusicas : ArrayList<Music> = ArrayList<Music>()
    var playlistAtual : ArrayList<Music> = ArrayList<Music>()


    var index = 0

    var tempoPause = 0
    var shuffleOn = false
    var repeatMusic = false
}