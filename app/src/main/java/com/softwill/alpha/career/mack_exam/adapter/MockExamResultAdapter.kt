package com.softwill.alpha.career.mack_exam.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.career.mack_exam.model.ExamResultModel
import com.softwill.alpha.databinding.ItemMockExamResultBinding
import com.softwill.alpha.utils.UtilsFunctions


class MockExamResultAdapter(
    private val context: Context,
    private var mList: ArrayList<ExamResultModel>,
) :
    RecyclerView.Adapter<MockExamResultAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemMockExamResultBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_mock_exam_result,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {


                binding.tvExamName.text = examName
                binding.tvTotalMarks.text = totalMarks.toString()
                binding.tvSubject.text = subject
                binding.tvTotalQuestions.text = totalQuestions.toString()
                binding.tvTime.text =
                    UtilsFunctions().getHHMMA2(scheduleStartTime) + " to " + UtilsFunctions().getHHMMA2(
                        scheduleEndTime
                    )
                binding.tvDate.text = UtilsFunctions().getDDMMMMYYYY(scheduleStartTime)

                if (obtainMarks != null){
                    binding.btnMarks.text = "$obtainMarks/$totalMarks"
                }else{
                    binding.btnMarks.text = "0/$totalMarks"
                }



            }


        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemMockExamResultBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}