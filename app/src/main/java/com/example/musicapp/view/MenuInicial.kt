package com.example.musicapp.view

import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.CodeBoy.MediaFacer.AudioGet
import com.CodeBoy.MediaFacer.MediaFacer
import com.example.musicapp.R
import com.example.musicapp.model.Music
import com.example.musicapp.model.PlaylistMusic
import com.example.musicapp.model.singleton.MusicSingleton
import com.example.musicapp.model.singleton.MusicSingleton.listaMusicas
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MenuInicial : AppCompatActivity() {

    lateinit var SHARED_PREFERENCES_MUSIC: SharedPreferences
    lateinit var SHARED_PREFERENCES_MUSIC_EDITOR: SharedPreferences.Editor

    var myPermissionRequest = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list__musics)

        try {
            SHARED_PREFERENCES_MUSIC = getSharedPreferences(SHARED_MAIN, MODE_PRIVATE)
            SHARED_PREFERENCES_MUSIC_EDITOR = SHARED_PREFERENCES_MUSIC.edit()

            verificarPermissao()

            val navController = findNavController(R.id.fragment_container)
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

            bottomNav.setupWithNavController(navController)
        }catch(e : Exception){
            Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()
        }
    }

    fun getMusic() {
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

        val removeDuplicates = listaMusicas.distinctBy { it.diretorio }
        listaMusicas = removeDuplicates as ArrayList<Music>

        val gson = Gson()
        val listMusics = SHARED_PREFERENCES_MUSIC.getString(SHARED_LIST_MUSIC, "")


        if(!listMusics.isNullOrEmpty()){
            val verificarFavoritos = gson.fromJson<ArrayList<Music>>(listMusics, object : TypeToken<ArrayList<Music>>(){}.type)


            listaMusicas.forEachIndexed { index, music ->
                if(music.diretorio == verificarFavoritos[index].diretorio && verificarFavoritos[index].favorito){
                    music.favoritarMusic()
                }
            }
        }

        val playlistsJson = SHARED_PREFERENCES_MUSIC.getString(SHARED_PLAYLISTS, "")

        if(playlistsJson != ""){
            val playlistsArray = gson.fromJson<ArrayList<PlaylistMusic>>(playlistsJson, object : TypeToken<ArrayList<PlaylistMusic>>(){}.type)
            MusicSingleton.playlistMusicas = playlistsArray
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

    companion object{
        const val SHARED_MAIN = "MUSICS"
        const val SHARED_LIST_MUSIC = "LIST_MUSICS"

        const val SHARED_LIST_MUSIC_ACTIVE = "LIST_MUSIC_ACTIVE"
        const val SHARED_MUSIC_ACTIVE = "MUSIC_ACTIVE"

        const val SHARED_PLAYLISTS = "SHARED_PLAYLISTS"
    }
}