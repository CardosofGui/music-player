package com.example.musicapp.singleton

import com.CodeBoy.MediaFacer.mediaHolders.audioContent
import com.example.musicapp.model.Music

object MusicSingleton{
    var listaMusicas : ArrayList<audioContent> = ArrayList<audioContent>()
    var index = 0
    var tempoPause = 0
    var shuffleOn = false
    var repeatMusic = false
}