package com.campusstudyhub.service;

import com.campusstudyhub.dto.BookingRequest;
import com.campusstudyhub.dto.BookingResponse;
import com.campusstudyhub.entity.Booking;
import com.campusstudyhub.entity.Room;
import com.campusstudyhub.entity.User;
import com.campusstudyhub.repository.BookingRepository;
import com.campusstudyhub.repository.RoomRepository;
import com.campusstudyhub.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service handling room booking operations including overlap validation
 * and admin approval workflow.
 */
@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);
    private static final long MAX_BOOKING_HOURS = 4;

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AnalyticsService analyticsService;

    public BookingService(BookingRepository bookingRepository,
            RoomRepository roomRepository,
            UserRepository userRepository,
            NotificationService notificationService,
            AnalyticsService analyticsService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.analyticsService = analyticsService;
    }

    /**
     * Create a new booking request. Validates time constraints and overlap.
     */
    @Transactional
    public BookingResponse requestBooking(BookingRequest request, String userEmail) {
        // Validate time range
        if (request.getEndAt().isBefore(request.getStartAt()) ||
                request.getEndAt().isEqual(request.getStartAt())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Validate max duration
        long hours = Duration.between(request.getStartAt(), request.getEndAt()).toHours();
        if (hours > MAX_BOOKING_HOURS) {
            throw new IllegalArgumentException(
                    "Booking duration cannot exceed " + MAX_BOOKING_HOURS + " hours");
        }

        // Look up user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        // Look up room
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + request.getRoomId()));

        // Check for overlapping approved bookings (transactional)
        List<Booking> overlapping = bookingRepository.findOverlappingApprovedBookings(
                room.getId(), request.getStartAt(), request.getEndAt());
        if (!overlapping.isEmpty()) {
            throw new IllegalStateException(
                    "Room '" + room.getName() + "' is already booked during the requested time slot");
        }

        // Create and save booking
        Booking booking = new Booking();
        booking.setUserId(user.getId());
        booking.setRoom(room);
        booking.setStartAt(request.getStartAt());
        booking.setEndAt(request.getEndAt());
        booking.setPurpose(request.getPurpose());
        booking.setStatus("PENDING");

        booking = bookingRepository.save(booking);
        log.info("Booking {} created by user {} for room {}", booking.getId(), userEmail, room.getName());

        analyticsService.trackEvent("booking_create",
                Map.of("bookingId", booking.getId(), "roomId", room.getId(), "roomName", room.getName()),
                userEmail);

        return toResponse(booking, user.getFullName());
    }

    /**
     * Approve a pending booking (admin action).
     */
    @Transactional
    public BookingResponse approveBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        if (!"PENDING".equals(booking.getStatus())) {
            throw new IllegalStateException(
                    "Only PENDING bookings can be approved. Current status: " + booking.getStatus());
        }

        // Re-check for overlap at approval time
        List<Booking> overlapping = bookingRepository.findOverlappingApprovedBookings(
                booking.getRoom().getId(), booking.getStartAt(), booking.getEndAt());
        if (!overlapping.isEmpty()) {
            throw new IllegalStateException("Cannot approve — another booking was already approved for this time slot");
        }

        booking.setStatus("APPROVED");
        booking = bookingRepository.save(booking);

        // Notify the requester
        notificationService.sendToUser(booking.getUserId(),
                "Booking Approved",
                "Your booking for " + booking.getRoom().getName() + " has been approved.");

        analyticsService.trackEvent("booking_approve",
                Map.of("bookingId", bookingId, "roomId", booking.getRoom().getId()),
                null);

        log.info("Booking {} approved", bookingId);
        return toResponse(booking, null);
    }

    /**
     * Reject a pending booking (admin action).
     */
    @Transactional
    public BookingResponse rejectBooking(Long bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        if (!"PENDING".equals(booking.getStatus())) {
            throw new IllegalStateException(
                    "Only PENDING bookings can be rejected. Current status: " + booking.getStatus());
        }

        booking.setStatus("REJECTED");
        booking = bookingRepository.save(booking);

        String body = "Your booking for " + booking.getRoom().getName() + " has been rejected.";
        if (reason != null && !reason.isBlank()) {
            body += " Reason: " + reason;
        }
        notificationService.sendToUser(booking.getUserId(), "Booking Rejected", body);

        analyticsService.trackEvent("booking_reject",
                Map.of("bookingId", bookingId, "reason", reason != null ? reason : "none"),
                null);

        log.info("Booking {} rejected. Reason: {}", bookingId, reason);
        return toResponse(booking, null);
    }

    /**
     * Cancel a booking (by creator or admin).
     */
    @Transactional
    public BookingResponse cancelBooking(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        // Only the creator or an admin can cancel
        boolean isCreator = booking.getUserId().equals(user.getId());
        boolean isAdmin = "ROLE_ADMIN".equals(user.getRole());

        if (!isCreator && !isAdmin) {
            throw new IllegalStateException("Only the booking creator or an admin can cancel this booking");
        }

        if ("CANCELLED".equals(booking.getStatus()) || "REJECTED".equals(booking.getStatus())) {
            throw new IllegalStateException("Booking is already " + booking.getStatus());
        }

        booking.setStatus("CANCELLED");
        booking = bookingRepository.save(booking);

        notificationService.sendToUser(booking.getUserId(),
                "Booking Cancelled",
                "Your booking for " + booking.getRoom().getName() + " has been cancelled.");

        analyticsService.trackEvent("booking_cancel",
                Map.of("bookingId", bookingId),
                userEmail);

        log.info("Booking {} cancelled by {}", bookingId, userEmail);
        return toResponse(booking, null);
    }

    /**
     * List all bookings (admin view).
     */
    public List<BookingResponse> listAll() {
        return bookingRepository.findAll().stream()
                .map(b -> toResponse(b, null))
                .collect(Collectors.toList());
    }

    /**
     * List bookings for a specific user.
     */
    public List<BookingResponse> listByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
        return bookingRepository.findByUserId(user.getId()).stream()
                .map(b -> toResponse(b, user.getFullName()))
                .collect(Collectors.toList());
    }

    /**
     * List pending bookings (for admin approval dashboard).
     */
    public List<BookingResponse> listPending() {
        return bookingRepository.findByStatus("PENDING").stream()
                .map(b -> toResponse(b, null))
                .collect(Collectors.toList());
    }

    /**
     * Get a single booking by ID.
     */
    public BookingResponse getBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + id));
        return toResponse(booking, null);
    }

    /**
     * Convert entity to response DTO.
     */
    private BookingResponse toResponse(Booking booking, String userName) {
        BookingResponse resp = new BookingResponse();
        resp.setId(booking.getId());
        resp.setRoomId(booking.getRoom().getId());
        resp.setRoomName(booking.getRoom().getName());
        resp.setUserId(booking.getUserId());
        resp.setStartAt(booking.getStartAt());
        resp.setEndAt(booking.getEndAt());
        resp.setStatus(booking.getStatus());
        resp.setPurpose(booking.getPurpose());
        resp.setCreatedAt(booking.getCreatedAt());

        if (userName != null) {
            resp.setUserName(userName);
        } else {
            // Look up user name
            userRepository.findById(booking.getUserId())
                    .ifPresent(u -> resp.setUserName(u.getFullName()));
        }

        return resp;
    }
}
