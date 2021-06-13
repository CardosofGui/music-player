package com.example.musicapp.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicapp.model.Music
import com.example.musicapp.model.singleton.MusicSingleton

class MainViewModel : ViewModel() {
    private lateinit var listaMusica : ArrayList<Music>

    var nomeArtista = MutableLiveData<String>().apply { value = "Nome Artista" }
    var nomeMusica = MutableLiveData<String>().apply { value = "Nome Musica" }
    var musicaDuration = MutableLiveData<Int>().apply { value = 0 }
    var fotoMusica = MutableLiveData<Uri?>()

    fun atualizarDadosMusica(){
        nomeArtista.value = listaMusica[MusicSingleton.index].nomeArtista
        nomeMusica.value = listaMusica[MusicSingleton.index].nomeMusica
        fotoMusica.value = Uri.parse(listaMusica[MusicSingleton.index].imagem)
        musicaDuration.value = listaMusica[MusicSingleton.index].duration.toInt()
    }
    fun setListaMusica(playlist : ArrayList<Music>){
        this.listaMusica = playlist
    }
}