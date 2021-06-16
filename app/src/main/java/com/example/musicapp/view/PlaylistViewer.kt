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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.databinding.ActivityPlaylistViewerBinding
import com.example.musicapp.model.PlaylistMusic
import com.example.musicapp.model.adapter.MusicPlaylistAdapter
import com.example.musicapp.model.singleton.MusicSingleton
import com.example.musicapp.model.singleton.MusicSingleton.playlistMusicas
import com.google.gson.Gson
import java.util.*

class PlaylistViewer : AppCompatActivity() {


    lateinit var adapter : MusicPlaylistAdapter

    lateinit var SHARED_PREFERENCES_MUSIC: SharedPreferences
    lateinit var SHARED_PREFERENCES_MUSIC_EDITOR: SharedPreferences.Editor

    lateinit var playlistSelecionada : PlaylistMusic

    private lateinit var binding : ActivityPlaylistViewerBinding
    private lateinit var itemTouchHelper: ItemTouchHelper
    private var indexPlaylist : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistViewerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        SHARED_PREFERENCES_MUSIC = getSharedPreferences(
            MenuInicial.SHARED_MAIN,
            MODE_PRIVATE
        )
        SHARED_PREFERENCES_MUSIC_EDITOR = SHARED_PREFERENCES_MUSIC.edit()

        itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewMusics)

        initDados()
    }


    private fun initDados() {
        indexPlaylist = intent.getIntExtra("INDEX-PLAYLIST", 0)
        playlistSelecionada = playlistMusicas[indexPlaylist ?: 0]

        initToolbar("Playlist")
        val artUri : Uri? = Uri.parse(playlistSelecionada.playlist[0].imagem)
        binding.imgPlaylist.setImageURI(artUri)
        binding.txtNomePlaylist.text = playlistSelecionada.nomePlaylist


        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapter = MusicPlaylistAdapter(this, playlistSelecionada.playlist, { onSelectMusic(it) })
        binding.recyclerViewMusics.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMusics.adapter = adapter
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
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)
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

    private val simpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END, 0){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition

            Collections.swap(playlistSelecionada.playlist, fromPosition, toPosition)
            recyclerView.adapter?.notifyItemMoved(toPosition, fromPosition)


            playlistMusicas[indexPlaylist ?: 0] = playlistSelecionada
            val gson = Gson()
            val jsonPlaylists = gson.toJson(playlistMusicas)

            SHARED_PREFERENCES_MUSIC_EDITOR.putString(MenuInicial.SHARED_PLAYLISTS, jsonPlaylists).commit()

            playlistSelecionada = playlistMusicas[indexPlaylist ?: 0]
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        }
    }
}