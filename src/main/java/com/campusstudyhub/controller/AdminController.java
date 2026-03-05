package com.campusstudyhub.controller;

import com.campusstudyhub.dto.ResourceUploadDto;
import com.campusstudyhub.dto.SubjectDto;
import com.campusstudyhub.dto.VideoLinkDto;
import com.campusstudyhub.service.ResourceService;
import com.campusstudyhub.service.SubjectService;
import com.campusstudyhub.service.BookingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

/**
 * Controller for admin operations.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final SubjectService subjectService;
    private final ResourceService resourceService;
    private final BookingService bookingService;

    public AdminController(SubjectService subjectService, ResourceService resourceService,
            BookingService bookingService) {
        this.subjectService = subjectService;
        this.resourceService = resourceService;
        this.bookingService = bookingService;
    }

    /**
     * Admin dashboard.
     */
    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("subjects", subjectService.listAllSubjects());
        model.addAttribute("semesters", subjectService.getAllSemesters());
        return "admin/dashboard";
    }

    // ============== SUBJECT MANAGEMENT ==============

    /**
     * List all subjects for management.
     */
    @GetMapping("/subjects")
    public String listSubjects(Model model) {
        model.addAttribute("subjects", subjectService.listAllSubjects());
        model.addAttribute("semesters", subjectService.getAllSemesters());
        model.addAttribute("subjectDto", new SubjectDto());
        return "admin/subjects";
    }

    /**
     * Create a new subject.
     */
    @PostMapping("/subjects")
    public String createSubject(@Valid @ModelAttribute("subjectDto") SubjectDto subjectDto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("subjects", subjectService.listAllSubjects());
            model.addAttribute("semesters", subjectService.getAllSemesters());
            return "admin/subjects";
        }

        try {
            subjectService.createSubject(subjectDto);
            redirectAttributes.addFlashAttribute("success", "Subject created successfully!");
        } catch (Exception e) {
            log.error("Error creating subject", e);
            redirectAttributes.addFlashAttribute("error", "Failed to create subject: " + e.getMessage());
        }

        return "redirect:/admin/subjects";
    }

    /**
     * Show edit form for a subject.
     */
    @GetMapping("/subjects/{id}/edit")
    public String editSubjectForm(@PathVariable Long id, Model model) {
        SubjectDto subject = subjectService.getSubject(id);
        model.addAttribute("subjectDto", subject);
        model.addAttribute("semesters", subjectService.getAllSemesters());
        return "admin/edit-subject";
    }

    /**
     * Update a subject.
     */
    @PostMapping("/subjects/{id}")
    public String updateSubject(@PathVariable Long id,
            @Valid @ModelAttribute("subjectDto") SubjectDto subjectDto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("semesters", subjectService.getAllSemesters());
            return "admin/edit-subject";
        }

        try {
            subjectService.updateSubject(id, subjectDto);
            redirectAttributes.addFlashAttribute("success", "Subject updated successfully!");
        } catch (Exception e) {
            log.error("Error updating subject", e);
            redirectAttributes.addFlashAttribute("error", "Failed to update subject: " + e.getMessage());
        }

        return "redirect:/admin/subjects";
    }

    /**
     * Delete a subject.
     */
    @PostMapping("/subjects/{id}/delete")
    public String deleteSubject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            subjectService.deleteSubject(id);
            redirectAttributes.addFlashAttribute("success", "Subject deleted successfully!");
        } catch (Exception e) {
            log.error("Error deleting subject", e);
            redirectAttributes.addFlashAttribute("error", "Failed to delete subject: " + e.getMessage());
        }

        return "redirect:/admin/subjects";
    }

    // ============== NOTE UPLOAD ==============

    /**
     * Show note upload form.
     */
    @GetMapping("/upload/note")
    public String uploadNoteForm(Model model) {
        model.addAttribute("uploadDto", new ResourceUploadDto());
        model.addAttribute("subjects", subjectService.listAllSubjects());
        return "admin/upload-note";
    }

    /**
     * Upload a note.
     */
    @PostMapping("/notes")
    public String uploadNote(@ModelAttribute ResourceUploadDto uploadDto,
            @RequestParam("file") MultipartFile file,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            resourceService.uploadNote(uploadDto, file, auth.getName());
            redirectAttributes.addFlashAttribute("success", "Note uploaded successfully!");
        } catch (IllegalArgumentException e) {
            log.warn("Invalid file upload: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/upload/note";
        } catch (IOException e) {
            log.error("Error uploading note", e);
            redirectAttributes.addFlashAttribute("error", "Failed to upload file: " + e.getMessage());
            return "redirect:/admin/upload/note";
        }

        return "redirect:/subjects/" + uploadDto.getSubjectId();
    }

    /**
     * Delete a note.
     */
    @PostMapping("/notes/{id}/delete")
    public String deleteNote(@PathVariable Long id,
            @RequestParam(required = false) Long subjectId,
            RedirectAttributes redirectAttributes) {
        try {
            resourceService.deleteNote(id);
            redirectAttributes.addFlashAttribute("success", "Note deleted successfully!");
        } catch (Exception e) {
            log.error("Error deleting note", e);
            redirectAttributes.addFlashAttribute("error", "Failed to delete note: " + e.getMessage());
        }

        if (subjectId != null) {
            return "redirect:/subjects/" + subjectId;
        }
        return "redirect:/admin";
    }

    // ============== PAPER UPLOAD ==============

    /**
     * Show paper upload form.
     */
    @GetMapping("/upload/paper")
    public String uploadPaperForm(Model model) {
        model.addAttribute("uploadDto", new ResourceUploadDto());
        model.addAttribute("subjects", subjectService.listAllSubjects());
        return "admin/upload-paper";
    }

    /**
     * Upload a question paper.
     */
    @PostMapping("/papers")
    public String uploadPaper(@ModelAttribute ResourceUploadDto uploadDto,
            @RequestParam("file") MultipartFile file,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            resourceService.uploadPaper(uploadDto, file, auth.getName());
            redirectAttributes.addFlashAttribute("success", "Question paper uploaded successfully!");
        } catch (IllegalArgumentException e) {
            log.warn("Invalid file upload: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/upload/paper";
        } catch (IOException e) {
            log.error("Error uploading paper", e);
            redirectAttributes.addFlashAttribute("error", "Failed to upload file: " + e.getMessage());
            return "redirect:/admin/upload/paper";
        }

        return "redirect:/subjects/" + uploadDto.getSubjectId();
    }

    /**
     * Delete a paper.
     */
    @PostMapping("/papers/{id}/delete")
    public String deletePaper(@PathVariable Long id,
            @RequestParam(required = false) Long subjectId,
            RedirectAttributes redirectAttributes) {
        try {
            resourceService.deletePaper(id);
            redirectAttributes.addFlashAttribute("success", "Question paper deleted successfully!");
        } catch (Exception e) {
            log.error("Error deleting paper", e);
            redirectAttributes.addFlashAttribute("error", "Failed to delete paper: " + e.getMessage());
        }

        if (subjectId != null) {
            return "redirect:/subjects/" + subjectId;
        }
        return "redirect:/admin";
    }

    // ============== VIDEO LINK MANAGEMENT ==============

    /**
     * Show video link form.
     */
    @GetMapping("/add/video")
    public String addVideoForm(Model model) {
        model.addAttribute("videoDto", new VideoLinkDto());
        model.addAttribute("subjects", subjectService.listAllSubjects());
        return "admin/add-video";
    }

    /**
     * Add a video link.
     */
    @PostMapping("/videos")
    public String addVideo(@Valid @ModelAttribute("videoDto") VideoLinkDto videoDto,
            BindingResult result,
            Authentication auth,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("subjects", subjectService.listAllSubjects());
            return "admin/add-video";
        }

        try {
            resourceService.addVideoLink(videoDto, auth.getName());
            redirectAttributes.addFlashAttribute("success", "Video link added successfully!");
        } catch (Exception e) {
            log.error("Error adding video", e);
            redirectAttributes.addFlashAttribute("error", "Failed to add video: " + e.getMessage());
            return "redirect:/admin/add/video";
        }

        return "redirect:/subjects/" + videoDto.getSubjectId();
    }

    /**
     * Delete a video.
     */
    @PostMapping("/videos/{id}/delete")
    public String deleteVideo(@PathVariable Long id,
            @RequestParam(required = false) Long subjectId,
            RedirectAttributes redirectAttributes) {
        try {
            resourceService.deleteVideo(id);
            redirectAttributes.addFlashAttribute("success", "Video deleted successfully!");
        } catch (Exception e) {
            log.error("Error deleting video", e);
            redirectAttributes.addFlashAttribute("error", "Failed to delete video: " + e.getMessage());
        }

        if (subjectId != null) {
            return "redirect:/subjects/" + subjectId;
        }
        return "redirect:/admin";
    }
    // ============== BOOKING MANAGEMENT ==============

    /**
     * List pending bookings for approval.
     */
    @GetMapping("/bookings")
    public String listPendingBookings(Model model) {
        model.addAttribute("bookings", bookingService.listPending());
        return "admin/bookings";
    }

    /**
     * Approve a booking.
     */
    @PostMapping("/bookings/{id}/approve")
    public String approveBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.approveBooking(id);
            redirectAttributes.addFlashAttribute("success", "Booking approved successfully!");
        } catch (Exception e) {
            log.error("Error approving booking", e);
            redirectAttributes.addFlashAttribute("error", "Failed to approve booking: " + e.getMessage());
        }
        return "redirect:/admin/bookings";
    }

    /**
     * Reject a booking.
     */
    @PostMapping("/bookings/{id}/reject")
    public String rejectBooking(@PathVariable Long id,
            @RequestParam(required = false) String reason,
            RedirectAttributes redirectAttributes) {
        try {
            bookingService.rejectBooking(id, reason);
            redirectAttributes.addFlashAttribute("success", "Booking rejected.");
        } catch (Exception e) {
            log.error("Error rejecting booking", e);
            redirectAttributes.addFlashAttribute("error", "Failed to reject booking: " + e.getMessage());
        }
        return "redirect:/admin/bookings";
    }
}
