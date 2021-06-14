package com.example.musicapp.model.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.databinding.CardPlaylistBinding
import com.example.musicapp.model.PlaylistMusic

class PlaylistAdapter(
    val context : Context,
    val listaPlaylists : ArrayList<PlaylistMusic>,
    val onClickPlaylist : ((Int) -> Unit)
) : RecyclerView.Adapter<PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = CardPlaylistBinding.inflate(LayoutInflater.from(context), parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun getItemCount(): Int = listaPlaylists.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlistSelecionada = listaPlaylists[position]
        val binding = holder.binding
        val animation = AnimationUtils.loadAnimation(context, R.anim.animation)

        val artUri : Uri? = Uri.parse(listaPlaylists[position].playlist[0].imagem)
        binding.imgMusicCard.setImageURI(artUri)

        binding.txtNomePlaylist.text = playlistSelecionada.nomePlaylist
        binding.txtDescPlaylist.text = playlistSelecionada.descPlaylist
        binding.txtQuantidadeMusic.text = "${playlistSelecionada.playlist.size} Músicas"

        binding.cardPlaylist.setOnClickListener {
            binding.cardPlaylist.startAnimation(animation)
            onClickPlaylist(position)
        }
    }
}

class PlaylistViewHolder(val binding : CardPlaylistBinding) : RecyclerView.ViewHolder(binding.root)
