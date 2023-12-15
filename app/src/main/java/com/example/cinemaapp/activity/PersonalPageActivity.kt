package com.example.cinemaapp.activity

import android.R
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.cinemaapp.databinding.ActivityPersonalPageBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern


class PersonalPageActivity : AppCompatActivity() {
    private lateinit var phoneTv: TextView
    private lateinit var userNameTv: TextView
    private lateinit var birthdayTv: TextView
    private lateinit var emailTv: TextView
    private lateinit var binding: ActivityPersonalPageBinding

    private lateinit var userId: String
    private lateinit var userRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setContentView(binding.root)

        initUser()
        initView()
        setInfo(userRef)
        btnManager()
    }

    private fun btnManager() {
        val regex = "\\+375\\d{9}"
        val pattern: Pattern = Pattern.compile(regex)
        phoneTv.setOnClickListener {
            val currentPhone = phoneTv.text.toString()
            phoneTv.text = currentPhone
            phoneTv.isFocusableInTouchMode = true
        }
        phoneTv.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
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
        }
        birthdayTv.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH)
            val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this@PersonalPageActivity,
                { view, year, month, dayOfMonth ->
                    val birthdate = String.format(
                        Locale.getDefault(),
                        "%02d.%02d.%04d",
                        dayOfMonth,
                        month + 1,
                        year
                    )
                    birthdayTv.text = birthdate
                    userRef.child("birthday").setValue(birthdate)
                },
                year,
                month,
                dayOfMonth
            )
            datePickerDialog.show()
        }
        birthdayTv.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val newBirthdate = birthdayTv.text.toString()
                userRef.child("birthday").setValue(newBirthdate)
            }
        }
    }
    private fun setInfo(userRef: DatabaseReference) {
        userRef.child("name").get().addOnSuccessListener { dataSnapshot: DataSnapshot ->
            val nameStr = dataSnapshot.getValue(
                String::class.java
            )
            userNameTv.text = nameStr
        }.addOnFailureListener { userNameTv.text = "" }

        userRef.child("email").get().addOnSuccessListener { dataSnapshot: DataSnapshot ->
            val emailStr = dataSnapshot.getValue(
                String::class.java
            )
            emailTv.text = emailStr
        }.addOnFailureListener { emailTv.text = "" }

        userRef.child("phone").get().addOnSuccessListener { dataSnapshot: DataSnapshot ->
            val phoneStr = dataSnapshot.getValue(
                String::class.java
            )
            phoneTv.text = phoneStr
        }.addOnFailureListener { phoneTv.text = "" }

        userRef.child("birthday").get().addOnSuccessListener { dataSnapshot: DataSnapshot ->
            val birthdayStr = dataSnapshot.getValue(
                String::class.java
            )
            birthdayTv.text = birthdayStr
        }.addOnFailureListener { birthdayTv.text = "" }
    }
    private fun initUser() {
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
    }

    private fun initView() {
        phoneTv = binding.phone
        userNameTv = binding.userName
        birthdayTv = binding.birthdayText
        emailTv = binding.emailText
    }

    private fun initBinding() {
        binding = ActivityPersonalPageBinding.inflate(layoutInflater)
    }
}