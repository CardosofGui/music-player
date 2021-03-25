package com.example.musicapp.features.list_music

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.R
import com.example.musicapp.features.list_music.adapter.MusicAdapter
import com.example.musicapp.features.music.MainActivity
import com.example.musicapp.model.Music
import com.example.musicapp.singleton.MusicSingleton
import kotlinx.android.synthetic.main.activity_list__musics.*
import java.lang.Exception

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

    // Recupera as musicas
    fun getMusic(){
        val contentResolver = contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver.query(uri, null, null, null, null)

        if(cursor != null && cursor.moveToFirst()){
            val songTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songLocation = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val songDuration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)

            do {
                val currentTitle = cursor.getString(songTitle) ?: "erro"
                val currentArtist = cursor.getString(songArtist) ?: "erro"
                val currentSongLocation = cursor.getString(songLocation) ?: "erro"
                val currentSongDuration = cursor.getString(songDuration) ?: "erro"

                if(currentTitle != "erro" && currentArtist != "erro" && currentSongLocation != "erro" && currentSongDuration != "erro"){
                    val musica = Music(
                        currentTitle,
                        currentArtist,
                        currentSongLocation,
                        currentSongDuration
                    )

                    MusicSingleton.listaMusicas.add(musica)
                }
            } while(cursor.moveToNext())
        }

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
                                getMusic()

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
                getMusic()
            }
        }catch (e : Exception){
            Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()
        }
    }
}