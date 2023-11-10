package com.softwill.alpha.institute.sport.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemCompetitionsBinding
import com.softwill.alpha.institute.canteen.adapter.FacilityAdapter
import com.softwill.alpha.institute.sport.model.SportCompetitions


class CompetitionsAdapter(
    private val context: Context,
    private var mList: ArrayList<SportCompetitions>,
) :
    RecyclerView.Adapter<CompetitionsAdapter.ViewHolder>() {

    private val imageList = ArrayList<SlideModel>()
    var mFacilityAdapter: FacilityAdapter? = null

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): CompetitionsAdapter.ViewHolder {
        val binding: ItemCompetitionsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_competitions,
            parent,
            false
        )
        return ViewHolder(binding)

    }

    /*init {
        // Add images to the imageList in the constructor
        imageList.add(
            SlideModel(
                "https://bit.ly/2YoJ77H",
                "The animal population decreased by 58 percent in 42 years.",
                ScaleTypes.FIT
            )
        )
        imageList.add(
            SlideModel(
                "https://bit.ly/2BteuF2",
                "Elephants and tigers may become extinct.",
                ScaleTypes.FIT
            )
        )

    }*/


    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {


                binding.tvDescription.text = desc

                imageList.clear()

                for (banner in banners) {
                    imageList.add(SlideModel(banner, "", ScaleTypes.FIT))
                }

                binding.imageSlider.setImageList(imageList)

                mFacilityAdapter = FacilityAdapter(context, provided)
                binding.rvFacility.adapter = mFacilityAdapter
                mFacilityAdapter!!.notifyDataSetChanged()

            }


        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemCompetitionsBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}