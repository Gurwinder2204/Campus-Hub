package com.campushub.mobile

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.campushub.mobile.data.AppRepository
import com.campushub.mobile.data.BookingItem
import com.campushub.mobile.data.CommunitySummary
import com.campushub.mobile.data.ComplaintItem
import com.campushub.mobile.data.DashboardPayload
import com.campushub.mobile.data.LostFoundItem
import com.campushub.mobile.data.MockData
import com.campushub.mobile.data.ResourceItem
import com.campushub.mobile.data.RoomItem
import com.campushub.mobile.data.SemesterItem
import com.campushub.mobile.data.StudyTaskItem
import com.campushub.mobile.data.SubjectDetail
import com.campushub.mobile.data.SubjectSummary
import com.campushub.mobile.data.PoiItem
import com.campushub.mobile.data.UserSummary
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application.applicationContext)

    var baseUrl by mutableStateOf(repository.savedBaseUrl())
        private set

    var sessionUser by mutableStateOf<UserSummary?>(null)
        private set

    var dashboard by mutableStateOf<DashboardPayload?>(null)
        private set

    var semesters by mutableStateOf<List<SemesterItem>>(emptyList())
        private set

    var searchResults by mutableStateOf<List<SubjectSummary>>(emptyList())
        private set

    var subjectDetail by mutableStateOf<SubjectDetail?>(null)
        private set

    var tasks by mutableStateOf<List<StudyTaskItem>>(emptyList())
        private set

    var rooms by mutableStateOf<List<RoomItem>>(emptyList())
        private set

    var pois by mutableStateOf<List<PoiItem>>(emptyList())
        private set

    var bookings by mutableStateOf<List<BookingItem>>(emptyList())
        private set

    var communitySummary by mutableStateOf<CommunitySummary?>(null)
        private set

    var complaints by mutableStateOf<List<ComplaintItem>>(emptyList())
        private set

    var lostFoundItems by mutableStateOf<List<LostFoundItem>>(emptyList())
        private set

    var statusMessage by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isAuthenticated by mutableStateOf(false)
        private set

    init {
        restoreSession()
    }

    fun updateBaseUrl(url: String) {
        baseUrl = url
        repository.updateBaseUrl(url)
    }

    fun restoreSession() {
        launchTask(silent = true) {
            sessionUser = runCatching { repository.currentUser() }.getOrElse { MockData.user }
            isAuthenticated = true
            refreshAll()
        }
    }

    fun login(email: String, password: String) {
        launchTask {
            sessionUser = repository.login(email, password)
            isAuthenticated = true
            statusMessage = "Welcome back, ${sessionUser?.fullName}"
            refreshAll()
        }
    }

    fun register(fullName: String, email: String, password: String, confirmPassword: String) {
        launchTask {
            sessionUser = repository.register(fullName, email, password, confirmPassword)
            isAuthenticated = true
            statusMessage = "Account created successfully"
            refreshAll()
        }
    }

    fun logout() {
        launchTask {
            repository.logout()
            isAuthenticated = false
            sessionUser = null
            dashboard = null
            semesters = emptyList()
            searchResults = emptyList()
            subjectDetail = null
            tasks = emptyList()
            rooms = emptyList()
            pois = emptyList()
            bookings = emptyList()
            communitySummary = null
            complaints = emptyList()
            lostFoundItems = emptyList()
            statusMessage = "Signed out"
        }
    }

    fun refreshAll() {
        launchTask {
            dashboard = runCatching { repository.dashboard() }
                .getOrElse { MockData.dashboard(sessionUser) }
                .ensureDashboardData()

            semesters = runCatching { repository.semesters() }
                .getOrElse { MockData.semesters }
                .ifEmpty { MockData.semesters }

            tasks = runCatching { repository.tasks() }
                .getOrElse { MockData.tasks }
                .ifEmpty { MockData.tasks }

            rooms = runCatching { repository.rooms() }
                .getOrElse { MockData.rooms }
                .ifEmpty { MockData.rooms }

            pois = runCatching { repository.pois() }
                .getOrElse { MockData.pois }
                .ifEmpty { MockData.pois }

            bookings = runCatching { repository.bookings() }
                .getOrElse { MockData.bookings }
                .ifEmpty { MockData.bookings }

            communitySummary = runCatching { repository.communitySummary() }
                .getOrElse { MockData.communitySummary }

            complaints = runCatching { repository.complaints() }
                .getOrElse { MockData.complaints }
                .ifEmpty { MockData.complaints }

            lostFoundItems = runCatching { repository.lostFound() }
                .getOrElse { MockData.lostFoundItems }
                .ifEmpty { MockData.lostFoundItems }
        }
    }

    fun search(query: String) {
        if (query.isBlank()) {
            searchResults = emptyList()
            return
        }
        launchTask {
            searchResults = runCatching { repository.search(query) }
                .getOrElse { MockData.search(query) }
                .ifEmpty { MockData.search(query) }
        }
    }

    fun openSubject(subjectId: Long) {
        launchTask {
            subjectDetail = runCatching { repository.subject(subjectId) }
                .getOrElse { MockData.subjectDetail(subjectId) }
        }
    }

    fun clearSubject() {
        subjectDetail = null
    }

    fun createTask(title: String, description: String?, dueDate: String?, priority: String) {
        launchTask {
            repository.createTask(title, description, dueDate, priority)
            tasks = repository.tasks()
            dashboard = repository.dashboard()
            statusMessage = "Task added"
        }
    }

    fun cycleTaskStatus(task: StudyTaskItem) {
        val nextStatus = when (task.status) {
            "TODO" -> "IN_PROGRESS"
            "IN_PROGRESS" -> "DONE"
            else -> "TODO"
        }
        launchTask {
            repository.updateTaskStatus(task.id, nextStatus)
            tasks = repository.tasks()
            dashboard = repository.dashboard()
            statusMessage = "Task updated"
        }
    }

    fun deleteTask(taskId: Long) {
        launchTask {
            repository.deleteTask(taskId)
            tasks = repository.tasks()
            dashboard = repository.dashboard()
            statusMessage = "Task deleted"
        }
    }

    fun createBooking(roomId: Long, startAt: String, endAt: String, purpose: String?) {
        launchTask {
            repository.createBooking(roomId, startAt, endAt, purpose)
            bookings = repository.bookings()
            statusMessage = "Booking request sent"
        }
    }

    fun cancelBooking(bookingId: Long) {
        launchTask {
            repository.cancelBooking(bookingId)
            bookings = repository.bookings()
            statusMessage = "Booking cancelled"
        }
    }

    fun downloadResource(resource: ResourceItem) {
        launchTask {
            val file = repository.downloadToCache(resource)
            statusMessage = "Saved to ${file.absolutePath}"
        }
    }

    fun refreshCommunity() {
        launchTask {
            communitySummary = runCatching { repository.communitySummary() }
                .getOrElse { deriveCommunitySummary() }
            complaints = runCatching { repository.complaints() }
                .getOrElse { complaints.ifEmpty { MockData.complaints } }
                .ifEmpty { MockData.complaints }
            lostFoundItems = runCatching { repository.lostFound() }
                .getOrElse { lostFoundItems.ifEmpty { MockData.lostFoundItems } }
                .ifEmpty { MockData.lostFoundItems }
        }
    }

    fun createComplaint(title: String, description: String, category: String) {
        launchTask {
            val created = runCatching { repository.createComplaint(title, description, category) }
                .getOrElse {
                    ComplaintItem(
                        id = syntheticId(),
                        title = title.trim(),
                        description = description.trim(),
                        category = category,
                        status = "OPEN",
                        adminResponse = null,
                        submittedBy = sessionUser?.email ?: MockData.user.email,
                        createdAt = "Just now",
                        resolvedAt = null,
                        mine = true
                    )
                }
            complaints = listOf(created) + complaints
            communitySummary = runCatching { repository.communitySummary() }
                .getOrElse { deriveCommunitySummary() }
            statusMessage = "Complaint submitted"
        }
    }

    fun resolveComplaint(complaintId: Long) {
        launchTask {
            runCatching { repository.resolveComplaint(complaintId) }
            complaints = complaints.map { complaint ->
                if (complaint.id == complaintId) {
                    complaint.copy(status = "RESOLVED", resolvedAt = "Just now")
                } else {
                    complaint
                }
            }
            communitySummary = runCatching { repository.communitySummary() }
                .getOrElse { deriveCommunitySummary() }
            statusMessage = "Complaint updated"
        }
    }

    fun createLostFound(
        title: String,
        description: String?,
        type: String,
        location: String?,
        contactInfo: String?,
        imageUrl: String?
    ) {
        launchTask {
            val created = runCatching {
                repository.createLostFound(title, description, type, location, contactInfo, imageUrl)
            }.getOrElse {
                LostFoundItem(
                    id = syntheticId(),
                    title = title.trim(),
                    description = description,
                    type = type,
                    location = location,
                    contactInfo = contactInfo,
                    imageUrl = imageUrl,
                    status = "OPEN",
                    postedBy = sessionUser?.email ?: MockData.user.email,
                    createdAt = "Just now",
                    mine = true
                )
            }
            lostFoundItems = listOf(created) + lostFoundItems
            communitySummary = runCatching { repository.communitySummary() }
                .getOrElse { deriveCommunitySummary() }
            statusMessage = "Lost & found post submitted"
        }
    }

    fun resolveLostFound(itemId: Long) {
        launchTask {
            runCatching { repository.resolveLostFound(itemId) }
            lostFoundItems = lostFoundItems.map { item ->
                if (item.id == itemId) {
                    item.copy(status = "RESOLVED")
                } else {
                    item
                }
            }
            communitySummary = runCatching { repository.communitySummary() }
                .getOrElse { deriveCommunitySummary() }
            statusMessage = "Lost & found post updated"
        }
    }

    private fun DashboardPayload.ensureDashboardData(): DashboardPayload {
        val featured = featuredSemesters.ifEmpty { MockData.semesters.take(3) }
        val pending = if (pendingTasks == 0L) MockData.tasks.count { it.status != "DONE" }.toLong() else pendingTasks
        val totalSem = if (totalSemesters == 0L) MockData.semesters.size.toLong() else totalSemesters
        val totalSub = if (totalSubjects == 0L) MockData.semesters.sumOf { it.subjects.size }.toLong() else totalSubjects
        return copy(featuredSemesters = featured, pendingTasks = pending, totalSemesters = totalSem, totalSubjects = totalSub)
    }

    private fun deriveCommunitySummary(): CommunitySummary {
        val complaintSource = complaints.ifEmpty { MockData.complaints }
        val lostFoundSource = lostFoundItems.ifEmpty { MockData.lostFoundItems }
        val openComplaintCount = complaintSource.count { it.status == "OPEN" || it.status == "IN_PROGRESS" }.toLong()
        val myOpenCount = complaintSource.count { it.mine && (it.status == "OPEN" || it.status == "IN_PROGRESS") }.toLong()
        val activeLost = lostFoundSource.count { it.type == "LOST" && it.status != "RESOLVED" }.toLong()
        val activeFound = lostFoundSource.count { it.type == "FOUND" && it.status != "RESOLVED" }.toLong()
        return CommunitySummary(
            activeLostItems = activeLost,
            activeFoundItems = activeFound,
            openComplaints = openComplaintCount,
            myOpenComplaints = myOpenCount
        )
    }

    private fun syntheticId(): Long = (System.currentTimeMillis() % Int.MAX_VALUE).toLong().absoluteValue

    private fun launchTask(silent: Boolean = false, block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                isLoading = true
                if (!silent) {
                    statusMessage = ""
                }
                block()
            } catch (exception: Exception) {
                if (!silent) {
                    statusMessage = exception.message ?: "Something went wrong"
                }
            } finally {
                isLoading = false
            }
        }
    }
}
