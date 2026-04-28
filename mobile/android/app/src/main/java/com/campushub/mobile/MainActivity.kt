package com.campushub.mobile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Support
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.campushub.mobile.data.PoiItem
import com.campushub.mobile.data.ResourceItem
import com.campushub.mobile.data.RoomItem
import com.campushub.mobile.data.SemesterItem
import com.campushub.mobile.data.SubjectDetail
import com.campushub.mobile.data.SubjectSummary
import com.campushub.mobile.data.BookingItem
import com.campushub.mobile.data.ComplaintItem
import com.campushub.mobile.data.LostFoundItem
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = campusHubColorScheme(),
                typography = campusHubTypography()
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CampusHubApp()
                }
            }
        }
    }
}

private enum class AppTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Outlined.Home),
    STUDY("Study", Icons.Outlined.MenuBook),
    CAMPUS("Campus", Icons.Outlined.Explore),
    COMMUNITY("Community", Icons.Outlined.Groups),
    BOOKING("Booking", Icons.Outlined.EventAvailable)
}

private enum class CommunitySection {
    COMPLAINTS,
    LOST_FOUND
}

private enum class ResourceTab(val title: String) {
    NOTES("Notes"),
    PAPERS("PYQs"),
    VIDEOS("Videos")
}

private enum class BookingSection {
    ROOMS,
    MY_BOOKINGS
}

private data class QuickLink(
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val background: Color,
    val tab: AppTab
)

@Composable
private fun CampusHubApp(viewModel: MainViewModel = viewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.statusMessage) {
        if (viewModel.statusMessage.isNotBlank()) {
            snackbarHostState.showSnackbar(viewModel.statusMessage)
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (viewModel.isAuthenticated) {
                AppShell(viewModel)
            } else {
                AuthScreen(viewModel)
            }

            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x33000000)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CampusTheme.Primary)
                }
            }
        }
    }
}

