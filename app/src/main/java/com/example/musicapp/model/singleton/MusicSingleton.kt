package com.example.musicapp.model.singleton

import com.example.musicapp.model.Music
import com.example.musicapp.model.PlaylistMusic

object MusicSingleton{
    var listaMusicas : ArrayList<Music> = ArrayList<Music>()
    var playlistAtual : ArrayList<Music> = ArrayList<Music>()

    var playlistMusicas : ArrayList<PlaylistMusic> = ArrayList()


    var index = 0

    var tempoPause = 0
    var shuffleOn = false
    var repeatMusic = false
}