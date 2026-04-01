package com.campushub.mobile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

private data class QuickLink(
    val label: String,
    val icon: ImageVector,
    val color: Color,
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
    val quickLinks = listOf(
        QuickLink("PYQs", Icons.Outlined.Description, CampusTheme.Study, AppTab.STUDY),
        QuickLink("Notes", Icons.Outlined.Book, CampusTheme.Secondary, AppTab.STUDY),
        QuickLink("Campus Map", Icons.Outlined.Map, CampusTheme.Campus, AppTab.CAMPUS),
        QuickLink("Support", Icons.Outlined.Support, CampusTheme.Community, AppTab.COMMUNITY),
        QuickLink("Complaints", Icons.Outlined.ReportProblem, CampusTheme.Danger, AppTab.COMMUNITY),
        QuickLink("Rooms", Icons.Outlined.Apartment, CampusTheme.Booking, AppTab.BOOKING),
        QuickLink("Planner", Icons.Outlined.Schedule, CampusTheme.Info, AppTab.STUDY),
        QuickLink("Videos", Icons.Outlined.PlayCircleOutline, CampusTheme.Danger, AppTab.STUDY)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Good day,", style = MaterialTheme.typography.bodyMedium, color = CampusTheme.TextMuted)
                    Text(
                        user?.fullName?.substringBefore(" ") ?: "Student",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(CampusTheme.PrimaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.School, contentDescription = null, tint = CampusTheme.Primary)
                }
            }
        }
        item {
            ElevatedCard(shape = AppRadius.lg, colors = CardDefaults.elevatedCardColors(containerColor = CampusTheme.Surface)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(Color(0xFF0F3B7B), Color(0xFF2563EB))))
                        .padding(AppSpacing.lg)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                        Text("Welcome to Campus Hub", style = MaterialTheme.typography.titleLarge, color = Color.White)
                        Text(
                            user?.email ?: "Connected to your campus workspace",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.sm))
                        Text("Library, planner, campus tools, and bookings in one place.", color = Color.White.copy(alpha = 0.9f))
                    }
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                StatCard(Modifier.weight(1f), "Tasks", dashboard?.pendingTasks?.toString() ?: "--", CampusTheme.Study)
                StatCard(Modifier.weight(1f), "Semesters", dashboard?.totalSemesters?.toString() ?: "--", CampusTheme.Campus)
                StatCard(Modifier.weight(1f), "Subjects", dashboard?.totalSubjects?.toString() ?: "--", CampusTheme.Booking)
            }
        }
        item {
            SectionHeader("Quick Access")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                maxItemsInEachRow = 4
            ) {
                quickLinks.forEach { link ->
                    QuickAccessCard(link = link, onClick = { onOpenTab(link.tab) })
                }
            }
        }
        item { SectionHeader("Featured Semesters") }
        if (dashboard == null) {
            item { EmptyStateCard("Loading dashboard data...") }
        } else {
            items(dashboard.featuredSemesters) { semester ->
                FeaturedSemesterCard(semester = semester, onOpen = { onOpenTab(AppTab.STUDY); viewModel.openSubject(it) })
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StudyTab(viewModel: MainViewModel, padding: PaddingValues) {
    var query by rememberSaveable { mutableStateOf("") }
    val subjectDetail = viewModel.subjectDetail
    val plannerTasks = viewModel.tasks.take(5)

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
            .padding(padding),
        contentPadding = PaddingValues(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        item {
            ElevatedCard(shape = AppRadius.lg, colors = CardDefaults.elevatedCardColors(containerColor = CampusTheme.Surface)) {
                Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(CampusTheme.AccentLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = CampusTheme.Accent)
                        }
                        Column {
                            Text("AI Study Planner", style = MaterialTheme.typography.titleMedium)
                            Text("Generate a focused plan from your live tasks and resources.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Card(shape = AppRadius.md, colors = CardDefaults.cardColors(containerColor = CampusTheme.AccentLight)) {
                        Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                            Text("Suggested routine", style = MaterialTheme.typography.labelLarge, color = CampusTheme.Accent)
                            Text(
                                if (plannerTasks.isEmpty()) "Add tasks and due dates to receive a smarter study plan."
                                else "Start with your highest-priority items, then review notes and PYQs before moving to videos.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = CampusTheme.TextPrimary)
                            )
                        }
                    }
                }
            }
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    viewModel.search(it)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search notes, papers, and subjects") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                singleLine = true
            )
        }
        item {
            SectionHeader("Modules")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                maxItemsInEachRow = 2
            ) {
                StudyModuleCard("Notes", "Browse uploaded study notes", Icons.Outlined.NoteAlt, CampusTheme.Study)
                StudyModuleCard("PYQs", "Practice previous year papers", Icons.Outlined.Description, CampusTheme.Secondary)
                StudyModuleCard("Videos", "Open tutorial playlists", Icons.Outlined.VideoLibrary, CampusTheme.Danger)
                StudyModuleCard("Planner", "Track deadlines and sessions", Icons.Outlined.CalendarMonth, CampusTheme.Info)
            }
        }
        item {
            SectionHeader("Priority Snapshot")
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                listOf("LOW", "MEDIUM", "HIGH").forEach { priority ->
                    val count = viewModel.tasks.count { it.priority == priority }
                    AssistChip(
                        onClick = {},
                        label = { Text("$priority $count") },
                        colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
                            containerColor = when (priority) {
                                "HIGH" -> CampusTheme.DangerLight
                                "MEDIUM" -> CampusTheme.WarningLight
                                else -> CampusTheme.SuccessLight
                            }
                        )
                    )
                }
            }
        }
        item { SectionHeader("Semester Library") }

        val content = if (query.isBlank()) {
            viewModel.semesters.flatMap { it.subjects }
        } else {
            viewModel.searchResults
        }

        if (content.isEmpty()) {
            item { EmptyStateCard("No study resources found yet.") }
        } else {
            items(content) { subject ->
                SubjectLibraryCard(subject = subject, onOpen = { viewModel.openSubject(subject.id) })
            }
        }
    }
}

