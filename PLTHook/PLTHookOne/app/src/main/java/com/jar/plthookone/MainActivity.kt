package com.jar.plthookone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.jar.plthookone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    /**
     * A native method that is implemented by the 'plthookone' native library,
     * which is packaged with this application.
     */

    external fun hookOne()

    companion object {
        // Used to load the 'plthookone' library on application startup.
        init {
            System.loadLibrary("plthookone")
        }
    }
}