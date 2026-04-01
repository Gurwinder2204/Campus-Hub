package com.campushub.mobile.data

data class UserSummary(
    val id: Long,
    val fullName: String,
    val email: String,
    val role: String
)

data class AuthPayload(
    val message: String,
    val user: UserSummary
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)

data class DashboardPayload(
    val user: UserSummary,
    val pendingTasks: Long,
    val totalSemesters: Long,
    val totalSubjects: Long,
    val featuredSemesters: List<SemesterItem>
)

data class SemesterItem(
    val id: Long,
    val number: Int,
    val name: String,
    val subjectCount: Int,
    val subjects: List<SubjectSummary>
)

data class SubjectSummary(
    val id: Long,
    val name: String,
    val code: String?,
    val description: String?,
    val semesterNumber: Int,
    val notesCount: Long,
    val papersCount: Long,
    val videosCount: Long
)

data class SubjectDetail(
    val id: Long,
    val name: String,
    val code: String?,
    val description: String?,
    val semesterNumber: Int,
    val notesCount: Long,
    val papersCount: Long,
    val videosCount: Long,
    val notes: List<ResourceItem>,
    val papers: List<ResourceItem>,
    val videos: List<ResourceItem>
)

data class ResourceItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val description: String?,
    val url: String,
    val type: String
)

data class StudyTaskItem(
    val id: Long,
    val userId: Long,
    val title: String,
    val description: String?,
    val dueDate: String?,
    val status: String,
    val priority: String,
    val createdAt: String?
)

data class CreateTaskRequest(
    val title: String,
    val description: String?,
    val dueDate: String?,
    val status: String = "TODO",
    val priority: String = "MEDIUM",
    val userId: Long = 0
)

data class RoomItem(
    val id: Long,
    val name: String,
    val capacity: Int,
    val building: String,
    val floor: String?,
    val roomNumber: String?,
    val resources: String?
)

data class BookingItem(
    val id: Long,
    val roomId: Long,
    val roomName: String,
    val userId: Long,
    val userName: String,
    val startAt: String,
    val endAt: String,
    val status: String,
    val purpose: String?,
    val createdAt: String?
)

data class BookingRequest(
    val roomId: Long,
    val startAt: String,
    val endAt: String,
    val purpose: String?
)

data class PoiItem(
    val id: Long,
    val name: String,
    val description: String?,
    val category: String,
    val latitude: Double?,
    val longitude: Double?,
    val imageUrl: String?,
    val floor: String?,
    val openingHours: String?
)
