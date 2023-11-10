package com.softwill.alpha.institute_detail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.databinding.ItemSelectedStudentBinding


class SelectedStudentsAdapter(
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
            /*with(mList[position]) {*/

           /* binding.tvName.text = "Facility ${position + 1}"*/


            /* holder.itemView.setOnClickListener {
                 val intent = Intent(context, CollegeDetailsActivity::class.java)
                 intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                 intent.putExtra("mName", name)
                 context.startActivity(intent)
             }*/

            /*}*/
        }


    }

    override fun getItemCount(): Int {
        return 2
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