@Composable
private fun CampusTab(viewModel: MainViewModel, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        item {
            ElevatedCard(shape = AppRadius.lg, colors = CardDefaults.elevatedCardColors(containerColor = CampusTheme.Surface)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(CampusTheme.CampusLight, Color.White)))
                        .padding(AppSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                ) {
                    Text("Explore Campus", style = MaterialTheme.typography.titleLarge)
                    Text("Live points of interest from your backend.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        item { SectionHeader("Locations") }
        if (viewModel.pois.isEmpty()) {
            item { EmptyStateCard("No campus locations are available right now.") }
        } else {
            items(viewModel.pois) { poi ->
                PoiCard(poi = poi)
            }
        }
    }
}

@Composable
private fun CommunityTab(viewModel: MainViewModel, padding: PaddingValues) {
    val baseUrl = viewModel.baseUrl.trimEnd('/')
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        item {
            ElevatedCard(shape = AppRadius.lg, colors = CardDefaults.elevatedCardColors(containerColor = CampusTheme.Surface)) {
                Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    Text("Community Hub", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "The AI reference app includes complaints and lost & found. Your backend already has those pages, and native APIs are the next migration step.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        item {
            CommunityActionCard(
                title = "Complaints",
                subtitle = "Open the current complaints flow from your backend",
                icon = Icons.Outlined.ReportProblem,
                color = CampusTheme.Danger
            ) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("$baseUrl/complaints")))
            }
        }
        item {
            CommunityActionCard(
                title = "Lost & Found",
                subtitle = "Open the current lost & found flow from your backend",
                icon = Icons.Outlined.Search,
                color = CampusTheme.Community
            ) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("$baseUrl/lost-found")))
            }
        }
        item {
            CommunityActionCard(
                title = "Server Settings",
                subtitle = "Current backend: ${viewModel.baseUrl}",
                icon = Icons.Outlined.Settings,
                color = CampusTheme.Primary
            ) {}
        }
    }
}

