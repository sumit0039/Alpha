package com.softwill.alpha.institute_detail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.databinding.ItemFacultyStreamBinding
import com.softwill.alpha.institute_detail.model.InstituteFacultyModel


class FacultyStreamAdapter(
    private var mList: ArrayList<InstituteFacultyModel>,
    private val context: Context
) :
    RecyclerView.Adapter<FacultyStreamAdapter.ViewHolder>() {

    private lateinit var mFacultyStream2Adapter: FacultyStream2Adapter

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemFacultyStreamBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                com.softwill.alpha.R.layout.item_faculty_stream,
                parent,
                false
            )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvfacultyName.text = facultyName

                mFacultyStream2Adapter = FacultyStream2Adapter(institute_streams, context)
                binding.rvFacultyStream2.adapter = mFacultyStream2Adapter
                mFacultyStream2Adapter.notifyDataSetChanged()

                if (isShowing) {
                    binding.llShow.visibility = View.VISIBLE
                } else {
                    binding.llShow.visibility = View.GONE
                }

                binding.llTop.setOnClickListener {
                    isShowing = !isShowing
                    notifyDataSetChanged()
                }

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemFacultyStreamBinding) :
        RecyclerView.ViewHolder(binding.root)


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }

    //Filter New
    fun filterList(filteredList: ArrayList<InstituteFacultyModel>) {
        mList = filteredList
        notifyDataSetChanged()
    }
}