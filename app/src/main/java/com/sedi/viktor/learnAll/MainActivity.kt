package com.sedi.viktor.learnAll

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.sedi.viktor.learnAll.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.apply {
            tvAppName.text = resources.getText(R.string.app_name)
            tvDeveloper.text = "Viktor Morgachev"
        }


    }
}
