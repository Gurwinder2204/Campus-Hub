package com.campushub.mobile.data

import android.content.Context
import android.net.Uri
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class AppRepository(private val context: Context) {

    private val preferences = context.getSharedPreferences("campus_hub_prefs", Context.MODE_PRIVATE)
    private val cookieStore = mutableMapOf<String, MutableList<Cookie>>()

    private var apiService: ApiService = createApiService(savedBaseUrl())

    fun savedBaseUrl(): String {
        return preferences.getString("base_url", DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
    }

    fun updateBaseUrl(url: String) {
        preferences.edit().putString("base_url", normalizeBaseUrl(url)).apply()
        cookieStore.clear()
        apiService = createApiService(savedBaseUrl())
    }

    suspend fun login(email: String, password: String): UserSummary =
        apiService.login(LoginRequest(email, password)).requireBody().user

    suspend fun register(fullName: String, email: String, password: String, confirmPassword: String): UserSummary =
        apiService.register(RegisterRequest(fullName, email, password, confirmPassword)).requireBody().user

    suspend fun currentUser(): UserSummary = apiService.me().requireBody()

    suspend fun logout() {
        apiService.logout()
        cookieStore.clear()
    }

    suspend fun dashboard(): DashboardPayload = apiService.dashboard().requireBody()

    suspend fun semesters(): List<SemesterItem> = apiService.semesters().requireBody()

    suspend fun subject(id: Long): SubjectDetail = apiService.subject(id).requireBody()

    suspend fun search(query: String): List<SubjectSummary> = apiService.search(query).requireBody()

    suspend fun tasks(): List<StudyTaskItem> = apiService.tasks().requireBody()

    suspend fun createTask(title: String, description: String?, dueDate: String?, priority: String): StudyTaskItem =
        apiService.createTask(CreateTaskRequest(title = title, description = description, dueDate = dueDate, priority = priority))
            .requireBody()

    suspend fun updateTaskStatus(id: Long, status: String): StudyTaskItem =
        apiService.updateTaskStatus(id, status).requireBody()

    suspend fun deleteTask(id: Long) {
        apiService.deleteTask(id).throwOnError()
    }

    suspend fun rooms(): List<RoomItem> = apiService.rooms().requireBody()

    suspend fun bookings(): List<BookingItem> = apiService.bookings().requireBody()

    suspend fun createBooking(roomId: Long, startAt: String, endAt: String, purpose: String?): BookingItem =
        apiService.createBooking(BookingRequest(roomId, startAt, endAt, purpose)).requireBody()

    suspend fun cancelBooking(id: Long): BookingItem = apiService.cancelBooking(id).requireBody()

    suspend fun pois(category: String? = null): List<PoiItem> = apiService.pois(category).requireBody()

    suspend fun downloadToCache(resource: ResourceItem): File {
        val fileName = Uri.parse(resource.url).lastPathSegment ?: "${resource.type.lowercase()}-${resource.id}"
        val target = File(context.cacheDir, fileName)
        apiService.downloadFile(resolveUrl(resource.url)).throwOnError().body()?.byteStream()?.use { input ->
            target.outputStream().use { output -> input.copyTo(output) }
        } ?: error("Empty file response")
        return target
    }

    fun resolveUrl(url: String): String {
        return if (url.startsWith("http://") || url.startsWith("https://")) {
            url
        } else {
            savedBaseUrl().trimEnd('/') + "/" + url.trimStart('/')
        }
    }

    private fun createApiService(baseUrl: String): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .cookieJar(object : CookieJar {
                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    cookieStore[url.host] = cookies.toMutableList()
                }

                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    return cookieStore[url.host] ?: emptyList()
                }
            })
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(normalizeBaseUrl(baseUrl))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private fun <T> Response<T>.requireBody(): T {
        if (isSuccessful) {
            return body() ?: error("Empty response from server")
        }
        throw IllegalStateException(errorBody()?.string().orEmpty().ifBlank { message() })
    }

    private fun <T> Response<T>.throwOnError(): Response<T> {
        if (!isSuccessful) {
            throw IllegalStateException(errorBody()?.string().orEmpty().ifBlank { message() })
        }
        return this
    }

    private fun normalizeBaseUrl(url: String): String {
        val cleaned = if (url.endsWith("/")) url else "$url/"
        return if (cleaned.startsWith("http://") || cleaned.startsWith("https://")) cleaned else "http://$cleaned"
    }

    companion object {
        private const val DEFAULT_BASE_URL = "http://10.0.2.2:8080/"
    }
}
