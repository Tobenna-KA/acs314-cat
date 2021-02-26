package com.tobey.catapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class Signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val loginBtn: TextView = findViewById(R.id.login_btn)
        loginBtn.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
    }
}