@Composable
private fun AuthScreen(viewModel: MainViewModel) {
    var isRegisterMode by rememberSaveable { mutableStateOf(false) }
    var baseUrl by rememberSaveable { mutableStateOf(viewModel.baseUrl) }
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF15356F), Color(0xFF1D4ED8), Color(0xFF60A5FA))
                )
            )
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.height(AppSpacing.md))
            Text("Campus Hub", style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            Text(
                "Your all-in-one campus companion",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.88f)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = AppRadius.xl,
            colors = CardDefaults.cardColors(containerColor = CampusTheme.Surface)
        ) {
            Column(
                modifier = Modifier.padding(AppSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                Text(
                    if (isRegisterMode) "Create your account" else "Welcome Back!",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    if (isRegisterMode) "Join Campus Hub and access your campus services." else "Sign in to access your campus.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Card(
                    shape = AppRadius.md,
                    colors = CardDefaults.cardColors(containerColor = CampusTheme.PrimaryLight)
                ) {
                    Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                        Text("Server Settings", style = MaterialTheme.typography.titleMedium, color = CampusTheme.Primary)
                        OutlinedTextField(
                            value = baseUrl,
                            onValueChange = { baseUrl = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Backend URL") },
                            singleLine = true,
                            supportingText = { Text("Use http://10.0.2.2:8080 on emulator or your LAN IP on phone.") }
                        )
                    }
                }

                if (!isRegisterMode) {
                    DemoLogins(
                        onStudent = {
                            email = "admin@campus.com"
                            password = "admin123"
                        },
                        onAdmin = {
                            email = "admin@campus.com"
                            password = "admin123"
                        }
                    )
                }

                if (isRegisterMode) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Full name") },
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email address") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password") },
                    singleLine = true
                )

                if (isRegisterMode) {
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Confirm password") },
                        singleLine = true
                    )
                }

                Button(
                    onClick = {
                        viewModel.updateBaseUrl(baseUrl)
                        if (isRegisterMode) {
                            viewModel.register(fullName, email, password, confirmPassword)
                        } else {
                            viewModel.login(email, password)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppRadius.md
                ) {
                    Icon(if (isRegisterMode) Icons.Outlined.School else Icons.Outlined.Login, contentDescription = null)
                    Spacer(modifier = Modifier.width(AppSpacing.sm))
                    Text(if (isRegisterMode) "Create Account" else "Sign In")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (isRegisterMode) "Already have an account? " else "Do not have an account? ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
                        Text(if (isRegisterMode) "Sign In" else "Sign Up")
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoLogins(onStudent: () -> Unit, onAdmin: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Demo:", style = MaterialTheme.typography.labelLarge, color = CampusTheme.TextSecondary)
        AssistChip(onClick = onStudent, label = { Text("Student Login") })
        AssistChip(
            onClick = onAdmin,
            label = { Text("Admin Login") },
            colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
                containerColor = CampusTheme.SecondaryLight,
                labelColor = CampusTheme.Secondary
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppShell(viewModel: MainViewModel) {
    var selectedTab by rememberSaveable { mutableStateOf(AppTab.HOME) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedTab.title) },
                actions = {
                    IconButton(onClick = { viewModel.refreshAll() }) {
                        Icon(Icons.Outlined.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.AutoMirrored.Outlined.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = CampusTheme.Surface) {
                AppTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) }
                    )
                }
            }
        },
        containerColor = CampusTheme.Background
    ) { padding ->
        when (selectedTab) {
            AppTab.HOME -> HomeTab(viewModel, padding) { selectedTab = it }
            AppTab.STUDY -> StudyTab(viewModel, padding)
            AppTab.CAMPUS -> CampusTab(viewModel, padding)
            AppTab.COMMUNITY -> CommunityTab(viewModel, padding)
            AppTab.BOOKING -> BookingTab(viewModel, padding)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HomeTab(viewModel: MainViewModel, padding: PaddingValues, onOpenTab: (AppTab) -> Unit) {
    val dashboard = viewModel.dashboard
    val user = viewModel.sessionUser
    val studyLinks = listOf(
        QuickLink("PYQs", Icons.Outlined.Description, Color(0xFFB7791F), Color(0xFFFAEEDA), AppTab.STUDY),
        QuickLink("Notes", Icons.Outlined.NoteAlt, Color(0xFF185FA5), Color(0xFFE6F1FB), AppTab.STUDY),
        QuickLink("Videos", Icons.Outlined.PlayCircleOutline, Color(0xFFDC2626), Color(0xFFFCEBEB), AppTab.STUDY),
        QuickLink("Planner", Icons.Outlined.CalendarMonth, Color(0xFF6D5BD0), Color(0xFFEEEDFE), AppTab.STUDY)
    )
    val campusLinks = listOf(
        QuickLink("Campus", Icons.Outlined.Map, Color(0xFF1D9E75), Color(0xFFE1F5EE), AppTab.CAMPUS),
        QuickLink("Complaints", Icons.Outlined.ReportProblem, Color(0xFFD85A30), Color(0xFFFAECE7), AppTab.COMMUNITY),
        QuickLink("Rooms", Icons.Outlined.Apartment, Color(0xFF639922), Color(0xFFEAF3DE), AppTab.BOOKING),
        QuickLink("Support", Icons.Outlined.Support, Color(0xFFB83268), Color(0xFFFBEAF0), AppTab.COMMUNITY)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(CampusTheme.Background),
        contentPadding = PaddingValues(bottom = AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        item {
            HomeHero(
                name = user?.fullName?.substringBefore(" ") ?: "Student",
                tasksDue = dashboard?.pendingTasks?.toString() ?: "--",
                deadlines = viewModel.tasks.count { it.status != "DONE" && !it.dueDate.isNullOrBlank() }.toString(),
                subjects = dashboard?.totalSubjects?.toString() ?: "--"
            )
        }
        item {
            Column(
                modifier = Modifier.padding(horizontal = AppSpacing.md),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                SectionHeader("Quick Access")
                QuickAccessGroup("Study", studyLinks, onOpenTab)
                QuickAccessGroup("Campus", campusLinks, onOpenTab)
            }
        }
        item {
            Column(modifier = Modifier.padding(horizontal = AppSpacing.md)) {
                SectionHeader("Featured Semesters")
            }
        }
        if (dashboard == null) {
            item {
                Box(modifier = Modifier.padding(horizontal = AppSpacing.md)) {
                    EmptyStateCard("Loading dashboard data...")
                }
            }
        } else {
            items(dashboard.featuredSemesters) { semester ->
                Box(modifier = Modifier.padding(horizontal = AppSpacing.md)) {
                    FeaturedSemesterCard(semester = semester, onOpen = { onOpenTab(AppTab.STUDY); viewModel.openSubject(it) })
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StudyTab(viewModel: MainViewModel, padding: PaddingValues) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedSemester by rememberSaveable { mutableStateOf(0) }
    val subjectDetail = viewModel.subjectDetail
    val plannerTasks = viewModel.tasks.take(5)
    val modules = listOf(
        QuickLink("Notes", Icons.Outlined.NoteAlt, Color(0xFF185FA5), Color(0xFFE6F1FB), AppTab.STUDY),
        QuickLink("PYQs", Icons.Outlined.Description, Color(0xFF854F0B), Color(0xFFFAEEDA), AppTab.STUDY),
        QuickLink("Videos", Icons.Outlined.PlayCircleOutline, Color(0xFFA32D2D), Color(0xFFFCEBEB), AppTab.STUDY),
        QuickLink("Planner", Icons.Outlined.CalendarMonth, Color(0xFF534AB7), Color(0xFFEEEDFE), AppTab.STUDY)
    )

    if (subjectDetail != null) {
        SubjectDetailScaffold(
            detail = subjectDetail,
            padding = padding,
            onBack = { viewModel.clearSubject() },
            onDownload = viewModel::downloadResource
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(CampusTheme.Background),
        contentPadding = PaddingValues(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                Text("Study", style = MaterialTheme.typography.headlineSmall)
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        viewModel.search(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search notes, PYQs, subjects...") },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                    singleLine = true,
                    shape = AppRadius.md
                )
            }
        }
        item {
            StudyPlannerCard(hasTasks = plannerTasks.isNotEmpty())
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                CompactSectionLabel("Modules")
                Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    StudyModuleTile(Modifier.weight(1f), modules[0])
                    StudyModuleTile(Modifier.weight(1f), modules[1])
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    StudyModuleTile(Modifier.weight(1f), modules[2])
                    StudyModuleTile(Modifier.weight(1f), modules[3])
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                CompactSectionLabel("Priority Snapshot")
                Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    PrioritySnapshotCard(Modifier.weight(1f), "LOW", viewModel.tasks.count { it.priority == "LOW" })
                    PrioritySnapshotCard(Modifier.weight(1f), "MEDIUM", viewModel.tasks.count { it.priority == "MEDIUM" })
                    PrioritySnapshotCard(Modifier.weight(1f), "HIGH", viewModel.tasks.count { it.priority == "HIGH" })
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                CompactSectionLabel("Semester Library")
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                ) {
                    FilterChip(
                        selected = selectedSemester == 0,
                        onClick = { selectedSemester = 0 },
                        label = { Text("All") }
                    )
                    viewModel.semesters.take(6).forEach { semester ->
                        FilterChip(
                            selected = selectedSemester == semester.number,
                            onClick = { selectedSemester = semester.number },
                            label = { Text("Sem ${semester.number}") }
                        )
                    }
                }
            }
        }

        val allSubjects = if (query.isBlank()) {
            viewModel.semesters.flatMap { it.subjects }
        } else {
            viewModel.searchResults
        }
        val content = if (selectedSemester == 0) {
            allSubjects
        } else {
            allSubjects.filter { it.semesterNumber == selectedSemester }
        }

        if (content.isEmpty()) {
            item { EmptyStateCard("No study resources found yet.") }
        } else {
            items(content) { subject ->
                SubjectLibraryRow(subject = subject, onOpen = { viewModel.openSubject(subject.id) })
            }
        }
    }
}

@Composable
private fun CampusTab(viewModel: MainViewModel, padding: PaddingValues) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("ALL") }
    val categories = listOf("ALL" to "All", "ACADEMIC" to "Academic", "ADMIN" to "Admin", "SERVICES" to "Services")
    val filteredPois = viewModel.pois.filter { poi ->
        val matchesQuery = query.isBlank() ||
            poi.name.contains(query, ignoreCase = true) ||
            poi.category.contains(query, ignoreCase = true) ||
            poi.description.orEmpty().contains(query, ignoreCase = true)
        val group = poiCampusGroup(poi)
        val matchesCategory = selectedCategory == "ALL" || group == selectedCategory
        matchesQuery && matchesCategory
    }
    val openCount = viewModel.pois.count { isPoiOpenNow(it.openingHours) }
    val groupedPois = filteredPois.groupBy { poiCampusGroup(it) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(CampusTheme.Background),
        contentPadding = PaddingValues(bottom = AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CampusTheme.Surface)
                    .padding(AppSpacing.md),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Campus", style = MaterialTheme.typography.headlineSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                        CampusStatChip("${viewModel.pois.size} locations")
                        CampusStatChip("$openCount open")
                    }
                }
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search buildings, departments...") },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                    singleLine = true,
                    shape = AppRadius.sm
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                ) {
                    categories.forEach { (value, label) ->
                        FilterChip(
                            selected = selectedCategory == value,
                            onClick = { selectedCategory = value },
                            label = { Text(label) }
                        )
                    }
                }
            }
        }
        if (filteredPois.isEmpty()) {
            item { EmptyStateCard("No campus locations are available right now.") }
        } else {
            groupedPois.forEach { (group, pois) ->
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = AppSpacing.md),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                    ) {
                        CompactSectionLabel(campusGroupTitle(group))
                    }
                }
                items(pois) { poi ->
                    Box(modifier = Modifier.padding(horizontal = AppSpacing.md)) {
                        CampusLocationRow(poi = poi)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommunityTab(viewModel: MainViewModel, padding: PaddingValues) {
    var complaintTitle by rememberSaveable { mutableStateOf("") }
    var complaintDescription by rememberSaveable { mutableStateOf("") }
    var complaintCategory by rememberSaveable { mutableStateOf("INFRASTRUCTURE") }
    var complaintFilter by rememberSaveable { mutableStateOf("ALL") }
    var selectedSection by rememberSaveable { mutableStateOf(CommunitySection.COMPLAINTS) }
    var showComplaintSheet by rememberSaveable { mutableStateOf(false) }

    var itemTitle by rememberSaveable { mutableStateOf("") }
    var itemDescription by rememberSaveable { mutableStateOf("") }
    var itemType by rememberSaveable { mutableStateOf("LOST") }
    var itemLocation by rememberSaveable { mutableStateOf("") }
    var itemContact by rememberSaveable { mutableStateOf("") }
    var itemFilter by rememberSaveable { mutableStateOf("ALL") }
    var showLostFoundSheet by rememberSaveable { mutableStateOf(false) }

    val filteredComplaints = viewModel.complaints.filter { complaint ->
        complaintFilter == "ALL" || complaint.status.equals(complaintFilter, ignoreCase = true)
    }
    val filteredItems = viewModel.lostFoundItems.filter { item ->
        itemFilter == "ALL" ||
            item.type.equals(itemFilter, ignoreCase = true) ||
            (itemFilter == "RECOVERED" && item.status.equals("RESOLVED", ignoreCase = true))
    }
    val openComplaints = viewModel.complaints.count { it.status.equals("OPEN", ignoreCase = true) }
    val inProgressComplaints = viewModel.complaints.count { it.status.equals("IN_PROGRESS", ignoreCase = true) }
    val resolvedComplaints = viewModel.complaints.count { it.status.equals("RESOLVED", ignoreCase = true) || it.status.equals("CLOSED", ignoreCase = true) }
    val lostCount = viewModel.lostFoundItems.count { it.type.equals("LOST", ignoreCase = true) && !it.status.equals("RESOLVED", ignoreCase = true) }
    val foundCount = viewModel.lostFoundItems.count { it.type.equals("FOUND", ignoreCase = true) && !it.status.equals("RESOLVED", ignoreCase = true) }
    val recoveredCount = viewModel.lostFoundItems.count { it.status.equals("RESOLVED", ignoreCase = true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(CampusTheme.Background),
        contentPadding = PaddingValues(bottom = AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        item {
            CommunityTopBar(
                selectedSection = selectedSection,
                openComplaints = openComplaints,
                inProgressComplaints = inProgressComplaints,
                resolvedComplaints = resolvedComplaints,
                lostCount = lostCount,
                foundCount = foundCount,
                recoveredCount = recoveredCount,
                onSectionChange = { selectedSection = it }
            )
        }

        if (selectedSection == CommunitySection.COMPLAINTS) {
            item {
                CommunityFilterRow(
                    filters = listOf("ALL" to "All", "OPEN" to "Open", "IN_PROGRESS" to "In progress", "RESOLVED" to "Resolved"),
                    selected = complaintFilter,
                    onSelect = { complaintFilter = it }
                )
            }
            if (filteredComplaints.isEmpty()) {
                item { Box(modifier = Modifier.padding(horizontal = AppSpacing.md)) { EmptyStateCard("No complaints match this filter yet.") } }
            } else {
                items(filteredComplaints) { complaint ->
                    Box(modifier = Modifier.padding(horizontal = AppSpacing.md)) {
                        ComplaintCard(complaint = complaint, onResolve = { viewModel.resolveComplaint(complaint.id) })
                    }
                }
            }
            item {
                Box(modifier = Modifier.padding(horizontal = AppSpacing.md)) {
                    CommunityFabButton("File a complaint", Color(0xFF534AB7)) { showComplaintSheet = true }
                }
            }
        } else {
            item {
                CommunityFilterRow(
                    filters = listOf("ALL" to "All", "LOST" to "Lost", "FOUND" to "Found", "RECOVERED" to "Recovered"),
                    selected = itemFilter,
                    onSelect = { itemFilter = it }
                )
            }
            if (filteredItems.isEmpty()) {
                item { Box(modifier = Modifier.padding(horizontal = AppSpacing.md)) { EmptyStateCard("No lost or found posts are available right now.") } }
            } else {
                items(filteredItems) { item ->
                    Box(modifier = Modifier.padding(horizontal = AppSpacing.md)) {
                        LostFoundCard(item = item, onResolve = { viewModel.resolveLostFound(item.id) })
                    }
                }
            }
            item {
                Box(modifier = Modifier.padding(horizontal = AppSpacing.md)) {
                    CommunityFabButton("Post lost or found item", Color(0xFF1D9E75)) { showLostFoundSheet = true }
                }
            }
        }
    }

    if (showComplaintSheet) {
        ModalBottomSheet(onDismissRequest = { showComplaintSheet = false }) {
            ComplaintSheetContent(
                category = complaintCategory,
                onCategoryChange = { complaintCategory = it },
                title = complaintTitle,
                onTitleChange = { complaintTitle = it },
                description = complaintDescription,
                onDescriptionChange = { complaintDescription = it },
                onSubmit = {
                    viewModel.createComplaint(complaintTitle, complaintDescription, complaintCategory)
                    complaintTitle = ""
                    complaintDescription = ""
                    showComplaintSheet = false
                }
            )
        }
    }

    if (showLostFoundSheet) {
        ModalBottomSheet(onDismissRequest = { showLostFoundSheet = false }) {
            LostFoundSheetContent(
                type = itemType,
                onTypeChange = { itemType = it },
                title = itemTitle,
                onTitleChange = { itemTitle = it },
                description = itemDescription,
                onDescriptionChange = { itemDescription = it },
                location = itemLocation,
                onLocationChange = { itemLocation = it },
                contact = itemContact,
                onContactChange = { itemContact = it },
                onSubmit = {
                    viewModel.createLostFound(
                        title = itemTitle,
                        description = itemDescription.ifBlank { null },
                        type = itemType,
                        location = itemLocation.ifBlank { null },
                        contactInfo = itemContact.ifBlank { null },
                        imageUrl = null
                    )
                    itemTitle = ""
                    itemDescription = ""
                    itemLocation = ""
                    itemContact = ""
                    showLostFoundSheet = false
                }
            )
        }
    }
}

@Composable
private fun CommunityTopBar(
    selectedSection: CommunitySection,
    openComplaints: Int,
    inProgressComplaints: Int,
    resolvedComplaints: Int,
    lostCount: Int,
    foundCount: Int,
    recoveredCount: Int,
    onSectionChange: (CommunitySection) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CampusTheme.Surface)
            .padding(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Text("Community", style = MaterialTheme.typography.headlineSmall)
        if (selectedSection == CommunitySection.COMPLAINTS) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                CommunityStatCard(Modifier.weight(1f), openComplaints.toString(), "Open", Color(0xFF0C447C))
                CommunityStatCard(Modifier.weight(1f), inProgressComplaints.toString(), "In progress", Color(0xFF854F0B))
                CommunityStatCard(Modifier.weight(1f), resolvedComplaints.toString(), "Resolved", Color(0xFF27500A))
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                CommunityStatCard(Modifier.weight(1f), lostCount.toString(), "Lost", Color(0xFFA32D2D))
                CommunityStatCard(Modifier.weight(1f), foundCount.toString(), "Found", Color(0xFF3B6D11))
                CommunityStatCard(Modifier.weight(1f), recoveredCount.toString(), "Recovered", Color(0xFF27500A))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppRadius.md)
                .background(CampusTheme.SurfaceSecondary)
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            CommunitySegmentButton(
                modifier = Modifier.weight(1f),
                text = "Complaints",
                selected = selectedSection == CommunitySection.COMPLAINTS,
                onClick = { onSectionChange(CommunitySection.COMPLAINTS) }
            )
            CommunitySegmentButton(
                modifier = Modifier.weight(1f),
                text = "Lost & Found",
                selected = selectedSection == CommunitySection.LOST_FOUND,
                onClick = { onSectionChange(CommunitySection.LOST_FOUND) }
            )
        }
    }
}

@Composable
private fun CommunityStatCard(modifier: Modifier = Modifier, value: String, label: String, color: Color) {
    Column(
        modifier = modifier
            .clip(AppRadius.sm)
            .background(CampusTheme.SurfaceSecondary)
            .padding(vertical = AppSpacing.sm, horizontal = AppSpacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = color)
        Text(label, style = MaterialTheme.typography.labelMedium, color = CampusTheme.TextSecondary, maxLines = 1)
    }
}

@Composable
private fun CommunitySegmentButton(modifier: Modifier = Modifier, text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(AppRadius.sm)
            .background(if (selected) CampusTheme.Surface else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = AppSpacing.sm),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) CampusTheme.TextPrimary else CampusTheme.TextSecondary
        )
    }
}