@Composable
private fun BookingTab(viewModel: MainViewModel, padding: PaddingValues) {
    var selectedRoomId by rememberSaveable { mutableStateOf<Long?>(null) }
    var startAt by rememberSaveable { mutableStateOf("") }
    var endAt by rememberSaveable { mutableStateOf("") }
    var purpose by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        item {
            ElevatedCard(shape = AppRadius.lg, colors = CardDefaults.elevatedCardColors(containerColor = CampusTheme.Surface)) {
                Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    Text("Room Booking", style = MaterialTheme.typography.titleLarge)
                    Text("Request study rooms and track their approval status.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        item {
            BookingFormSection(
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
                    }
                }
            )
        }
        item { SectionHeader("Available Rooms") }
        if (viewModel.rooms.isEmpty()) {
            item { EmptyStateCard("No rooms are available right now.") }
        } else {
            items(viewModel.rooms) { room ->
                RoomCard(room = room, onSelect = { selectedRoomId = room.id })
            }
        }
        item { SectionHeader("My Booking Requests") }
        if (viewModel.bookings.isEmpty()) {
            item { EmptyStateCard("You have not submitted any booking requests yet.") }
        } else {
            items(viewModel.bookings) { booking ->
                BookingCard(booking = booking, onCancel = { viewModel.cancelBooking(booking.id) })
            }
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier = Modifier, label: String, value: String, color: Color) {
    Card(
        modifier = modifier,
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
private fun QuickAccessCard(link: QuickLink, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(78.dp)
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = AppRadius.md,
        colors = CardDefaults.cardColors(containerColor = CampusTheme.Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppSpacing.sm),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(AppRadius.md)
                    .background(link.color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(link.icon, contentDescription = null, tint = link.color)
            }
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            Text(link.label, style = MaterialTheme.typography.labelMedium, color = CampusTheme.TextSecondary)
        }
    }
}

@Composable
private fun FeaturedSemesterCard(semester: SemesterItem, onOpen: (Long) -> Unit) {
    ElevatedCard(shape = AppRadius.lg, colors = CardDefaults.elevatedCardColors(containerColor = CampusTheme.Surface)) {
        Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            Text(semester.name, style = MaterialTheme.typography.titleMedium)
            Text("${semester.subjectCount} subjects", style = MaterialTheme.typography.bodySmall)
            semester.subjects.take(3).forEach { subject ->
                SubjectLibraryCard(subject = subject, onOpen = { onOpen(subject.id) })
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
private fun SubjectDetailScaffold(
    detail: SubjectDetail,
    padding: PaddingValues,
    onBack: () -> Unit,
    onDownload: (ResourceItem) -> Unit
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        item {
            ElevatedCard(shape = AppRadius.lg, colors = CardDefaults.elevatedCardColors(containerColor = CampusTheme.Surface)) {
                Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    Text(detail.name, style = MaterialTheme.typography.headlineSmall)
                    Text(detail.code ?: "Semester ${detail.semesterNumber}", style = MaterialTheme.typography.bodyMedium)
                    if (!detail.description.isNullOrBlank()) {
                        Text(detail.description, style = MaterialTheme.typography.bodyMedium)
                    }
                    TextButton(onClick = onBack) { Text("Back to library") }
                }
            }
        }
        item { ResourceSection("Notes", detail.notes, onDownload) { } }
        item { ResourceSection("Question Papers", detail.papers, onDownload) { } }
        item {
            ResourceSection("Videos", detail.videos, onDownload) { resource ->
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(resource.url)))
            }
        }
    }
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
private fun BookingFormSection(
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
    val scrollState = rememberScrollState()
    Card(shape = AppRadius.lg, colors = CardDefaults.cardColors(containerColor = CampusTheme.Surface)) {
        Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            Text("Request Room Booking", style = MaterialTheme.typography.titleLarge)
            Row(
                modifier = Modifier.horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                rooms.forEach { room ->
                    FilterChip(
                        selected = selectedRoomId == room.id,
                        onClick = { onRoomSelected(room.id) },
                        label = { Text(room.roomNumber ?: room.name) }
                    )
                }
            }
            OutlinedTextField(
                value = startAt,
                onValueChange = onStartAtChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Start (2026-04-01T10:00:00)") },
                singleLine = true
            )
            OutlinedTextField(
                value = endAt,
                onValueChange = onEndAtChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("End (2026-04-01T12:00:00)") },
                singleLine = true
            )
            OutlinedTextField(
                value = purpose,
                onValueChange = onPurposeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Purpose") }
            )
            Button(onClick = onSubmit, enabled = selectedRoomId != null, modifier = Modifier.fillMaxWidth(), shape = AppRadius.md) {
                Text("Submit Booking Request")
            }
        }
    }
}

@Composable
private fun RoomCard(room: RoomItem, onSelect: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = AppRadius.lg,
        colors = CardDefaults.cardColors(containerColor = CampusTheme.Surface)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(room.roomNumber ?: room.name, style = MaterialTheme.typography.titleMedium)
                AssistChip(onClick = onSelect, label = { Text("Capacity ${room.capacity}") })
            }
            Text("${room.building}${room.floor?.let { " · Floor $it" } ?: ""}", style = MaterialTheme.typography.bodySmall)
            room.resources?.takeIf { it.isNotBlank() }?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun BookingCard(booking: BookingItem, onCancel: () -> Unit) {
    Card(shape = AppRadius.lg, colors = CardDefaults.cardColors(containerColor = CampusTheme.Surface)) {
        Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            Text(booking.roomName, style = MaterialTheme.typography.titleMedium)
            Text("${booking.startAt} to ${booking.endAt}", style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm), verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(booking.status)
                if (!booking.purpose.isNullOrBlank()) {
                    Text(booking.purpose, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            if (booking.status == "PENDING" || booking.status == "APPROVED") {
                OutlinedButton(onClick = onCancel, shape = AppRadius.md) {
                    Text("Cancel Booking")
                }
            }
        }
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
