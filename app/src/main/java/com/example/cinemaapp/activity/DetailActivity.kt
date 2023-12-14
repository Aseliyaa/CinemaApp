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
import com.example.cinemaapp.adapter.GenresListAdapter
import com.example.cinemaapp.adapter.GenresListDetailAdapter

import com.example.cinemaapp.adapter.ImageListAdapter
import com.example.cinemaapp.api.ApiService
import com.example.cinemaapp.databinding.ActivityDetailBinding
import com.example.cinemaapp.db.AppDatabase
import com.example.cinemaapp.db.FilmItemDao
import com.example.cinemaapp.domain.FilmItem
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    private lateinit var apiService: ApiService
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setContentView(binding.root)

        initUserAndFilmId()
        initDb()
        initView()
        updateLikeBtnColor(idFilm)
        initApiService()
        sendRequest()
        likeItem()
    }

    private fun initApiService() {
        apiService = ApiService.create()
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
                if (!isContain(item.id)) {
                    withContext(Dispatchers.IO) {
                        item.userId = userId
                        filmItemDao.addFilm(item)
                    }

                    updateLikeBtnColor(item.id)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Added to Favorites!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.IO) {
                        filmItemDao.deleteFilm(item.id, userId)
                        filmItemDao.updateFilmsUid(item.uid)
                    }

                    updateLikeBtnColor(item.id)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Deleted from Favorites!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun isContain(id: Int?): Boolean {
        val films = filmItemDao.getById(userId)

        for (f in films) {
            if (f.id == id)
                return true
        }
        return false
    }

    private fun sendRequest() {
        progressBar.visibility = View.VISIBLE
        scrollView.visibility = View.GONE

        val call = apiService.getMovieDetails(idFilm)

        call.enqueue(object : Callback<FilmItem>{
            override fun onResponse(call: Call<FilmItem>, response: Response<FilmItem>) {

                if (response.isSuccessful){
                    item = response.body()!!

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
                }
            }

            override fun onFailure(call: Call<FilmItem>, t: Throwable) {
                progressBar.visibility = View.GONE
            }
        })
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

    private fun initDb() {
        db = AppDatabase.initDb(this)
        filmItemDao = db.filmItemDao()
    }

    private fun initUserAndFilmId() {
        userId = intent.getStringExtra("userId").toString()
        idFilm = intent.getIntExtra("filmId", 0)
    }

    private fun initBinding() {
        binding = ActivityDetailBinding.inflate(layoutInflater)
    }
}