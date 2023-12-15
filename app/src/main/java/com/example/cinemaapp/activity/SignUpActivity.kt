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
import com.example.cinemaapp.models.User
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    private lateinit var signUpBtn: AppCompatButton
    private lateinit var passwordTxt: EditText
    private lateinit var repeatPasswordTxt: EditText
    private lateinit var tvRedirectLogin: TextView
    private lateinit var emailTxt: EditText

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setContentView(binding.root)

        initAuth()
        initView()
        onButtonsClick()
    }

    private fun initBinding() {
        binding = ActivitySignUpBinding.inflate(layoutInflater)
    }

    private fun initAuth() {
        auth = Firebase.auth
        db = FirebaseDatabase.getInstance()
        usersRef = db.getReference("users")
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
                val user = User()
                user.email = email
                user.password = password
                user.name = ""
                user.birthday = ""
                user.phone = ""

                usersRef.child(auth.currentUser!!.uid)
                    .setValue(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Successfully Signed Up!", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("userId", auth.currentUser?.uid)
                        startActivity(intent)

                        finish()
                    }.addOnFailureListener(this) { exception ->
                        Toast.makeText(
                            applicationContext,
                            exception.localizedMessage,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
            }
        }.addOnFailureListener(this) { exception ->
            Toast.makeText(
                applicationContext,
                exception.localizedMessage,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }
}