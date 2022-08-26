package com.jar.inlinehook.inlinehookdemofour

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.jar.inlinehook.inlinehookdemofour.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initEvent()
    }


    private fun initEvent() {
        binding.testInlineHookOne.setOnClickListener {
            val result = stringFromJNI()
            Log.d(TAG, "initEvent----------：  $result")
        }

        binding.testInlineHookTwo.setOnClickListener {
            testHookTwo()
        }

    }

    /**
     * A native method that is implemented by the 'inlinehookdemo1' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    external fun testHookTwo()

    companion object {
        private const val TAG = "MainActivity"
        // Used to load the 'inlinehookdemofour' library on application startup.
        init {
             System.loadLibrary("inline_hook_demo_four")
        }
    }
}