package com.example.cinemaapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.cinemaapp.adapter.FilmListAdapter
import com.example.cinemaapp.adapter.GenresListAdapter
import com.example.cinemaapp.adapter.SliderAdapter
import com.example.cinemaapp.api.ApiService
import com.example.cinemaapp.databinding.ActivityMainBinding
import com.example.cinemaapp.domain.Data
import com.example.cinemaapp.domain.Genres
import com.example.cinemaapp.domain.ListFilm
import com.example.cinemaapp.domain.SliderItems

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.abs


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerViewNewMovies: RecyclerView
    private lateinit var recyclerViewUpcoming: RecyclerView
    private lateinit var recyclerViewGenres: RecyclerView
    private lateinit var loading1: ProgressBar
    private lateinit var loading2: ProgressBar
    private lateinit var loading3: ProgressBar

    private lateinit var likedMoviesActivityBtn: ImageView
    private lateinit var searchTxt: EditText

    private lateinit var userId: String

    private lateinit var apiService: ApiService

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewPager2: ViewPager2

    val slideHandler = Handler()

    private lateinit var newMovies: ArrayList<Data>
    private lateinit var upComingMovies: ArrayList<Data>

    private var activeCategories = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setContentView(binding.root)

        initUserId()
        initView()
        initListeners()
        initApiService()

        getAndSetGenres()
        getBannersImages()
        newMovies = sendRequest(loading1, recyclerViewNewMovies, 1)
        upComingMovies = sendRequest(loading2, recyclerViewUpcoming, 3)
    }

    private fun getAndSetGenres() {
        val call = apiService.getGenres()

        call.enqueue(object : Callback<List<Genres>>{

            override fun onResponse(call: Call<List<Genres>>, response: Response<List<Genres>>) {
                if (response.isSuccessful){
                    val genres = response.body()

                    recyclerViewGenres.adapter = genres?.let { GenresListAdapter(it) }
                }
            }

            override fun onFailure(call: Call<List<Genres>>, t: Throwable) {

            }

        })
    }

    fun showFilmsByCategory(category: String, isClicked: Boolean){

        if (!activeCategories.contains(category)) {
            activeCategories.add(category)
        } else {
            activeCategories.remove(category)
        }

        val filteredNewMovies: List<Data> = newMovies.filter { movie ->
            movie.genres.containsAll(activeCategories)
        }

        val filteredUpcomingMovies: List<Data> = upComingMovies.filter { movie ->
            movie.genres.containsAll(activeCategories)
        }

        recyclerViewNewMovies.adapter = FilmListAdapter(filteredNewMovies, userId)
        recyclerViewUpcoming.adapter = FilmListAdapter(filteredUpcomingMovies, userId)
    }

    private fun getBannersImages() {
        loading2.visibility = View.VISIBLE

        val call = apiService.getMovies(10)

        call.enqueue(object : Callback<ListFilm> {
            override fun onResponse(call: Call<ListFilm>, response: Response<ListFilm>) {
                loading2.visibility = View.GONE

                if (response.isSuccessful) {

                    val items = response.body()?.data
                    setBanners(items)
                }
            }

            override fun onFailure(call: Call<ListFilm>, t: Throwable) {
                loading2.visibility = View.GONE
            }

        })
    }

    private fun setBanners(films: ArrayList<Data>?) {
        val sliderItems: MutableList<SliderItems> = mutableListOf()

        if (films != null) {
            for (film in films) {
                sliderItems.add(SliderItems(film))
            }
        }

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

    private fun sendRequest(loading: ProgressBar, recyclerView: RecyclerView, pageId: Int): ArrayList<Data> {
        loading.visibility = View.VISIBLE

        val call = apiService.getMovies(pageId)
        val filmsForReturn = ArrayList<Data>()

        call.enqueue(object : Callback<ListFilm> {
            override fun onResponse(call: Call<ListFilm>, response: Response<ListFilm>) {
                loading.visibility = View.GONE

                if (response.isSuccessful) {
                    val films = response.body()?.data!!

                    filmsForReturn.addAll(films)
                    if (searchTxt.text.isEmpty()) {
                        recyclerView.adapter = FilmListAdapter(films, userId)
                        filmsForReturn.clear()
                        filmsForReturn.addAll(films)
                    } else {
                        val filteredItems = films.filter { item ->
                            item.title?.contains(searchTxt.text.toString()) ?: false
                        }
                        recyclerView.adapter = FilmListAdapter(filteredItems, userId)
                        filmsForReturn.clear()
                        filmsForReturn.addAll(filteredItems)
                    }
                }
            }

            override fun onFailure(call: Call<ListFilm>, t: Throwable) {
                loading.visibility = View.GONE
            }
        })

        return filmsForReturn
    }

    private fun initApiService() {
        apiService = ApiService.create()
    }

    private fun initListeners() {
        searchTxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                sendRequest(loading1, recyclerViewNewMovies, 1)
                sendRequest(loading2, recyclerViewUpcoming, 3)
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        likedMoviesActivityBtn.setOnClickListener {

            val intent = Intent(this, LikedMovies::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)

        }
    }

    private fun initView() {
        searchTxt = binding.searchEditTxt
        recyclerViewNewMovies = binding.view
        recyclerViewUpcoming = binding.view1
        recyclerViewGenres = binding.genresRecyclerView
        loading1 = binding.loading
        loading2 = binding.loading1
        loading3 = binding.loading2

        likedMoviesActivityBtn = binding.likedMoviesActivityBtn

        viewPager2 = binding.viewPager

        recyclerViewNewMovies.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewUpcoming.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewGenres.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun initUserId() {
        userId = intent.getStringExtra("userId").toString()
    }

    private fun initBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
    }
}