@Composable
private fun CommunityFilterRow(filters: List<Pair<String, String>>, selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = AppSpacing.md)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        filters.forEach { (value, label) ->
            FilterChip(selected = selected == value, onClick = { onSelect(value) }, label = { Text(label) })
        }
    }
}

@Composable
private fun CommunityFabButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = AppRadius.md,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text("+  $text")
    }
}

@Composable
private fun ComplaintSheetContent(
    category: String,
    onCategoryChange: (String) -> Unit,
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        Text("File a complaint", style = MaterialTheme.typography.titleLarge)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            listOf("INFRASTRUCTURE" to "Infrastructure", "ACADEMIC" to "Academic", "HOSTEL" to "Hostel", "CANTEEN" to "Canteen", "OTHER" to "Other").forEach { (value, label) ->
                FilterChip(selected = category == value, onClick = { onCategoryChange(value) }, label = { Text(label) })
            }
        }
        OutlinedTextField(value = title, onValueChange = onTitleChange, modifier = Modifier.fillMaxWidth(), label = { Text("Complaint title") }, singleLine = true)
        OutlinedTextField(value = description, onValueChange = onDescriptionChange, modifier = Modifier.fillMaxWidth(), label = { Text("Describe the issue") }, minLines = 3)
        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && description.isNotBlank(),
            shape = AppRadius.md
        ) {
            Text("Submit complaint")
        }
        Spacer(modifier = Modifier.height(AppSpacing.md))
    }
}

@Composable
private fun LostFoundSheetContent(
    type: String,
    onTypeChange: (String) -> Unit,
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    contact: String,
    onContactChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        Text("Post lost or found item", style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            FilterChip(selected = type == "LOST", onClick = { onTypeChange("LOST") }, label = { Text("Lost") })
            FilterChip(selected = type == "FOUND", onClick = { onTypeChange("FOUND") }, label = { Text("Found") })
        }
        OutlinedTextField(value = title, onValueChange = onTitleChange, modifier = Modifier.fillMaxWidth(), label = { Text("Item title") }, singleLine = true)
        OutlinedTextField(value = description, onValueChange = onDescriptionChange, modifier = Modifier.fillMaxWidth(), label = { Text("Description") }, minLines = 2)
        OutlinedTextField(value = location, onValueChange = onLocationChange, modifier = Modifier.fillMaxWidth(), label = { Text("Location") }, singleLine = true)
        OutlinedTextField(value = contact, onValueChange = onContactChange, modifier = Modifier.fillMaxWidth(), label = { Text("Contact info") }, singleLine = true)
        Button(onClick = onSubmit, modifier = Modifier.fillMaxWidth(), enabled = title.isNotBlank(), shape = AppRadius.md) {
            Text("Post ${type.lowercase().replaceFirstChar { it.uppercase() }} item")
        }
        Spacer(modifier = Modifier.height(AppSpacing.md))
    }
}

