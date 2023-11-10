package com.softwill.alpha.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.softwill.alpha.R
import com.softwill.alpha.dashboard.DashboardActivity
import com.softwill.alpha.signIn.SignInActivity
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.YourPreference


class SplashActivity : AppCompatActivity() {

    private var mDelayHandler: Handler? = null
    var yourPreference: YourPreference? = null
    private val SPLASH_DELAY: Long = 3000 //3 seconds
    var IsLogin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        yourPreference = YourPreference(this)


        //Initializing the Handler
        mDelayHandler = Handler()

        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)

    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }


    private fun hideSystemUI() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private val mRunnable: Runnable = Runnable {
        IsLogin= yourPreference?.getData(Constant.IsLogin).toBoolean()

        if(IsLogin){
            val intent = Intent(applicationContext, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            val intent = Intent(applicationContext, SignInActivity::class.java)
            //val intent = Intent(applicationContext, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

}