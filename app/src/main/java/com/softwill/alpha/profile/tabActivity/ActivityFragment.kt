package com.softwill.alpha.profile.tabActivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.databinding.FragmentActivityBinding
import com.softwill.alpha.home.adapter.CommentAdapter
import com.softwill.alpha.home.adapter.ShareAdapter
import com.softwill.alpha.home.adapter.ViewPagerAdapter
import com.softwill.alpha.home.model.CommentModel
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.profile_guest.adapter.ConnectionListModel
import com.softwill.alpha.profile_guest.model.GuestUserDetailsResponse
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import com.softwill.alpha.utils.YourPreference
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator
import java.util.*


class ActivityFragment : Fragment(), ActivityPostAdapter.CallbackInterface , CommentAdapter.CommentCallbackInterface{

    private lateinit var binding: FragmentActivityBinding
    lateinit var mActivityPostAdapter: ActivityPostAdapter
    var mIsGuestUser: Boolean = false
    val mPostList = ArrayList<PostModel>()
    var yourPreference: YourPreference? = null
    var mCommentAdapter: CommentAdapter? = null
    var mCommentCount : Int = 0
    var mLikeCount : Int = 0
    var mName : String = ""
    lateinit var ivLiked : ImageView
    lateinit var tvLikesCount : TextView
    var mUserId: Int = -1
    var mGuestProfileImage : String = ""

