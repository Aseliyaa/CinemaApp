package com.example.cinemaapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.cinemaapp.databinding.ActivityLoginBinding
import com.example.cinemaapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var emailTxt: EditText
    private lateinit var passwordTxt: EditText
    private lateinit var logInBtn: AppCompatButton
    private lateinit var redirectSignUp: TextView
    private lateinit var binding: ActivityLoginBinding

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
        binding = ActivityLoginBinding.inflate(layoutInflater)
    }
    private fun initAuth() {
        auth = FirebaseAuth.getInstance()
    }

    private fun initView() {
        emailTxt = binding.logInEmail
        passwordTxt = binding.password
        logInBtn = binding.logInBtn
        redirectSignUp = binding.tvRedirectSignUp
    }

    private fun onButtonsClick() {
        redirectSignUp.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        logInBtn.setOnClickListener{
            loginUser()
        }
    }

    private fun loginUser() {
        val email = emailTxt.text.toString()
        val password = passwordTxt.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter the data!", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful){
                Toast.makeText(this, "Successfully Logged In!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("userId", auth.currentUser?.uid)
                startActivity(intent)

                finish()
            }
        }.addOnFailureListener{ exception->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }
}