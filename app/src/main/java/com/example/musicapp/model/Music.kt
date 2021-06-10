package com.example.musicapp.model

import android.net.Uri

class Music(
    val nomeMusica: String,
    val nomeArtista: String,
    val diretorio: String,
    val duration: Long,
    var favorito: Boolean,
    val imagem: String
){
    var position : Int? = null

    fun favoritarMusic(){
        favorito = !favorito
    }
    fun setPosition(index : Int){
        this.position = index
    }
}