@Composable
private fun ComplaintCard(complaint: ComplaintItem, onResolve: () -> Unit) {
    Card(
        shape = AppRadius.md,
        border = BorderStroke(1.dp, CampusTheme.BorderLight),
        colors = CardDefaults.cardColors(containerColor = CampusTheme.Surface)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            CommunityCategoryBadge(complaint.category)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(complaint.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
                StatusBadge(complaint.status)
            }
            Text(complaint.description, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${formatCommunityDate(complaint.createdAt)}${if (complaint.mine) " · Your post" else ""}",
                style = MaterialTheme.typography.bodySmall
            )
            complaint.adminResponse?.takeIf { it.isNotBlank() }?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEAF3DE))
                        .padding(AppSpacing.sm)
                ) {
                    Column {
                        Text("Admin response", style = MaterialTheme.typography.labelMedium, color = Color(0xFF3B6D11))
                        Text(it, style = MaterialTheme.typography.bodySmall, color = Color(0xFF27500A))
                    }
                }
            }
            if (complaint.mine && complaint.status != "RESOLVED" && complaint.status != "CLOSED") {
                OutlinedButton(onClick = onResolve, shape = AppRadius.sm) {
                    Text("Mark as resolved")
                }
            }
        }
    }
}

@Composable
private fun LostFoundCard(item: LostFoundItem, onResolve: () -> Unit) {
    Card(
        shape = AppRadius.md,
        border = BorderStroke(1.dp, CampusTheme.BorderLight),
        colors = CardDefaults.cardColors(containerColor = CampusTheme.Surface.copy(alpha = if (item.status.equals("RESOLVED", ignoreCase = true)) 0.62f else 1f))
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            LostFoundTypeBadge(item.type)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(item.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
                StatusBadge(item.status)
            }
            item.description?.takeIf { it.isNotBlank() }?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                item.location?.takeIf { it.isNotBlank() }?.let {
                    LostFoundMetaPill(text = it, color = Color(0xFFD85A30))
                }
                item.contactInfo?.takeIf { it.isNotBlank() }?.let {
                    LostFoundMetaPill(text = it, color = Color(0xFF7F77DD))
                }
            }
            Text(
                "${formatCommunityDate(item.createdAt)}${if (item.mine) " · Your post" else ""}",
                style = MaterialTheme.typography.bodySmall
            )
            if (item.mine && item.status != "RESOLVED") {
                OutlinedButton(onClick = onResolve, shape = AppRadius.sm) {
                    Text("Mark as closed")
                }
            }
        }
    }
}

@Composable
private fun CommunityCategoryBadge(category: String) {
    val normalized = category.uppercase()
    val colors = when (normalized) {
        "INFRASTRUCTURE" -> Color(0xFFE6F1FB) to Color(0xFF0C447C)
        "CANTEEN" -> Color(0xFFFAEEDA) to Color(0xFF633806)
        "HOSTEL" -> Color(0xFFEEEDFE) to Color(0xFF3C3489)
        "ACADEMIC" -> Color(0xFFE1F5EE) to Color(0xFF085041)
        else -> CampusTheme.SurfaceSecondary to CampusTheme.TextSecondary
    }
    Text(
        text = normalized.lowercase().replaceFirstChar { it.uppercase() },
        modifier = Modifier
            .clip(AppRadius.sm)
            .background(colors.first)
            .padding(horizontal = AppSpacing.sm, vertical = 3.dp),
        style = MaterialTheme.typography.labelMedium,
        color = colors.second
    )
}

@Composable
private fun LostFoundTypeBadge(type: String) {
    val isLost = type.equals("LOST", ignoreCase = true)
    Text(
        text = if (isLost) "Lost" else "Found",
        modifier = Modifier
            .clip(AppRadius.sm)
            .background(if (isLost) Color(0xFFFCEBEB) else Color(0xFFEAF3DE))
            .padding(horizontal = AppSpacing.sm, vertical = 3.dp),
        style = MaterialTheme.typography.labelMedium,
        color = if (isLost) Color(0xFF791F1F) else Color(0xFF27500A)
    )
}

@Composable
private fun LostFoundMetaPill(text: String, color: Color) {
    Row(
        modifier = Modifier
            .clip(AppRadius.sm)
            .background(CampusTheme.SurfaceSecondary)
            .padding(horizontal = AppSpacing.sm, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(text, style = MaterialTheme.typography.labelMedium, color = CampusTheme.TextSecondary, maxLines = 1)
    }
}

private fun formatCommunityDate(value: String?): String {
    if (value.isNullOrBlank()) return "-"
    return value
        .replace("T", " · ")
        .replace(Regex(":\\d{2}(\\.\\d+)?$"), "")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingTab(viewModel: MainViewModel, padding: PaddingValues) {
    var selectedRoomId by rememberSaveable { mutableStateOf<Long?>(null) }
    var startAt by rememberSaveable { mutableStateOf("") }
    var endAt by rememberSaveable { mutableStateOf("") }
    var purpose by rememberSaveable { mutableStateOf("") }
    var selectedSection by rememberSaveable { mutableStateOf(BookingSection.ROOMS) }
    var showBookingSheet by rememberSaveable { mutableStateOf(false) }
    val pendingCount = viewModel.bookings.count { it.status.equals("PENDING", ignoreCase = true) }
    val approvedCount = viewModel.bookings.count { it.status.equals("APPROVED", ignoreCase = true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(CampusTheme.Background),
        contentPadding = PaddingValues(bottom = AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        item {
            BookingTopBar(
                availableCount = viewModel.rooms.size,
                pendingCount = pendingCount,
                approvedCount = approvedCount,
                selectedSection = selectedSection,
                onSectionChange = { selectedSection = it }
            )
        }
        if (selectedSection == BookingSection.ROOMS) {
            item {
                Box(modifier = Modifier.padding(horizontal = AppSpacing.md)) {
                    CompactSectionLabel("All rooms")
                }
            }
            if (viewModel.rooms.isEmpty()) {
                item { Box(modifier = Modifier.padding(horizontal = AppSpacing.md)) { EmptyStateCard("No rooms are available right now.") } }
            } else {
                items(viewModel.rooms) { room ->
                    Box(modifier = Modifier.padding(horizontal = AppSpacing.md)) {
                        RoomCard(
                            room = room,
                            selected = selectedRoomId == room.id,
                            onSelect = {
                                selectedRoomId = room.id
                                if (startAt.isBlank()) startAt = defaultBookingStartAt()
                                if (endAt.isBlank()) endAt = defaultBookingEndAt()
                                showBookingSheet = true
                            }
                        )
                    }
                }
            }
            item {
                Box(modifier = Modifier.padding(horizontal = AppSpacing.md)) {
                    CommunityFabButton("Book a Room", Color(0xFF534AB7)) {
                        if (selectedRoomId == null) selectedRoomId = viewModel.rooms.firstOrNull()?.id
                        if (startAt.isBlank()) startAt = defaultBookingStartAt()
                        if (endAt.isBlank()) endAt = defaultBookingEndAt()
                        showBookingSheet = true
                    }
                }
            }
        } else {
            if (viewModel.bookings.isEmpty()) {
                item { Box(modifier = Modifier.padding(horizontal = AppSpacing.md)) { EmptyStateCard("You have not submitted any booking requests yet.") } }
            } else {
                items(viewModel.bookings) { booking ->
                    Box(modifier = Modifier.padding(horizontal = AppSpacing.md)) {
                        BookingCard(
                            booking = booking,
                            onCancel = { viewModel.cancelBooking(booking.id) },
                            onRebook = {
                                selectedRoomId = booking.roomId
                                startAt = booking.startAt
                                endAt = booking.endAt
                                purpose = booking.purpose.orEmpty()
                                showBookingSheet = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showBookingSheet) {
        ModalBottomSheet(onDismissRequest = { showBookingSheet = false }) {
            BookingSheetContent(
                rooms = viewModel.rooms,
                selectedRoomId = selectedRoomId,
                onRoomSelected = { selectedRoomId = it },
                startAt = startAt,
                onStartAtChange = { startAt = it },
                endAt = endAt,
                onEndAtChange = { endAt = it },
                purpose = purpose,
                onPurposeChange = { purpose = it },
                onSubmit = {
                    selectedRoomId?.let {
                        viewModel.createBooking(it, startAt, endAt, purpose.ifBlank { null })
                        startAt = ""
                        endAt = ""
                        purpose = ""
                        showBookingSheet = false
                    }
                }
            )
        }
    }
}

@Composable
private fun HomeHero(name: String, tasksDue: String, deadlines: String, subjects: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF1A56C4), Color(0xFF2563EB))
                )
            )
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Text("Good morning,", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.72f))
        Text(name, style = MaterialTheme.typography.headlineSmall, color = Color.White)
        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            HeroStatCard(Modifier.weight(1f), tasksDue, "Tasks due")
            HeroStatCard(Modifier.weight(1f), deadlines, "Deadlines")
            HeroStatCard(Modifier.weight(1f), subjects, "Subjects")
        }
    }
}

@Composable
private fun HeroStatCard(modifier: Modifier = Modifier, value: String, label: String) {
    Box(
        modifier = modifier
            .clip(AppRadius.md)
            .background(Color.White.copy(alpha = 0.16f))
            .padding(vertical = AppSpacing.sm, horizontal = AppSpacing.xs),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text(label, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.74f))
        }
    }
}

@Composable
private fun CompactSectionLabel(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = CampusTheme.TextMuted
    )
}

