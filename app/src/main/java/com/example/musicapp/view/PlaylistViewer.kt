package com.example.musicapp.view

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.model.PlaylistMusic
import com.example.musicapp.model.adapter.MusicPlaylistAdapter
import com.example.musicapp.model.singleton.MusicSingleton
import com.example.musicapp.model.singleton.MusicSingleton.playlistMusicas
import com.google.gson.Gson
import kotlinx.android.synthetic.main.card_music.view.*

class PlaylistViewer : AppCompatActivity() {

    private lateinit var recyclerView : RecyclerView
    private lateinit var imagePlaylist : ImageView
    private lateinit var nomePlaylist : TextView
    private lateinit var descPlaylist : TextView
    private lateinit var contagemMusicas : TextView
    private lateinit var toolbar : Toolbar

    lateinit var adapter : MusicPlaylistAdapter

    lateinit var SHARED_PREFERENCES_MUSIC: SharedPreferences
    lateinit var SHARED_PREFERENCES_MUSIC_EDITOR: SharedPreferences.Editor

    lateinit var playlistSelecionada : PlaylistMusic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_viewer)

        SHARED_PREFERENCES_MUSIC = getSharedPreferences(
            MenuInicial.SHARED_MAIN,
            MODE_PRIVATE
        )
        SHARED_PREFERENCES_MUSIC_EDITOR = SHARED_PREFERENCES_MUSIC.edit()

        recyclerView = findViewById(R.id.recyclerViewMusics)
        imagePlaylist = findViewById(R.id.imgPlaylist)
        nomePlaylist = findViewById(R.id.txtNomePlaylist)
        toolbar = findViewById(R.id.toolbar)

        initDados()
    }

    private fun initDados() {
        val indexPlaylist = intent.getIntExtra("INDEX-PLAYLIST", 0)
        playlistSelecionada = playlistMusicas[indexPlaylist]

        initToolbar("Playlist")
        val artUri : Uri? = Uri.parse(playlistSelecionada.playlist[0].imagem)
        imagePlaylist.setImageURI(artUri)
        nomePlaylist.text = playlistSelecionada.nomePlaylist



        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapter = MusicPlaylistAdapter(this, playlistSelecionada.playlist, { onSelectMusic(it) })
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun onSelectMusic(it: Int) {
        val gson = Gson()
        val jsonPlaylist = gson.toJson(playlistSelecionada.playlist)

        val intent = Intent(this, MusicPlayer::class.java)
        SHARED_PREFERENCES_MUSIC_EDITOR.putString(MenuInicial.SHARED_LIST_MUSIC_ACTIVE, jsonPlaylist).commit()
        SHARED_PREFERENCES_MUSIC_EDITOR.putInt(MenuInicial.SHARED_MUSIC_ACTIVE, it).commit()
        startActivity(intent)
    }

    private fun initToolbar(title : String) {
        toolbar.title = title
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
                finish()
                return false
            }
            else -> return false
        }
    }
}