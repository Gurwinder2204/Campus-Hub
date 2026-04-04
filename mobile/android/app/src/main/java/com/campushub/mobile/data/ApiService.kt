package com.campushub.mobile.data

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

interface ApiService {

    @POST("api/v1/mobile/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthPayload>

    @POST("api/v1/mobile/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthPayload>

    @GET("api/v1/mobile/auth/me")
    suspend fun me(): Response<UserSummary>

    @POST("api/v1/mobile/auth/logout")
    suspend fun logout(): Response<Map<String, String>>

    @GET("api/v1/mobile/dashboard")
    suspend fun dashboard(): Response<DashboardPayload>

    @GET("api/v1/mobile/semesters")
    suspend fun semesters(): Response<List<SemesterItem>>

    @GET("api/v1/mobile/subjects/{id}")
    suspend fun subject(@Path("id") id: Long): Response<SubjectDetail>

    @GET("api/v1/mobile/search")
    suspend fun search(@Query("q") query: String): Response<List<SubjectSummary>>

    @GET("api/v1/tasks")
    suspend fun tasks(): Response<List<StudyTaskItem>>

    @POST("api/v1/tasks")
    suspend fun createTask(@Body request: CreateTaskRequest): Response<StudyTaskItem>

    @PATCH("api/v1/tasks/{id}/status")
    suspend fun updateTaskStatus(
        @Path("id") id: Long,
        @Query("status") status: String
    ): Response<StudyTaskItem>

    @DELETE("api/v1/tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Long): Response<Unit>

    @GET("api/v1/mobile/rooms")
    suspend fun rooms(): Response<List<RoomItem>>

    @GET("api/v1/bookings")
    suspend fun bookings(): Response<List<BookingItem>>

    @POST("api/v1/bookings")
    suspend fun createBooking(@Body request: BookingRequest): Response<BookingItem>

    @PUT("api/v1/bookings/{id}/cancel")
    suspend fun cancelBooking(@Path("id") id: Long): Response<BookingItem>

    @GET("pois/api")
    suspend fun pois(@Query("category") category: String? = null): Response<List<PoiItem>>

    @GET("api/v1/mobile/community/summary")
    suspend fun communitySummary(): Response<CommunitySummary>

    @GET("api/v1/mobile/community/complaints")
    suspend fun complaints(
        @Query("status") status: String? = null,
        @Query("mine") mine: Boolean = false
    ): Response<List<ComplaintItem>>

    @POST("api/v1/mobile/community/complaints")
    suspend fun createComplaint(@Body request: CreateComplaintRequest): Response<ComplaintItem>

    @PUT("api/v1/mobile/community/complaints/{id}/resolve")
    suspend fun resolveComplaint(@Path("id") id: Long): Response<ComplaintItem>

    @GET("api/v1/mobile/community/lost-found")
    suspend fun lostFound(
        @Query("type") type: String? = null,
        @Query("mine") mine: Boolean = false
    ): Response<List<LostFoundItem>>

    @POST("api/v1/mobile/community/lost-found")
    suspend fun createLostFound(@Body request: CreateLostFoundRequest): Response<LostFoundItem>

    @PUT("api/v1/mobile/community/lost-found/{id}/resolve")
    suspend fun resolveLostFound(@Path("id") id: Long): Response<LostFoundItem>

    @Streaming
    @GET
    suspend fun downloadFile(@Url url: String): Response<ResponseBody>
}
