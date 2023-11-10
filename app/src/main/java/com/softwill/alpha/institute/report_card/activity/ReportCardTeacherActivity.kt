package com.softwill.alpha.institute.report_card.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.common.adapter.LectureClassAdapter
import com.softwill.alpha.common.model.LectureClassModel
import com.softwill.alpha.databinding.ActivityReportCardTeacherBinding
import com.softwill.alpha.institute.classes.model.ClassInfo
import com.softwill.alpha.institute.classes.model.StudentInfo
import com.softwill.alpha.institute.report_card.adapter.ReportCardTeacherAdapter
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.profile.post.PostImageAdapter
import com.softwill.alpha.profile.post.PostImageItemModel
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ReportCardTeacherActivity : AppCompatActivity(),
    ReportCardTeacherAdapter.ReportCardAdapterCallbackInterface,
    LectureClassAdapter.LectureClassCallbackInterface {

    private lateinit var binding: ActivityReportCardTeacherBinding
    private var mReportCardTeacherAdapter: ReportCardTeacherAdapter? = null
    private var mLectureClassAdapter: LectureClassAdapter? = null


    val mLectureClassModel = ArrayList<LectureClassModel>()
    private val mStudentInfo = ArrayList<StudentInfo>()


    var mClassId: Int = -1
    var mClassName: String = ""


    var mPostImageAdapter: PostImageAdapter? = null
    private val PERMISSION_CODE = 111
    var launcher: ActivityResultLauncher<Intent>? = null
    val data = ArrayList<PostImageItemModel>()
    var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            com.softwill.alpha.R.layout.activity_report_card_teacher
        )


        setupBack()
        setupClassAdapter()
        setupReportCardAdapter()

        apiClassSubjects()



        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data!!.data
                var imagePath = uri!!.path?.let { File(it).toString() }
                println("IMAGEPATH : $imagePath")
                data.add(PostImageItemModel(imagePath.toString()))
                mPostImageAdapter?.notifyDataSetChanged()

            } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(result.data), Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun setupReportCardAdapter() {
        /*val data = ArrayList<ReportCardTeacherItemModel>()
        data.add(ReportCardTeacherItemModel("Akshay Kumar"))
        data.add(ReportCardTeacherItemModel("Lakshmikant Berde"))
        data.add(ReportCardTeacherItemModel("Vidya Balan"))
        data.add(ReportCardTeacherItemModel("Vikrant Sena"))
        data.add(ReportCardTeacherItemModel("Akshay Kumar"))
        data.add(ReportCardTeacherItemModel("Lakshmikant Berde"))
        data.add(ReportCardTeacherItemModel("Vidya Balan"))
        data.add(ReportCardTeacherItemModel("Vikrant Sena"))*/


        mReportCardTeacherAdapter = ReportCardTeacherAdapter(mStudentInfo, this@ReportCardTeacherActivity, this)
        binding.rvReportClassTeacher.adapter = mReportCardTeacherAdapter
        mReportCardTeacherAdapter!!.notifyDataSetChanged()
    }

    private fun setupClassAdapter() {
        mLectureClassAdapter = LectureClassAdapter(applicationContext, mLectureClassModel, this)
        binding.rvReportClass.adapter = mLectureClassAdapter
        mLectureClassAdapter?.notifyDataSetChanged()

    }


    override fun callback(pos: Int, name: String, studentId: Int) {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_add_report_card, null)

        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val tvClassName = view.findViewById<TextView>(R.id.tvClassName)
        val tvName = view.findViewById<TextView>(R.id.tvName)
        val etUpload = view.findViewById<EditText>(R.id.etUpload)
        val rvPostImage = view.findViewById<RecyclerView>(R.id.rvPostImage)


        mPostImageAdapter = PostImageAdapter(data, applicationContext)
        rvPostImage.adapter = mPostImageAdapter
        mPostImageAdapter!!.notifyDataSetChanged()


        tvClassName.text = mClassName
        tvName.text = name


        etUpload.setOnClickListener {
            if (data.size <= 0) {
                askStoragePermission()
            } else {
                UtilsFunctions().showToast(this@ReportCardTeacherActivity, "Max 1 Report card upload")
            }

        }


        btnSave.setOnClickListener {
            if (data.isNotEmpty()) {

                dialog.dismiss()
                apiAddReportCard(studentId.toString())


            } else {
                UtilsFunctions().showToast(this@ReportCardTeacherActivity, "please add report card")
            }

        }





        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()

    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_report_card)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
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

    override fun lectureClassClickCallback(classId: Int, position: Int, subjectId: Int, className : String) {
        mClassId = classId
        mClassName = className
        apiClassStudentList()

    }

    private fun apiClassSubjects() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ReportCardTeacherActivity).myApi.api_ClassSubjects()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<LectureClassModel>>() {}.type
                        val mList: List<LectureClassModel> = Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mLectureClassModel.clear()
                        mLectureClassModel.addAll(mList)


                        if (mLectureClassModel.isNotEmpty()) {
                            mClassId = mLectureClassModel[0].classId
                            mClassName = mLectureClassModel[0].className
                            mLectureClassAdapter?.notifyDataSetChanged()
                            apiClassStudentList()
                        }
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@ReportCardTeacherActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun apiClassStudentList() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ReportCardTeacherActivity).myApi.api_ClassStudentListByTeacher(mClassId);

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {

                        val classInfo =  Gson().fromJson(responseBody, ClassInfo::class.java)



                        if (!classInfo.students.isNullOrEmpty()) {
                            binding.rvReportClassTeacher.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                            mReportCardTeacherAdapter?.updateData(classInfo.students)
                        }else{
                            binding.rvReportClassTeacher.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }else{
                        binding.rvReportClassTeacher.visibility = View.GONE
                        binding.tvNoData.visibility = View.VISIBLE
                    }

                } else {
                    binding.rvReportClassTeacher.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@ReportCardTeacherActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun askStoragePermission() {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@ReportCardTeacherActivity,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSION_CODE
            )
        } else {
            ImagePicker.Companion.with(this@ReportCardTeacherActivity)
                .crop()
                .setMultipleAllowed(false)
                .maxResultSize(512, 1024, true)
                .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                .createIntentFromDialog { launcher?.launch(it) }
        }
    }


    private fun apiAddReportCard(mStudentId: String) {
        progressDialog = UtilsFunctions().showCustomProgressDialog(this@ReportCardTeacherActivity)

        val studentId : RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), mStudentId)
        val classId: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), mClassId.toString())

        val fileToUpload: MutableList<MultipartBody.Part> = ArrayList()


        for (image in data) {
            var file = File(image.Image)
            val imageRequest: MultipartBody.Part = prepareFilePart("reporCardFile", file)
            fileToUpload.add(imageRequest)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ReportCardTeacherActivity).myApi.api_AddReportCard(
                fileToUpload,
                studentId,
                classId,
            )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    progressDialog?.dismiss()

                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");
                        if (message == "Added successfully") {
                            data.clear()
                            UtilsFunctions().showToast(this@ReportCardTeacherActivity, message)
                        }
                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            this@ReportCardTeacherActivity, responseObject.getString("error")
                        )
                    }

                } else {
                    progressDialog?.dismiss()
                    UtilsFunctions().handleErrorResponse(response, this@ReportCardTeacherActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                progressDialog?.dismiss()
            }
        })
    }

    private fun prepareFilePart(partName: String, file: File): MultipartBody.Part {
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }


}