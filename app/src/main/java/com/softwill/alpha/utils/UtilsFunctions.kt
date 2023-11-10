package com.softwill.alpha.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.softwill.alpha.R
import com.softwill.alpha.career.career_guidance.model.FacultyModel2
import com.softwill.alpha.career.career_guidance.model.StreamModel2
import com.softwill.alpha.profile.privacy.blockedPeople.BlockedUserInfo
import com.softwill.alpha.profile.privacy.blockedPeople.BlockedUserResponse
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit


class UtilsFunctions {

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }


    fun upTo2digits(string: String): String {
        return string.padStart(2, '0')
    }

    fun showDatePicker(context: Context, editText: EditText, isTodayEnable: Boolean) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(context, { _, year, monthOfYear, dayOfMonth ->
            val formattedMonth = (monthOfYear + 1).toString().padStart(2, '0')
            val formattedDay = dayOfMonth.toString().padStart(2, '0')
            val selectedDate = "$year-$formattedMonth-$formattedDay"
            editText.setText(selectedDate)
        }, year, month, day)

        val tomorrowCalendar = Calendar.getInstance()
        if (!isTodayEnable) {
            tomorrowCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        dpd.datePicker.minDate = tomorrowCalendar.timeInMillis

        dpd.show()
    }


    fun showTimePicker(context: Context, editText: EditText) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val tpd = TimePickerDialog(context, { _, hourOfDay, minuteOfDay ->
            val formattedHour = hourOfDay.toString().padStart(2, '0')
            val formattedMinute = minuteOfDay.toString().padStart(2, '0')
            val selectedTime = "$formattedHour:$formattedMinute"
            editText.setText(selectedTime)
        }, hour, minute, true)

        tpd.show()
    }

    fun showEndTimePicker(context: Context, editText: EditText, startTime: String) {
        val initialTimeParts = startTime.split(":")
        val initialHour = initialTimeParts[0].toInt()
        val initialMinute = initialTimeParts[1].toInt()

        val endTimePicker = TimePickerDialog(context, { _, hourOfDay, minute ->
            val selectedTime = String.format("%02d:%02d", hourOfDay, minute)

            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val startDateTime = sdf.parse(startTime)
            val endDateTime = sdf.parse(selectedTime)

            if (endDateTime != null) {
                if (endDateTime.after(startDateTime)) {
                    editText.setText(selectedTime)
                } else {
                    UtilsFunctions().showToast(context, "End time can't be before start time")
                }
            }
        }, initialHour, initialMinute, true)

        endTimePicker.show()
    }

    fun showCustomProgressDialog(context: Context): Dialog {
        val progressDialog = Dialog(context)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setContentView(R.layout.progress_dialog)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        return progressDialog
    }



    fun getHHMMA2(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun getHHMMA(dateString: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }


    fun getDD(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd", Locale.getDefault())

        // Set the time zone to GMT+05:30 for the input format
        val timeZone = TimeZone.getTimeZone("GMT+05:30")
        inputFormat.timeZone = timeZone

        // Create a Calendar instance to manipulate the date
        val calendar = Calendar.getInstance(timeZone)

        return try {
            val date = inputFormat.parse(dateString)
            calendar.time = date

            // Add GMT+05:30 offset to the date
            calendar.add(Calendar.HOUR_OF_DAY, 5)
            calendar.add(Calendar.MINUTE, 30)

            // Get the updated date in the desired format
            outputFormat.format(calendar.time)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }


    fun getMMM(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM", Locale.getDefault())

        // Set the time zone to GMT+05:30 for the input format
        val timeZone = TimeZone.getTimeZone("GMT+05:30")
        inputFormat.timeZone = timeZone

        // Create a Calendar instance to manipulate the date
        val calendar = Calendar.getInstance(timeZone)

        return try {
            val date = inputFormat.parse(dateString)
            calendar.time = date

            // Add GMT+05:30 offset to the date
            calendar.add(Calendar.HOUR_OF_DAY, 5)
            calendar.add(Calendar.MINUTE, 30)

            // Get the updated date in the desired format
            outputFormat.format(calendar.time)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }


    fun getDDMMM(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

        // Set the time zone to GMT+05:30 for the input format
        val timeZone = TimeZone.getTimeZone("GMT+05:30")
        inputFormat.timeZone = timeZone

        // Create a Calendar instance to manipulate the date
        val calendar = Calendar.getInstance(timeZone)

        return try {
            val date = inputFormat.parse(dateString)
            calendar.time = date

            // Add GMT+05:30 offset to the date
            calendar.add(Calendar.HOUR_OF_DAY, 5)
            calendar.add(Calendar.MINUTE, 30)

            // Get the updated date in the desired format
            outputFormat.format(calendar.time)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun getDDMMMMYYYY(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun getDDMMMEEEEYYYY(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE dd MMM yyyy", Locale.getDefault())

        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }


    fun getDDMMMYYYY(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun getTimeAgoAndConvertToTimeZone(timestamp: String): String {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val utcDate = utcFormat.parse(timestamp)

        val currentTime = System.currentTimeMillis()
        val timeDiffInMillis = currentTime - utcDate.time

        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeDiffInMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiffInMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(timeDiffInMillis)
        val days = TimeUnit.MILLISECONDS.toDays(timeDiffInMillis)

        val gmtTimeZone = TimeZone.getTimeZone("GMT+05:30")
        val gmtFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        gmtFormat.timeZone = gmtTimeZone
        val gmtDate = gmtFormat.format(utcDate)

        val gmtPlus0530TimeZone = TimeZone.getTimeZone("GMT+05:30")
        val gmtPlus0530Format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        gmtPlus0530Format.timeZone = gmtPlus0530TimeZone
        val gmtPlus0530Date = gmtPlus0530Format.parse(gmtDate)

        val currentTimeZoneFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        currentTimeZoneFormat.timeZone = gmtPlus0530TimeZone
        val gmtPlus0530Time = currentTimeZoneFormat.format(gmtPlus0530Date)

        val timeAgo = when {
            days > 0 -> "$days days ago"
            hours > 0 -> "$hours hours ago"
            minutes > 0 -> "$minutes mins ago"
            else -> "$seconds secs ago"
        }

        return timeAgo
    }


    fun calculateTimeDifference(startTime: String, endTime: String): Pair<String, String> {
        val timeFormat = SimpleDateFormat("HH:mm:ss")
        val startTimeParsed = timeFormat.parse(startTime)
        val endTimeParsed = timeFormat.parse(endTime)

        val durationMillis = endTimeParsed.time - startTimeParsed.time
        val hours = TimeUnit.MILLISECONDS.toHours(durationMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60

        val formattedHours = hours.toString().padStart(2, '0')
        val formattedMinutes = minutes.toString().padStart(2, '0')

        return Pair(formattedHours, formattedMinutes)
    }

    fun capitalize(str: String): String {
        return str.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }

    fun handleErrorResponse(response: Response<ResponseBody>, context: Context) {

        val errorResponseJson = response.errorBody()?.string()
        val errorResponseObj = JSONObject(errorResponseJson)
        val errorsArray = errorResponseObj.getJSONArray("errors")
        val errorObj = errorsArray.getJSONObject(0)
        val errorMessage = errorObj.getString("message")

        UtilsFunctions().showToast(context, errorMessage)
    }


     fun parseFacultyModel2Json(jsonString: String?): List<FacultyModel2>? {
        return try {
            val jsonArray = JSONArray(jsonString)

            val faculties = mutableListOf<FacultyModel2>()

            for (i in 0 until jsonArray.length()) {
                val facultyObject = jsonArray.getJSONObject(i)
                val facultyId = facultyObject.getInt("id")
                val facultyName = facultyObject.getString("name")

                val streamsArray = facultyObject.getJSONArray("Streams")
                val streams = mutableListOf<StreamModel2>()

                for (j in 0 until streamsArray.length()) {
                    val streamObject = streamsArray.getJSONObject(j)
                    val streamId = streamObject.getInt("id")
                    val streamName = streamObject.getString("streamName")

                    val stream = StreamModel2(streamId, streamName)
                    streams.add(stream)
                }

                val faculty = FacultyModel2(facultyId, facultyName, streams)
                faculties.add(faculty)
            }

            faculties
        } catch (e: JSONException) {
            e.printStackTrace()
            null
        }
     }

    fun parseBlockUserList(jsonString: String?): List<BlockedUserResponse>? {
        return try {
            val jsonArray = JSONArray(jsonString)

            val blockUsers = mutableListOf<BlockedUserResponse>()

            for (i in 0 until jsonArray.length()) {
                val blockUserObject = jsonArray.getJSONObject(i)
                val id = blockUserObject.getInt("id")
                val userId = blockUserObject.getInt("userId")
                val blockUserId = blockUserObject.getInt("blockUserId")
                val createdAt = blockUserObject.getString("createdAt")

                val blockUserDetailObject = blockUserObject.getJSONObject("block_user")
                val avtarUrl = blockUserDetailObject.getString("avtarUrl")
                val blockUserDetailId = blockUserDetailObject.getInt("id")
                val userName = blockUserDetailObject.getString("userName")
                val name = blockUserDetailObject.getString("name")

                val blockUser = BlockedUserResponse(
                    id,
                    userId,
                    blockUserId,
                    createdAt,
                    BlockedUserInfo(avtarUrl, blockUserDetailId, userName, name)
                )
                blockUsers.add(blockUser)
            }

            blockUsers
        } catch (e: JSONException) {
            e.printStackTrace()
            null
        }
    }


    public fun hideKeyboard(view : View, context: Context) {
        val inputMethodManager = ContextCompat.getSystemService(context, InputMethodManager::class.java)
        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
    }


}