@Composable
private fun StudyPlannerCard(hasTasks: Boolean) {
    Card(
        shape = AppRadius.md,
        border = BorderStroke(1.dp, Color(0xFFAFA9EC)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEDFE))
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(AppRadius.sm)
                        .background(Color(0xFF534AB7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = Color(0xFFEEEDFE), modifier = Modifier.size(18.dp))
                }
                Text("Suggested plan for today", style = MaterialTheme.typography.titleMedium, color = Color(0xFF3C3489))
            }
            Text(
                if (hasTasks) "Start with your highest-priority items, then review notes and PYQs before moving to videos."
                else "Add tasks and due dates to receive a smarter study plan.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF534AB7)
            )
            Button(onClick = {}, shape = AppRadius.sm) {
                Text("Regenerate plan")
            }
        }
    }
}

@Composable
private fun StudyModuleTile(modifier: Modifier = Modifier, link: QuickLink) {
    Card(
        modifier = modifier,
        shape = AppRadius.md,
        border = BorderStroke(1.dp, CampusTheme.BorderLight),
        colors = CardDefaults.cardColors(containerColor = CampusTheme.Surface)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(AppRadius.sm)
                    .background(link.background),
                contentAlignment = Alignment.Center
            ) {
                Icon(link.icon, contentDescription = null, tint = link.color, modifier = Modifier.size(19.dp))
            }
            Text(link.label, style = MaterialTheme.typography.titleMedium)
            Text(
                when (link.label) {
                    "Notes" -> "Study notes"
                    "PYQs" -> "Past papers"
                    "Videos" -> "Playlists"
                    else -> "Deadlines"
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun PrioritySnapshotCard(modifier: Modifier = Modifier, priority: String, count: Int) {
    val colors = when (priority) {
        "HIGH" -> Triple(CampusTheme.DangerLight, Color(0xFFA32D2D), Color(0xFFF09595))
        "MEDIUM" -> Triple(CampusTheme.WarningLight, Color(0xFF854F0B), Color(0xFFEF9F27))
        else -> Triple(Color(0xFFEAF3DE), Color(0xFF3B6D11), Color(0xFF97C459))
    }
    val progress = when (priority) {
        "HIGH" -> 0.85f
        "MEDIUM" -> 0.6f
        else -> 0.3f
    }
    Card(
        modifier = modifier,
        shape = AppRadius.md,
        border = BorderStroke(1.dp, colors.third),
        colors = CardDefaults.cardColors(containerColor = colors.first)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.sm), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(count.toString(), style = MaterialTheme.typography.titleLarge, color = colors.second)
            Text(priority.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelMedium, color = colors.second)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(AppRadius.full)
                    .background(colors.third.copy(alpha = 0.42f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(4.dp)
                        .clip(AppRadius.full)
                        .background(colors.second)
                )
            }
        }
    }
}

@Composable
private fun SubjectLibraryRow(subject: SubjectSummary, onOpen: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppRadius.sm)
            .clickable(onClick = onOpen)
            .background(CampusTheme.Surface)
            .padding(AppSpacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
            Text(subject.name, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
            SubjectCodeBadge(subject.code ?: "S${subject.semesterNumber}")
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            val hasAnyResource = subject.notesCount + subject.papersCount + subject.videosCount > 0
            if (!hasAnyResource) {
                ResourceCountPill("No resources yet", false)
            } else {
                if (subject.notesCount > 0) ResourceCountPill("${subject.notesCount} Notes", true)
                if (subject.papersCount > 0) ResourceCountPill("${subject.papersCount} PYQs", true)
                if (subject.videosCount > 0) ResourceCountPill("${subject.videosCount} Video${if (subject.videosCount == 1L) "" else "s"}", true)
            }
            Text(">", color = CampusTheme.TextMuted, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun QuickAccessGroup(title: String, links: List<QuickLink>, onOpenTab: (AppTab) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
        Text(
            title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = CampusTheme.TextMuted
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            links.forEach { link ->
                QuickAccessCard(
                    modifier = Modifier.weight(1f),
                    link = link,
                    onClick = { onOpenTab(link.tab) }
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: Color,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier,
        shape = AppRadius.md,
        colors = CardDefaults.cardColors(containerColor = CampusTheme.Surface)
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = color, style = MaterialTheme.typography.titleLarge)
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleLarge)
}

@Composable
private fun QuickAccessCard(modifier: Modifier = Modifier, link: QuickLink, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = AppRadius.md,
        border = BorderStroke(1.dp, CampusTheme.BorderLight),
        colors = CardDefaults.cardColors(containerColor = CampusTheme.Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppSpacing.xs),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(AppRadius.md)
                    .background(link.background),
                contentAlignment = Alignment.Center
            ) {
                Icon(link.icon, contentDescription = null, tint = link.color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Text(link.label, style = MaterialTheme.typography.labelMedium, color = CampusTheme.TextSecondary)
        }
    }
}

@Composable
private fun FeaturedSemesterCard(semester: SemesterItem, onOpen: (Long) -> Unit) {
    Card(
        shape = AppRadius.lg,
        border = BorderStroke(1.dp, CampusTheme.BorderLight),
        colors = CardDefaults.cardColors(containerColor = CampusTheme.Surface)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(semester.name, style = MaterialTheme.typography.titleMedium)
                Text("${semester.subjectCount} subjects", style = MaterialTheme.typography.bodySmall)
            }
            semester.subjects.take(4).forEach { subject ->
                CompactSubjectRow(subject = subject, onOpen = { onOpen(subject.id) })
            }
        }
    }
}

@Composable
private fun StudyModuleCard(title: String, subtitle: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppRadius.lg,
        colors = CardDefaults.cardColors(containerColor = CampusTheme.Surface)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(AppRadius.md)
                    .background(color.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun SubjectLibraryCard(subject: SubjectSummary, onOpen: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        shape = AppRadius.lg,
        colors = CardDefaults.cardColors(containerColor = CampusTheme.Surface)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            Text(subject.name, style = MaterialTheme.typography.titleMedium)
            Text(subject.code ?: "Semester ${subject.semesterNumber}", style = MaterialTheme.typography.bodySmall)
            if (!subject.description.isNullOrBlank()) {
                Text(subject.description, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                AssistChip(onClick = onOpen, label = { Text("${subject.notesCount} Notes") })
                AssistChip(onClick = onOpen, label = { Text("${subject.papersCount} PYQs") })
                AssistChip(onClick = onOpen, label = { Text("${subject.videosCount} Videos") })
            }
        }
    }
}

@Composable
private fun CompactSubjectRow(subject: SubjectSummary, onOpen: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppRadius.sm)
            .clickable(onClick = onOpen)
            .padding(vertical = AppSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(subject.name, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
            SubjectCodeBadge(subject.code ?: "S${subject.semesterNumber}")
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ResourceCountPill("${subject.notesCount} Notes", subject.notesCount > 0)
            ResourceCountPill("${subject.papersCount} PYQs", subject.papersCount > 0)
            if (subject.videosCount > 0) {
                ResourceCountPill("${subject.videosCount} Video${if (subject.videosCount == 1L) "" else "s"}", true)
            }
            Text(">", color = CampusTheme.TextMuted, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun SubjectCodeBadge(code: String) {
    val badgeColor = when {
        code.startsWith("CS", ignoreCase = true) -> Color(0xFFE6F1FB) to Color(0xFF0C447C)
        code.startsWith("MA", ignoreCase = true) -> Color(0xFFFAEEDA) to Color(0xFF633806)
        code.startsWith("PH", ignoreCase = true) -> Color(0xFFE1F5EE) to Color(0xFF085041)
        code.startsWith("EN", ignoreCase = true) -> Color(0xFFEAF3DE) to Color(0xFF27500A)
        else -> CampusTheme.SurfaceSecondary to CampusTheme.TextSecondary
    }
    Text(
        text = code,
        modifier = Modifier
            .clip(AppRadius.sm)
            .background(badgeColor.first)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        style = MaterialTheme.typography.labelMedium,
        color = badgeColor.second
    )
}

@Composable
private fun ResourceCountPill(text: String, hasContent: Boolean) {
    val container = if (hasContent) Color(0xFFE6F1FB) else CampusTheme.SurfaceSecondary
    val content = if (hasContent) Color(0xFF185FA5) else CampusTheme.TextSecondary
    Text(
        text = text,
        modifier = Modifier
            .clip(AppRadius.sm)
            .background(container)
            .padding(horizontal = 6.dp, vertical = 3.dp),
        style = MaterialTheme.typography.labelMedium,
        color = content,
        maxLines = 1
    )
}

@Composable
private fun SubjectDetailScaffold(
    detail: SubjectDetail,
    padding: PaddingValues,
    onBack: () -> Unit,
    onDownload: (ResourceItem) -> Unit
) {
    val context = LocalContext.current
    var activeTab by rememberSaveable(detail.id) { mutableStateOf(ResourceTab.NOTES) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(CampusTheme.Background),
        contentPadding = PaddingValues(bottom = AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CampusTheme.Surface)
                    .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back to library")
                }
                Text(detail.code ?: "Subject", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            }
        }
        item {
            Column(
                modifier = Modifier.padding(horizontal = AppSpacing.md),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                SubjectHeaderCard(detail)
                SubjectResourceTabs(activeTab = activeTab, onSelect = { activeTab = it })
                ResourceTabContent(
                    tab = activeTab,
                    detail = detail,
                    onDownload = onDownload,
                    onOpenVideo = { resource ->
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(resource.url)))
                    }
                )
            }
        }
    }
}

@Composable
private fun SubjectHeaderCard(detail: SubjectDetail) {
    val dept = departmentLabel(detail.code)
    Card(
        shape = AppRadius.lg,
        border = BorderStroke(1.dp, Color(0xFF85B7EB)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F1FB))
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                DetailBadge(dept, true)
                DetailBadge("Semester ${detail.semesterNumber}", false)
            }
            Text(detail.name, style = MaterialTheme.typography.headlineSmall, color = Color(0xFF0A2F5E))
            if (!detail.description.isNullOrBlank()) {
                Text(detail.description, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF185FA5))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                DetailResourceChip("${detail.notesCount} Notes", detail.notesCount > 0)
                DetailResourceChip("${detail.papersCount} PYQs", detail.papersCount > 0)
                DetailResourceChip("${detail.videosCount} Videos", detail.videosCount > 0)
            }
        }
    }
}

