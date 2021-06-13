package com.example.musicapp.model.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.model.PlaylistMusic
import kotlinx.android.synthetic.main.card_music.view.*
import kotlinx.android.synthetic.main.card_music.view.imgMusicCard
import kotlinx.android.synthetic.main.card_playlist.view.*

class PlaylistAdapter(
    val context : Context,
    val listaPlaylists : ArrayList<PlaylistMusic>,
    val onClickPlaylist : ((Int) -> Unit)
) : RecyclerView.Adapter<PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun getItemCount(): Int = listaPlaylists.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlistSelecionada = listaPlaylists[position]
        val view = holder.itemView
        val animation = AnimationUtils.loadAnimation(context, R.anim.animation)

        val artUri : Uri? = Uri.parse(listaPlaylists[position].playlist[0].imagem)
        view.imgMusicCard.setImageURI(artUri)

        view.txtNomePlaylist.text = playlistSelecionada.nomePlaylist
        view.txtDescPlaylist.text = playlistSelecionada.descPlaylist
        view.txtQuantidadeMusic.text = "${playlistSelecionada.playlist.size} Músicas"

        view.cardPlaylist.setOnClickListener {
            view.cardPlaylist.startAnimation(animation)
            onClickPlaylist(position)
        }
    }
}

class PlaylistViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
