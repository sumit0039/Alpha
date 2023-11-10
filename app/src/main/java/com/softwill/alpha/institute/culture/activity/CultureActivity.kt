package com.softwill.alpha.institute.culture.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityCultureBinding

class CultureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCultureBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_culture)


        setupBack()
        setupOnClickListener()
    }


    private fun setupOnClickListener() {

        binding.llTeamMember.setOnClickListener {
            val intent = Intent(applicationContext, TeamCultureActivity::class.java)
            startActivity(intent)
        }


        binding.llCalendar.setOnClickListener {
            val intent = Intent(applicationContext, CalendarActivity::class.java)
            startActivity(intent)
        }


        binding.llActivity.setOnClickListener {

        }

        binding.llSponsors.setOnClickListener {
            val intent = Intent(applicationContext, SponsorsActivity::class.java)
            startActivity(intent)
        }

        binding.llTrips.setOnClickListener {
            val intent = Intent(applicationContext, TripActivity::class.java)
            startActivity(intent)
        }

        binding.llGallery.setOnClickListener {
            val intent = Intent(applicationContext, GalleryActivity::class.java)
            startActivity(intent)
        }
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_culture_programs2)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}