package com.softwill.alpha.institute.online_exam.teacher.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.softwill.alpha.R
import com.softwill.alpha.career.mack_exam.model.QuestionModel

class PagerObjectiveAdapter(
    private val mContext: Context,
    private val mList: List<QuestionModel>,
    private val callbackInterface: PagerObjectiveCallbackInterface
) :
    PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private var selectedOptionsMap: MutableMap<Int, Int> = mutableMapOf()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(mContext)
        val view = layoutInflater!!.inflate(R.layout.item_objective_question, container, false)
        val tvQues: TextView = view.findViewById(R.id.tvQues)
        val optionsLayout: RadioGroup = view.findViewById(R.id.radioGroup)

        val questionModel = mList[position]
        tvQues.text = questionModel.question

        optionsLayout.removeAllViews()
        for (optionIndex in questionModel.options.size - 1 downTo 0) {
            val option = questionModel.options[optionIndex]
            val radioButton = RadioButton(mContext)
            radioButton.text = option.answer
            radioButton.id = option.id

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
            if (selectedOptionsMap.containsKey(position) && selectedOptionsMap[position] == option.id) {
                radioButton.isChecked = true
            }

            radioButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    callbackInterface.onAnswerCallback(position , option.id )
                    selectedOptionsMap[position] = option.id
                }
            }
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

    interface PagerObjectiveCallbackInterface {
        fun onAnswerCallback(position: Int, answerId: Int)
    }


}
