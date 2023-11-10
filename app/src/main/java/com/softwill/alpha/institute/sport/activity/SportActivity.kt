package com.softwill.alpha.institute.sport.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivitySportBinding

class SportActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySportBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_sport)




        setupBack()
        setupOnClickListener()
    }

    private fun setupOnClickListener() {

        binding.llTeamMember.setOnClickListener {
            val intent = Intent(applicationContext, TeamSportActivity::class.java)
            startActivity(intent)
        }


        binding.llCompititions.setOnClickListener {
            val intent = Intent(applicationContext, CompetitionsActivity::class.java)
            startActivity(intent)
        }



        binding.llAccessories.setOnClickListener {
            val intent = Intent(applicationContext, AccessoriesActivity::class.java)
            startActivity(intent)
        }

        binding.llOpportunity.setOnClickListener {
            val intent = Intent(applicationContext, OpportunityActivity::class.java)
            startActivity(intent)
        }

        binding.llExhibition.setOnClickListener {
            val intent = Intent(applicationContext, ExhibitionActivity::class.java)
            startActivity(intent)
        }
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_sport)
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