@Composable
private fun DetailBadge(text: String, strong: Boolean) {
    Text(
        text = text,
        modifier = Modifier
            .clip(AppRadius.sm)
            .background(Color.White.copy(alpha = if (strong) 0.76f else 0.52f))
            .padding(horizontal = AppSpacing.sm, vertical = 3.dp),
        style = MaterialTheme.typography.labelMedium,
        color = if (strong) Color(0xFF0C447C) else Color(0xFF185FA5)
    )
}

@Composable
private fun DetailResourceChip(text: String, hasContent: Boolean) {
    Text(
        text = text,
        modifier = Modifier
            .clip(AppRadius.full)
            .background(if (hasContent) Color(0xFF185FA5) else Color.White.copy(alpha = 0.65f))
            .padding(horizontal = AppSpacing.sm, vertical = 4.dp),
        style = MaterialTheme.typography.labelMedium,
        color = if (hasContent) Color.White else Color(0xFF185FA5)
    )
}

@Composable
private fun SubjectResourceTabs(activeTab: ResourceTab, onSelect: (ResourceTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CampusTheme.Surface),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        ResourceTab.entries.forEach { tab ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(tab) }
                    .padding(vertical = AppSpacing.sm),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    tab.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (activeTab == tab) Color(0xFF185FA5) else CampusTheme.TextSecondary
                )
                Spacer(modifier = Modifier.height(5.dp))
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(if (activeTab == tab) Color(0xFF185FA5) else Color.Transparent)
                )
            }
        }
    }
}

@Composable
private fun ResourceTabContent(
    tab: ResourceTab,
    detail: SubjectDetail,
    onDownload: (ResourceItem) -> Unit,
    onOpenVideo: (ResourceItem) -> Unit
) {
    val resources = when (tab) {
        ResourceTab.NOTES -> detail.notes
        ResourceTab.PAPERS -> detail.papers
        ResourceTab.VIDEOS -> detail.videos
    }

    if (resources.isEmpty()) {
        SubjectResourceEmptyState(tab)
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        if (tab == ResourceTab.NOTES) {
            ResourceProgress(viewed = minOf(2, resources.size), total = resources.size)
        }
        resources.forEachIndexed { index, resource ->
            ResourceListRow(
                resource = resource,
                dimmed = tab == ResourceTab.NOTES && index >= 2,
                onOpen = {
                    if (tab == ResourceTab.VIDEOS || resource.type == "VIDEO") onOpenVideo(resource) else onDownload(resource)
                }
            )
        }
    }
}

@Composable
private fun ResourceProgress(viewed: Int, total: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        Text("$viewed of $total viewed", style = MaterialTheme.typography.labelMedium, color = CampusTheme.TextSecondary)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(5.dp)
                .clip(AppRadius.full)
                .background(CampusTheme.SurfaceSecondary)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (total == 0) 0f else viewed.toFloat() / total.toFloat())
                    .height(5.dp)
                    .clip(AppRadius.full)
                    .background(Color(0xFF185FA5))
            )
        }
    }
}

