package com.softwill.alpha.institute.sport.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityParticipantBinding
import com.softwill.alpha.institute.culture.adapter.GalleryAdapter
import com.softwill.alpha.institute.sport.model.SportExhibitions

class ParticipantActivity : AppCompatActivity(), GalleryAdapter.CallbackInterface {

    private lateinit var binding: ActivityParticipantBinding

    var mGalleryAdapter: GalleryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_participant)
        setupBack()


        val itemData = intent.getSerializableExtra("itemData") as? SportExhibitions

        if (itemData != null) {

            Glide.with(this).load(itemData.avtarUrl).placeholder(R.drawable.icon_avatar)
                .into(binding.ivProfileImage)


            binding.tvName.text = itemData.studentName
            binding.tvUserName.text = itemData.userName
            binding.tvTitle.text = itemData.title
            binding.tvDesc.text = itemData.desc


            mGalleryAdapter = GalleryAdapter(this@ParticipantActivity, this, itemData.photos)
            val layoutManager =
                GridLayoutManager(this@ParticipantActivity, 2, GridLayoutManager.VERTICAL, false)

            binding.rvPhoto.setHasFixedSize(true)
            binding.rvPhoto.layoutManager = layoutManager
            binding.rvPhoto.itemAnimator = DefaultItemAnimator()
            binding.rvPhoto.adapter = mGalleryAdapter
            mGalleryAdapter!!.notifyDataSetChanged()
        }





    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_participant)
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

    override fun onImageCallback(position: Int, picUrl: String) {
        TODO("Not yet implemented")
    }
}