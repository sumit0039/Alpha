package com.softwill.alpha.institute.assignment.student.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.softwill.alpha.R
import com.softwill.alpha.institute.assignment.student.model.AssignmentQuestionModel

class AssignmentPagerAdapter(
    private val mContext: Context,
    private val mList: List<AssignmentQuestionModel>,
    private val callbackInterface: AssignmentPagerCallbackInterface
) :
    PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private var selectedOptionsMap: MutableMap<Int, Int> = mutableMapOf()
    private var selectedAnswerMap: MutableMap<Int, String> = mutableMapOf()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(mContext)

        val view: View

        if (mList[position].questionType == 1) {
            view = layoutInflater!!.inflate(R.layout.item_objective_question, container, false)

            val tvQues: TextView = view.findViewById(R.id.tvQues)
            val optionsLayout: RadioGroup = view.findViewById(R.id.radioGroup)

            val questionModel = mList[position]
            tvQues.text = questionModel.question

            optionsLayout.removeAllViews()
            for (optionIndex in questionModel.options.size - 1 downTo 0) {
                val option = questionModel.options[optionIndex]
                val radioButton = RadioButton(mContext)
                radioButton.text = option.answer
                radioButton.id = option.answerId

                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                radioButton.setTextColor(mContext.resources.getColor(R.color.grey_light2))
                radioButton.setPadding(8, radioButton.paddingTop, 8, radioButton.paddingBottom)
                val params = RadioGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(15, 15, 15, 0)
                radioButton.layoutParams = params

                optionsLayout.addView(radioButton)

                // Check if this option is previously selected and restore its state
                if (selectedOptionsMap.containsKey(position) && selectedOptionsMap[position] == option.answerId) {
                    radioButton.isChecked = true
                }

                radioButton.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        callbackInterface.onObjectiveAnswerCallback(position, option.answerId)
                        selectedOptionsMap[position] = option.answerId
                    }
                }
            }

        } else {
            view = layoutInflater!!.inflate(R.layout.item_subjective_question, container, false)


            val tvQues: TextView = view.findViewById(R.id.tvQues)
            val etAnswer: TextView = view.findViewById(R.id.etAnswer)


            val questionModel = mList[position]
            tvQues.text = questionModel.question

            if (selectedAnswerMap.containsKey(position) && !selectedAnswerMap[position].isNullOrEmpty()) {
                etAnswer.text = questionModel.selectedAnswer
            }


            etAnswer.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(char: CharSequence?, start: Int, before: Int, count: Int) {
                    callbackInterface.onSubjectiveAnswerCallback(position, char.toString().trim())
                    selectedAnswerMap[position] = char.toString().trim()
                }
            })


        }





        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        return mList.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    // This function can be used to retrieve the selected option index for a particular question
    fun getSelectedOptionIndex(optionId: Int): Int? {
        return selectedOptionsMap[optionId]
    }

    interface AssignmentPagerCallbackInterface {
        fun onObjectiveAnswerCallback(position: Int, answerId: Int)
        fun onSubjectiveAnswerCallback(position: Int, answer: String)
    }


}
