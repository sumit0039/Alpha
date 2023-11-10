package com.softwill.alpha.institute.assignment.teacher.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemCheckQuestionsBinding
import com.softwill.alpha.institute.assignment.teacher.Model.QuestionAnswer

class CheckQuestionsAdapter(
    private val context: Context,
    private var mList: ArrayList<QuestionAnswer>,
    private val callbackInterface: CheckQuestionsAdapterCallbackInterface
) :
    RecyclerView.Adapter<CheckQuestionsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): CheckQuestionsAdapter.ViewHolder {
        val binding: ItemCheckQuestionsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_check_questions,
            parent,
            false
        )
        return ViewHolder(binding)


    }


    @SuppressLint("UseCompatLoadingForColorStateLists")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvQues.text = "Q.${position + 1} $question"
                binding.tvAnswer.text = answer

                /*if (givenMark != null && givenMark!! > 0 && isCorrect == true) {
                    binding.llGivenMarks.visibility = View.VISIBLE
                    binding.tvGivenMarks.text = givenMark.toString()
                } else {
                    binding.llGivenMarks.visibility = View.GONE
                }*/

                when (isCorrect) {
                    true -> {
                        binding.tvCorrect.backgroundTintList = context.resources.getColorStateList(R.color.green);
                        binding.tvWrong.backgroundTintList = context.resources.getColorStateList(R.color.light_red);
                    }
                    false -> {
                        binding.tvCorrect.backgroundTintList = context.resources.getColorStateList(R.color.light_green);
                        binding.tvWrong.backgroundTintList = context.resources.getColorStateList(R.color.red);
                    }
                    else -> {
                        binding.tvCorrect.backgroundTintList = context.resources.getColorStateList(R.color.light_green);
                        binding.tvWrong.backgroundTintList = context.resources.getColorStateList(R.color.light_red);
                    }
                }




                binding.tvCorrect.setOnClickListener {
                    callbackInterface.correctAnswerCallback(id, position)
                }

                binding.tvWrong.setOnClickListener {
                    callbackInterface.wrongAnswerCallback(id, position)
                }

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemCheckQuestionsBinding) : RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

    /*  fun updateData(newStudents: ArrayList<QuestionAnswer>) {
          mList = newStudents
          notifyDataSetChanged()
      }*/

    interface CheckQuestionsAdapterCallbackInterface {
        fun correctAnswerCallback(id: Int, position: Int)
        fun wrongAnswerCallback(id: Int, position: Int)
    }

}