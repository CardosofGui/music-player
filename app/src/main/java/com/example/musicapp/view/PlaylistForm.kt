package com.example.musicapp.view

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beardedhen.androidbootstrap.BootstrapButton
import com.beardedhen.androidbootstrap.BootstrapEditText
import com.example.musicapp.R
import com.example.musicapp.model.Music
import com.example.musicapp.model.PlaylistMusic
import com.example.musicapp.model.adapter.MusicAddPlaylistAdapter
import com.example.musicapp.model.singleton.MusicSingleton
import com.example.musicapp.view.MenuInicial.Companion.SHARED_PLAYLISTS
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_playlist_form.*

class PlaylistForm : AppCompatActivity() {

    private val playlistCriada = ArrayList<Music>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar : Toolbar
    private lateinit var btnCriarPlaylist : BootstrapButton
    private lateinit var edtNome : BootstrapEditText
    private lateinit var edtDesc : BootstrapEditText

    private lateinit var SHARED_PREFERENCES_MUSIC: SharedPreferences
    private lateinit var SHARED_PREFERENCES_MUSIC_EDITOR: SharedPreferences.Editor


    lateinit var adapter : MusicAddPlaylistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_form)

        SHARED_PREFERENCES_MUSIC = getSharedPreferences(
            MenuInicial.SHARED_MAIN,
            MODE_PRIVATE
        )
        SHARED_PREFERENCES_MUSIC_EDITOR = SHARED_PREFERENCES_MUSIC.edit()

        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.recyclerViewMusicPlaylist)
        btnCriarPlaylist = findViewById(R.id.btnCriarPlaylist)
        edtNome = findViewById(R.id.edtNome)
        edtDesc = findViewById(R.id.edtDesc)

        initToolbar("Criar Playlist")
        initRecyclerView()
        initClick()
    }

    private fun initClick() {
        btnCriarPlaylist.setOnClickListener {
            val nome = edtNome.text.toString()
            val desc = edtDesc.text.toString()

            val playlist = PlaylistMusic(
                nome,
                desc,
                playlistCriada
            )

            MusicSingleton.playlistMusicas.add(playlist)
            val gson = Gson()
            val jsonPlaylists = gson.toJson(MusicSingleton.playlistMusicas)

            SHARED_PREFERENCES_MUSIC_EDITOR.putString(SHARED_PLAYLISTS, jsonPlaylists).commit()
            onBackPressed()
            finish()
        }
    }

    private fun initRecyclerView() {
        adapter = MusicAddPlaylistAdapter(this, MusicSingleton.listaMusicas) { onSelectMusic(it) }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
    private fun initToolbar(title : String) {
        toolbar.title = title
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    private fun onSelectMusic(index : Int){
        val musicSelecionada = MusicSingleton.listaMusicas[index]

        if(playlistCriada.contains(musicSelecionada)){
            playlistCriada.remove(musicSelecionada)
        }else{
            playlistCriada.add(musicSelecionada)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
                return false
            }
            else -> return false
        }
    }
}