package com.example.musicapp.model

import android.net.Uri

class Music(
    val nomeMusica: String,
    val nomeArtista: String,
    val diretorio: String,
    val duration: Long,
    var favorito: Boolean,
    val imagem: String,
    val position: Int
){
    fun favoritarMusic(){
        favorito = !favorito
    }
}

