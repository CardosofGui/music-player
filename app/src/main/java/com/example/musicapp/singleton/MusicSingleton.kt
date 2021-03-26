package com.example.musicapp.singleton

import com.CodeBoy.MediaFacer.mediaHolders.audioContent
import com.example.musicapp.model.Music

object MusicSingleton{
    var listaMusicas : ArrayList<audioContent> = ArrayList<audioContent>()
    var index = 0
}