package com.example.cinemaapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.cinemaapp.adapter.LikedMoviesAdapter
import com.example.cinemaapp.databinding.ActivityLikedMoviesBinding
import com.example.cinemaapp.db.AppDatabase
import com.example.cinemaapp.db.FilmItemDao
import com.example.cinemaapp.domain.FilmItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LikedMovies : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var db: AppDatabase
    private lateinit var filmItemDao: FilmItemDao
    private lateinit var films: List<FilmItem>
    private lateinit var userId: String
    private lateinit var homeBtn: ImageView
    private lateinit var personalPageBtn: ImageView
    private lateinit var binding: ActivityLikedMoviesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setContentView(binding.root)

        initUserId()
        initDb()
        initView()
        getFilms()
        btnManager()
    }

    private fun btnManager() {
        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        personalPageBtn.setOnClickListener {
            val intent = Intent(this, PersonalPageActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
    }

    private fun getFilms() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                films = filmItemDao.getById(userId)
            }
            withContext(Dispatchers.Main) {
                recyclerView.adapter = LikedMoviesAdapter(films, userId)
            }
        }
    }

    private fun initView() {
        homeBtn = binding.homePageBtn
        personalPageBtn = binding.personalPageBtn
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2)
    }

    private fun initDb() {
        db = AppDatabase.initDb(applicationContext)
        filmItemDao = db.filmItemDao()
    }


    private fun initUserId() {
        userId = intent.getStringExtra("userId").toString()
    }

    private fun initBinding() {
        binding = ActivityLikedMoviesBinding.inflate(layoutInflater)
    }

    override fun onResume() {
        super.onResume()
        getFilms()
    }
}