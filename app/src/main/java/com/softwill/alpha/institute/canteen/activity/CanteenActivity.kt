package com.softwill.alpha.institute.canteen.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityCanteenBinding

class CanteenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCanteenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_canteen)



        setupBack()
        setupOnClickListener()

    }

    private fun setupOnClickListener() {

        binding.llTeamMember.setOnClickListener {
            val intent = Intent(applicationContext, TeamCanteenActivity::class.java)
            startActivity(intent)
        }


        binding.llMenuCard.setOnClickListener {
            val intent = Intent(applicationContext, MenuCardActivity::class.java)
            startActivity(intent)
        }


        binding.llActivity.setOnClickListener {
            val intent = Intent(applicationContext, FacilitiesActivity::class.java)
            startActivity(intent)
        }


    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_canteen)
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