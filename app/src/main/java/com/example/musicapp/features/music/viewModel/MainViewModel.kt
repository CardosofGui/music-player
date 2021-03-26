package com.example.musicapp.features.music.viewModel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicapp.singleton.MusicSingleton

class MainViewModel : ViewModel() {
    var nomeArtista = MutableLiveData<String>().apply { value = "Nome Artista" }
    var nomeMusica = MutableLiveData<String>().apply { value = "Nome Musica" }
    var musicaDuration = MutableLiveData<Int>().apply { value = 0 }
    var fotoMusica = MutableLiveData<Uri?>()

    fun atualizarDadosMusica(){
        nomeArtista.value = MusicSingleton.listaMusicas[MusicSingleton.index].artist
        nomeMusica.value = MusicSingleton.listaMusicas[MusicSingleton.index].title
        fotoMusica.value = MusicSingleton.listaMusicas[MusicSingleton.index].art_uri
        musicaDuration.value = MusicSingleton.listaMusicas[MusicSingleton.index].duration.toInt()
    }
}