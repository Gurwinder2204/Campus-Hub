package com.campushub.mobile

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.campushub.mobile.data.AppRepository
import com.campushub.mobile.data.BookingItem
import com.campushub.mobile.data.DashboardPayload
import com.campushub.mobile.data.ResourceItem
import com.campushub.mobile.data.RoomItem
import com.campushub.mobile.data.SemesterItem
import com.campushub.mobile.data.StudyTaskItem
import com.campushub.mobile.data.SubjectDetail
import com.campushub.mobile.data.SubjectSummary
import com.campushub.mobile.data.PoiItem
import com.campushub.mobile.data.UserSummary
import kotlinx.coroutines.launch

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
            sessionUser = repository.currentUser()
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
            statusMessage = "Signed out"
        }
    }

    fun refreshAll() {
        launchTask {
            dashboard = repository.dashboard()
            semesters = repository.semesters()
            tasks = repository.tasks()
            rooms = repository.rooms()
            pois = repository.pois()
            bookings = repository.bookings()
        }
    }

    fun search(query: String) {
        if (query.isBlank()) {
            searchResults = emptyList()
            return
        }
        launchTask {
            searchResults = repository.search(query)
        }
    }

    fun openSubject(subjectId: Long) {
        launchTask {
            subjectDetail = repository.subject(subjectId)
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
