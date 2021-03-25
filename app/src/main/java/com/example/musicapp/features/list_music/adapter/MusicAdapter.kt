package com.example.musicapp.features.list_music.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicapp.R
import com.example.musicapp.features.music.MainViewModel.Companion.getImage
import com.example.musicapp.model.Music
import com.example.musicapp.singleton.MusicSingleton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.card_music.view.*

class MusicAdapter(
    val context: Context,
    val onClickMusic: ((Int) -> Unit)
) : RecyclerView.Adapter<musicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): musicViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_music, parent, false)
        return musicViewHolder(view)
    }

    override fun getItemCount(): Int = MusicSingleton.listaMusicas.size

    override fun onBindViewHolder(holder: musicViewHolder, position: Int) {
        val music = MusicSingleton.listaMusicas[position]
        val view = holder.itemView
        val img = getImage(Uri.parse(music.diretorio))
        val animation = AnimationUtils.loadAnimation(context, R.anim.animation)

        view.txtNomeMusicaCard.text = music.nomeMusica
        view.txtNomeArtistaCard.text = music.nomeArtista

        if (img != null) {
            Glide.with(context).asBitmap().load(img).into(view.imgMusicCard)
        } else {
            Glide.with(context).load(R.drawable.img_music).into(view.imgMusicCard)
        }

        view.cardMusic.setOnClickListener {
            view.cardMusic.startAnimation(animation)
            onClickMusic(position)
        }

        view.btnFavorite.setOnClickListener {
            view.btnFavorite.setImageResource(R.drawable.ic_baseline_star_24)
        }
    }

}

class musicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