    val mConnectionListModel = java.util.ArrayList<ConnectionListModel>()
    var mShareAdapter: ShareAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_activity, container, false);
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        yourPreference = YourPreference(requireActivity())

        mPostList.clear()
        mActivityPostAdapter = ActivityPostAdapter(this, this, mPostList)
        binding.rvActivity.adapter = mActivityPostAdapter
        mActivityPostAdapter.notifyDataSetChanged()


        if (mIsGuestUser) {
            apiGuestUserDetails()
        }else{
            apiPosts();
        }



    }

    override fun onImageCallback(
        position: Int,
        id: Int,
    ) {
        apiPostDetails(id)
    }

    private fun apiPostDetails(mPostId: Int) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_PostDetails(mPostId)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    val id = responseObject.getInt("id")
                    val title = responseObject.getString("title")
                    val desc = responseObject.getString("desc")
                    mName = responseObject.getString("name")
                    val username = responseObject.getString("username")
                    mLikeCount = responseObject.getInt("likes")
                    val instituteName = responseObject.getString("instituteName")
                    mCommentCount = responseObject.getInt("comments")
                    val createdAt = responseObject.getString("createdAt")
                    val isMyPost = responseObject.getInt("isMyPost")
                    val isLiked = responseObject.getInt("isLiked")

                    val photosArray = responseObject.getJSONArray("photos")
                    val photoList = ArrayList<PhotoModel>()
                    for (i in 0 until photosArray.length()) {
                        val photoObject = photosArray.getJSONObject(i)
                        val pathUrl = photoObject.getString("pathUrl")
                        val photo = PhotoModel(pathUrl)
                        photoList.add(photo)
                    }

                    openPostImage(title, desc, mIsGuestUser, createdAt, photoList, id, isLiked)


                } else {
                    UtilsFunctions().handleErrorResponse(response, requireActivity())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun openPostImage(
        title: String,
        desc: String,
        mVisitor: Boolean,
        createdAt: String,
        photosList: List<PhotoModel>,
        id: Int,
        isLiked: Int,
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
        val viewPager = popupview.findViewById<ViewPager>(R.id.viewPager)
        val dotsIndicator = popupview.findViewById<ScrollingPagerIndicator>(R.id.dots_indicator)
        val tvName = popupview.findViewById<TextView>(R.id.tvName)
        val tvDate = popupview.findViewById<TextView>(R.id.tvDate)
        tvLikesCount = popupview.findViewById(R.id.tvLikesCount)
        val tvCommentCount = popupview.findViewById<TextView>(R.id.tvCommentCount)
        val ivMore = popupview.findViewById<ImageView>(R.id.ivMore)
        val llRead = popupview.findViewById<LinearLayout>(R.id.llRead)
        val llComment = popupview.findViewById<LinearLayout>(R.id.llComment)
        val ivProfileImage = popupview.findViewById<CircleImageView>(R.id.ivProfileImage)
        ivLiked = popupview.findViewById(R.id.ivLiked)
        val llLikeUnlike = popupview.findViewById<LinearLayout>(R.id.llLikeUnlike)
        var llShare = popupview.findViewById<LinearLayout>(R.id.llShare)

        if (!mVisitor) {
            ivMore.setImageResource(R.drawable.icon_delete)
            ivMore.setColorFilter(
                ContextCompat.getColor(requireActivity(), R.color.red),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            Glide.with(requireActivity()).load(yourPreference?.getData(Constant.avtarUrl)).placeholder(R.drawable.icon_no_image).into(ivProfileImage)

        } else {
            ivMore.setImageResource(R.drawable.ic_more)
            ivMore.setColorFilter(
                ContextCompat.getColor(requireActivity(), R.color.black),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )

            Glide.with(requireActivity()).load(mGuestProfileImage).placeholder(R.drawable.icon_no_image).into(ivProfileImage)
        }


        tvName.text = mName
        tvDate.text = UtilsFunctions().getDDMMMMYYYY(createdAt)
        tvLikesCount.text = mLikeCount.toString()
        tvCommentCount.text = mCommentCount.toString()

        if (isLiked == 1) {
            ivLiked.setImageResource(R.drawable.icon_liked)
        } else {
            ivLiked.setImageResource(R.drawable.icon_like)
        }



        llRead.setOnClickListener {
            postDetailsCallback(title, desc);
        }

        llComment.setOnClickListener {
            apiPostComments(id, tvCommentCount)
        }

        llLikeUnlike.setOnClickListener {
            apiPostLikeUnLike(id)
        }

        mViewPagerAdapter = ViewPagerAdapter(requireActivity(), photosList.reversed(), builder)
        viewPager.adapter = mViewPagerAdapter
        mViewPagerAdapter.notifyDataSetChanged()
        dotsIndicator.attachToPager(viewPager)



        ivMore.setOnClickListener {
            if (!mVisitor) {
                DeletePostBottomSheet(id , builder)
            }
        }

        llShare.setOnClickListener {
            openShareBottomSheet()
            apiConnectionsList()
        }


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


    private fun apiPostLikeUnLike(id: Int) {
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
                        mLikeCount += likesDelta

                        if (message == "Like successfully") {
                            ivLiked.setImageResource(R.drawable.icon_liked)
                        } else {
                            ivLiked.setImageResource(R.drawable.icon_like)
                        }

                        tvLikesCount.text = mLikeCount.toString()

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


    private fun DeletePostBottomSheet(id: Int, builder: PopupWindow) {
        val dialog = context?.let { BottomSheetDialog(it, R.style.AppBottomSheetDialogTheme) }
        val view = layoutInflater.inflate(R.layout.bottomsheet_delete_post, null)


        val btnNo = view.findViewById<Button>(R.id.btnNo)
        val btnYes = view.findViewById<Button>(R.id.btnYes)

        btnYes.setOnClickListener {
            dialog?.dismiss()
            builder.dismiss()
            apiPostDelete(id)
        }

        btnNo.setOnClickListener {
            dialog?.dismiss()
        }


        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()
    }


    private fun postDetailsCallback(title: String, desc: String) {

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


    companion object {
        fun newInstance(mIsGuestUser: Boolean, mUserId: Int): ActivityFragment {
            val fragment = ActivityFragment()
            fragment.mIsGuestUser = mIsGuestUser
            fragment.mUserId = mUserId
            return fragment
        }
    }

    private fun apiPosts() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_posts()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()

                    val postList = Gson().fromJson(responseJson, Array<PostModel>::class.java).toList()
                    mPostList.clear()

                    for (post in postList) {
                        if (post.photos.isNotEmpty()) {
                            mPostList.add(post)
                        }
                    }


                    if (mPostList.isNotEmpty()) {
                        mActivityPostAdapter.notifyDataSetChanged()
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

    private fun apiPostDelete(id: Int) {


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
                            apiPosts()
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



    private fun apiPostComments(id: Int, tvCommentCount: TextView) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_PostComments(id)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()

                    val mCommentList = ArrayList(Gson().fromJson(responseJson, Array<CommentModel>::class.java).toList())

                    openCommentBottomSheet(id, mCommentList,  tvCommentCount)

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
    private fun openCommentBottomSheet(
        id: Int,
        mCommentList: ArrayList<CommentModel>,
        tvCommentCount: TextView
    ) {
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
                apiPostWriteComments(id, etComment.text.toString().trim(), tvCommentCount);
                etComment.text.clear()
                dialog?.dismiss()

            }
        }



        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()
    }

    private fun apiPostWriteComments(
        id: Int,
        value: String,
        tvCommentCount: TextView
    ) {
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

                            mCommentCount += 1
                            tvCommentCount.text  = mCommentCount.toString()

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



    override fun onResume() {
        if (mIsGuestUser) {
            apiGuestUserDetails()
        }else{
            apiPosts();
        }
        super.onResume()
    }

    override fun deleteCommentCallback(commentId: Int, position: Int) {
        DeleteCommentBottomSheet(commentId, position);
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


    private fun apiGuestUserDetails() {

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireActivity()).myApi.api_GuestUserDetails(mUserId)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = Gson().fromJson(responseJson, GuestUserDetailsResponse::class.java)
                    mPostList.clear()

                    mGuestProfileImage = responseObject.avtarUrl.toString()

                    for (post in responseObject.posts!!) {
                        if (post.photos.isNotEmpty()) {
                            mPostList.add(post)
                        }
                    }

                    if (mPostList.isNotEmpty()) {
                        mActivityPostAdapter.notifyDataSetChanged()
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