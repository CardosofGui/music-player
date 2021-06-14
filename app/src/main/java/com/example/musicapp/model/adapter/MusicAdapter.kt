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
import com.example.musicapp.databinding.CardMusicBinding
import com.example.musicapp.model.Music

class MusicAdapter(
    val context: Context,
    val onClickMusic: ((Int) -> Unit),
    val onFavoriteMusic: ((Int) -> Unit),
    val listaMusicas : ArrayList<Music>
) : RecyclerView.Adapter<musicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): musicViewHolder {
        val binding = CardMusicBinding.inflate(LayoutInflater.from(context), parent, false)
        return musicViewHolder(binding)
    }

    override fun getItemCount(): Int = listaMusicas.size

    override fun onBindViewHolder(holder: musicViewHolder, position: Int) {
        val music = listaMusicas[position]
        val binding = holder.binding

        try {
            val artUri : Uri? = Uri.parse(listaMusicas[position].imagem)
            binding.imgMusicCard.setImageURI(artUri)
        }catch (e : Exception){
            Toast.makeText(context, "$e", Toast.LENGTH_LONG).show()
        }

        if(binding.imgMusicCard.drawable == null) binding.imgMusicCard.setImageResource(R.drawable.img_music)

        val animation = AnimationUtils.loadAnimation(context, R.anim.animation)

        binding.txtNomeMusicaCard.text = music.nomeMusica
        binding.txtNomeArtistaCard.text = music.nomeArtista

        binding.cardMusic.setOnClickListener {
            binding.cardMusic.startAnimation(animation)
            onClickMusic(position)
        }

        binding.btnFavorite.setOnClickListener {
            if(!music.favorito){
                binding.btnFavorite.setImageResource(R.drawable.ic_baseline_star_24)
            }else{
                binding.btnFavorite.setImageResource(R.drawable.ic_baseline_star_border_24)
            }

            onFavoriteMusic(music.position ?: 0)
        }

        if(music.favorito){
            binding.btnFavorite.setImageResource(R.drawable.ic_baseline_star_24)
        }
    }

}

class musicViewHolder(val binding : CardMusicBinding) : RecyclerView.ViewHolder(binding.root)
