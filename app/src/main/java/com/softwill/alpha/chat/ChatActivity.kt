package com.softwill.alpha.chat

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityChatBinding


class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    var mName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_chat)
        val bundle: Bundle? = intent.extras
        mName = bundle?.getString("mName").toString()

        setupBack()


    }

    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = mName
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        actionBar?.setDisplayUseLogoEnabled(true);


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.softwill.alpha.R.menu.chat_2_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}