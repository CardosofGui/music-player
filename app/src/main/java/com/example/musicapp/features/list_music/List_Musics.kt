package com.example.musicapp.features.list_music

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.CodeBoy.MediaFacer.AudioGet
import com.CodeBoy.MediaFacer.MediaFacer
import com.CodeBoy.MediaFacer.mediaHolders.audioContent
import com.example.musicapp.R
import com.example.musicapp.application.MusicApplication
import com.example.musicapp.features.list_music.adapter.MusicAdapter
import com.example.musicapp.features.music.MainActivity
import com.example.musicapp.features.music.services.MusicService
import com.example.musicapp.features.music.services.NotificationReceiver
import com.example.musicapp.features.music.viewModel.MainViewModel
import com.example.musicapp.model.ActionClick
import com.example.musicapp.model.Music
import com.example.musicapp.singleton.MusicSingleton
import com.example.musicapp.singleton.MusicSingleton.listaMusicas
import com.example.musicapp.singleton.MusicSingleton.tempoPause
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_list__musics.*
import org.json.JSONArray
import java.io.File
import java.util.LinkedHashSet

class List_Musics : AppCompatActivity() {

    lateinit var SHARED_PREFERENCES_MUSIC: SharedPreferences
    lateinit var SHARED_PREFERENCES_MUSIC_EDITOR: SharedPreferences.Editor

    var myPermissionRequest = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list__musics)

        SHARED_PREFERENCES_MUSIC = getSharedPreferences(SHARED_MAIN, MODE_PRIVATE)
        SHARED_PREFERENCES_MUSIC_EDITOR = SHARED_PREFERENCES_MUSIC.edit()

        verificarPermissao()

        val navController = findNavController(R.id.fragment_container)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.setupWithNavController(navController)
        //initToolbar("Músicas")
    }

    /*
    private fun initToolbar(title : String) {
        toolbar.title = title
        setSupportActionBar(toolbar)
    }
     */

    fun getMusic2() {
        MediaFacer
            .withAudioContex(this)
            .getAllAudioContent(AudioGet.externalContentUri).forEach {
                listaMusicas.add(
                    Music(
                        it.title,
                        it.artist,
                        it.assetFileStringUri,
                        it.duration,
                        false,
                        it.art_uri.toString()
                    )
                )
            }

        val removeDuplicates = listaMusicas.distinctBy { it.diretorio }
        listaMusicas = removeDuplicates as ArrayList<Music>

        val gson = Gson()
        val listMusics = SHARED_PREFERENCES_MUSIC.getString(SHARED_LIST_MUSIC, "")


        if(!listMusics.isNullOrEmpty()){
            val verificarFavoritos = gson.fromJson<ArrayList<Music>>(listMusics, object : TypeToken<ArrayList<Music>>(){}.type)

            var index = 0

            listaMusicas.forEach {
                if(it.diretorio == verificarFavoritos[index].diretorio && verificarFavoritos[index].favorito){
                    it.favoritarMusic()
                }
                index++
            }
        }
    }

    // Exibe o request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        try {
            when(requestCode){
                myPermissionRequest -> {
                    if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                                Toast.makeText(this, "Permissao aceita", Toast.LENGTH_SHORT).show()
                            try {
                                getMusic2()

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
                    getMusic2()

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
    }
}