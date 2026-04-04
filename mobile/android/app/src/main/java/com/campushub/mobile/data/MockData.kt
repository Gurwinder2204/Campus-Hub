package com.campushub.mobile.data

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object MockData {

    private val isoFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    val user = UserSummary(
        id = 1L,
        fullName = "Campus Demo User",
        email = "admin@campus.com",
        role = "ROLE_STUDENT"
    )

    val semesters: List<SemesterItem> = listOf(
        SemesterItem(
            id = 1L,
            number = 1,
            name = "Semester 1",
            subjectCount = 3,
            subjects = listOf(
                SubjectSummary(101L, "Programming Fundamentals", "CS101", "Core coding basics", 1, 4, 2, 3),
                SubjectSummary(102L, "Engineering Mathematics I", "MA101", "Calculus and algebra", 1, 3, 4, 1),
                SubjectSummary(103L, "Physics for Engineers", "PH101", "Mechanics and waves", 1, 2, 3, 2)
            )
        ),
        SemesterItem(
            id = 2L,
            number = 2,
            name = "Semester 2",
            subjectCount = 3,
            subjects = listOf(
                SubjectSummary(201L, "Data Structures", "CS201", "Lists, stacks, queues and trees", 2, 6, 4, 5),
                SubjectSummary(202L, "Discrete Mathematics", "MA201", "Logic, sets and combinatorics", 2, 2, 5, 2),
                SubjectSummary(203L, "Digital Electronics", "EC201", "Boolean algebra and logic circuits", 2, 3, 2, 2)
            )
        ),
        SemesterItem(
            id = 3L,
            number = 3,
            name = "Semester 3",
            subjectCount = 2,
            subjects = listOf(
                SubjectSummary(301L, "Database Management Systems", "CS301", "SQL and relational design", 3, 5, 3, 4),
                SubjectSummary(302L, "Operating Systems", "CS302", "Processes, scheduling and memory", 3, 4, 3, 3)
            )
        )
    )

    val tasks: List<StudyTaskItem> = listOf(
        StudyTaskItem(1L, 1L, "Complete DBMS assignment", "Normalize and write SQL queries", "2026-04-05", "TODO", "HIGH", nowMinusDays(1)),
        StudyTaskItem(2L, 1L, "Revise Data Structures", "Trees and graph traversals", "2026-04-03", "IN_PROGRESS", "MEDIUM", nowMinusDays(2)),
        StudyTaskItem(3L, 1L, "Practice PYQs", "Solve 2023 OS paper", "2026-04-07", "DONE", "LOW", nowMinusDays(3))
    )

    val rooms: List<RoomItem> = listOf(
        RoomItem(1L, "Innovation Lab", 40, "Block A", "2", "A-204", "Projector, Whiteboard, WiFi"),
        RoomItem(2L, "Seminar Hall", 120, "Main Building", "1", "MB-101", "PA System, Smart Display"),
        RoomItem(3L, "Group Study Room", 12, "Library", "Ground", "L-07", "Discussion Table, Charging Points")
    )

    val bookings: List<BookingItem> = listOf(
        BookingItem(1L, 1L, "Innovation Lab", 1L, "Campus Demo User", "2026-04-04T10:00:00", "2026-04-04T12:00:00", "PENDING", "Mini project discussion", nowMinusDays(1)),
        BookingItem(2L, 3L, "Group Study Room", 1L, "Campus Demo User", "2026-04-06T15:00:00", "2026-04-06T16:00:00", "APPROVED", "Study group session", nowMinusDays(2)),
        BookingItem(3L, 2L, "Seminar Hall", 1L, "Campus Demo User", "2026-03-30T09:00:00", "2026-03-30T11:00:00", "CANCELLED", "Guest lecture practice", nowMinusDays(4))
    )

    val pois: List<PoiItem> = listOf(
        PoiItem(1L, "Central Library", "Main reading and reference section", "ACADEMIC", 30.721, 76.768, null, "Ground", "08:00 - 20:00"),
        PoiItem(2L, "Student Canteen", "Affordable meals and snacks", "FOOD", 30.722, 76.767, null, "Ground", "09:00 - 18:00"),
        PoiItem(3L, "Admin Office", "Fees, certificates, and records", "OFFICE", 30.720, 76.769, null, "1", "10:00 - 17:00")
    )

    val complaints: List<ComplaintItem> = listOf(
        ComplaintItem(
            id = 11L,
            title = "Water cooler not working",
            description = "Cooler near Block B is not dispensing cold water.",
            category = "INFRASTRUCTURE",
            status = "OPEN",
            adminResponse = null,
            submittedBy = "student@campus.com",
            createdAt = "02 Apr 2026, 09:30 AM",
            resolvedAt = null,
            mine = false
        ),
        ComplaintItem(
            id = 12L,
            title = "Canteen hygiene issue",
            description = "Please improve table cleaning during peak lunch hours.",
            category = "CANTEEN",
            status = "IN_PROGRESS",
            adminResponse = "Canteen staff notified and extra cleaning rounds added.",
            submittedBy = "admin@campus.com",
            createdAt = "01 Apr 2026, 01:05 PM",
            resolvedAt = null,
            mine = true
        ),
        ComplaintItem(
            id = 13L,
            title = "Projector brightness low",
            description = "Classroom A-204 projector is too dim for morning lectures.",
            category = "ACADEMIC",
            status = "RESOLVED",
            adminResponse = "Projector lamp replaced yesterday.",
            submittedBy = "admin@campus.com",
            createdAt = "28 Mar 2026, 11:10 AM",
            resolvedAt = "30 Mar 2026, 10:20 AM",
            mine = true
        )
    )

    val lostFoundItems: List<LostFoundItem> = listOf(
        LostFoundItem(
            id = 21L,
            title = "Lost blue water bottle",
            description = "Steel bottle with CS department sticker.",
            type = "LOST",
            location = "Near Block A stairs",
            contactInfo = "student@campus.com",
            imageUrl = null,
            status = "OPEN",
            postedBy = "student@campus.com",
            createdAt = "02 Apr 2026, 08:45 AM",
            mine = false
        ),
        LostFoundItem(
            id = 22L,
            title = "Found calculator",
            description = "Scientific calculator found in Lab 3.",
            type = "FOUND",
            location = "Electronics Lab 3",
            contactInfo = "admin@campus.com",
            imageUrl = null,
            status = "OPEN",
            postedBy = "admin@campus.com",
            createdAt = "01 Apr 2026, 03:20 PM",
            mine = true
        ),
        LostFoundItem(
            id = 23L,
            title = "Lost ID Card",
            description = "ID card for 2nd year CSE student.",
            type = "LOST",
            location = "Main gate",
            contactInfo = "owner@campus.com",
            imageUrl = null,
            status = "RESOLVED",
            postedBy = "owner@campus.com",
            createdAt = "29 Mar 2026, 05:40 PM",
            mine = false
        )
    )

    val communitySummary: CommunitySummary = CommunitySummary(
        activeLostItems = lostFoundItems.count { it.type == "LOST" && it.status != "RESOLVED" }.toLong(),
        activeFoundItems = lostFoundItems.count { it.type == "FOUND" && it.status != "RESOLVED" }.toLong(),
        openComplaints = complaints.count { it.status == "OPEN" || it.status == "IN_PROGRESS" }.toLong(),
        myOpenComplaints = complaints.count { it.mine && (it.status == "OPEN" || it.status == "IN_PROGRESS") }.toLong()
    )

    fun dashboard(currentUser: UserSummary?): DashboardPayload {
        val selectedUser = currentUser ?: user
        return DashboardPayload(
            user = selectedUser,
            pendingTasks = tasks.count { it.status != "DONE" }.toLong(),
            totalSemesters = semesters.size.toLong(),
            totalSubjects = semesters.sumOf { it.subjects.size }.toLong(),
            featuredSemesters = semesters.take(3)
        )
    }

    fun subjectDetail(subjectId: Long): SubjectDetail {
        val subject = semesters.flatMap { it.subjects }.firstOrNull { it.id == subjectId }
            ?: semesters.first().subjects.first()

        val notes = listOf(
            ResourceItem(1L, "${subject.name} Notes Unit 1", "Uploaded 2 days ago", "Theory notes with examples", "/files/notes/mock-1", "NOTE"),
            ResourceItem(2L, "${subject.name} Quick Revision", "Uploaded 5 days ago", "One-page summary sheets", "/files/notes/mock-2", "NOTE")
        )
        val papers = listOf(
            ResourceItem(3L, "${subject.name} PYQ 2024", "Year 2024", "Semester exam paper", "/files/papers/mock-1", "PAPER"),
            ResourceItem(4L, "${subject.name} PYQ 2023", "Year 2023", "Semester exam paper", "/files/papers/mock-2", "PAPER")
        )
        val videos = listOf(
            ResourceItem(5L, "${subject.name} Crash Course", "Lecture Playlist", "Topic-wise video playlist", "https://www.youtube.com/watch?v=dQw4w9WgXcQ", "VIDEO"),
            ResourceItem(6L, "${subject.name} Important Questions", "Exam Prep", "Important expected questions", "https://www.youtube.com/watch?v=ysz5S6PUM-U", "VIDEO")
        )

        return SubjectDetail(
            id = subject.id,
            name = subject.name,
            code = subject.code,
            description = subject.description,
            semesterNumber = subject.semesterNumber,
            notesCount = notes.size.toLong(),
            papersCount = papers.size.toLong(),
            videosCount = videos.size.toLong(),
            notes = notes,
            papers = papers,
            videos = videos
        )
    }

    fun search(query: String): List<SubjectSummary> {
        val q = query.trim().lowercase()
        return semesters
            .flatMap { it.subjects }
            .filter {
                it.name.lowercase().contains(q) ||
                    (it.code?.lowercase()?.contains(q) == true) ||
                    (it.description?.lowercase()?.contains(q) == true)
            }
    }

    private fun nowMinusDays(days: Long): String = LocalDateTime.now().minusDays(days).format(isoFormatter)
}
