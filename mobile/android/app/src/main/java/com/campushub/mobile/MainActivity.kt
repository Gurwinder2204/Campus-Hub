package com.campushub.mobile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.campushub.mobile.data.BookingItem
import com.campushub.mobile.data.ResourceItem
import com.campushub.mobile.data.RoomItem
import com.campushub.mobile.data.SemesterItem
import com.campushub.mobile.data.StudyTaskItem
import com.campushub.mobile.data.SubjectDetail
import com.campushub.mobile.data.SubjectSummary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = campusHubColorScheme()) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CampusHubApp()
                }
            }
        }
    }
}

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
                    CircularProgressIndicator()
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
                    listOf(Color(0xFF102542), Color(0xFF1F3B4D), Color(0xFFE6D5B8))
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Campus Hub", style = MaterialTheme.typography.headlineLarge, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Use your Campus Study Hub backend directly from Android.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFE8F0F2)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F1E5))
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = baseUrl,
                    onValueChange = { baseUrl = it },
                    label = { Text("Backend URL") },
                    supportingText = { Text("For emulator use http://10.0.2.2:8080") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                if (isRegisterMode) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                if (isRegisterMode) {
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm password") },
                        modifier = Modifier.fillMaxWidth(),
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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isRegisterMode) "Create account" else "Sign in")
                }
                TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
                    Text(if (isRegisterMode) "Already have an account? Sign in" else "Need an account? Register")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppShell(viewModel: MainViewModel) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val titles = listOf("Home", "Library", "Tasks", "Bookings")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titles[selectedTab]) },
                actions = {
                    IconButton(onClick = { viewModel.refreshAll() }) {
                        Icon(Icons.Outlined.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Outlined.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                titles.forEachIndexed { index, title ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
                }
            }

            when (selectedTab) {
                0 -> HomeTab(viewModel)
                1 -> LibraryTab(viewModel)
                2 -> TasksTab(viewModel)
                else -> BookingsTab(viewModel)
            }
        }
    }
}

@Composable
private fun HomeTab(viewModel: MainViewModel) {
    val dashboard = viewModel.dashboard
    if (dashboard == null) {
        EmptyState("Loading dashboard...")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF12343B))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Welcome, ${dashboard.user.fullName}", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Your study materials, planner, and room bookings all live here.", color = Color(0xFFE0F2F1))
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatPill("Tasks", dashboard.pendingTasks.toString())
                        StatPill("Semesters", dashboard.totalSemesters.toString())
                        StatPill("Subjects", dashboard.totalSubjects.toString())
                    }
                }
            }
        }
        items(dashboard.featuredSemesters) { semester ->
            SemesterCard(semester = semester, onOpen = { })
        }
    }
}

@Composable
private fun LibraryTab(viewModel: MainViewModel) {
    var query by rememberSaveable { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.search(it)
            },
            label = { Text("Search subjects") },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        viewModel.subjectDetail?.let { detail ->
            SubjectDetailCard(
                detail = detail,
                onBack = { viewModel.clearSubject() },
                onDownload = viewModel::downloadResource
            )
            return
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (query.isBlank()) {
                items(viewModel.semesters) { semester ->
                    SemesterCard(semester = semester, onOpen = { subjectId -> viewModel.openSubject(subjectId) })
                }
            } else {
                items(viewModel.searchResults) { subject ->
                    SubjectSummaryCard(subject = subject, onOpen = { viewModel.openSubject(subject.id) })
                }
            }
        }
    }
}

@Composable
private fun TasksTab(viewModel: MainViewModel) {
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var dueDate by rememberSaveable { mutableStateOf("") }
    var priority by rememberSaveable { mutableStateOf("MEDIUM") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF2EFE7))) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Add Study Task", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due date (2026-03-15T18:00:00)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("LOW", "MEDIUM", "HIGH").forEach { item ->
                        FilterChip(selected = priority == item, onClick = { priority = item }, label = { Text(item) })
                    }
                }
                Button(
                    onClick = {
                        viewModel.createTask(title, description.ifBlank { null }, dueDate.ifBlank { null }, priority)
                        title = ""
                        description = ""
                        dueDate = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save task")
                }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(viewModel.tasks) { task ->
                TaskCard(task = task, onAdvance = { viewModel.cycleTaskStatus(task) }, onDelete = { viewModel.deleteTask(task.id) })
            }
        }
    }
}

@Composable
private fun BookingsTab(viewModel: MainViewModel) {
    var selectedRoomId by rememberSaveable { mutableStateOf<Long?>(null) }
    var startAt by rememberSaveable { mutableStateOf("") }
    var endAt by rememberSaveable { mutableStateOf("") }
    var purpose by rememberSaveable { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        BookingForm(
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

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(viewModel.bookings) { booking ->
                BookingCard(booking = booking, onCancel = { viewModel.cancelBooking(booking.id) })
            }
        }
    }
}

