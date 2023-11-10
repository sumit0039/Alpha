package com.softwill.alpha.profile_guest.activity

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityConnectionsBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.profile_guest.adapter.ConnectionAdapter
import com.softwill.alpha.profile_guest.adapter.ConnectionListModel
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ConnectionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConnectionsBinding
    var mConnectionAdapter: ConnectionAdapter? = null
    private var mUserId: Int = -1
    val mConnectionListModel = ArrayList<ConnectionListModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_connections)

        setupBack()

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUserId = bundle.getInt("mUserId", -1)
        }

        setupConnections()

        apiConnectionsList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupConnections() {
        mConnectionAdapter = ConnectionAdapter(this@ConnectionsActivity, mConnectionListModel , mUserId , this@ConnectionsActivity)
        binding.rvConnections.adapter = mConnectionAdapter
        binding.rvConnections.addItemDecoration(
            DividerItemDecoration(
                binding.rvConnections.context,
                DividerItemDecoration.VERTICAL
            )
        )
        mConnectionAdapter!!.notifyDataSetChanged()
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_Connections)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

    }

    private fun apiConnectionsList() {
        val retrofit = RetrofitClient.getInstance(this@ConnectionsActivity).myApi
        val call: Call<ResponseBody> = when (mUserId) {
            -1 -> retrofit.api_ConnectionsList()
            else -> retrofit.api_GuestConnectionsList(mUserId)
        }


        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val connection = Gson().fromJson(responseJson, Array<ConnectionListModel>::class.java).toList()
                    mConnectionListModel.clear()
                    mConnectionListModel.addAll(connection)

                    if (mConnectionListModel.isNotEmpty()) {
                        mConnectionAdapter?.notifyDataSetChanged()
                    }

                    binding.tvListSize.text = mConnectionListModel.size.toString().padStart(2, '0')


                    println("Total Connection Count : ${mConnectionListModel.size}")

                } else {
                    UtilsFunctions().handleErrorResponse(response, this@ConnectionsActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }




    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchItem: MenuItem? = menu?.findItem(R.id.menu_search)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView: androidx.appcompat.widget.SearchView =
            searchItem?.actionView as androidx.appcompat.widget.SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                filter(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    fun apiRemoveConnection(userId: Int, position: Int) {
        val jsonObject = JsonObject().apply {
            addProperty("userId", userId)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ConnectionsActivity).myApi.api_RemoveConnection(
                jsonObject
            )


        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {

                        if (responseObject.getString("message") == "Cancelled successfully") {
                            mConnectionAdapter?.removeItem(position)
                            apiConnectionsList()
                        }
                    }

                } else {
                    UtilsFunctions().handleErrorResponse(response, this@ConnectionsActivity)
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
            mConnectionAdapter?.filterList(filteredList)
        }
    }



}