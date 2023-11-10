package com.softwill.alpha.career.education_loan

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemLoanLinkBinding


class LoanLinksAdapter(
    private val context: Context,
    private val mList: ArrayList<String>,

    ) :
    RecyclerView.Adapter<LoanLinksAdapter.ViewHolder>() {


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemLoanLinkBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_loan_link,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    // binds the list items to a view

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvTitle.movementMethod = LinkMovementMethod.getInstance();
                binding.tvTitle.text = mList[position]

                binding.tvTitle.setOnClickListener {

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mList[position]))
                    context.startActivity(intent)
                }
            }
        }


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemLoanLinkBinding) : RecyclerView.ViewHolder(binding.root)


}