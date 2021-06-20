package com.example.musicapp.view.fragments

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.databinding.FragmentPlaylistListBinding
import com.example.musicapp.model.PlaylistMusic
import com.example.musicapp.model.adapter.PlaylistAdapter
import com.example.musicapp.model.singleton.MusicSingleton
import com.example.musicapp.model.singleton.MusicSingleton.playlistMusicas
import com.example.musicapp.view.MenuInicial
import com.example.musicapp.view.MusicPlayer
import com.example.musicapp.view.PlaylistForm
import com.example.musicapp.view.PlaylistViewer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlaylistList.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlaylistList : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var adapter : PlaylistAdapter

    lateinit var SHARED_PREFERENCES_MUSIC: SharedPreferences
    lateinit var SHARED_PREFERENCES_MUSIC_EDITOR: SharedPreferences.Editor

    private lateinit var binding : FragmentPlaylistListBinding

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

        binding = FragmentPlaylistListBinding.inflate(inflater, container, false)
        val view = binding.root

        SHARED_PREFERENCES_MUSIC = requireActivity().getSharedPreferences(
            MenuInicial.SHARED_MAIN,
            AppCompatActivity.MODE_PRIVATE
        )
        SHARED_PREFERENCES_MUSIC_EDITOR = SHARED_PREFERENCES_MUSIC.edit()

        initClick()
        initRecyclerView()

        return view
    }

    private fun initRecyclerView() {
        adapter = PlaylistAdapter(requireContext(), MusicSingleton.playlistMusicas, {onClickPlaylist(it)}, {onPressPlaylist(it)}) 
        binding.recyclerViewPlaylist.layoutManager = GridLayoutManager(this.context, 2)
        binding.recyclerViewPlaylist.adapter = adapter
    }

    private fun onPressPlaylist(it: Int) {
        showDialog(it)
    }

    private fun initClick() {
        binding.btnCriarPlaylist.setOnClickListener {
            startActivity(Intent(this.context, PlaylistForm::class.java))
        }
    }

    private fun onClickPlaylist(index : Int) {
        val intent = Intent(this.context, PlaylistViewer::class.java)
        intent.putExtra("INDEX-PLAYLIST", index)

        startActivity(intent)
    }

    private fun showDialog(index: Int) {
        val playlist = playlistMusicas[index]

        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.edit_popup)

        val edtNomePlaylist = dialog.findViewById<EditText>(R.id.edtNomePlaylist)
        val edtDescPlaylist = dialog.findViewById<EditText>(R.id.edtDescPlaylist)
        val btnCancelar = dialog.findViewById<Button>(R.id.btnCancelar)
        val btnEditar = dialog.findViewById<Button>(R.id.btnEditar)
        val btnExcluir = dialog.findViewById<Button>(R.id.btnExcluir)

        edtNomePlaylist.setText(playlist.nomePlaylist)
        edtDescPlaylist.setText(playlist.descPlaylist)

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }
        btnEditar.setOnClickListener {
            if(edtNomePlaylist.text.toString().isNullOrEmpty() || edtDescPlaylist.text.toString().isNullOrEmpty()){
                Toast.makeText(context, "Erro - Todos os campos devem ser preenchidos", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val nomePlaylist = edtNomePlaylist.text.toString()
            val descPlaylist = edtDescPlaylist.text.toString()

            playlistMusicas[index].nomePlaylist = nomePlaylist
            playlistMusicas[index].descPlaylist = descPlaylist

            val jsonPlaylists = Gson().toJson(playlistMusicas)
            SHARED_PREFERENCES_MUSIC_EDITOR.putString(MenuInicial.SHARED_PLAYLISTS, jsonPlaylists).commit()
            adapter.notifyDataSetChanged()
            dialog.dismiss()
        }
        btnExcluir.setOnClickListener {
            playlistMusicas.removeAt(index)
            val jsonPlaylists = Gson().toJson(playlistMusicas)
            SHARED_PREFERENCES_MUSIC_EDITOR.putString(MenuInicial.SHARED_PLAYLISTS, jsonPlaylists).commit()
            adapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlaylistList.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlaylistList().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}