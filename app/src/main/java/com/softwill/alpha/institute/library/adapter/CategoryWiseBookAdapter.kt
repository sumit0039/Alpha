package com.softwill.alpha.institute.library.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemLibraryClassBinding
import com.softwill.alpha.institute.library.Model.Book
import com.softwill.alpha.institute.library.activity.ReadBookActivity
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CategoryWiseBookAdapter(
    private val context: Context,
    private var mList: ArrayList<Book>,
) :
    RecyclerView.Adapter<CategoryWiseBookAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): CategoryWiseBookAdapter.ViewHolder {
        val binding: ItemLibraryClassBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_library_class,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                Glide.with(context).load(thumbnailUrl).placeholder(R.drawable.icon_no_image)
                    .into(binding.ivImage)


                binding.tvBookTitle.text = name

                if (isSaved == 1) {
                    binding.ivSaved.setImageResource(com.softwill.alpha.R.drawable.icon_saved)
                } else {
                    binding.ivSaved.setImageResource(com.softwill.alpha.R.drawable.icon_save)
                }

                binding.ivSaved.setOnClickListener {
                    if (isSaved == 1) {
                        apiSaveRemoveBook(id, "remove", position)
                    } else {
                        apiSaveRemoveBook(id, "add", position)

                    }
                }

                holder.itemView.setOnClickListener {
                    val intent = Intent(context, ReadBookActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                    intent.putExtra("mScreenTitle", "Read Book")
                    intent.putExtra("mBookId", id)
                    intent.putExtra("mThumbnailUrl", thumbnailUrl)
                    intent.putExtra("mPdfUrl", pdfUrl)
                    intent.putExtra("mName", name)
                    intent.putExtra("mWriterName", writerName)
                    context.startActivity(intent)
                }

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemLibraryClassBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

    private fun apiSaveRemoveBook(id: Int, status: String, position: Int) {

        val jsonObject = JsonObject().apply {
            addProperty("status", status)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(context).myApi.api_SaveRemoveBook(
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

                        if (message == "Save successfully") {
                            mList[position].isSaved = 1
                            notifyDataSetChanged()
                        } else if (message == "Remove successfully") {
                            mList[position].isSaved = 0
                            notifyDataSetChanged()
                        }

                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            context, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, context)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

}