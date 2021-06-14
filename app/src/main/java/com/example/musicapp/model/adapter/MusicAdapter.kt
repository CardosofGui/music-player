package com.example.musicapp.model.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.model.Music
import kotlinx.android.synthetic.main.card_music.view.*

class MusicAdapter(
    val context: Context,
    val onClickMusic: ((Int) -> Unit),
    val onFavoriteMusic: ((Int) -> Unit),
    val listaMusicas : ArrayList<Music>
) : RecyclerView.Adapter<musicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): musicViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_music, parent, false)
        return musicViewHolder(view)
    }

    override fun getItemCount(): Int = listaMusicas.size

    override fun onBindViewHolder(holder: musicViewHolder, position: Int) {
        val music = listaMusicas[position]
        val view = holder.itemView

        try {
            val artUri : Uri? = Uri.parse(listaMusicas[position].imagem)
            view.imgMusicCard.setImageURI(artUri)
        }catch (e : Exception){
            Toast.makeText(context, "$e", Toast.LENGTH_LONG).show()
        }

        if(view.imgMusicCard.drawable == null) view.imgMusicCard.setImageResource(R.drawable.img_music)

        val animation = AnimationUtils.loadAnimation(context, R.anim.animation)

        view.txtNomeMusicaCard.text = music.nomeMusica
        view.txtNomeArtistaCard.text = music.nomeArtista

        view.cardMusic.setOnClickListener {
            view.cardMusic.startAnimation(animation)
            onClickMusic(position)
        }

        view.btnFavorite.setOnClickListener {
            if(!music.favorito){
                view.btnFavorite.setImageResource(R.drawable.ic_baseline_star_24)
            }else{
                view.btnFavorite.setImageResource(R.drawable.ic_baseline_star_border_24)
            }

            onFavoriteMusic(music.position ?: 0)
        }

        if(music.favorito){
            view.btnFavorite.setImageResource(R.drawable.ic_baseline_star_24)
        }
    }

}

class musicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
