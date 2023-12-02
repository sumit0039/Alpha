package com.softwill.alpha.institute_detail.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.institute_detail.model.EntranceExamItemModel
import com.softwill.alpha.databinding.ItemEntranceExamBinding
import com.softwill.alpha.institute_detail.model.EntranceExamResponseItem


class InstituteEntranceExamAdapter(
    private var mList: ArrayList<EntranceExamResponseItem>,
    private val context: Context
) :
    RecyclerView.Adapter<InstituteEntranceExamAdapter.ViewHolder>() {

    lateinit var mInstituteEntranceExam2Adapter: InstituteEntranceExam2Adapter

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemEntranceExamBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                com.softwill.alpha.R.layout.item_entrance_exam,
                parent,
                false
            )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.txtTitle.text = entranceName
                binding.txtExmLink.text = Html.fromHtml(examLink)
                binding.txtExmLink.setOnClickListener {
                    val uri = Uri.parse(examLink) // missing 'http://' will cause crashed
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    context.startActivity(intent)
                }
                binding.txtExmLink.text = examLink
                if (merit.equals("")){
                    binding.txtMerit.text = "0/0"
                }else{
                    binding.txtMerit.text = merit
                }


//                mInstituteEntranceExam2Adapter = InstituteEntranceExam2Adapter( context)
//                binding.rvEntranceExam2.adapter = mInstituteEntranceExam2Adapter
//                mInstituteEntranceExam2Adapter.notifyDataSetChanged()
//
//                if (isShowing) {
//                    binding.llShow.visibility = View.VISIBLE
//                } else {
//                    binding.llShow.visibility = View.GONE
//                }
//
//                binding.llTop.setOnClickListener {
//                    isShowing = !isShowing
//                    notifyDataSetChanged()
//                }

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemEntranceExamBinding) :
        RecyclerView.ViewHolder(binding.root)


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }

    //Filter New
    fun filterList(filteredList: ArrayList<EntranceExamResponseItem>) {
        mList = filteredList
        notifyDataSetChanged()
    }
}