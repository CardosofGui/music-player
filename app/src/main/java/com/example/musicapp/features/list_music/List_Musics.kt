package com.example.musicapp.features.list_music

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
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
import com.example.musicapp.singleton.MusicSingleton.tempoPause
import kotlinx.android.synthetic.main.activity_list__musics.*
import java.io.File
import java.util.LinkedHashSet

class List_Musics : AppCompatActivity() {

    var myPermissionRequest = 1

    lateinit var adapter : MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list__musics)


        verificarPermissao()
        initToolbar("Músicas")
    }

    private fun initToolbar(title : String) {
        toolbar.title = title
        setSupportActionBar(toolbar)
    }

    private fun initRecyclerView() {
        adapter = MusicAdapter(this) { onClickMusic(it) }
        recyclerViewMusics.layoutManager = LinearLayoutManager(this)
        recyclerViewMusics.adapter = adapter
    }

    private fun onClickMusic(it: Int) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("MUSICA_SELECIONADA", it)
        startActivity(intent)
    }

    fun getMusic2(){
        MusicSingleton.listaMusicas = MediaFacer
            .withAudioContex(this)
            .getAllAudioContent(AudioGet.externalContentUri)

        val removeDuplicates = MusicSingleton.listaMusicas.distinctBy { it.title }
        MusicSingleton.listaMusicas = removeDuplicates as ArrayList<audioContent>

        initRecyclerView()
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




}