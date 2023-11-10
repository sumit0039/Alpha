package com.softwill.alpha.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemSearchHomeBinding
import com.softwill.alpha.home.model.SearchResponse
import com.softwill.alpha.institute_detail.CollegeDetailsActivity
import com.softwill.alpha.profile_guest.activity.ProfileGuestActivity


class SearchAdapter(
    private val mList: ArrayList<SearchResponse>,
    private val context: Context,
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemSearchHomeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_search_home, parent, false
        )
        return ViewHolder(binding)
    }

    // binds the list items to a view

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {


               /* Glide.with(context).load(avtarUrl).placeholder(R.drawable.icon_profile)
                    .into(binding.ivProfileImage)*/

                binding.tvName.text = name
                binding.tvUserName.text = userName
                Glide.with(context).load(avtarUrl).placeholder(R.drawable.icon_avatar).into(binding.ivProfileImage)


                binding.root.setOnClickListener {


                    if (userTypeId == 3 || userTypeId == 2) {

                        val intent = Intent(context, ProfileGuestActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                        intent.putExtra("mUserId", id)
                        context.startActivity(intent)

                    } else {

                        val intent = Intent(context, CollegeDetailsActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                        intent.putExtra("mMyInstitute", false)
                        intent.putExtra("mInstituteId", id)
                        context.startActivity(intent)

                    }
                }

            }


        }


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemSearchHomeBinding) :
        RecyclerView.ViewHolder(binding.root)


    /* fun removeItem(position: Int) {
         mList.removeAt(position)
         notifyItemRemoved(position)
         notifyItemRangeChanged(position, mList.size)
         notifyDataSetChanged()
     }*/


}