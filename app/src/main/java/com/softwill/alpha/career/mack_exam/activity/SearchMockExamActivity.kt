package com.softwill.alpha.career.mack_exam.activity

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.career.mack_exam.adapter.MockExamAdapter
import com.softwill.alpha.career.mack_exam.model.MockExamModel
import com.softwill.alpha.databinding.ActivitySearchMockExamBinding
import com.softwill.alpha.institute.online_exam.student.activity.StudentExam2Activity
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchMockExamActivity : AppCompatActivity(), MockExamAdapter.CallbackInterface {


    private lateinit var binding: ActivitySearchMockExamBinding
    var mMockExamAdapter: MockExamAdapter? = null

    val mMockExamModel = java.util.ArrayList<MockExamModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(
                this,
                com.softwill.alpha.R.layout.activity_search_mock_exam
            )



        setupBack()
        setupAdapter()

    }

    private fun setupAdapter() {
        mMockExamAdapter = MockExamAdapter(mMockExamModel, this@SearchMockExamActivity, this)
        binding.rvSearchMockExam.adapter = mMockExamAdapter
        mMockExamAdapter?.notifyDataSetChanged()

    }

    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(com.softwill.alpha.R.drawable.ic_arrow_back)
        actionBar?.setDisplayUseLogoEnabled(true);


    }


    private fun apiSearchMockExam(searchText: String) {
        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@SearchMockExamActivity)
            .myApi.api_SearchMockExam(searchText)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    if (!responseJson.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<MockExamModel>>() {}.type
                        val mList: List<MockExamModel> = Gson().fromJson(responseJson, listType)

                        // Update your mEntranceExamList with the new data
                        mMockExamModel.clear()
                        mMockExamModel.addAll(mList)

                        if (mMockExamModel.isNotEmpty()) {
                            binding.tvExam.visibility = View.VISIBLE
                            binding.rvSearchMockExam.visibility = View.VISIBLE
                            binding.llNoData.visibility = View.GONE
                            mMockExamAdapter?.notifyDataSetChanged()
                        } else {
                            binding.tvExam.visibility = View.GONE
                            binding.rvSearchMockExam.visibility = View.GONE
                            binding.llNoData.visibility = View.VISIBLE
                        }
                    } else {
                        binding.tvExam.visibility = View.GONE
                        binding.rvSearchMockExam.visibility = View.GONE
                        binding.llNoData.visibility = View.VISIBLE
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@SearchMockExamActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.softwill.alpha.R.menu.search_menu2, menu)
        val searchItem: MenuItem? = menu?.findItem(com.softwill.alpha.R.id.menu_search2)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView: SearchView = searchItem?.actionView as SearchView
        searchView.isIconified = false;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.length >= 3) {
                    apiSearchMockExam(newText)
                } else {
                    binding.tvExam.visibility = View.GONE
                    binding.rvSearchMockExam.visibility = View.GONE
                    binding.llNoData.visibility = View.VISIBLE
                }
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

        })






        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSolveCallback(position: Int, examId: Int, subject: String) {
        addConfirmationBottomSheet("MockExam", examId, subject)
    }


    private fun addConfirmationBottomSheet(type: String, examId: Int, subject: String) {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_start_exam, null)


        val subTitle = view.findViewById<TextView>(R.id.textView10)
        val btnNo = view.findViewById<Button>(R.id.btnNo)
        val btnYes = view.findViewById<Button>(R.id.btnYes)


        if (type == "OnlineExam") {
            subTitle.text = resources.getText(R.string.do_you_want_to_start_the_exam)
        } else {
            subTitle.text = resources.getText(R.string.do_you_want_to_start_the_mock_exam)
        }



        btnYes.setOnClickListener {
            dialog.dismiss()

            val intent = Intent(applicationContext, StudentExam2Activity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
            intent.putExtra("mFrom", type)
            intent.putExtra("mExamId", examId)
            intent.putExtra("mSubject", subject)
            startActivity(intent)
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }


        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

}