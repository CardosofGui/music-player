package com.example.musicapp.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicapp.model.Music
import com.example.musicapp.model.singleton.MusicSingleton
import com.example.musicapp.model.singleton.MusicSingleton.index

class MainViewModel : ViewModel() {
    private lateinit var listaMusica : ArrayList<Music>

    var musicaSelecionada = MutableLiveData<Music>()

    fun setDados(playlist : ArrayList<Music>){
        this.listaMusica = playlist
        musicaSelecionada.value = listaMusica[index]
    }
    fun trocarMusica(){
        musicaSelecionada.value = listaMusica[index]
    }
}