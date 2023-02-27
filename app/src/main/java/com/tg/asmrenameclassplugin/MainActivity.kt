package com.tg.asmrenameclassplugin

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.drake.net.utils.scopeNetLife

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var tvTitle = findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = Users().getRealName()
        scopeNetLife {
            println("bbbbbbbbbbbbb")
        }
    }
}