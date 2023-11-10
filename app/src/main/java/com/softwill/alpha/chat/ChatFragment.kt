package com.softwill.alpha.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.softwill.alpha.R
import com.softwill.alpha.databinding.FragmentChatBinding
import com.softwill.alpha.profile.privacy.manageNotifications.ManageNotificationsActivity


class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    var mChatAdapter: ChatAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClickListener()

        val data = ArrayList<ChatItemModel>()

        data.add(ChatItemModel("Tony Stark", "10:30 am", false, 2))
        data.add(ChatItemModel("Ant-Man", "10:30 am", false, 2))
        data.add(ChatItemModel("Aquaman", "10:30 am", true, 2))
        data.add(ChatItemModel("Batman", "10:30 am", false, 2))
        data.add(ChatItemModel("Black Panther", "10:30 am", false, 2))
        data.add(ChatItemModel("Captain America", "12:34 am", true, 8))
        data.add(ChatItemModel("Captain Marvel", "10:13 am", false, 2))
        data.add(ChatItemModel("Green Lantern", "10:32 am", true, 1))
        data.add(ChatItemModel("Ghost Rider", "10:12 am", false, 2))
        data.add(ChatItemModel("Spider-Man", "12:32 pm", false, 2))
        data.add(ChatItemModel("Wonder Woman", "9:00 am", false, 2))

        mChatAdapter = context?.let { ChatAdapter(data, it) }
        binding.rvChat.adapter = mChatAdapter
        mChatAdapter!!.notifyDataSetChanged()

    }

    private fun onClickListener() {
        binding.ivMenu.setOnClickListener {
            val popupMenu = PopupMenu(activity, binding.ivMenu)

            popupMenu.menuInflater.inflate(R.menu.chat_menu, popupMenu.getMenu())
            popupMenu.setOnMenuItemClickListener {
                val intent = Intent(activity, ManageNotificationsActivity::class.java)
                startActivity(intent)
                true
            }
            popupMenu.show()
        }
    }

}