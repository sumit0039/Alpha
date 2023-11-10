package com.softwill.alpha.home.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.databinding.FragmentHomeBinding
import com.softwill.alpha.home.activity.SearchHomeActivity
import com.softwill.alpha.home.adapter.*
import com.softwill.alpha.home.adapter.HomePostAdapter.CallbackInterface
import com.softwill.alpha.home.model.CommentModel
import com.softwill.alpha.home.model.HomePostModel
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.notification.NotificationActivity
import com.softwill.alpha.profile.tabActivity.PhotoModel
import com.softwill.alpha.profile_guest.adapter.ConnectionListModel
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import com.softwill.alpha.utils.YourPreference
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator
import java.util.*


class HomeFragment : Fragment(), CallbackInterface, CommentAdapter.CommentCallbackInterface,
    View.OnClickListener, ViewPagerAdapter2.ViewPagerCallbackInterface {

    private lateinit var binding: FragmentHomeBinding
    lateinit var radioButton : RadioButton
    private var mDelayHandler: Handler? = null
    var yourPreference: YourPreference? = null
    var mHomePostAdapter: HomePostAdapter? = null
    var mCommentAdapter: CommentAdapter? = null
    var mShareAdapter: ShareAdapter? = null

    val mHomePostList = ArrayList<HomePostModel>()

    var progressDialog: Dialog? = null
    var currentPage: Int = 0
    var lastPage: Int = 0

    val mConnectionListModel = java.util.ArrayList<ConnectionListModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        yourPreference = YourPreference(activity)


        //9589YourPreference.saveData(Constant.IsStudentLogin, false)

        setupSwipeListener()
        binding.ibNotification.setOnClickListener(this)
        binding.ivSearch.setOnClickListener(this)



        mHomePostAdapter = HomePostAdapter(mHomePostList, requireActivity(), this, this)
        binding.rvPost.adapter = mHomePostAdapter
        mHomePostAdapter?.notifyDataSetChanged()




        binding.rvPost.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItemPosition == mHomePostAdapter!!.itemCount - 1) {
                    if (currentPage <= lastPage) {
                        currentPage +=1
                        apiHomePosts()

                    }
                }
            }
        })


        apiHomePosts()

    }


    private fun setupSwipeListener() {
        mDelayHandler = Handler()
        binding.swiperefresh.setColorSchemeColors(resources.getColor(R.color.primary_color))
        binding.swiperefresh.setOnRefreshListener {
            mDelayHandler!!.postDelayed(mRunnable, Constant.SWIPE_DELAY)
        }


    }

    private val mRunnable: Runnable = Runnable {
        binding.swiperefresh.isRefreshing = false
        apiHomePosts()
        // Toast.makeText(requireActivity(), "Updated!!", Toast.LENGTH_SHORT).show()
    }


    override fun postDetailsCallback(title: String, desc: String) {
        val dialog = context?.let { BottomSheetDialog(it, R.style.AppBottomSheetDialogTheme) }
        val view = layoutInflater.inflate(R.layout.bottomsheet_post_details, null)

        var tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        var tvDescription = view.findViewById<TextView>(R.id.tvDescription)

        tvTitle.text = title
        tvDescription.text = desc


        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()
    }

    @SuppressLint("RestrictedApi", "ResourceType")
    override fun reportAbuseCallback(view: View, id: Int) {

        val dialog = context?.let { BottomSheetDialog(it, R.style.AppBottomSheetDialogTheme) }
        val view = layoutInflater.inflate(R.layout.bottomsheet_report_abuse, null)

        val btnReport = view.findViewById<Button>(R.id.btnReport)
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)

        var Clicked = false;
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            Clicked = true
            radioButton = radioGroup.findViewById(checkedId)
        }

        btnReport.setOnClickListener {
            if (Clicked){
                apiReportPost(id, radioButton.text.toString())
                dialog?.dismiss()
            }else{
                UtilsFunctions().showToast(requireActivity(), "Please select any one to report")
            }

        }


        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()



        /*val menuBuilder = MenuBuilder(context)
        val inflater = MenuInflater(context)
        inflater.inflate(R.menu.home_post_menu, menuBuilder)
        val optionsMenu = activity?.let {
            MenuPopupHelper(
                it,
                menuBuilder,
                view,
                false,
                0,
                R.style.OverflowMenuStyle
            )
        }
        optionsMenu?.setForceShowIcon(true)



        menuBuilder.setCallback(object : MenuBuilder.Callback {
            override fun onMenuItemSelected(menu: MenuBuilder, item: MenuItem): Boolean {
                Toast.makeText(activity, "Reported Successfully", Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onMenuModeChange(menu: MenuBuilder) {}
        })

        optionsMenu?.show();*/
    }


    override fun commentCallback(id: Int, position: Int) {
        apiPostComments(id, position)
    }

    override fun deleteCommentCallback(commentId: Int , position: Int) {
        DeleteCommentBottomSheet(commentId, position);
    }


    override fun onLikeUnlikeCallback(position: Int, id: Int) {
        apiPostLikeUnLike(id, position)
    }


    override fun onShareCallback(position: Int) {
        openShareBottomSheet()
        apiConnectionsList()

    }

    private fun apiConnectionsList() {

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_ConnectionsList()


        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val connection =
                        Gson().fromJson(responseJson, Array<ConnectionListModel>::class.java)
                            .toList()
                    mConnectionListModel.clear()
                    mConnectionListModel.addAll(connection)

                    if (mConnectionListModel.isNotEmpty()) {
                        mShareAdapter?.notifyDataSetChanged()
                    }


                    println("Total Connection Count : ${mConnectionListModel.size}")

                } else {
                    UtilsFunctions().handleErrorResponse(response, requireActivity())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun openShareBottomSheet() {
        val dialog = context?.let { BottomSheetDialog(it, R.style.AppBottomSheetDialogTheme) }
        val view = layoutInflater.inflate(R.layout.bottomsheet_share, null)

        val rvShare = view.findViewById<RecyclerView>(R.id.rvShare)
        val etSearch = view.findViewById<EditText>(R.id.etSearch)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text is being changed
                val newText = s.toString()
                filter(newText)
                // Do something with the new text, like update UI or perform a search
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text has been changed
            }
        })


        mShareAdapter = ShareAdapter(mConnectionListModel, requireActivity())
        rvShare.adapter = mShareAdapter
        mShareAdapter!!.notifyDataSetChanged()


        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()

    }




    override fun onDeleteCallback(position: Int, view: View, postId: Int) {
        DeletePostBottomSheet(postId, position)
    }

    private fun DeleteCommentBottomSheet(commentId: Int, position: Int) {
        val dialog = context?.let { BottomSheetDialog(it, R.style.AppBottomSheetDialogTheme) }
        val view = layoutInflater.inflate(R.layout.bottomsheet_delete_post, null)


        val btnNo = view.findViewById<Button>(R.id.btnNo)
        val tvText = view.findViewById<TextView>(R.id.tvText)
        val btnYes = view.findViewById<Button>(R.id.btnYes)

        tvText.text = getString(R.string.this_comment_will_be_deleted_permanently_from_your_account)

        btnYes.setOnClickListener {
            dialog?.dismiss()
            apiDeleteComment(commentId, position)
        }

        btnNo.setOnClickListener {
            dialog?.dismiss()
        }


        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()
    }

    private fun apiPostDelete(id: Int, position: Int) {

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_PostDelete(id)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");
                        if (message == "Deleted successfully") {

                            mCommentAdapter?.removeItem(position)
                        }
                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            requireActivity(), responseObject.getString("error")
                        )
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


    private fun apiReportPost(id: Int, value: String) {
        val jsonObject = JsonObject().apply {
            addProperty("postId", id)
            addProperty("report", value)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_ReportPost(jsonObject)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {

                        UtilsFunctions().showToast(
                            requireActivity(),
                            responseObject.getString("message")
                        )

                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            requireActivity(), responseObject.getString("error")
                        )
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


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ibNotification -> {
                val intent = Intent(context, NotificationActivity::class.java)
                startActivity(intent)
            }
            R.id.ivSearch -> {
                val intent = Intent(context, SearchHomeActivity::class.java)
                startActivity(intent)
            }
        }

    }

    private fun apiHomePosts() {
        if (currentPage == 0){
            progressDialog = UtilsFunctions().showCustomProgressDialog(requireActivity())
        }
        val call: Call<ResponseBody> = RetrofitClient.getInstance(requireActivity()).myApi.api_HomePosts(currentPage, 5)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    progressDialog?.dismiss()
                    lastPage = currentPage
                    val responseJson = response.body()?.string()

                    val homePostList = Gson().fromJson(responseJson, Array<HomePostModel>::class.java).toList()
                    if (currentPage == 0){
                        mHomePostList.clear()
                    }
                    mHomePostList.addAll(homePostList)

                    if (mHomePostList.isNotEmpty()) {
                        mHomePostAdapter?.notifyDataSetChanged()
                    }


                } else {
                    progressDialog?.dismiss()
                    UtilsFunctions().handleErrorResponse(response, requireActivity())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                progressDialog?.dismiss()
            }
        })
    }

    private fun apiPostLikeUnLike(id: Int, position: Int) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_PostLikeUnLike(id)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");

                        val likesDelta = if (message == "Like successfully") 1 else -1
                        mHomePostList[position].likes += likesDelta
                        mHomePostList[position].isLiked = if (message == "Like successfully") 1 else 0

                        mHomePostAdapter?.notifyDataSetChanged()

                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            requireActivity(), responseObject.getString("error")
                        )
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


    private fun apiPostComments(id: Int, position: Int) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_PostComments(id)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    
                    val mCommentList = ArrayList(Gson().fromJson(responseJson, Array<CommentModel>::class.java).toList())

                    openCommentBottomSheet(id, mCommentList, position)

                } else {
                    UtilsFunctions().handleErrorResponse(response, requireActivity())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun openCommentBottomSheet(id: Int, mCommentList: ArrayList<CommentModel>, position: Int) {
        val dialog = context?.let { BottomSheetDialog(it, R.style.AppBottomSheetDialogTheme) }
        val view = layoutInflater.inflate(R.layout.bottomsheet_comment, null)

        val rvComment = view.findViewById<RecyclerView>(R.id.rvComment)
        val etComment = view.findViewById<EditText>(R.id.etComment)
        val ivSend = view.findViewById<ImageButton>(R.id.ivSend)



        mCommentAdapter = CommentAdapter(mCommentList, requireActivity(), this)
        rvComment.adapter = mCommentAdapter
        mCommentAdapter!!.notifyDataSetChanged()


        ivSend.setOnClickListener {
            if (etComment.text.toString().trim().isNotEmpty()) {
                apiPostWriteComments(id, etComment.text.toString().trim(), position);
                etComment.text.clear()
                dialog?.dismiss()

            }
        }



        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()
    }

    private fun apiPostWriteComments(id: Int, value: String, position: Int) {
        val jsonObject = JsonObject().apply {
            addProperty("comment", value)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_PostWriteComments(
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
                        var message = responseObject.getString("message");

                        if (message == "Updated successfully") {

                            mHomePostList[position].comments += 1
                            mHomePostAdapter?.notifyDataSetChanged()

                        }


                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            requireActivity(), responseObject.getString("error")
                        )
                    }


                } else {
                    UtilsFunctions().handleErrorResponse(response, requireActivity());
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun viewPagerImageCallback(position: Int, mPostId: Int) {
        apiPostDetails(mPostId);
    }

    private fun apiPostDetails(mPostId: Int) {
        val call: Call<ResponseBody> = RetrofitClient.getInstance(requireActivity()).myApi.api_PostDetails(mPostId)


        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    val id = responseObject.getInt("id")
                    val title = responseObject.getString("title")
                    val desc = responseObject.getString("desc")
                    val name = responseObject.getString("name")
                    val username = responseObject.getString("username")
                    val likes = responseObject.getInt("likes")
                    val instituteName = responseObject.getString("instituteName")
                    val comments = responseObject.getInt("comments")
                    val createdAt = responseObject.getString("createdAt")

                    val photosArray = responseObject.getJSONArray("photos")
                    val photoList = ArrayList<PhotoModel>()
                    for (i in 0 until photosArray.length()) {
                        val photoObject = photosArray.getJSONObject(i)
                        val pathUrl = photoObject.getString("pathUrl")
                        val photo = PhotoModel(pathUrl)
                        photoList.add(photo)
                    }


                    openPostImage(title, desc, createdAt, photoList, id, name, likes, comments)


                } else {
                    UtilsFunctions().handleErrorResponse(response, requireActivity())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun openPostImage(
        title: String,
        desc: String,
        createdAt: String,
        photosList: List<PhotoModel>,
        id: Int,
        name: String,
        likes: Int,
        comments: Int
    ) {

        var inflater = LayoutInflater.from(context)
        var popupview = inflater.inflate(R.layout.popup_post, null, false)

        var builder = PopupWindow(
            popupview,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        builder.animationStyle = R.style.DialogAnimation
        builder.setBackgroundDrawable(context?.getDrawable(R.drawable.bg_rounded_2))
        builder.showAtLocation(this.binding.root, Gravity.BOTTOM, 0, 0)

        popupview.setOnClickListener {
            builder.dismiss()
        }


        var mViewPagerAdapter: ViewPagerAdapter? = null
        var viewPager = popupview.findViewById<ViewPager>(R.id.viewPager)
        var dotsIndicator = popupview.findViewById<ScrollingPagerIndicator>(R.id.dots_indicator)
        val tvName = popupview.findViewById<TextView>(R.id.tvName)
        var tvLikesCount = popupview.findViewById<TextView>(R.id.tvLikesCount)
        var tvCommentCount = popupview.findViewById<TextView>(R.id.tvCommentCount)
        var tvDate = popupview.findViewById<TextView>(R.id.tvDate)
        var ivMore = popupview.findViewById<ImageView>(R.id.ivMore)
        var llRead = popupview.findViewById<LinearLayout>(R.id.llRead)
        var llShare = popupview.findViewById<LinearLayout>(R.id.llShare)



        /*if (!mVisitor) {


            ivMore.setImageResource(R.drawable.icon_delete)
            ivMore.setColorFilter(
                ContextCompat.getColor(requireActivity(), R.color.red),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )


        } else {
            ivMore.setImageResource(R.drawable.ic_more)
            ivMore.setColorFilter(
                ContextCompat.getColor(requireActivity(), R.color.black),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
        }
*/



        tvName.text = name
        tvLikesCount.text = likes.toString()
        tvCommentCount.text = comments.toString()
        tvDate.text = UtilsFunctions().getDDMMMMYYYY(createdAt)
        llRead.setOnClickListener {
            postDetailsCallback(title, desc);
        }

        mViewPagerAdapter = ViewPagerAdapter(requireActivity(), photosList.reversed(), builder)
        viewPager.adapter = mViewPagerAdapter
        mViewPagerAdapter.notifyDataSetChanged()
        dotsIndicator.attachToPager(viewPager)


        llShare.setOnClickListener {
            openShareBottomSheet()
            apiConnectionsList()
        }

        ivMore.setOnClickListener {
            /*if (!mVisitor) {
                DeletePostBottomSheet(id , builder)
            }*/
        }

    }

    private fun DeletePostBottomSheet(postId: Int, position: Int) {
        val dialog = context?.let { BottomSheetDialog(it, R.style.AppBottomSheetDialogTheme) }
        val view = layoutInflater.inflate(R.layout.bottomsheet_delete_post, null)


        val btnNo = view.findViewById<Button>(R.id.btnNo)
        val btnYes = view.findViewById<Button>(R.id.btnYes)

        btnYes.setOnClickListener {
            dialog?.dismiss()
            apiPostDelete(postId, position)
        }

        btnNo.setOnClickListener {
            dialog?.dismiss()
        }


        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()
    }



    private fun apiDeleteComment(commentId: Int, position: Int) {

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_DeleteComment(commentId)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");
                        if (message == "Deleted successfully") {

                            mHomePostAdapter?.removeItem(position)


                        }
                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            requireActivity(), responseObject.getString("error")
                        )
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


    private fun filter(key: String) {
        val filteredList: java.util.ArrayList<ConnectionListModel> = java.util.ArrayList()
        for (item in mConnectionListModel) {
            if (item.name.lowercase(Locale.ROOT).contains(key.lowercase(Locale.getDefault()))) {
                filteredList.add(item)
            }
            mShareAdapter?.filterList(filteredList)
        }
    }

}