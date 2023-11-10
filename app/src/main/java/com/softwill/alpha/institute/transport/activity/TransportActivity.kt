package com.softwill.alpha.institute.transport.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityTransportBinding

class TransportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransportBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_transport)


        setupBack()
        setupOnClickListener()
    }




    private fun setupOnClickListener() {

        binding.llTeamMember.setOnClickListener {
            val intent = Intent(applicationContext, TeamTransportActivity::class.java)
            startActivity(intent)
        }


        binding.llFees.setOnClickListener {
            val intent = Intent(applicationContext, FeesActivity::class.java)
            startActivity(intent)
        }


        binding.llTransportDetails.setOnClickListener {
            val intent = Intent(applicationContext, TransportDetailsActivity::class.java)
            startActivity(intent)
        }

        binding.llTTrackBus.setOnClickListener {
            val intent = Intent(applicationContext, TrackBusActivity::class.java)
            startActivity(intent)
        }
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_transport)
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