@Composable
private fun ResourceListRow(resource: ResourceItem, dimmed: Boolean, onOpen: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        shape = AppRadius.md,
        border = BorderStroke(1.dp, CampusTheme.BorderLight),
        colors = CardDefaults.cardColors(containerColor = CampusTheme.Surface.copy(alpha = if (dimmed) 0.55f else 1f))
    ) {
        Row(
            modifier = Modifier.padding(AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(AppRadius.sm)
                    .background(if (resource.type == "VIDEO") CampusTheme.DangerLight else Color(0xFFE6F1FB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (resource.type == "VIDEO") Icons.Outlined.PlayCircleOutline else Icons.Outlined.Description,
                    contentDescription = null,
                    tint = if (resource.type == "VIDEO") CampusTheme.Danger else Color(0xFF185FA5)
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(resource.title, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(resource.subtitle ?: resource.description ?: resource.type, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            TextButton(onClick = onOpen) {
                Text(if (resource.type == "VIDEO") "Play" else "Open")
            }
        }
    }
}

@Composable
private fun SubjectResourceEmptyState(tab: ResourceTab) {
    val label = when (tab) {
        ResourceTab.NOTES -> "notes"
        ResourceTab.PAPERS -> "PYQs"
        ResourceTab.VIDEOS -> "videos"
    }
    Card(
        shape = AppRadius.lg,
        colors = CardDefaults.cardColors(containerColor = CampusTheme.Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(AppRadius.md)
                    .background(Color(0xFFE6F1FB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Description, contentDescription = null, tint = Color(0xFF185FA5))
            }
            Text("No $label yet", style = MaterialTheme.typography.titleMedium)
            Text(
                "$label for this subject have not been uploaded yet. You can request them or check again later.",
                style = MaterialTheme.typography.bodySmall,
                color = CampusTheme.TextSecondary
            )
            OutlinedButton(onClick = {}, shape = AppRadius.sm) {
                Text("Request $label")
            }
        }
    }
}

private fun departmentLabel(code: String?): String = when {
    code.isNullOrBlank() -> "SUB"
    code.startsWith("CS", ignoreCase = true) -> "CSE"
    code.startsWith("MA", ignoreCase = true) -> "MATH"
    code.startsWith("PH", ignoreCase = true) -> "PHY"
    code.startsWith("EN", ignoreCase = true) -> "ENG"
    else -> code.take(2).uppercase()
}

@Composable
private fun ResourceSection(
    title: String,
    resources: List<ResourceItem>,
    onDownload: (ResourceItem) -> Unit,
    onOpenVideo: (ResourceItem) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        SectionHeader(title)
        if (resources.isEmpty()) {
            EmptyStateCard("No $title available.")
        } else {
            resources.forEach { resource ->
                Card(shape = AppRadius.lg, colors = CardDefaults.cardColors(containerColor = CampusTheme.Surface)) {
                    Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                        Text(resource.title, style = MaterialTheme.typography.titleMedium)
                        if (!resource.subtitle.isNullOrBlank()) {
                            Text(resource.subtitle, style = MaterialTheme.typography.bodySmall)
                        }
                        if (!resource.description.isNullOrBlank()) {
                            Text(resource.description, style = MaterialTheme.typography.bodyMedium)
                        }
                        if (resource.type == "VIDEO") {
                            Button(onClick = { onOpenVideo(resource) }, shape = AppRadius.md) {
                                Icon(Icons.Outlined.Visibility, contentDescription = null)
                                Spacer(modifier = Modifier.width(AppSpacing.sm))
                                Text("Open Video")
                            }
                        } else {
                            Button(onClick = { onDownload(resource) }, shape = AppRadius.md) {
                                Icon(Icons.Outlined.Description, contentDescription = null)
                                Spacer(modifier = Modifier.width(AppSpacing.sm))
                                Text("Download")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PoiCard(poi: PoiItem) {
    Card(shape = AppRadius.lg, colors = CardDefaults.cardColors(containerColor = CampusTheme.Surface)) {
        Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(AppRadius.md)
                        .background(CampusTheme.CampusLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = CampusTheme.Campus)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(poi.name, style = MaterialTheme.typography.titleMedium)
                    Text(poi.category, style = MaterialTheme.typography.bodySmall)
                }
            }
            if (!poi.description.isNullOrBlank()) {
                Text(poi.description, style = MaterialTheme.typography.bodyMedium)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                poi.floor?.takeIf { it.isNotBlank() }?.let {
                    AssistChip(onClick = {}, label = { Text("Floor $it") })
                }
                poi.openingHours?.takeIf { it.isNotBlank() }?.let {
                    AssistChip(onClick = {}, label = { Text(it) })
                }
            }
        }
    }
}

@Composable
private fun CampusStatChip(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .clip(AppRadius.full)
            .background(CampusTheme.CampusLight)
            .padding(horizontal = AppSpacing.sm, vertical = 4.dp),
        style = MaterialTheme.typography.labelMedium,
        color = Color(0xFF085041)
    )
}

@Composable
private fun CampusLocationRow(poi: PoiItem) {
    val group = poiCampusGroup(poi)
    val isOpen = isPoiOpenNow(poi.openingHours)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppRadius.md,
        border = BorderStroke(1.dp, CampusTheme.BorderLight),
        colors = CardDefaults.cardColors(containerColor = CampusTheme.Surface)
    ) {
        Row(
            modifier = Modifier.padding(AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(AppRadius.sm)
                    .background(campusIconBackground(group)),
                contentAlignment = Alignment.Center
            ) {
                Icon(campusIcon(group), contentDescription = null, tint = campusIconTint(group), modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                Text(poi.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                    CampusMetaPill(shortFloor(poi.floor))
                    CampusMetaPill(shortCampusMeta(poi))
                }
            }
            CampusStatusBadge(isOpen)
        }
    }
}

@Composable
private fun CampusMetaPill(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .clip(AppRadius.sm)
            .background(CampusTheme.SurfaceSecondary)
            .padding(horizontal = AppSpacing.sm, vertical = 3.dp),
        style = MaterialTheme.typography.labelMedium,
        color = CampusTheme.TextSecondary,
        maxLines = 1
    )
}

@Composable
private fun CampusStatusBadge(isOpen: Boolean) {
    val background = if (isOpen) Color(0xFFEAF3DE) else Color(0xFFFCEBEB)
    val content = if (isOpen) Color(0xFF3B6D11) else Color(0xFFA32D2D)
    Text(
        text = if (isOpen) "Open" else "Closed",
        modifier = Modifier
            .clip(AppRadius.sm)
            .background(background)
            .padding(horizontal = AppSpacing.sm, vertical = 4.dp),
        style = MaterialTheme.typography.labelMedium,
        color = content
    )
}

private fun poiCampusGroup(poi: PoiItem): String {
    val text = "${poi.category} ${poi.name} ${poi.description.orEmpty()}".lowercase()
    return when {
        text.contains("academic") || text.contains("department") || text.contains("block a") || text.contains("block b") || text.contains("block c") -> "ACADEMIC"
        text.contains("admin") || text.contains("registrar") || text.contains("accounts") || text.contains("exam") -> "ADMIN"
        else -> "SERVICES"
    }
}

private fun campusGroupTitle(group: String): String = when (group) {
    "ACADEMIC" -> "Academic"
    "ADMIN" -> "Admin & Facilities"
    else -> "Services"
}

private fun campusIcon(group: String): ImageVector = when (group) {
    "ACADEMIC" -> Icons.Outlined.Apartment
    "ADMIN" -> Icons.Outlined.Groups
    else -> Icons.Outlined.Map
}

private fun campusIconBackground(group: String): Color = when (group) {
    "ACADEMIC" -> Color(0xFFE6F1FB)
    "ADMIN" -> Color(0xFFFAEEDA)
    else -> CampusTheme.CampusLight
}

private fun campusIconTint(group: String): Color = when (group) {
    "ACADEMIC" -> Color(0xFF0C447C)
    "ADMIN" -> Color(0xFF633806)
    else -> Color(0xFF085041)
}

private fun shortCampusMeta(poi: PoiItem): String {
    val description = poi.description.orEmpty()
    return when {
        description.contains("CSE", ignoreCase = true) || description.contains("IT", ignoreCase = true) -> "CSE & IT"
        description.contains("ECE", ignoreCase = true) || description.contains("EE", ignoreCase = true) -> "ECE, EE, ME"
        description.contains("MBA", ignoreCase = true) || description.contains("BBA", ignoreCase = true) -> "MBA, BBA"
        description.contains("auditorium", ignoreCase = true) || description.contains("500", ignoreCase = true) -> "500 seats"
        !poi.openingHours.isNullOrBlank() -> poi.openingHours.substringBefore(" ")
        else -> poi.category.lowercase().replaceFirstChar { it.uppercase() }
    }
}

private fun shortFloor(floor: String?): String {
    val value = floor?.trim().orEmpty()
    return when {
        value.isBlank() -> "Floor info"
        value.contains("ground to 4th", ignoreCase = true) -> "G - 4F"
        value.contains("ground to 3rd", ignoreCase = true) -> "G - 3F"
        value.contains("ground to 2nd", ignoreCase = true) -> "G - 2F"
        value.contains("ground to 1st", ignoreCase = true) -> "G - 1F"
        value.equals("ground floor", ignoreCase = true) -> "Ground"
        else -> value.replace("Floor ", "", ignoreCase = true)
    }
}

private fun isPoiOpenNow(openingHours: String?): Boolean {
    if (openingHours.isNullOrBlank()) return false
    val text = openingHours.trim()
    if (text.contains("as per", ignoreCase = true)) return false
    val pattern = Regex("(?i)(Mon|Tue|Wed|Thu|Fri|Sat|Sun)(?:-(Mon|Tue|Wed|Thu|Fri|Sat|Sun))?\\s+(\\d{1,2}:\\d{2}\\s*[AP]M)\\s*-\\s*(\\d{1,2}:\\d{2}\\s*[AP]M)")
    val match = pattern.find(text) ?: return false
    val now = LocalDateTime.now()
    val startDay = parseDay(match.groupValues[1])
    val endDay = match.groupValues[2].takeIf { it.isNotBlank() }?.let(::parseDay) ?: startDay
    val currentDay = now.dayOfWeek
    if (!isDayInRange(currentDay, startDay, endDay)) return false
    val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
    val start = LocalTime.parse(match.groupValues[3].replace(" ", "").uppercase().replace("AM", " AM").replace("PM", " PM"), formatter)
    val end = LocalTime.parse(match.groupValues[4].replace(" ", "").uppercase().replace("AM", " AM").replace("PM", " PM"), formatter)
    val currentTime = now.toLocalTime()
    return !currentTime.isBefore(start) && !currentTime.isAfter(end)
}

private fun parseDay(value: String): DayOfWeek = when (value.take(3).lowercase()) {
    "mon" -> DayOfWeek.MONDAY
    "tue" -> DayOfWeek.TUESDAY
    "wed" -> DayOfWeek.WEDNESDAY
    "thu" -> DayOfWeek.THURSDAY
    "fri" -> DayOfWeek.FRIDAY
    "sat" -> DayOfWeek.SATURDAY
    else -> DayOfWeek.SUNDAY
}

private fun isDayInRange(day: DayOfWeek, start: DayOfWeek, end: DayOfWeek): Boolean {
    val currentValue = day.value
    val startValue = start.value
    val endValue = end.value
    return if (startValue <= endValue) currentValue in startValue..endValue else currentValue >= startValue || currentValue <= endValue
}

@Composable
private fun CommunityActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = AppRadius.lg,
        colors = CardDefaults.cardColors(containerColor = CampusTheme.Surface)
    ) {
        Row(
            modifier = Modifier.padding(AppSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(AppRadius.md)
                    .background(color.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun BookingTopBar(
    availableCount: Int,
    pendingCount: Int,
    approvedCount: Int,
    selectedSection: BookingSection,
    onSectionChange: (BookingSection) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CampusTheme.Surface)
            .padding(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Text("Booking", style = MaterialTheme.typography.headlineSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            CommunityStatCard(Modifier.weight(1f), availableCount.toString(), "Available", Color(0xFF1D9E75))
            CommunityStatCard(Modifier.weight(1f), pendingCount.toString(), "Pending", Color(0xFF633806))
            CommunityStatCard(Modifier.weight(1f), approvedCount.toString(), "Approved", Color(0xFF27500A))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppRadius.md)
                .background(CampusTheme.SurfaceSecondary)
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            CommunitySegmentButton(
                modifier = Modifier.weight(1f),
                text = "Available Rooms",
                selected = selectedSection == BookingSection.ROOMS,
                onClick = { onSectionChange(BookingSection.ROOMS) }
            )
            CommunitySegmentButton(
                modifier = Modifier.weight(1f),
                text = "My Bookings",
                selected = selectedSection == BookingSection.MY_BOOKINGS,
                onClick = { onSectionChange(BookingSection.MY_BOOKINGS) }
            )
        }
    }
}

@Composable
private fun BookingSheetContent(
    rooms: List<RoomItem>,
    selectedRoomId: Long?,
    onRoomSelected: (Long) -> Unit,
    startAt: String,
    onStartAtChange: (String) -> Unit,
    endAt: String,
    onEndAtChange: (String) -> Unit,
    purpose: String,
    onPurposeChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        Text("Book a Room", style = MaterialTheme.typography.titleLarge)
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
            Text("Select room", style = MaterialTheme.typography.labelMedium, color = CampusTheme.TextSecondary)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
            ) {
                rooms.forEach { room ->
                    FilterChip(
                        selected = selectedRoomId == room.id,
                        onClick = { onRoomSelected(room.id) },
                        label = { Text(room.roomNumber ?: room.name) }
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            OutlinedTextField(
                value = startAt.substringBefore("T", startAt),
                onValueChange = { value -> onStartAtChange(mergeDateTime(value, startAt.substringAfter("T", "10:00:00"))) },
                modifier = Modifier.weight(1f),
                label = { Text("Start date") },
                singleLine = true
            )
            OutlinedTextField(
                value = startAt.substringAfter("T", ""),
                onValueChange = { value -> onStartAtChange(mergeDateTime(startAt.substringBefore("T", "2026-04-04"), value)) },
                modifier = Modifier.weight(1f),
                label = { Text("Start time") },
                singleLine = true
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            OutlinedTextField(
                value = endAt.substringBefore("T", endAt),
                onValueChange = { value -> onEndAtChange(mergeDateTime(value, endAt.substringAfter("T", "12:00:00"))) },
                modifier = Modifier.weight(1f),
                label = { Text("End date") },
                singleLine = true
            )
            OutlinedTextField(
                value = endAt.substringAfter("T", ""),
                onValueChange = { value -> onEndAtChange(mergeDateTime(endAt.substringBefore("T", "2026-04-04"), value)) },
                modifier = Modifier.weight(1f),
                label = { Text("End time") },
                singleLine = true
            )
        }
        OutlinedTextField(value = purpose, onValueChange = onPurposeChange, modifier = Modifier.fillMaxWidth(), label = { Text("Purpose") })
        Button(
            onClick = onSubmit,
            enabled = selectedRoomId != null && startAt.isNotBlank() && endAt.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            shape = AppRadius.md
        ) {
            Text("Submit booking request")
        }
        Spacer(modifier = Modifier.height(AppSpacing.md))
    }
}

@Composable
private fun RoomCard(room: RoomItem, selected: Boolean, onSelect: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = AppRadius.md,
        border = BorderStroke(1.dp, if (selected) Color(0xFF534AB7) else CampusTheme.BorderLight),
        colors = CardDefaults.cardColors(containerColor = if (selected) Color(0xFFF4F3FD) else CampusTheme.Surface)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            RoomTypeBadge(room)
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(room.roomNumber ?: room.name, style = MaterialTheme.typography.titleMedium)
                CampusMetaPill("${room.capacity} seats")
            }
            Text("${room.building}${room.floor?.let { " · Floor $it" } ?: ""}", style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                roomAmenities(room.resources).forEach { amenity ->
                    AmenityPill(amenity)
                }
            }
        }
    }
}

@Composable
private fun BookingCard(booking: BookingItem, onCancel: () -> Unit, onRebook: () -> Unit) {
    Card(
        shape = AppRadius.md,
        border = BorderStroke(1.dp, CampusTheme.BorderLight),
        colors = CardDefaults.cardColors(containerColor = CampusTheme.Surface.copy(alpha = if (booking.status.equals("CANCELLED", ignoreCase = true)) 0.72f else 1f))
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(booking.roomName, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                StatusBadge(booking.status)
            }
            Text(formatBookingRange(booking.startAt, booking.endAt), style = MaterialTheme.typography.bodySmall)
            booking.purpose?.takeIf { it.isNotBlank() }?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            if (booking.status == "PENDING" || booking.status == "APPROVED") {
                OutlinedButton(onClick = onCancel, shape = AppRadius.sm) {
                    Text("Cancel booking")
                }
            } else if (booking.status == "CANCELLED") {
                OutlinedButton(onClick = onRebook, shape = AppRadius.sm) {
                    Text("Rebook this room")
                }
            }
        }
    }
}

@Composable
private fun RoomTypeBadge(room: RoomItem) {
    val isStudyRoom = room.name.contains("study", ignoreCase = true) || room.roomNumber?.contains("LSR", ignoreCase = true) == true || room.capacity <= 15
    Text(
        text = if (isStudyRoom) "Study Room" else "Lecture Hall",
        modifier = Modifier
            .clip(AppRadius.sm)
            .background(if (isStudyRoom) Color(0xFFE6F1FB) else Color(0xFFE1F5EE))
            .padding(horizontal = AppSpacing.sm, vertical = 3.dp),
        style = MaterialTheme.typography.labelMedium,
        color = if (isStudyRoom) Color(0xFF0C447C) else Color(0xFF085041)
    )
}

@Composable
private fun AmenityPill(text: String) {
    Row(
        modifier = Modifier
            .clip(AppRadius.sm)
            .background(CampusTheme.SurfaceSecondary)
            .padding(horizontal = AppSpacing.sm, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(amenityColor(text))
        )
        Text(text, style = MaterialTheme.typography.labelMedium, color = CampusTheme.TextSecondary)
    }
}

private fun roomAmenities(resources: String?): List<String> {
    val text = resources.orEmpty()
    val amenities = mutableListOf<String>()
    if (text.contains("projector", ignoreCase = true)) amenities += "Projector"
    if (text.contains("whiteboard", ignoreCase = true)) amenities += "Whiteboard"
    if (text.contains("ac", ignoreCase = true)) amenities += "AC"
    if (text.contains("smart", ignoreCase = true)) amenities += "Smart Board"
    Regex("powerOutlets\"?\\s*:?\\s*(\\d+)", RegexOption.IGNORE_CASE).find(text)?.groupValues?.getOrNull(1)?.let {
        amenities += "$it outlets"
    }
    return amenities.ifEmpty { listOf("Basic setup") }
}

private fun amenityColor(text: String): Color = when {
    text.contains("Projector", ignoreCase = true) -> Color(0xFFD85A30)
    text.contains("Whiteboard", ignoreCase = true) -> Color(0xFF378ADD)
    text.contains("AC", ignoreCase = true) -> Color(0xFF1D9E75)
    text.contains("Smart", ignoreCase = true) -> Color(0xFF7F77DD)
    else -> Color(0xFFEF9F27)
}

private fun mergeDateTime(date: String, time: String): String {
    if (date.isBlank() && time.isBlank()) return ""
    return "${date.ifBlank { "2026-04-04" }}T${time.ifBlank { "10:00:00" }}"
}

private fun defaultBookingStartAt(): String = LocalDateTime.now()
    .plusDays(1)
    .withHour(10)
    .withMinute(0)
    .withSecond(0)
    .withNano(0)
    .toString()

private fun defaultBookingEndAt(): String = LocalDateTime.now()
    .plusDays(1)
    .withHour(12)
    .withMinute(0)
    .withSecond(0)
    .withNano(0)
    .toString()

private fun formatBookingRange(startAt: String, endAt: String): String {
    return try {
        val start = LocalDateTime.parse(startAt)
        val end = LocalDateTime.parse(endAt)
        val date = start.format(DateTimeFormatter.ofPattern("MMM d", Locale.US))
        val startTime = start.format(DateTimeFormatter.ofPattern("h:mm", Locale.US))
        val endTime = end.format(DateTimeFormatter.ofPattern("h:mm a", Locale.US))
        "$date · $startTime - $endTime"
    } catch (_: Exception) {
        "$startAt to $endAt"
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (container, textColor) = when (status) {
        "APPROVED", "DONE" -> CampusTheme.SuccessLight to CampusTheme.Success
        "REJECTED", "CANCELLED" -> CampusTheme.DangerLight to CampusTheme.Danger
        "IN_PROGRESS" -> CampusTheme.WarningLight to CampusTheme.Warning
        else -> CampusTheme.PrimaryLight to CampusTheme.Primary
    }
    Box(
        modifier = Modifier
            .clip(AppRadius.full)
            .background(container)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(status, color = textColor, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
private fun EmptyStateCard(message: String) {
    Card(shape = AppRadius.lg, colors = CardDefaults.cardColors(containerColor = CampusTheme.SurfaceSecondary)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.lg),
            contentAlignment = Alignment.Center
        ) {
            Text(message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