@Composable
private fun SemesterCard(semester: SemesterItem, onOpen: (Long) -> Unit) {
    Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F0E6))) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(semester.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            Text("${semester.subjectCount} subjects")
            semester.subjects.forEach { subject ->
                SubjectSummaryCard(subject = subject, onOpen = { onOpen(subject.id) })
            }
        }
    }
}

@Composable
private fun SubjectSummaryCard(subject: SubjectSummary, onOpen: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onOpen),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(subject.name, fontWeight = FontWeight.SemiBold)
            Text(subject.code ?: "Semester ${subject.semesterNumber}", style = MaterialTheme.typography.bodySmall)
            if (!subject.description.isNullOrBlank()) {
                Text(subject.description, style = MaterialTheme.typography.bodySmall)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = onOpen, label = { Text("${subject.notesCount} notes") })
                AssistChip(onClick = onOpen, label = { Text("${subject.papersCount} papers") })
                AssistChip(onClick = onOpen, label = { Text("${subject.videosCount} videos") })
            }
        }
    }
}

@Composable
private fun SubjectDetailCard(detail: SubjectDetail, onBack: () -> Unit, onDownload: (ResourceItem) -> Unit) {
    val context = LocalContext.current

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFDDE5B6))) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(detail.name, style = MaterialTheme.typography.headlineSmall)
                    Text(detail.code ?: "Semester ${detail.semesterNumber}")
                    if (!detail.description.isNullOrBlank()) {
                        Text(detail.description)
                    }
                    TextButton(onClick = onBack) { Text("Back to subjects") }
                }
            }
        }
        item { ResourceSection("Notes", detail.notes, onDownload, onOpenVideo = { }) }
        item { ResourceSection("Question Papers", detail.papers, onDownload, onOpenVideo = { }) }
        item {
            ResourceSection(
                "Videos",
                detail.videos,
                onDownload = onDownload,
                onOpenVideo = { resource ->
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(resource.url)))
                }
            )
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
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        if (resources.isEmpty()) {
            Text("No items yet.")
        } else {
            resources.forEach { resource ->
                Card(shape = RoundedCornerShape(18.dp)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(resource.title, fontWeight = FontWeight.SemiBold)
                        if (!resource.subtitle.isNullOrBlank()) {
                            Text(resource.subtitle, style = MaterialTheme.typography.bodySmall)
                        }
                        if (!resource.description.isNullOrBlank()) {
                            Text(resource.description, style = MaterialTheme.typography.bodySmall)
                        }
                        if (resource.type == "VIDEO") {
                            Button(onClick = { onOpenVideo(resource) }) { Text("Open video") }
                        } else {
                            Button(onClick = { onDownload(resource) }) { Text("Download") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskCard(task: StudyTaskItem, onAdvance: () -> Unit, onDelete: () -> Unit) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F2))) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(task.title, fontWeight = FontWeight.Bold)
            if (!task.description.isNullOrBlank()) {
                Text(task.description)
            }
            Text("Priority: ${task.priority} | Status: ${task.status}")
            if (!task.dueDate.isNullOrBlank()) {
                Text("Due: ${task.dueDate}")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onAdvance) { Text("Next status") }
                OutlinedButton(onClick = onDelete) { Text("Delete") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingForm(
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
    var expanded by remember { mutableStateOf(false) }
    val selectedRoom = rooms.firstOrNull { it.id == selectedRoomId }

    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F3D6))) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Request a Room Booking", style = MaterialTheme.typography.titleLarge)
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = selectedRoom?.let { "${it.name} (${it.building})" } ?: "",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Room") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    rooms.forEach { room ->
                        DropdownMenuItem(
                            text = { Text("${room.name} • ${room.building}") },
                            onClick = {
                                onRoomSelected(room.id)
                                expanded = false
                            }
                        )
                    }
                }
            }
            OutlinedTextField(value = startAt, onValueChange = onStartAtChange, label = { Text("Start (2026-03-20T10:00:00)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = endAt, onValueChange = onEndAtChange, label = { Text("End (2026-03-20T12:00:00)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = purpose, onValueChange = onPurposeChange, label = { Text("Purpose") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = onSubmit, modifier = Modifier.fillMaxWidth(), enabled = selectedRoomId != null) {
                Text("Submit booking")
            }
        }
    }
}

@Composable
private fun BookingCard(booking: BookingItem, onCancel: () -> Unit) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(booking.roomName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("${booking.startAt} to ${booking.endAt}")
            Text("Status: ${booking.status}")
            if (!booking.purpose.isNullOrBlank()) {
                Text("Purpose: ${booking.purpose}")
            }
            if (booking.status == "PENDING" || booking.status == "APPROVED") {
                OutlinedButton(onClick = onCancel) { Text("Cancel booking") }
            }
        }
    }
}

@Composable
private fun StatPill(label: String, value: String) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1E5F74))) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = Color.White, fontWeight = FontWeight.Bold)
            Text(label, color = Color(0xFFDAF1DE), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message)
    }
}
