package com.softwill.alpha.institute.lecture.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemLectureBinding
import com.softwill.alpha.institute.lecture.model.LectureTeacher
import com.softwill.alpha.utils.UtilsFunctions


class LectureAdapter(
    private val mList: ArrayList<LectureTeacher>,
    private val context: Context,
    private val IsStudentLogin: Boolean,
    private val isToday: Boolean,
    private val callbackInterface: LectureAdapterCallbackInterface
) :
    RecyclerView.Adapter<LectureAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): LectureAdapter.ViewHolder {
        val binding: ItemLectureBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_lecture,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvLectureDate.text = UtilsFunctions().getDDMMMEEEEYYYY(lectureDate)
                binding.tvLectureTime.text = UtilsFunctions().getHHMMA(lectureStartTime) + " to " + UtilsFunctions().getHHMMA(lectureEndTime)
                binding.tvSubjectName.text = subjectName
                binding.tvStreamName.text = streamName
                binding.tvClassName.text = "Class $className"
                binding.tvTeacherName.text = teacherName

                val (hours, minutes) = UtilsFunctions().calculateTimeDifference(lectureStartTime, lectureEndTime)

                binding.tvTimeDifference.text = "$hours Hr $minutes Min"


                Glide.with(context).load(teacherAvatarUrl).placeholder(R.drawable.icon_avatar)
                    .into(binding.ivTeacherAvtar)

                if (isToday) {
                    binding.tvLectureDate.visibility = View.GONE
                } else {
                    binding.tvLectureDate.visibility = View.VISIBLE
                }


                if (IsStudentLogin) {
                    binding.linearLayout2.visibility = View.GONE
                    binding.ibDelete.visibility = View.GONE
                } else {
                    binding.linearLayout2.visibility = View.VISIBLE
                    binding.ibDelete.visibility = View.VISIBLE
                }

                binding.ibDelete.setOnClickListener {

                    callbackInterface.deleteLectureCallback(position, id)
                }

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemLectureBinding) :
        RecyclerView.ViewHolder(binding.root)


    interface LectureAdapterCallbackInterface {
        fun deleteLectureCallback(position: Int, id: Int)
    }


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }

}