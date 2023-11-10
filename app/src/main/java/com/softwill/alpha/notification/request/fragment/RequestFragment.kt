package com.softwill.alpha.notification.request.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.databinding.FragmentRequestBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.notification.request.adapter.RequestMainAdapter
import com.softwill.alpha.notification.request.adapter.RequestSubAdapter
import com.softwill.alpha.notification.request.model.RequestMainModel
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RequestFragment : Fragment(), RequestSubAdapter.CallbackInterface {

    private lateinit var binding: FragmentRequestBinding
    private var mDelayHandler: Handler? = null
    lateinit var mRequestMainAdapter: RequestMainAdapter
    val mRequestMainModel = ArrayList<RequestMainModel>()
    private var mListener: RequestFragmentInterface? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_request, container, false);
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is RequestFragmentInterface) {
            context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement RequestFragmentInterface"
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDelayHandler = Handler()
        binding.swiperefresh.setColorSchemeColors(resources.getColor(R.color.primary_color))
        binding.swiperefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            mDelayHandler!!.postDelayed(mRunnable, Constant.SWIPE_DELAY)
        })


        /* val data = ArrayList<RequestMainModel>()
         val subRequest = ArrayList<RequestModel>()
         subRequest.add(RequestModel(1, "Tony Stark", null, "", ))
         subRequest.add(RequestModel(2, "Captain Marvel", null, "", ))
         data.add(RequestMainModel("Today", subRequest))
         data.add(RequestMainModel("03 May 2023", subRequest))
         data.add(RequestMainModel("06 Feb 2023", subRequest))*/

        mRequestMainAdapter = RequestMainAdapter(mRequestMainModel, requireActivity(), this)
        binding.rvRequestMain.adapter = mRequestMainAdapter
        mRequestMainAdapter.notifyDataSetChanged()


        apiConnectionRequests()

    }

    private val mRunnable: Runnable = Runnable {
        binding.swiperefresh.isRefreshing = false
        apiConnectionRequests()
    }

    private fun apiConnectionRequests() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_ConnectionRequests()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val requests =
                        Gson().fromJson(responseJson, Array<RequestMainModel>::class.java).toList()
                    mRequestMainModel.clear()

                    mRequestMainModel.addAll(requests)


                    mListener?.requestsCount(mRequestMainModel.size)


                    if (mRequestMainModel.isNotEmpty()) {
                        mRequestMainAdapter.notifyDataSetChanged()
                    }


                    println("Total Request Count : ${mRequestMainModel.size}")

                } else {
                    UtilsFunctions().handleErrorResponse(response, requireActivity())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    public fun apiAcceptRejectConnectionRequest(id: Int, status: String, position: Int) {

        val jsonObject = JsonObject().apply {
            addProperty("status", status)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_AcceptRejectConnectionRequest(
                id,
                jsonObject
            )


        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {

                        if (responseObject.getString("message") == "Accepted successfully") {
                            apiConnectionRequests()
                            mRequestMainAdapter.removeItem(position)
                        }
                    }

                } else {
                    UtilsFunctions().handleErrorResponse(response, requireActivity())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    interface RequestFragmentInterface {
        fun requestsCount(count: Int)
    }

    override fun connectionAcceptRejectCallback(id: Int, type: String, position: Int) {
        apiAcceptRejectConnectionRequest(id, type, position)
    }

}