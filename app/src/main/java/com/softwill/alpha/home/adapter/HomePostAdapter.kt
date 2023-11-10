package com.softwill.alpha.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemHomePostBinding
import com.softwill.alpha.home.model.HomePostModel
import com.softwill.alpha.profile_guest.activity.ProfileGuestActivity
import com.softwill.alpha.utils.UtilsFunctions


class HomePostAdapter(
    private val mList: ArrayList<HomePostModel>,
    private val context: Context,
    private val callbackInterface: CallbackInterface,
    private val viewPagerCallbackInterface2 : ViewPagerAdapter2.ViewPagerCallbackInterface
) :
    RecyclerView.Adapter<HomePostAdapter.ViewHolder>() {


    var mViewPagerAdapter2: ViewPagerAdapter2? = null


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemHomePostBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            com.softwill.alpha.R.layout.item_home_post,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    // binds the list items to a view

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {


                mViewPagerAdapter2 = ViewPagerAdapter2(context, photos.reversed(), viewPagerCallbackInterface2, id )
                binding.viewPager.adapter = mViewPagerAdapter2
                mViewPagerAdapter2!!.notifyDataSetChanged()
                binding.dotsIndicator.attachToPager(binding.viewPager)


                binding.tvName.text = name
                binding.tvInstituteName.text = instituteName
                binding.tvLikeCount.text = likes.toString()
                binding.tvCommentCount.text = comments.toString()
                binding.tvDate.text = UtilsFunctions().getDDMMMMYYYY(createdAt)

                if (isMyPost == 1){
                    binding.ivMore.setImageResource(R.drawable.icon_delete)
                    binding.ivMore.setColorFilter(
                        ContextCompat.getColor(context, R.color.red),
                        android.graphics.PorterDuff.Mode.MULTIPLY
                    )
                }else{
                    binding.ivMore.setImageResource(R.drawable.ic_more)
                    binding.ivMore.setColorFilter(
                        ContextCompat.getColor(context, R.color.black),
                        android.graphics.PorterDuff.Mode.MULTIPLY
                    )
                }

                if (!user.avtarUrl.isNullOrEmpty()){
                    Glide.with(context).load(user.avtarUrl).placeholder(R.drawable.icon_avatar).into(binding.ivProfileImage)
                }

                if(isLiked == 1){
                    binding.ivLiked.setImageResource(R.drawable.icon_liked)
                }else{
                    binding.ivLiked.setImageResource(R.drawable.icon_like)
                }

                binding.ivMore.setOnClickListener {
                    if (isMyPost == 1) {
                        callbackInterface.onDeleteCallback(position, binding.ivMore , id)
                    } else {
                        callbackInterface.reportAbuseCallback(binding.ivMore , id)
                    }
                }

                binding.llRead.setOnClickListener {
                    callbackInterface.postDetailsCallback(title, desc);
                }

                binding.llComment.setOnClickListener {
                    callbackInterface.commentCallback(id , position);
                }

                binding.ivProfileImage.setOnClickListener {
                    if (isMyPost != 1){
                        val intent = Intent(context, ProfileGuestActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                        intent.putExtra("mUserId", user.id)
                        context.startActivity(intent)
                    }
                }

                binding.llShare.setOnClickListener {
                    callbackInterface.onShareCallback(position);
                }

                binding.llLikeUnlike.setOnClickListener {
                    callbackInterface.onLikeUnlikeCallback(position, id)
                }



            }
        }


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemHomePostBinding) : RecyclerView.ViewHolder(binding.root)


    interface CallbackInterface {
        fun postDetailsCallback(title: String, desc: String)
        fun reportAbuseCallback(view: View, id: Int)
        fun commentCallback(id: Int, position: Int)
        fun onShareCallback(position: Int)
        fun onDeleteCallback(position: Int , view : View, postId : Int)
        fun onLikeUnlikeCallback(position: Int, id: Int)
    }



    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }
}