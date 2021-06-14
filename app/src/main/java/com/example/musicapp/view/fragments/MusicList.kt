package com.example.musicapp.view.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.databinding.FragmentMusicListBinding
import com.example.musicapp.model.adapter.MusicAdapter
import com.example.musicapp.view.MusicPlayer
import com.example.musicapp.model.singleton.MusicSingleton
import com.google.gson.Gson
import com.example.musicapp.view.MenuInicial
import com.example.musicapp.view.MenuInicial.Companion.SHARED_LIST_MUSIC_ACTIVE
import com.example.musicapp.view.MenuInicial.Companion.SHARED_MUSIC_ACTIVE

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MusicList.newInstance] factory method to
 * create an instance of this fragment.
 */
class MusicList : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var SHARED_PREFERENCES_MUSIC: SharedPreferences
    lateinit var SHARED_PREFERENCES_MUSIC_EDITOR: SharedPreferences.Editor

    lateinit var adapter : MusicAdapter

    private lateinit var binding : FragmentMusicListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMusicListBinding.inflate(inflater, container, false)
        val view = binding.root

        try {
            SHARED_PREFERENCES_MUSIC = requireActivity().getSharedPreferences(
                MenuInicial.SHARED_MAIN,
                AppCompatActivity.MODE_PRIVATE
            )
            SHARED_PREFERENCES_MUSIC_EDITOR = SHARED_PREFERENCES_MUSIC.edit()

            initRecyclerView()
        }catch(e: Exception){
            Toast.makeText(this.context, "$e", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun favoriteMusic(it : Int){
        MusicSingleton.listaMusicas[it].favoritarMusic()

        val gson = Gson()
        val json = gson.toJson(MusicSingleton.listaMusicas)

        SHARED_PREFERENCES_MUSIC_EDITOR.putString(MenuInicial.SHARED_LIST_MUSIC, json).apply()
    }
    private fun onClickMusic(it: Int) {
        val gson = Gson()
        val jsonPlaylist = gson.toJson(MusicSingleton.listaMusicas)

        val intent = Intent(this.context, MusicPlayer::class.java)
        SHARED_PREFERENCES_MUSIC_EDITOR.putString(SHARED_LIST_MUSIC_ACTIVE, jsonPlaylist).commit()
        SHARED_PREFERENCES_MUSIC_EDITOR.putInt(SHARED_MUSIC_ACTIVE, it).commit()
        startActivity(intent)
    }

    private fun initRecyclerView() {
        adapter = MusicAdapter(requireActivity(), {onClickMusic(it)}, { favoriteMusic(it) }, MusicSingleton.listaMusicas)
        binding.recyclerViewMusics.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerViewMusics.adapter = adapter
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MusicList.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MusicList().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}