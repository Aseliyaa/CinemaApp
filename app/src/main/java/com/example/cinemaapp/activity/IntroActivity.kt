package com.example.cinemaapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import com.example.cinemaapp.R

class IntroActivity : AppCompatActivity() {
    private lateinit var getInBtn: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        initView()
        onButtonClick()
    }

    private fun initView() {
        getInBtn = findViewById(R.id.getInBtn)
    }

    private fun onButtonClick() {
        getInBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}