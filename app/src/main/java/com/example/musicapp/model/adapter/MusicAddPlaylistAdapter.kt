package com.example.musicapp.model.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.model.Music
import kotlinx.android.synthetic.main.card_music.view.*
import kotlinx.android.synthetic.main.card_music.view.imgMusicCard
import kotlinx.android.synthetic.main.card_music.view.txtNomeArtistaCard
import kotlinx.android.synthetic.main.card_music.view.txtNomeMusicaCard
import kotlinx.android.synthetic.main.card_music_add_playlist.view.*

class MusicAddPlaylistAdapter(
    val context : Context,
    val listaMusicas : ArrayList<Music>,
    val onSelectMusic : ((Int) -> Unit)
) : RecyclerView.Adapter<MusicAddViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAddViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_music_add_playlist, parent, false)
        return MusicAddViewHolder(view)
    }

    override fun getItemCount(): Int = listaMusicas.size

    override fun onBindViewHolder(holder: MusicAddViewHolder, position: Int) {
        val music = listaMusicas[position]
        val view = holder.itemView

        val artUri : Uri? = Uri.parse(listaMusicas[position].imagem)
        view.imgMusicCard.setImageURI(artUri)
        if(view.imgMusicCard.drawable == null) view.imgMusicCard.setImageResource(R.drawable.img_music)
        view.txtNomeMusicaCard.text = music.nomeMusica
        view.txtNomeArtistaCard.text = music.nomeArtista

        view.cardSelectMusic.setOnClickListener {
            if(view.btnSelectMusic.visibility == View.GONE){
                view.btnSelectMusic.visibility = View.VISIBLE
            }else{
                view.btnSelectMusic.visibility = View.GONE
            }

            onSelectMusic(position)
        }
    }

}

class MusicAddViewHolder(view : View) : RecyclerView.ViewHolder(view)
