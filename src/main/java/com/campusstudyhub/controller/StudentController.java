package com.campusstudyhub.controller;

import com.campusstudyhub.dto.SubjectDto;
import com.campusstudyhub.entity.Semester;
import com.campusstudyhub.service.ResourceService;
import com.campusstudyhub.service.SubjectService;
import com.campusstudyhub.service.BookingService;
import com.campusstudyhub.service.StudyTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller for student-facing pages.
 */
@Controller
public class StudentController {

    private static final Logger log = LoggerFactory.getLogger(StudentController.class);

    private final SubjectService subjectService;
    private final ResourceService resourceService;
    private final BookingService bookingService;
    private final StudyTaskService taskService;

    public StudentController(SubjectService subjectService,
            ResourceService resourceService,
            BookingService bookingService,
            StudyTaskService taskService) {
        this.subjectService = subjectService;
        this.resourceService = resourceService;
        this.bookingService = bookingService;
        this.taskService = taskService;
    }

    /**
     * Display the dashboard.
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        String email = auth.getName();
        log.debug("Showing dashboard for user: {}", email);

        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("userName", email);
        model.addAttribute("semesters", subjectService.getAllSemesters());

        // Add booking summary
        model.addAttribute("myBookingsCount", bookingService.listByUserEmail(email).size());

        // Add task summary
        model.addAttribute("pendingTasksCount", taskService.countPendingTasks(email));
        model.addAttribute("activeTasks", taskService.listActiveTasks(email));

        return "dashboard";
    }

    /**
     * Display list of all semesters.
     */
    @GetMapping("/semesters")
    public String semesterList(Model model) {
        List<Semester> semesters = subjectService.getAllSemesters();
        model.addAttribute("semesters", semesters);
        return "semester-list";
    }

    /**
     * Display subjects for a semester.
     */
    @GetMapping("/semesters/{id}")
    public String semesterSubjects(@PathVariable Long id, Model model) {
        Semester semester = subjectService.getSemester(id);
        List<SubjectDto> subjects = subjectService.listBySemester(id);

        model.addAttribute("semester", semester);
        model.addAttribute("subjects", subjects);
        return "semester-subjects";
    }

    /**
     * Display subject detail with resources.
     */
    @GetMapping("/subjects/{id}")
    public String subjectDetail(@PathVariable Long id, Model model) {
        SubjectDto subject = subjectService.getSubject(id);

        model.addAttribute("subject", subject);
        model.addAttribute("notes", resourceService.listNotesBySubject(id));
        model.addAttribute("papers", resourceService.listPapersBySubject(id));
        model.addAttribute("videos", resourceService.listVideosBySubject(id));

        return "subject-detail";
    }

    /**
     * Search subjects.
     */
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String query, Model model) {
        if (query != null && !query.trim().isEmpty()) {
            List<SubjectDto> results = subjectService.searchSubjects(query.trim());
            model.addAttribute("subjects", results);
            model.addAttribute("query", query);
        }
        return "search";
    }
}
