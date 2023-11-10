package com.softwill.alpha.career.best_college.adapter

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
import com.softwill.alpha.career.best_college.model.BestCollegeModel
import com.softwill.alpha.databinding.ItemBestCollegeBinding
import com.softwill.alpha.institute_detail.CollegeDetailsActivity
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BestCollegeAdapter(
    private var mList: ArrayList<BestCollegeModel>,
    private val context: Context,
) :
    RecyclerView.Adapter<BestCollegeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: ItemBestCollegeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            com.softwill.alpha.R.layout.item_best_college,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {

                binding.tvCollegeName.text = instituteName

                if (isFavourite == 1) {
                    binding.ivFav.setImageResource(com.softwill.alpha.R.drawable.icon_favourite)
                } else {
                    binding.ivFav.setImageResource(com.softwill.alpha.R.drawable.icon_unfavourite)
                }

                Glide.with(context).load(user.avatarUrl).placeholder(R.drawable.icon_no_image).into(binding.image)

                binding.ivFav.setOnClickListener {
                    if (isFavourite == 1) {
                        apiAddRemoveFavCollage(id, "remove", position)
                    } else {
                        apiAddRemoveFavCollage(id, "add", position)

                    }
                }

                holder.itemView.setOnClickListener{
                    val intent = Intent(context, CollegeDetailsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                    intent.putExtra("mMyInstitute", false)
                    intent.putExtra("mInstituteId", id)
                    context.startActivity(intent)
                }


                binding.tvFaculties.text = ""
                for (i in faculties.indices){
                    binding.tvFaculties.append("${i + 1}. ${faculties[i].facultyName}")
                }

                if (instituteRating != null){
                    binding.RatingBar.rating = instituteRating.toFloat()
                }

            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemBestCollegeBinding) :
        RecyclerView.ViewHolder(binding.root)


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }

    //Filter New
    fun filterList(filteredList: ArrayList<BestCollegeModel>) {
        mList = filteredList
        notifyDataSetChanged()
    }



    private fun apiAddRemoveFavCollage(id: Int, status: String, position: Int) {

        val jsonObject = JsonObject().apply {
            addProperty("instituteId", id)
            addProperty("status", status)
        }


        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(context).myApi.api_AddRemoveFavCollage(
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

                        if (message == "Added favourite collage successfully") {
                            mList[position].isFavourite = 1
                            notifyDataSetChanged()
                        } else if (message == "Removed favourite collage successfully") {
                            mList[position].isFavourite = 0
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