package com.softwill.alpha.profile_guest.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemConnectionsBinding
import com.softwill.alpha.profile_guest.activity.ConnectionsActivity
import com.softwill.alpha.profile_guest.activity.ProfileGuestActivity
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.YourPreference


class ConnectionAdapter(
    private val context: Context,
    private var mList: ArrayList<ConnectionListModel>,
    private val mUserId: Int,
    private  val connectionsActivity: ConnectionsActivity,


    ) : RecyclerView.Adapter<ConnectionAdapter.ViewHolder>() {

    var yourPreference: YourPreference? = null



    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemConnectionsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_connections, parent, false
        )
        return ViewHolder(binding)
    }


    // binds the list items to a view

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            yourPreference = YourPreference(context)

            with(mList[position]) {


                binding.tvName.text = name
                binding.tvUsername.text = userName


                Glide.with(context).load(avtarUrl)
                    .placeholder(R.drawable.icon_avatar).into(binding.ivProfileImage)

                /*if (position % 2 == 0) {
                    binding.btnConnect.text = "Connect"
                    binding.btnConnect.backgroundTintList =
                        context.getResources().getColorStateList(R.color.primary_color);

                } else {
                    binding.btnConnect.text = "Remove"
                    binding.btnConnect.backgroundTintList =
                        context.getResources().getColorStateList(R.color.colorYellow);
                }*/

                if (mUserId != -1 && yourPreference!!.getData(Constant.userId).toInt() == userId){
                    binding.btnRemove.visibility = View.INVISIBLE
                }


                binding.btnRemove.setOnClickListener {
                    connectionsActivity.apiRemoveConnection(userId, position)
                }

                holder.itemView.setOnClickListener {
                    val intent = Intent(context, ProfileGuestActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                    intent.putExtra("mUserId", userId)
                    context.startActivity(intent)
                }
            }

        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(val binding: ItemConnectionsBinding) :
        RecyclerView.ViewHolder(binding.root)


    fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }

    fun filterList(filteredList: ArrayList<ConnectionListModel>) {
        mList = filteredList
        notifyDataSetChanged()
    }


}


