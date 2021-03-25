package com.example.musicapp.features.music

import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicapp.singleton.MusicSingleton

class MainViewModel : ViewModel() {
    var nomeArtista = MutableLiveData<String>().apply { value = "Nome Artista" }
    var nomeMusica = MutableLiveData<String>().apply { value = "Nome Musica" }
    var musicaDuration = MutableLiveData<Int>().apply { value = 0 }
    var fotoMusica = MutableLiveData<ByteArray?>()
    var index = 0

    fun atualizarDadosMusica(){
        nomeArtista.value = MusicSingleton.listaMusicas[index].nomeArtista
        nomeMusica.value = MusicSingleton.listaMusicas[index].nomeMusica
        fotoMusica.value = getImage(Uri.parse(MusicSingleton.listaMusicas[index].diretorio))
        musicaDuration.value = MusicSingleton.listaMusicas[index].duration.toInt()
    }

    fun proximaMusica(){
        index++
        atualizarDadosMusica()
    }

    fun musicaAnterior(){
        index--
        atualizarDadosMusica()
    }

    companion object{
        fun getImage(uri: Uri) : ByteArray? {
            val retrieve = MediaMetadataRetriever()
            retrieve.setDataSource(uri.toString())
            val image = retrieve.embeddedPicture
            retrieve.release()
            return image
        }
    }
}