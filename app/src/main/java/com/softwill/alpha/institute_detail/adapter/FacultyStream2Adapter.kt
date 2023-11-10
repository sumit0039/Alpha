package com.softwill.alpha.institute_detail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.databinding.ItemFacultyStream2Binding
import com.softwill.alpha.institute_detail.model.InstituteStreamModel


class FacultyStream2Adapter(
    private var mList: ArrayList<InstituteStreamModel>,
    private val context: Context
) :
    RecyclerView.Adapter<FacultyStream2Adapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemFacultyStream2Binding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                com.softwill.alpha.R.layout.item_faculty_stream2,
                parent,
                false
            )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvName.text = streamName

                if (isShowing) {
                    binding.tvShowHide.text = "Hide fees"
                    binding.llFeesStructure.visibility = View.VISIBLE
                } else {
                    binding.tvShowHide.text = "Show fees"
                    binding.llFeesStructure.visibility = View.GONE
                }


                binding.tvShowHide.setOnClickListener {
                    isShowing = !isShowing
                    notifyDataSetChanged()
                }


               /* holder.itemView.setOnClickListener {
                    val intent = Intent(context, CollegeDetailsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                    intent.putExtra("mName", name)
                    context.startActivity(intent)
                }*/

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemFacultyStream2Binding) :
        RecyclerView.ViewHolder(binding.root)


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }

    //Filter New
    fun filterList(filteredList: ArrayList<InstituteStreamModel>) {
        mList = filteredList
        notifyDataSetChanged()
    }
}