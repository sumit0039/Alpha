package com.softwill.alpha.networking;


import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {


    //------------------------------------------------------AUTH---------------------------------------------------------//

    @POST("api/otp/sendOTP")
    Call<ResponseBody> api_sendOTP(@Body JsonObject jsonObject);

    @POST("api/otp/verifyOTP")
    Call<ResponseBody> api_VerifyOTP(@Body JsonObject jsonObject);

    @POST("api/check_institue")
    Call<ResponseBody> api_CheckInstitute(@Body JsonObject jsonObject);

    @POST("api/student/register")
    Call<ResponseBody> api_StudentRegister(@Body JsonObject jsonObject);

    @POST("api/teacher/register")
    Call<ResponseBody> api_TeacherRegister(@Body JsonObject jsonObject);

    @DELETE("api/logout")
    Call<ResponseBody> api_logout();


    //------------------------------------------------------COMPLAINT---------------------------------------------------------//

    @GET("api/complain/list")
    Call<ResponseBody> api_complainList(@Query("year") String year);

    @POST("api/complain/create")
    Call<ResponseBody> api_complainCreate(@Body JsonObject jsonObject);


    //------------------------------------------------------USER---------------------------------------------------------//

    @GET("api/user_profile/{id}")
    Call<ResponseBody> api_GuestUserDetails(@Path("id") int id);

    @GET("api/current_user/details")
    Call<ResponseBody> api_CurrentUserDetails();

    @Multipart
    @POST("api/current_user/change_profile_picture")
    Call<ResponseBody> api_CurrentUserChangeProfilePicture(@Part MultipartBody.Part profile);

    @PUT("api/current_user/update_profile")
    Call<ResponseBody> api_CurrentUserUpdateProfile(@Body JsonObject jsonObject);

    @PUT("api/current_user/update_bio")
    Call<ResponseBody> api_CurrentUserUpdateBio(@Body JsonObject jsonObject);

    @GET("api/current_user/privacy_settings")
    Call<ResponseBody> api_CurrentUserPrivacySettings();

    @GET("api/current_user/notification_settings")
    Call<ResponseBody> api_CurrentUserNotificationSettings();

    @POST("api/current_user/update_privacy_setting")
    Call<ResponseBody> api_UpdatePrivacySettings(@Body JsonObject jsonObject);

    @POST("api/current_user/update_notification_setting")
    Call<ResponseBody> api_UpdateNotificationSettings(@Body JsonObject jsonObject);


    @POST("api/current_user/update_profile_setting")
    Call<ResponseBody> api_UpdateProfileSettings(@Body JsonObject jsonObject);


    @POST("api/current_user/change_mobile_number")
    Call<ResponseBody> api_ChangeMobileNumber(@Body JsonObject jsonObject);

    @POST("api/current_user/change_mobile_otp_verification")
    Call<ResponseBody> api_ChangeMobileOTPVerification(@Body JsonObject jsonObject);


    @Multipart
    @POST("api/post/create")
    Call<ResponseBody> api_PostCreate(@Part("title") RequestBody title,
                                      @Part("desc") RequestBody desc,
                                      @Part List<MultipartBody.Part> files);

    @GET("api/posts")
    Call<ResponseBody> api_posts();


    @DELETE("api/post/{id}")
    Call<ResponseBody> api_PostDelete(@Path("id") int id);


    //--------------------------------------------USER CONNECTIONS------------------------------------------//

    @GET("api/current_user/connection_requests")
    Call<ResponseBody> api_ConnectionRequests();

    @POST("api/current_user/accept_or_reject_connection_request/{id}")
    Call<ResponseBody> api_AcceptRejectConnectionRequest(@Path("id") int id,
                                                         @Body JsonObject jsonObject);

    @GET("api/current_user/connections")
    Call<ResponseBody> api_ConnectionsList();

    @GET("api/user/{id}/connections")
    Call<ResponseBody> api_GuestConnectionsList(@Path("id") int id);

    @POST("api/current_user/send_connection_request")
    Call<ResponseBody> api_SendConnectionRequest(@Body JsonObject jsonObject);

    @POST("api/current_user/cancel_or_remove_connection_request")
    Call<ResponseBody> api_RemoveConnection(@Body JsonObject jsonObject);

    @POST("api/user_report")
    Call<ResponseBody> api_ReportUser(@Body JsonObject jsonObject);

    @POST("api/block_or_unblock_user")
    Call<ResponseBody> api_BlockUnblockUser(@Body JsonObject jsonObject);

    @GET("api/block_user_list")
    Call<ResponseBody> api_BlockUserList();

    //----------------------------------------------HOME_POST-------------------------------------------------//

    @GET("api/home/posts")
    Call<ResponseBody> api_HomePosts(@Query("offset") int offset,
                                     @Query("limit") int limit);

    @GET("api/post/{id}")
    Call<ResponseBody> api_PostDetails(@Path("id") int id);

    @POST("api/post/like_or_unlike/{id}")
    Call<ResponseBody> api_PostLikeUnLike(@Path("id") int id);

    @GET("api/post/comments/{id}")
    Call<ResponseBody> api_PostComments(
            @Path("id") int id);

    @POST("api/post/write_comment/{id}")
    Call<ResponseBody> api_PostWriteComments(@Path("id") int id,
                                             @Body JsonObject jsonObject);

    @DELETE("api/post/comment/{id}")
    Call<ResponseBody> api_DeleteComment(@Path("id") int id);


    @POST("api/user_report_post")
    Call<ResponseBody> api_ReportPost(@Body JsonObject jsonObject);

    @GET("api/user/search")
    Call<ResponseBody> api_Search(@Query("userType") String userType,
                                  @Query("search") String search);


    //--------------------------------------------EDUCATION LOAN------------------------------------------//

    @GET("api/education_loan_details")
    Call<ResponseBody> api_EducationLoanDetails();


    //--------------------------------------------CAREER GUIDANCE------------------------------------------//

    @GET("api/faculties")
    Call<ResponseBody> api_Faculties();

    @GET("api/career_guidance/list")
    Call<ResponseBody> api_CareerGuidanceList(@Query("facultyId") int facultyId,
                                              @Query("streamId") int streamId);

    @GET("api/career_guidance/list")
    Call<ResponseBody> api_SearchCareerGuidance(@Query("search") String search);

    @GET("api/career_guidance/get/{id}")
    Call<ResponseBody> api_CareerGuidanceDetail(@Path("id") int id);


    //--------------------------------------------BEST COLLEGE------------------------------------------//


    @GET("api/states")
    Call<ResponseBody> api_States();

    @GET("api/best_collages")
    Call<ResponseBody> api_BestCollegeList(@Query("stateId") int stateId,
                                           @Query("facultyId") int facultyId,
                                           @Query("streamId") int streamId);

    @GET("api/best_collages")
    Call<ResponseBody> api_SearchBestCollegeList(@Query("search") String search);

    @GET("api/best_collages")
    Call<ResponseBody> api_FavouriteBestCollege(@Query("onlyFavCllg") boolean onlyFavCllg);

    @POST("api/add_or_remove_fav_collage")
    Call<ResponseBody> api_AddRemoveFavCollage(@Body JsonObject jsonObject);


    //--------------------------------------------ENTRANCE EXAM------------------------------------------//

    @GET("api/entrance_exam/list")
    Call<ResponseBody> api_EntranceExamList(@Query("facultyId") int facultyId);

    @GET("api/entrance_exam/list")
    Call<ResponseBody> api_SearchEntranceExam(@Query("search") String search);

    @GET("api/entrance_exam/get/{id}")
    Call<ResponseBody> api_EntranceExamDetail(@Path("id") int id);

    //--------------------------------------------MOCK EXAM------------------------------------------//

    @GET("api/mock_exam/list")
    Call<ResponseBody> api_MockExamList(@Query("facultyId") int facultyId);

    @GET("api/mock_exam/list")
    Call<ResponseBody> api_SearchMockExam(@Query("search") String search);

    @GET("api/mock_exam/get/{id}")
    Call<ResponseBody> api_MockExamDetail(@Path("id") int id);

    @POST("api/mock_exam/submit/{id}")
    Call<ResponseBody> api_SubmitMockExam(@Path("id") int id,
                                          @Body JsonObject jsonObject);


    @GET("api/mock_exam/completed")
    Call<ResponseBody> api_MockExamCompleted();

    @GET("api/mock_exam/completed")
    Call<ResponseBody> api_SearchCompletedMockExam(@Query("search") String search,
                                                   @Query("offset") String offset,
                                                   @Query("limit") String limit);


    //-----------------------------------------------INSTITUTE--------------------------------------------//

    @GET("api/institute_details")
    Call<ResponseBody> api_MyInstitute();

    @GET("api/institute/{id}")
    Call<ResponseBody> api_InstituteDetail(@Path("id") int id);

    @POST("api/institute/submit_rating")
    Call<ResponseBody> api_SubmitRating(@Body JsonObject jsonObject);

    @GET("api/institute/user/rating")
    Call<ResponseBody> api_InstituteRating();

    @GET("api/institute/faculties")
    Call<ResponseBody> api_FacultiesList(@Query("instituteId") int id);

    @GET("api/institute/streams")
    Call<ResponseBody> api_StreamsList(@Query("facultyId") int id);

    @GET("api/institute/classess")
    Call<ResponseBody> api_ClassesList(@Query("streamId") int id);

    @GET("api/institute/entrance_exams")
    Call<ResponseBody> api_entranceExams();

    @GET("api/institute/galleries")
    Call<ResponseBody> api_Galleries();


    //-----------------------------------------------TRANSPORT--------------------------------------------//

    @GET("api/transport/team_members")
    Call<ResponseBody> api_TransportTeamMember();


    @GET("api/transport/fees")
    Call<ResponseBody> api_TransportFees();

    @GET("api/transport/details")
    Call<ResponseBody> api_TransportDetails();


    //-----------------------------------------------CULTURE--------------------------------------------//


    @GET("api/culture/team_members")
    Call<ResponseBody> api_CultureTeamMember();


    @GET("api/culture/programs")
    Call<ResponseBody> api_CultureProgram(@Query("date") String date);

    @GET("api/culture/sponsers")
    Call<ResponseBody> api_CultureSponsers(@Query("year") String year);

    @GET("api/culture/pictures")
    Call<ResponseBody> api_CulturePictures(@Query("year") String year);

    @GET("api/institute_trips")
    Call<ResponseBody> api_InstituteTrips();


    //-----------------------------------------------CANTEEN--------------------------------------------//

    @GET("api/canteen/facilities")
    Call<ResponseBody> api_CanteenFacilities();

    @GET("api/canteen/menu_card")
    Call<ResponseBody> api_CanteenMenuCard();

    @GET("api/canteen/team_members")
    Call<ResponseBody> api_CanteenTeamMembers();

    //------------------------------------------------SPORT---------------------------------------------//

    @GET("api/sport/team_members")
    Call<ResponseBody> api_SportTeamMembers();

    @GET("api/sport/competitions")
    Call<ResponseBody> api_SportCompetitions();

    @GET("api/sport/acceseries")
    Call<ResponseBody> api_SportAcceseries();

    @GET("api/sport/opportunities")
    Call<ResponseBody> api_SportOpportunities();

    @GET("api/sport/exhibitions")
    Call<ResponseBody> api_SportExhibitions(@Query("year") String year);


    //-----------------------------------------------Library--------------------------------------------//

    @GET("api/book/categories")
    Call<ResponseBody> api_BookCategories();

    @GET("api/books")
    Call<ResponseBody> api_RecentBook(@Query("onlyRecentBook") Boolean onlyRecentBook,
                                      @Query("offset") int offset,
                                      @Query("limit") int limit);

    @GET("api/books")
    Call<ResponseBody> api_CategoryWiseBook(@Query("categoryId") int categoryId,
                                            @Query("offset") int offset,
                                            @Query("limit") int limit);

    @GET("api/books")
    Call<ResponseBody> api_AllBook(@Query("offset") int offset,
                                   @Query("limit") int limit);

    @GET("api/books")
    Call<ResponseBody> api_SearchBook(@Query("search") String search,
                                      @Query("offset") int offset,
                                      @Query("limit") int limit);

    @POST("api/book/save_or_unsave/{id}")
    Call<ResponseBody> api_SaveRemoveBook(@Path("id") int id,
                                          @Body JsonObject jsonObject);

    @GET("api/book/saved")
    Call<ResponseBody> api_SavedBook(@Query("search") String search);


    @GET("api/news_paper/categories")
    Call<ResponseBody> api_NewsPaperCategories();

    @GET("api/news_papers")
    Call<ResponseBody> api_NewsPaper(@Query("categoryId") int categoryId);

    @POST("api/book/mark_recent_read/{id}")
    Call<ResponseBody> api_MarkRecentRead(@Path("id") int id);

    //------------------------------------------------Dairy---------------------------------------------//

    @GET("api/dairy/list")
    Call<ResponseBody> api_DairyList(@Query("year") String year);

    @POST("api/dairy/create")
    Call<ResponseBody> api_CreateDairy(@Body JsonObject jsonObject);

    @PUT("api/dairy/update/{id}")
    Call<ResponseBody> api_UpdateDairy(@Path("id") int id,
                                       @Body JsonObject jsonObject);

    @DELETE("api/dairy/delete/{id}")
    Call<ResponseBody> api_DeleteDairy(@Path("id") int id);


    //---------------------------------------------Assignment-------------------------------------------//

    @GET("api/teacher/class_subjects")
    Call<ResponseBody> api_ClassSubjects();

    @POST("api/teacher/assignment/create")
    Call<ResponseBody> api_TeacherCreateAssignment(@Body JsonObject jsonObject);

    @GET("api/teacher/assignment/ongoing_list")
    Call<ResponseBody> api_TeacherOngoingAssignment();

    @GET("api/teacher/assignment/completed_list")
    Call<ResponseBody> api_TeacherCompletedAssignment(@Query("classId") String classId,
                                                      @Query("subjectId") String subjectId);

    @DELETE("api/teacher/assignment/delete/{id}")
    Call<ResponseBody> api_DeleteTeacherAssignment(@Path("id") int id);

    @GET("api/assignment/{id}/questions")
    Call<ResponseBody> api_ViewAssignment(@Path("id") int id);

    @GET("api/teacher/assignment/check_assignment/{id}")
    Call<ResponseBody> api_TeacherCheckAssignment(@Path("id") int id);


    @POST("api/teacher/assignment/submit_checked_assignment/{id}")
    Call<ResponseBody> api_TeacherSubmitCheckAssignment(@Path("id") int id,
                                                        @Body JsonObject jsonObject);


    @GET("api/student/assignment/ongoing_list")
    Call<ResponseBody> api_StudentOngoingAssignment();

    @GET("api/student/assignment/completed_list")
    Call<ResponseBody> api_StudentCompletedAssignment();

    @GET("api/student/assignment/start/{id}")
    Call<ResponseBody> api_StudentStartAssignment(@Path("id") int id);

    @POST("api/student/assignment/submit/{id}")
    Call<ResponseBody> api_StudentSubmitAssignment(@Path("id") int id,
                                                   @Body JsonObject jsonObject);

    //----------------------------------------------OnlineExam------------------------------------------//

    @GET("api/teacher/exam_types")
    Call<ResponseBody> api_ExamType();

    @POST("api/teacher/exam/create")
    Call<ResponseBody> api_TeacherCreateExam(@Body JsonObject jsonObject);

    @GET("api/teacher/exam/ongoing_list")
    Call<ResponseBody> api_TeacherOngoingExam(@Query("classId") String classId,
                                              @Query("subjectId") String subjectId);

    @GET("api/teacher/exam/completed_list")
    Call<ResponseBody> api_TeacherCompletedExam();

    @GET("api/exam/{id}/questions")
    Call<ResponseBody> api_ViewOnlineExam(@Path("id") int id);

    @DELETE("api/teacher/exam/delete/{id}")
    Call<ResponseBody> api_DeleteTeacherExam(@Path("id") int id);

    @GET("api/student/exam/ongoing_list")
    Call<ResponseBody> api_StudentOngoingExam();

    @GET("api/student/exam/completed_list")
    Call<ResponseBody> api_StudentCompletedExam();

    @GET("api/student/exam/start/{id}")
    Call<ResponseBody> api_StudentExamStart(@Path("id") int id);

    @POST("api/student/exam/submit/{id}")
    Call<ResponseBody> api_StudentSubmitExam(@Path("id") int id,
                                             @Body JsonObject jsonObject);

    //-----------------------------------------------Lecture--------------------------------------------//

    @POST("api/teacher/lecture/create")
    Call<ResponseBody> api_TeacherCreateLecture(@Body JsonObject jsonObject);

    @GET("api/teacher/lecture/list")
    Call<ResponseBody> api_TeacherLecture(@Query("type") String type);

    @GET("api/student/lecture/list")
    Call<ResponseBody> api_StudentLecture(@Query("type") String type);

    @DELETE("api/teacher/lecture/delete/{id}")
    Call<ResponseBody> api_DeleteLecture(@Path("id") int id);

    //---------------------------------------------ReportCard-------------------------------------------//

    @GET("api/student/report_card/list")
    Call<ResponseBody> api_ReportCardList(@Query("classId") int classId);

    @Multipart
    @POST("api/teacher/report_card/add")
    Call<ResponseBody> api_AddReportCard(@Part List<MultipartBody.Part> reporCardFile,
                                      @Part("studentId") RequestBody studentId,
                                      @Part("classId") RequestBody classId);

    //------------------------------------------------Class---------------------------------------------//

    @GET("api/teacher/class_student_list")
    Call<ResponseBody> api_ClassStudentListByTeacher(@Query("classId") int classId);

    @GET("api/student/class_student_list")
    Call<ResponseBody> api_ClassStudentListByStudent();

    @GET("api/student/class_subject_list")
    Call<ResponseBody> api_ClassSubjectList();

    @GET("api/student/{id}/class/performance")
    Call<ResponseBody> api_StudentClassPerformance(@Path("id") int id);

    @GET("api/student/{id}/class/subjects")
    Call<ResponseBody> api_StudentClassSubjects(@Path("id") int id);


    @GET("api/student/{id}/pending_exam")
    Call<ResponseBody> api_StudentPendingExam(@Path("id") int id);


    @GET("api/student/{id}/pending_assignment")
    Call<ResponseBody> api_StudentPendingAssignment(@Path("id") int id);

    //----------------------------------------------TimeTable-------------------------------------------// 5

    @POST("api/teacher/timetable/create")
    Call<ResponseBody> api_TeacherTimetableCreate(@Body JsonObject jsonObject);

    @GET("api/teacher/timetable/list")
    Call<ResponseBody> api_TeacherTimeTableList(@Query("date") String date,
                                                    @Query("classId") int classId);

    //---------------------------------------------Attendance-------------------------------------------// 4


}