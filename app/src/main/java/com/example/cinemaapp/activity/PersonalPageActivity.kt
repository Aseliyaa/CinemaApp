package com.example.cinemaapp.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import kotlinx.coroutines.async
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.lifecycleScope
import com.example.cinemaapp.databinding.ActivityPersonalPageBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern

class PersonalPageActivity : AppCompatActivity() {
    private lateinit var phoneTv: TextView
    private lateinit var userNameTv: TextView
    private lateinit var birthdayTv: TextView
    private lateinit var emailTv: TextView

    private lateinit var homeBtn: ImageView
    private lateinit var likedMoviesBtn: ImageView
    private lateinit var logOutBtn: AppCompatButton

    private lateinit var progressBar: ProgressBar

    private lateinit var selectedImage: AppCompatImageView

    private lateinit var scrollView: ScrollView

    private lateinit var binding: ActivityPersonalPageBinding

    private lateinit var userId: String
    private lateinit var userRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setContentView(binding.root)

        lifecycleScope.launch {
            initUser()
            initView()
            setInfo()
            userInfoManager()
            btnManager()
        }
    }

    private fun navigateToActivity(ktClass: Class<*>) {
        val intent = Intent(this, ktClass)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    private fun btnManager() {
        homeBtn.setOnClickListener {
            navigateToActivity(MainActivity::class.java)
        }
        likedMoviesBtn.setOnClickListener {
            navigateToActivity(LikedMovies::class.java)
        }
        logOutBtn.setOnClickListener {
            navigateToActivity(LoginActivity::class.java)
        }
    }

    private fun userInfoManager() {
        val regex = "\\+375\\d{9}"
        val pattern: Pattern = Pattern.compile(regex)

        phoneTv.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newPhone = phoneTv.text.toString()
                val matcher: Matcher = pattern.matcher(newPhone)
                if (matcher.matches()) {
                    userRef.child("phone").setValue(newPhone)
                } else {
                    Toast.makeText(this, "Incorrect number!", Toast.LENGTH_SHORT).show()
                    userRef.child("phone").setValue("")
                    phoneTv.text = ""
                }
            }
            phoneTv.isCursorVisible = hasFocus
        }

        birthdayTv.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH)
            val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this@PersonalPageActivity, { _, year, month, dayOfMonth ->
                    val birthDate = String.format(
                        Locale.getDefault(),
                        "%02d.%02d.%04d",
                        dayOfMonth,
                        month + 1,
                        year
                    )
                    birthdayTv.text = birthDate
                    userRef.child("birthday").setValue(birthDate)
                },
                year,
                month,
                dayOfMonth
            )
            datePickerDialog.show()
        }

        birthdayTv.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newBirthDate = birthdayTv.text.toString()
                userRef.child("birthday").setValue(newBirthDate)
            }
            birthdayTv.isCursorVisible = hasFocus
        }
    }

    private suspend fun setInfo(pathString: String, tv: TextView) {
        withContext(Dispatchers.IO) {
            userRef.child(pathString).get().addOnSuccessListener { dataSnapshot: DataSnapshot ->
                val str = dataSnapshot.getValue(
                    String::class.java
                )
                tv.text = str
            }.addOnFailureListener { tv.text = "" }
        }
    }

    private suspend fun getInfo(pathString: String): String {
        return withContext(Dispatchers.IO) {
            val dataSnapshot = userRef.child(pathString).get().await()
            dataSnapshot.getValue(String::class.java) ?: ""
        }
    }

    private suspend fun setInfo() {
        withContext(Dispatchers.Main) {
            scrollView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            val nameDeferred = async { getInfo("name") }
            val emailDeferred = async { getInfo("email") }
            val phoneDeferred = async { getInfo("phone") }
            val birthdayDeferred = async { getInfo("birthday") }

            val name = nameDeferred.await()
            val email = emailDeferred.await()
            val phone = phoneDeferred.await()
            val birthday = birthdayDeferred.await()

            userNameTv.text = name
            emailTv.text = email
            phoneTv.text = phone
            birthdayTv.text = birthday

            progressBar.visibility = View.GONE
            scrollView.visibility = View.VISIBLE
        }
    }

    private fun initUser() {
        userId = intent.getStringExtra("userId").toString()
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
    }

    private fun initView() {
        phoneTv = binding.phone
        userNameTv = binding.userName
        birthdayTv = binding.birthdayText
        emailTv = binding.emailText

        scrollView = binding.hide
        progressBar = binding.progressBar

        homeBtn = binding.homeBtn
        likedMoviesBtn = binding.likedMoviesBtn
        logOutBtn = binding.logOutBtn

        selectedImage = binding.selectedImage
    }

    private fun initBinding() {
        binding = ActivityPersonalPageBinding.inflate(layoutInflater)
    }
}