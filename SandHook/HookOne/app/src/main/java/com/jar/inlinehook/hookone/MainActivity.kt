package com.jar.inlinehook.hookone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.jar.inlinehook.hookone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initEvent()
    }

    private fun initEvent() {
        binding.btnTestHookOne.setOnClickListener {
            val result = testHook_strstr()
            Log.d(TAG, "initEvent: $result")
        }
    }

    external fun testGetString(): String

    external fun testHook_strstr(): String


    companion object {
        private const val TAG = "MainActivity : "

        // Used to load the 'hookone' library on application startup.
        init {
            System.loadLibrary("sandhook-native")
        }
    }
}