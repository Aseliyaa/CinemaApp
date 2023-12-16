package com.example.cinemaapp.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.cinemaapp.adapter.FilmListAdapter
import com.example.cinemaapp.adapter.GenresListAdapter
import com.example.cinemaapp.adapter.SliderAdapter
import com.example.cinemaapp.api.RequestsOperator.Companion.sendGenresRequest
import com.example.cinemaapp.api.RequestsOperator.Companion.sendRequest
import com.example.cinemaapp.databinding.ActivityMainBinding
import com.example.cinemaapp.domain.Data
import com.example.cinemaapp.domain.SliderItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerViewNewMovies: RecyclerView
    private lateinit var recyclerViewUpcoming: RecyclerView
    private lateinit var recyclerViewGenres: RecyclerView
    private lateinit var loading1: ProgressBar
    private lateinit var loading2: ProgressBar
    private lateinit var loading3: ProgressBar
    private lateinit var loading4: ProgressBar

    private lateinit var personalPageActivityBtn: ImageView

    private lateinit var likedMoviesActivityBtn: ImageView
    private lateinit var searchTxt: EditText

    private lateinit var scrollView: ScrollView

    private lateinit var userId: String

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewPager2: ViewPager2

    val slideHandler = Handler(Looper.myLooper()!!)

    private lateinit var newMovies: List<Data>
    private lateinit var upComingMovies: List<Data>

    private var activeCategories = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setContentView(binding.root)

        lifecycleScope.launch {
            initUserId()
            initView()
            initListeners()
            loadGenres()
            loadBanners(10)
            newMovies = loadData(loading3, recyclerViewNewMovies, 1)
            upComingMovies = loadData(loading4, recyclerViewUpcoming, 3)
        }
    }

    private suspend fun loadGenres() {
        try {
            val genres = sendGenresRequest()
            recyclerViewGenres.adapter = GenresListAdapter(genres)
            loading2.visibility = View.GONE
        } catch (_: Exception) {
            loading2.visibility = View.VISIBLE
        }
    }

    private fun showFilmsByCategory() {
        recyclerViewNewMovies.adapter =
            FilmListAdapter(filterMovies(newMovies), userId)

        recyclerViewUpcoming.adapter =
            FilmListAdapter(filterMovies(upComingMovies), userId)
    }

    fun updateActiveCategories(category: String) {
        if (!activeCategories.contains(category)) {
            activeCategories.add(category)
        } else {
            activeCategories.remove(category)
        }

        showFilmsByCategory()
    }

    private fun filterMovies(listMovies: List<Data>): List<Data> {
        return listMovies.filter { movie ->
            movie.genres.containsAll(activeCategories)
        }
    }

    private suspend fun loadBanners(pageId: Int) {
        try {
            val banners = sendRequest(pageId)
            setBanners(banners)
            loading2.visibility = View.GONE
        } catch (e: Exception) {
            loading1.visibility = View.VISIBLE
        }
    }

    private fun setBanners(films: List<Data>) {
        val sliderItems = films.map { SliderItems(it) }.toMutableList()

        viewPager2.adapter = SliderAdapter(sliderItems, viewPager2, userId)
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.offscreenPageLimit = 3
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }

        viewPager2.setPageTransformer(compositePageTransformer)
        viewPager2.currentItem = 1
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                slideHandler.removeCallbacks(sliderRunnable)
            }
        })
    }

    private val sliderRunnable = object : Runnable {
        override fun run() {
            viewPager2.currentItem = viewPager2.currentItem + 1
            slideHandler.postDelayed(this, 3000)
        }
    }

    override fun onPause() {
        super.onPause()
        slideHandler.removeCallbacks(sliderRunnable)
    }

    override fun onResume() {
        super.onResume()
        slideHandler.postDelayed(sliderRunnable, 3000)
    }

    private suspend fun loadData(
        loading: ProgressBar,
        recyclerView: RecyclerView,
        pageId: Int
    ): List<Data> {
        val filmsToReturn = mutableListOf<Data>()

        try {
            val films = sendRequest(pageId)
            if (searchTxt.text.isEmpty()) {
                recyclerView.adapter = FilmListAdapter(films, userId)
                filmsToReturn.clear()
                filmsToReturn.addAll(films)
            } else {
                val filteredItems = films.filter { item ->
                    item.title?.contains(searchTxt.text.toString()) ?: false
                }
                recyclerView.adapter = FilmListAdapter(filteredItems, userId)
                filmsToReturn.clear()
                filmsToReturn.addAll(filteredItems)
            }
            loading.visibility = View.GONE
        } catch (_: Exception) {
            loading.visibility = View.VISIBLE
        }

        Log.d("item", filmsToReturn.size.toString())
        return filmsToReturn
    }

    private fun initListeners() {
        searchTxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                lifecycleScope.launch {
                    loadData(loading3, recyclerViewNewMovies, 1)
                    loadData(loading4, recyclerViewUpcoming, 3)
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        likedMoviesActivityBtn.setOnClickListener {
            val intent = Intent(this, LikedMovies::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        personalPageActivityBtn.setOnClickListener {
            val intent = Intent(this, PersonalPageActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
    }

    private fun initView() {
        scrollView = binding.scrollView
        loading1 = binding.progressBar1
        loading2 = binding.progressBar2
        loading3 = binding.progressBar3
        loading4 = binding.progressBar4

        setLoadingVisibilityVisible()

        searchTxt = binding.searchEditTxt
        recyclerViewNewMovies = binding.view
        recyclerViewUpcoming = binding.view1
        recyclerViewGenres = binding.genresRecyclerView

        personalPageActivityBtn = binding.personalPageActivityBtn
        likedMoviesActivityBtn = binding.likedMoviesActivityBtn

        viewPager2 = binding.viewPager

        recyclerViewNewMovies.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewUpcoming.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewGenres.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setLoadingVisibilityVisible() {
        loading1.visibility = View.VISIBLE
        loading2.visibility = View.VISIBLE
        loading3.visibility = View.VISIBLE
        loading4.visibility = View.VISIBLE
    }

    private fun initUserId() {
        userId = intent.getStringExtra("userId").toString()
    }

    private fun initBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
    }
}

