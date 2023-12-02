package com.softwill.alpha.institute_detail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemSelectedStudentBinding
import com.softwill.alpha.institute_detail.model.placement.PlacementCompaniesResponseItem
import com.softwill.alpha.institute_detail.model.placement.PlacementStudentsResponseItem
import com.softwill.alpha.utils.UtilsFunctions


class SelectedStudentsAdapter(
    private var mList: ArrayList<PlacementStudentsResponseItem>,
    private val context: Context
) :
    RecyclerView.Adapter<SelectedStudentsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemSelectedStudentBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                com.softwill.alpha.R.layout.item_selected_student,
                parent,
                false
            )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvDate.text = UtilsFunctions().getDDMMM(createdAt)
                binding.txtStudent.text = studentName
                Glide.with(context).load(studentAvtar).error(R.drawable.icon_avatar).into(binding.ivImage)
                binding.companyName.text = companyName


            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemSelectedStudentBinding) :
        RecyclerView.ViewHolder(binding.root)


    /* fun removeItem(position: Int) {
         mList.removeAt(position)
         notifyItemRemoved(position)
         notifyItemRangeChanged(position, mList.size)
         notifyDataSetChanged()
     }*/

    //Filter New
    /* fun filterList(filteredList: ArrayList<FacultyStreamItem2Model>) {
         mList = filteredList
         notifyDataSetChanged()
     }*/
}