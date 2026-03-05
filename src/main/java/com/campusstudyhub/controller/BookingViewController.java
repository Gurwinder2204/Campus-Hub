package com.campusstudyhub.controller;

import com.campusstudyhub.dto.BookingRequest;
import com.campusstudyhub.dto.BookingResponse;
import com.campusstudyhub.service.BookingService;
import com.campusstudyhub.repository.RoomRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for rendering booking-related Thymeleaf views.
 */
@Controller
@RequestMapping("/bookings")
public class BookingViewController {

    private final BookingService bookingService;
    private final RoomRepository roomRepository;

    public BookingViewController(BookingService bookingService, RoomRepository roomRepository) {
        this.bookingService = bookingService;
        this.roomRepository = roomRepository;
    }

    /**
     * Show form to request a new booking.
     */
    @GetMapping("/new")
    public String showBookingForm(Model model) {
        model.addAttribute("bookingRequest", new BookingRequest());
        model.addAttribute("rooms", roomRepository.findAll());
        return "bookings/new";
    }

    /**
     * Submit a new booking request.
     */
    @PostMapping("/new")
    public String submitBooking(@Valid @ModelAttribute("bookingRequest") BookingRequest request,
            BindingResult result,
            Authentication auth,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("rooms", roomRepository.findAll());
            return "bookings/new";
        }

        try {
            bookingService.requestBooking(request, auth.getName());
            redirectAttributes.addFlashAttribute("success",
                    "Booking request submitted successfully! It is now pending admin approval.");
            return "redirect:/bookings/mine";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("rooms", roomRepository.findAll());
            return "bookings/new";
        }
    }

    /**
     * Show user's own bookings.
     */
    @GetMapping("/mine")
    public String showMyBookings(Authentication auth, Model model) {
        List<BookingResponse> bookings = bookingService.listByUserEmail(auth.getName());
        model.addAttribute("bookings", bookings);
        return "bookings/mine";
    }

    /**
     * Cancel a booking.
     */
    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable Long id,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(id, auth.getName());
            redirectAttributes.addFlashAttribute("success", "Booking cancelled successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to cancel booking: " + e.getMessage());
        }
        return "redirect:/bookings/mine";
    }
}
