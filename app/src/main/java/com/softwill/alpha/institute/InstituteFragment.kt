package com.softwill.alpha.institute

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.softwill.alpha.R
import com.softwill.alpha.databinding.FragmentInstituteBinding
import com.softwill.alpha.institute.assignment.student.activity.StudentAssignmentActivity
import com.softwill.alpha.institute.assignment.teacher.activity.TeacherAssignmentActivity
import com.softwill.alpha.institute.attendance.activity.AttendanceStudentActivity
import com.softwill.alpha.institute.attendance.activity.AttendanceTeacherActivity
import com.softwill.alpha.institute.canteen.activity.CanteenActivity
import com.softwill.alpha.institute.classes.activity.ClassesActivity
import com.softwill.alpha.institute.complaint.ComplaintActivity
import com.softwill.alpha.institute.culture.activity.CultureActivity
import com.softwill.alpha.institute.diary.DiaryActivity
import com.softwill.alpha.institute.lecture.activity.LectureActivity
import com.softwill.alpha.institute.library.activity.LibraryActivity
import com.softwill.alpha.institute.online_exam.student.activity.StudentExamActivity
import com.softwill.alpha.institute.online_exam.teacher.activity.TeacherExamActivity
import com.softwill.alpha.institute.report_card.activity.ReportCardStudentActivity
import com.softwill.alpha.institute.report_card.activity.ReportCardTeacherActivity
import com.softwill.alpha.institute.sport.activity.SportActivity
import com.softwill.alpha.institute.timetable.activity.TimeTableActivity
import com.softwill.alpha.institute.transport.activity.TransportActivity
import com.softwill.alpha.institute_detail.CollegeDetailsActivity
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.YourPreference


class InstituteFragment : Fragment() {

    private lateinit var binding: FragmentInstituteBinding
    var yourPreference: YourPreference? = null
    var IsStudentLogin: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_institute, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        yourPreference = YourPreference(activity)

        IsStudentLogin = yourPreference?.getData(Constant.IsStudentLogin).toBoolean()



        onClickListener()

    }

    private fun onClickListener() {

        binding.llTimetable.setOnClickListener {
            val intent = Intent(activity, TimeTableActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.llComplaints.setOnClickListener {
            val intent = Intent(activity, ComplaintActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.llAssignment.setOnClickListener {
            if (IsStudentLogin) {
                val intent = Intent(activity, StudentAssignmentActivity::class.java)
                activity?.startActivity(intent)
            } else {
                val intent = Intent(activity, TeacherAssignmentActivity::class.java)
                activity?.startActivity(intent)
            }
        }

        binding.llTransport.setOnClickListener {
            val intent = Intent(activity, TransportActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.llCulturePrograms.setOnClickListener {
            val intent = Intent(activity, CultureActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.llCanteen.setOnClickListener {
            val intent = Intent(activity, CanteenActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.llSport.setOnClickListener {
            val intent = Intent(activity, SportActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.llReportCard.setOnClickListener {
            if (IsStudentLogin) {
                val intent = Intent(activity, ReportCardStudentActivity::class.java)
                activity?.startActivity(intent)
            } else {
                val intent = Intent(activity, ReportCardTeacherActivity::class.java)
                activity?.startActivity(intent)
            }

        }

        binding.llOnlineExam.setOnClickListener {
            if (IsStudentLogin) {
                val intent = Intent(activity, StudentExamActivity::class.java)
                activity?.startActivity(intent)
            } else {
                val intent = Intent(activity, TeacherExamActivity::class.java)
                activity?.startActivity(intent)
            }
        }

        binding.llAttendance.setOnClickListener {
            if (IsStudentLogin) {
                val intent = Intent(activity, AttendanceStudentActivity::class.java)
                activity?.startActivity(intent)
            } else {
                val intent = Intent(activity, AttendanceTeacherActivity::class.java)
                activity?.startActivity(intent)
            }
        }

        binding.llClass.setOnClickListener {
            val intent = Intent(activity, ClassesActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.llLibrary.setOnClickListener {
            val intent = Intent(activity, LibraryActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.llLecture.setOnClickListener {
            val intent = Intent(activity, LectureActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.llDiary.setOnClickListener {
            val intent = Intent(activity, DiaryActivity::class.java)
            activity?.startActivity(intent)
        }


        binding.llMyInstitute.setOnClickListener {
            val intent = Intent(activity, CollegeDetailsActivity::class.java)
            intent.putExtra("mMyInstitute", true)
            activity?.startActivity(intent)
        }


    }


}