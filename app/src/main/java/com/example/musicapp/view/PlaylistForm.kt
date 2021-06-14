package com.example.musicapp.view

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beardedhen.androidbootstrap.BootstrapButton
import com.beardedhen.androidbootstrap.BootstrapEditText
import com.example.musicapp.R
import com.example.musicapp.databinding.ActivityPlaylistFormBinding
import com.example.musicapp.model.Music
import com.example.musicapp.model.PlaylistMusic
import com.example.musicapp.model.adapter.MusicPlaylistAdapter
import com.example.musicapp.model.singleton.MusicSingleton
import com.example.musicapp.view.MenuInicial.Companion.SHARED_PLAYLISTS
import com.google.gson.Gson

class PlaylistForm : AppCompatActivity() {

    private val playlistCriada = ArrayList<Music>()

    private lateinit var SHARED_PREFERENCES_MUSIC: SharedPreferences
    private lateinit var SHARED_PREFERENCES_MUSIC_EDITOR: SharedPreferences.Editor

    lateinit var adapter : MusicPlaylistAdapter

    private lateinit var binding : ActivityPlaylistFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistFormBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        SHARED_PREFERENCES_MUSIC = getSharedPreferences(
            MenuInicial.SHARED_MAIN,
            MODE_PRIVATE
        )
        SHARED_PREFERENCES_MUSIC_EDITOR = SHARED_PREFERENCES_MUSIC.edit()

        initToolbar("Criar Playlist")
        initRecyclerView()
        initClick()
    }

    private fun initClick() {
        binding.btnCriarPlaylist.setOnClickListener {
            if(binding.edtNome.text.toString().isNullOrEmpty()){
                Toast.makeText(this, "Digite um nome para sua playlist", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(binding.edtDesc.text.toString().isNullOrEmpty()){
                Toast.makeText(this, "Digite uma descrição para sua playlist", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(playlistCriada.size <= 0){
                Toast.makeText(this, "Selecione ao menos 1 música para a playlist", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val nome = binding.edtNome.text.toString()
            val desc = binding.edtDesc.text.toString()

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
        adapter = MusicPlaylistAdapter(this, MusicSingleton.listaMusicas, { onSelectMusic(it) }, true)
        binding.recyclerViewMusicPlaylist.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMusicPlaylist.adapter = adapter
    }
    private fun initToolbar(title : String) {
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)
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
                finish()
                return false
            }
            else -> return false
        }
    }
}