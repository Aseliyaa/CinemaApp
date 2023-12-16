package com.example.cinemaapp.activity


import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cinemaapp.adapter.GenresListDetailAdapter

import com.example.cinemaapp.adapter.ImageListAdapter
import com.example.cinemaapp.api.ApiService
import com.example.cinemaapp.api.RequestsOperator.Companion.sendDetailsRequest
import com.example.cinemaapp.databinding.ActivityDetailBinding
import com.example.cinemaapp.db.AppDatabase
import com.example.cinemaapp.db.FilmItemDao
import com.example.cinemaapp.domain.FilmItem
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : AppCompatActivity() {
    private lateinit var titleTxt: TextView
    private lateinit var movieRateTxt: TextView
    private lateinit var movieTimeTxt: TextView
    private lateinit var movieDateTxt: TextView
    private lateinit var movieSummaryInfo: TextView
    private lateinit var movieActorInfo: TextView
    private lateinit var genresRecyclerView: RecyclerView
    private var idFilm: Int = 0
    private lateinit var posterNormalImg: ShapeableImageView
    private lateinit var posterBigImg: ImageView
    private lateinit var backImg: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var scrollView: ScrollView
    private lateinit var likeBtn: ImageView
    private lateinit var item: FilmItem

    private lateinit var binding: ActivityDetailBinding
    private lateinit var db: AppDatabase
    private lateinit var filmItemDao: FilmItemDao

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setContentView(binding.root)

        initUserAndFilmId()
        initView()

        lifecycleScope.launch {
            initDb()
            updateLikeBtnColor(idFilm)
            loadData()
            likeItem()
        }
    }

    private fun updateLikeBtnColor(id: Int?) {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                if (isContain(id))
                    likeBtn.setColorFilter(Color.RED)
                else
                    likeBtn.setColorFilter(Color.WHITE)
            }
        }
    }

    private fun likeItem() {
        likeBtn.setOnClickListener {

            lifecycleScope.launch {
                val isContained = isContain(item.id)

                val toastMessage = if (!isContained) {
                    item.userId = userId
                    withContext(Dispatchers.Default) {
                        filmItemDao.addFilm(item)
                    }
                    "Added to Favorites!"
                } else {
                    withContext(Dispatchers.Default) {
                        filmItemDao.deleteFilm(item.id, userId)
                        filmItemDao.updateFilmsUid(item.uid)
                    }
                    "Deleted from Favorites!"
                }

                updateLikeBtnColor(item.id)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        applicationContext,
                        toastMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private suspend fun isContain(id: Int?): Boolean {
        return withContext(Dispatchers.Default) {
            val films = filmItemDao.getById(userId)
            films.any { it.id == id }
        }
    }

    private fun loadData() {
        progressBar.visibility = View.VISIBLE
        scrollView.visibility = View.GONE

        lifecycleScope.launch {
            try {
                item = sendDetailsRequest(idFilm)

                progressBar.visibility = View.GONE
                scrollView.visibility = View.VISIBLE

                posterNormalImg.load(item.poster)
                posterBigImg.load(item.poster)
                titleTxt.text = item.title
                movieRateTxt.text = item.imdbRating
                movieTimeTxt.text = item.runtime
                movieDateTxt.text = item.released
                movieSummaryInfo.text = item.plot
                movieActorInfo.text = item.actors
                recyclerView.adapter = ImageListAdapter(item.images)
                genresRecyclerView.adapter = item.genres?.let { GenresListDetailAdapter(it) }

            } catch (_: Exception) {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun initView() {
        titleTxt = binding.movieNameTxt
        movieRateTxt = binding.movieRateTxt
        movieTimeTxt = binding.movieTimeTxt
        movieDateTxt = binding.movieDateTxt
        movieSummaryInfo = binding.movieSummaryInfo
        movieActorInfo = binding.movieActorInfo
        posterNormalImg = binding.posterNormalImg
        posterBigImg = binding.posterBigImg
        backImg = binding.backImg
        progressBar = binding.detailLoading
        scrollView = binding.scrollView3
        recyclerView = binding.imageRecyclerView
        likeBtn = binding.likeBtn
        genresRecyclerView = binding.genresRecyclerView

        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        genresRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        backImg.setOnClickListener {
            finish()
        }
    }

    private suspend fun initDb() {
        withContext(Dispatchers.Default) {
            db = AppDatabase.initDb(applicationContext)
            filmItemDao = db.filmItemDao()
        }
    }

    private fun initUserAndFilmId() {
        userId = intent.getStringExtra("userId").toString()
        idFilm = intent.getIntExtra("filmId", 0)
    }

    private fun initBinding() {
        binding = ActivityDetailBinding.inflate(layoutInflater)
    }
}