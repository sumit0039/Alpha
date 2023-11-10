package com.softwill.alpha.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.profile.tabActivity.PhotoModel

class ViewPagerAdapter(
    private val mContext: Context,
    private val mList: List<PhotoModel>,
    private val builder: PopupWindow
) :
    PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(mContext)
        val view = layoutInflater!!.inflate(R.layout.item_post, container, false)
        var imageview: ImageView = view.findViewById(R.id.img)

        Glide.with(mContext).load(mList[position].pathUrl)
            .placeholder(R.drawable.icon_no_image)
            .into(imageview)

        view.setOnClickListener {
            builder.dismiss()
        }


        container.addView(view, position)
        return view
    }

    override fun getCount(): Int {
        return mList.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }


}