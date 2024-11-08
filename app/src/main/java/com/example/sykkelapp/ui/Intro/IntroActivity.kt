@file:Suppress("DEPRECATION")

package com.example.sykkelapp.ui.Intro

import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import android.widget.LinearLayout
import android.widget.TextView
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.WindowManager
import android.view.View
import android.view.Window
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.sykkelapp.MainActivity
import com.example.sykkelapp.R
import com.example.sykkelapp.ui.map.LOCATION_REQUEST
import com.example.sykkelapp.ui.map.location.GpsUtils

class IntroActivity : AppCompatActivity(), View.OnClickListener {
    private var viewPager: ViewPager? = null
    private var dotsLayout: LinearLayout? = null
    private var btnNext: Button? = null
    private var btnSkip: Button? = null
    private var introAdapter: IntroAdapter? = null
    private lateinit var mDots: Array<TextView?>
    private var currentItem = 0
    var prevStarted = "yes"

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        @Suppress("DEPRECATION")
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()

        setContentView(R.layout.activity_intro_screen)
        transparentStatusBar() //make status bar transparent
        findViews() // find xml views
        setClickListener() //set on click listeners
        setupViewPager() //setup the viewpager, set adapter and page listener
        addDotsIndicator(0) //called for the first launch, after this handled in page listener

        // GpsUtils from https://proandroiddev.com/android-tutorial-on-location-update-with-livedata-774f8fcc9f15
        GpsUtils(this).turnGPSOn(object : GpsUtils.OnGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                GPSEnabled.isGPSEnabled = isGPSEnable
            }
        })

        val locationButton = findViewById<Button>(R.id.location_button)
        locationButton.setOnClickListener {
            invokeLocationAction()
            it.visibility = View.GONE
        }

    }

    // Function from https://proandroiddev.com/android-tutorial-on-location-update-with-livedata-774f8fcc9f15
    private fun invokeLocationAction() {
        when {
            !GPSEnabled.isGPSEnabled ->  Log.d("Map fragment",getString(R.string.enable_gps))

            shouldShowRequestPermissionRationale() -> Log.d("Map fragment",getString(R.string.permission_request))

            else -> ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_REQUEST
            )
        }
    }

    // Function from https://proandroiddev.com/android-tutorial-on-location-update-with-livedata-774f8fcc9f15
    fun shouldShowRequestPermissionRationale() =
        ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) && ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    private fun launchMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun transparentStatusBar() {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    private fun setupViewPager() {
        introAdapter = IntroAdapter(this)
        viewPager?.adapter = introAdapter
        viewPager?.addOnPageChangeListener(pageChangeListener)
    }

    private fun findViews() {
        viewPager = findViewById(R.id.view_pager)
        dotsLayout = findViewById(R.id.layoutDots)
        btnNext = findViewById(R.id.btn_next)
        btnSkip = findViewById(R.id.btn_skip)
    }

    private fun setClickListener() {
        btnSkip?.setOnClickListener(this)
        btnNext?.setOnClickListener(this)
    }

    private fun addDotsIndicator(position: Int) {
        //Adding TextView dynamically
        mDots = arrayOfNulls(introAdapter!!.count)
        /*Remove previous views when called next time
         if not called then views will keep on adding*/
        dotsLayout?.removeAllViews()

        //Set | in each dot-textView
        for (i in mDots.indices) {
            mDots[i] = TextView(this)
            mDots[i]?.text = "|"
            mDots[i]?.textSize = 35f
            mDots[i]?.setTextColor(resources.getColor(R.color.dot_inactive_color))
            dotsLayout?.addView(mDots[i])
        }
        if (mDots.isNotEmpty()) {
            //change color of the current selected dot
            mDots[position]?.setTextColor(resources.getColor(R.color.dot_active_color))
        }
    }

    var pageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            currentItem =
                position //currentItem used to get current position and then increase position
            addDotsIndicator(position)

            //change the next button text to "finish"
            if (position == introAdapter!!.count - 1) {
                //last page, make it "finish" and make the skip  button invisible
                btnNext?.text = getString(R.string.finish)
                btnSkip?.visibility = View.INVISIBLE
            } else {
                //set "next" text and make skip button visible
                btnNext?.text = getString(R.string.next)
                btnSkip?.visibility = View.VISIBLE
            }
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_next -> if (currentItem < introAdapter!!.count - 1) {
                ++currentItem
                viewPager?.currentItem = currentItem
            } else launchMainScreen() //launch main screen (MainActivity) on last page
            R.id.btn_skip -> launchMainScreen()
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedpreferences =
            getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
        if (!sharedpreferences.getBoolean(prevStarted, false)) {
            val editor = sharedpreferences.edit()
            editor.putBoolean(prevStarted, true)
            editor.apply()
        } else {
            moveToSecondary()
        }
    }
    fun moveToSecondary() {
        // use an intent to travel from one activity to another.
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

class GPSEnabled {
    companion object {
        var isGPSEnabled : Boolean = false
    }
}