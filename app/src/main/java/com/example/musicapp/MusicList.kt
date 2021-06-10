package com.example.musicapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.features.list_music.adapter.MusicAdapter
import com.example.musicapp.features.music.MainActivity
import com.example.musicapp.singleton.MusicSingleton
import com.google.gson.Gson
import com.example.musicapp.features.list_music.List_Musics

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

    lateinit var recyclerViewMusics : RecyclerView

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

        val view = inflater.inflate(R.layout.fragment_music_list, container, false)

        SHARED_PREFERENCES_MUSIC = requireActivity().getSharedPreferences(
            List_Musics.SHARED_MAIN,
            AppCompatActivity.MODE_PRIVATE
        )
        SHARED_PREFERENCES_MUSIC_EDITOR = SHARED_PREFERENCES_MUSIC.edit()

        recyclerViewMusics = view.findViewById(R.id.recyclerViewMusics)

        initRecyclerView()
        return view
    }

    fun favoriteMusic(it : Int){
        MusicSingleton.listaMusicas[it].favoritarMusic()

        val gson = Gson()
        val json = gson.toJson(MusicSingleton.listaMusicas)

        SHARED_PREFERENCES_MUSIC_EDITOR.putString(List_Musics.SHARED_LIST_MUSIC, json).apply()
    }

    private fun onClickMusic(it: Int) {
        val gson = Gson()
        val jsonPlaylist = gson.toJson(MusicSingleton.listaMusicas)

        val intent = Intent(this.context, MainActivity::class.java)
        intent.putExtra("MUSICA_SELECIONADA", it)
        intent.putExtra("PLAYLIST-SELECIONADA", jsonPlaylist)
        startActivity(intent)
    }

    private fun initRecyclerView() {
        adapter = MusicAdapter(requireActivity(), {onClickMusic(it)}, { favoriteMusic(it) }, MusicSingleton.listaMusicas)
        recyclerViewMusics.layoutManager = LinearLayoutManager(this.context)
        recyclerViewMusics.adapter = adapter
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