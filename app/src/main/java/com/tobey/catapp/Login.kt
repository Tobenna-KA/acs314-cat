package com.tobey.catapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginBtn: Button = findViewById(R.id.login_btn)
        loginBtn.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }

        val signUpBtn: TextView = findViewById(R.id.sign_btn)
        signUpBtn.setOnClickListener { startActivity(Intent(this, Signup::class.java)) }
    }
}