package com.example.cinemaapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.cinemaapp.R
import com.example.cinemaapp.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SignUpActivity : AppCompatActivity() {
    private lateinit var signUpBtn: AppCompatButton
    private lateinit var passwordTxt: EditText
    private lateinit var repeatPasswordTxt: EditText
    private lateinit var tvRedirectLogin: TextView
    private lateinit var emailTxt: EditText

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setContentView(binding.root)

        initAuth()
        initView()
        onButtonsClick()
    }

    private fun initBinding(){
        binding = ActivitySignUpBinding.inflate(layoutInflater)
    }

    private fun initAuth() {
        auth = Firebase.auth
    }

    private fun initView() {
        signUpBtn = binding.signUpBtn
        passwordTxt = binding.password
        repeatPasswordTxt = binding.repeatPassword
        tvRedirectLogin = binding.tvRedirectLogin
        emailTxt = binding.email
    }

    private fun onButtonsClick() {
        signUpBtn.setOnClickListener {
            signUpUser()
        }

        tvRedirectLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun signUpUser() {
        val email = emailTxt.text.toString()
        val password = passwordTxt.text.toString()
        val repeatPassword = repeatPasswordTxt.text.toString()

        if (email.isBlank() || password.isBlank() || repeatPassword.isBlank()) {
            Toast.makeText(this, "Data can't be blank!", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != repeatPassword) {
            Toast.makeText(this, "Repeat password correctly!", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully Signed Up!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("userId", auth.currentUser?.uid)
                startActivity(intent)

                finish()
            }
        }.addOnFailureListener(this) { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_SHORT)
                .show()
        }
    }
}