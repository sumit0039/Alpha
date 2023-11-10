package com.softwill.alpha.notification.general

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.softwill.alpha.R
import com.softwill.alpha.databinding.FragmentGeneralBinding
import com.softwill.alpha.utils.Constant.Companion.SWIPE_DELAY


class GeneralFragment : Fragment() {

    private lateinit var binding: FragmentGeneralBinding
    private var mDelayHandler: Handler? = null
    var mGeneralAdapter: GeneralAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_general, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDelayHandler = Handler()
        binding.swiperefresh.setColorSchemeColors(resources.getColor(R.color.primary_color))
        binding.swiperefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            mDelayHandler!!.postDelayed(mRunnable, SWIPE_DELAY)
        })


        mGeneralAdapter = context?.let { GeneralAdapter(it) }
        binding.rvGeneral.adapter = mGeneralAdapter
        mGeneralAdapter!!.notifyDataSetChanged()


    }

    private val mRunnable: Runnable = Runnable {
        binding.swiperefresh.setRefreshing(false)
    }
}