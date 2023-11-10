package com.softwill.alpha.career.mack_exam.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.career.mack_exam.model.MockExamModel
import com.softwill.alpha.databinding.ItemMockExamBinding
import com.softwill.alpha.utils.UtilsFunctions


class MockExamAdapter
    (
    private var mList: ArrayList<MockExamModel>,
    private val context: Context,
    private val callbackInterface: CallbackInterface
) :
    RecyclerView.Adapter<MockExamAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemMockExamBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_mock_exam,
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


                binding.btnSolve.setOnClickListener {
                    callbackInterface.onSolveCallback(position , id , subject)
                }

            }

        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemMockExamBinding) :
        RecyclerView.ViewHolder(binding.root)


    interface CallbackInterface {
        fun onSolveCallback(position: Int, examId: Int, subject: String)

    }

    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}