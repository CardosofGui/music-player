package com.example.musicapp.model.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.databinding.CardMusicAddPlaylistBinding
import com.example.musicapp.model.Music


class MusicPlaylistAdapter(
    val context : Context,
    val listaMusicas : ArrayList<Music>,
    val onSelectMusic : ((Int) -> Unit),
    var addMusic : Boolean = false
) : RecyclerView.Adapter<MusicPlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicPlaylistViewHolder {
        val binding = CardMusicAddPlaylistBinding.inflate(LayoutInflater.from(context), parent, false)
        return MusicPlaylistViewHolder(binding)
    }

    override fun getItemCount(): Int = listaMusicas.size

    override fun onBindViewHolder(holder: MusicPlaylistViewHolder, position: Int) {
        val music = listaMusicas[position]
        val binding = holder.binding
        val animation = AnimationUtils.loadAnimation(context, R.anim.animation)

        val artUri : Uri? = Uri.parse(listaMusicas[position].imagem)
        binding.imgMusicCard.setImageURI(artUri)
        if(binding.imgMusicCard.drawable == null) binding.imgMusicCard.setImageResource(R.drawable.img_music)
        binding.txtNomeMusicaCard.text = music.nomeMusica
        binding.txtNomeArtistaCard.text = music.nomeArtista

        binding.cardSelectMusic.setOnClickListener {
            if(addMusic){
                if(binding.btnSelectMusic.visibility == View.GONE){
                    binding.btnSelectMusic.visibility = View.VISIBLE
                }else{
                    binding.btnSelectMusic.visibility = View.GONE
                }
            }else{
                binding.cardSelectMusic.startAnimation(animation)
            }

            onSelectMusic(position)
        }
    }

}

class MusicPlaylistViewHolder(val binding : CardMusicAddPlaylistBinding) : RecyclerView.ViewHolder(binding.root)
