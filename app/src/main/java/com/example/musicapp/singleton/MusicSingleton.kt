package com.example.musicapp.singleton

import com.example.musicapp.model.Music

object MusicSingleton{
    var listaMusicas : MutableList<Music> = mutableListOf()
    var musicaTocando : Boolean = false
}