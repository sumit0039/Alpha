package com.softwill.alpha.profile.post

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityPostBinding
import com.softwill.alpha.networking.RetrofitClient
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


class PostActivity : AppCompatActivity() {


    private lateinit var binding: ActivityPostBinding
    var mPostImageAdapter: PostImageAdapter? = null
    private val PERMISSION_CODE = 100
    var launcher: ActivityResultLauncher<Intent>? = null
    val dataImage = ArrayList<PostImageItemModel>()
    var progressDialog: Dialog? = null


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post)


        setupBack()
        onClickListener()



        mPostImageAdapter = PostImageAdapter(dataImage, applicationContext)
        binding.rvPostImage.adapter = mPostImageAdapter
        mPostImageAdapter!!.notifyDataSetChanged()


        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data!!.data
                var imagePath = uri!!.path?.let { File(it).toString() }
                println("IMAGEPATH : $imagePath")
                dataImage.add(PostImageItemModel(imagePath.toString()))
                mPostImageAdapter?.notifyDataSetChanged()

            } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(result.data), Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun onClickListener() {
        binding.etUpload.setOnClickListener {
            if (dataImage.size <= 2) {
//                askStoragePermission()
                getImageFromGalleryAndCamera()
            } else {
                UtilsFunctions().showToast(this@PostActivity, "Max 3 Image upload")
            }

        }

        binding.btnShare.setOnClickListener {
            if (dataImage.isNotEmpty()) {

                var value = binding.etTitle.text.toString().trim()

                if (value.isEmpty()) {
                    UtilsFunctions().showToast(this@PostActivity, "Title can't be empty")
                } else if (value.length < 3) {
                    UtilsFunctions().showToast(
                        this@PostActivity,
                        "Title must contains 3 letters"
                    )
                } else {


                    apiPostCreate(
                        value,
                        binding.etAboutPost.text.toString().trim(),
                    )
                }


            } else {
                UtilsFunctions().showToast(this@PostActivity, "No image to upload")
            }

        }

    }
    //open camera and gallery
    private fun getImageFromGalleryAndCamera() {
        com.github.dhaval2404.imagepicker.ImagePicker.with(this)
            .crop() //Crop image(Optional), Check Customization for more option
            .compress(1024) //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            ) //Final image resolution will be less than 1080 x 1080(Optional)
            .start()

    }
    //set image in imageview
    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            // Get the url from data
            val uri = data!!.data
            var imagePath = uri!!.path?.let { File(it).toString() }
            println("IMAGEPATH : $imagePath")
            dataImage.add(PostImageItemModel(imagePath.toString()))
            mPostImageAdapter?.notifyDataSetChanged()

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        }

    }
    private fun askStoragePermission() {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@PostActivity,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSION_CODE
            )
        } else {
            ImagePicker.Companion.with(this@PostActivity)
                .crop()
                .setMultipleAllowed(false)
                .cropSquare()
                .maxResultSize(512, 512, true)
                .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                .createIntentFromDialog { launcher?.launch(it) }
        }
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_post)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                dataImage.clear()
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun apiPostCreate(title: String, desc: String) {
        progressDialog = UtilsFunctions().showCustomProgressDialog(this@PostActivity)

        val title: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), title)
        val desc: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), desc)

        val fileToUpload: MutableList<MultipartBody.Part> = ArrayList()


        for (image in dataImage) {
            var file = File(image.Image)
            val imageRequest: MultipartBody.Part = prepareFilePart("files", file)
            fileToUpload.add(imageRequest)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@PostActivity).myApi.api_PostCreate(
                title,
                desc,
                fileToUpload
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
                            UtilsFunctions().showToast(this@PostActivity, message)
                            finish()
                        }
                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            this@PostActivity, responseObject.getString("error")
                        )
                    }

                } else {
                    progressDialog?.dismiss()
                    UtilsFunctions().handleErrorResponse(response, this@PostActivity)
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