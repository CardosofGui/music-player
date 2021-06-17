package com.example.musicapp.view

import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.CodeBoy.MediaFacer.AudioGet
import com.CodeBoy.MediaFacer.MediaFacer
import com.example.musicapp.R
import com.example.musicapp.databinding.ActivityListMusicsBinding
import com.example.musicapp.model.Music
import com.example.musicapp.model.PlaylistMusic
import com.example.musicapp.model.singleton.MusicSingleton
import com.example.musicapp.model.singleton.MusicSingleton.listaMusicas
import com.example.musicapp.model.singleton.MusicSingleton.playlistMusicas
import com.example.musicapp.view.fragments.FavoriteList
import com.example.musicapp.view.fragments.MusicList
import com.example.musicapp.view.fragments.PlaylistList
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MenuInicial : AppCompatActivity() {

    lateinit var SHARED_PREFERENCES_MUSIC: SharedPreferences
    lateinit var SHARED_PREFERENCES_MUSIC_EDITOR: SharedPreferences.Editor

    var myPermissionRequest = 1

    private lateinit var binding : ActivityListMusicsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListMusicsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        SHARED_PREFERENCES_MUSIC = getSharedPreferences(SHARED_MAIN, MODE_PRIVATE)
        SHARED_PREFERENCES_MUSIC_EDITOR = SHARED_PREFERENCES_MUSIC.edit()

        initToolbar("Músicas")
        verificarPermissao()

        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.musicList->{
                    switchFragment(MusicList())
                    initToolbar("Músicas")
                    true
                }
                R.id.favoriteList->{
                    switchFragment(FavoriteList())
                    initToolbar("Favoritas")
                    true
                }
                R.id.playlistList->{
                    switchFragment(PlaylistList())
                    initToolbar("Playlists")
                    true
                }
                else->false
            }
        }
    }

    private fun getMusic() {
        if(listaMusicas.size > 0) listaMusicas.clear()

        MediaFacer
            .withAudioContex(this)
            .getAllAudioContent(AudioGet.externalContentUri).forEachIndexed { index, music ->
                listaMusicas.add(
                    Music(
                        music.title,
                        music.artist,
                        music.assetFileStringUri,
                        music.duration,
                        false,
                        music.art_uri.toString(),
                        index
                    )
                )
            }

        val gson = Gson()
        val listMusics = SHARED_PREFERENCES_MUSIC.getString(SHARED_LIST_MUSIC, "")
        val playlistsJson = SHARED_PREFERENCES_MUSIC.getString(SHARED_PLAYLISTS, "")

        if(playlistsJson != ""){
            val playlistsArray = gson.fromJson<ArrayList<PlaylistMusic>>(playlistsJson, object : TypeToken<ArrayList<PlaylistMusic>>(){}.type)
            playlistMusicas = playlistsArray
        }

        if(!listMusics.isNullOrEmpty()){
            val verificarFavoritos = gson.fromJson<ArrayList<Music>>(listMusics, object : TypeToken<ArrayList<Music>>(){}.type)


            verificarFavoritos.forEach { musicSelecionada ->
                var musicaExistente = false

                listaMusicas.forEachIndexed { index, music ->
                    if(musicSelecionada.diretorio == music.diretorio) {
                        musicaExistente = true
                        listaMusicas[index] = musicSelecionada
                    }
                }

                playlistMusicas.forEach {
                    it.playlist.forEach { musicPlaylist ->
                        if(musicPlaylist.diretorio == musicSelecionada.diretorio && !musicaExistente) it.playlist.remove(musicPlaylist)
                    }
                }

                if(!musicaExistente){
                    verificarFavoritos.remove(musicSelecionada)

                    val jsonPlaylists = gson.toJson(playlistMusicas)
                    SHARED_PREFERENCES_MUSIC_EDITOR.putString(SHARED_PLAYLISTS, jsonPlaylists).commit()
                    val json = gson.toJson(listaMusicas)
                    SHARED_PREFERENCES_MUSIC_EDITOR.putString(SHARED_LIST_MUSIC, json).commit()
                }
            }
        }
    }

    // Exibe o request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            when(requestCode){
                myPermissionRequest -> {
                    if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                                Toast.makeText(this, "Permissao aceita", Toast.LENGTH_SHORT).show()
                            try {
                                getMusic()
                            }catch (e : Exception){
                                Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            finish()
                        }
                        return
                    }
                }
            }
        }catch (e : Exception){
            Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()
        }
    }
    private fun verificarPermissao(){
        // Verifica se existe a permissão para acessar Store
        try {
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), myPermissionRequest)
                }else{
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), myPermissionRequest)
                }
            }else{
                Toast.makeText(this, "Permissao aceita", Toast.LENGTH_SHORT).show()
                try {
                    getMusic()

                }catch (e : Exception){
                    Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e : Exception){
            Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()
        }
    }

    private fun switchFragment(fragment : Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun initToolbar(title : String) {
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)
    }

    companion object{
        const val SHARED_MAIN = "MUSICS"
        const val SHARED_LIST_MUSIC = "LIST_MUSICS"

        const val SHARED_LIST_MUSIC_ACTIVE = "LIST_MUSIC_ACTIVE"
        const val SHARED_MUSIC_ACTIVE = "MUSIC_ACTIVE"

        const val SHARED_PLAYLISTS = "SHARED_PLAYLISTS"
    }
}