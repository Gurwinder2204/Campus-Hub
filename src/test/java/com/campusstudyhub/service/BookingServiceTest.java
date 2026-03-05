package com.campusstudyhub.service;

import com.campusstudyhub.dto.BookingRequest;
import com.campusstudyhub.dto.BookingResponse;
import com.campusstudyhub.entity.Booking;
import com.campusstudyhub.entity.Room;
import com.campusstudyhub.entity.User;
import com.campusstudyhub.repository.BookingRepository;
import com.campusstudyhub.repository.RoomRepository;
import com.campusstudyhub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookingService validating overlap detection,
 * status transitions, and cancel authorization.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BookingService bookingService;

    private User testUser;
    private User adminUser;
    private Room testRoom;

    @BeforeEach
    void setUp() {
        testUser = new User("Test Student", "student@campus.com", "password", "ROLE_STUDENT");
        testUser.setId(1L);

        adminUser = new User("Admin", "admin@campus.com", "password", "ROLE_ADMIN");
        adminUser.setId(2L);

        testRoom = new Room("Room 101", 30, "Main Block", "1", "101");
        testRoom.setId(1L);
    }

    @Test
    void requestBooking_shouldSucceed_whenNoOverlap() {
        BookingRequest request = new BookingRequest(
                1L,
                LocalDateTime.of(2026, 3, 1, 10, 0),
                LocalDateTime.of(2026, 3, 1, 12, 0),
                "Study session");

        when(userRepository.findByEmail("student@campus.com")).thenReturn(Optional.of(testUser));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(bookingRepository.findOverlappingApprovedBookings(eq(1L), any(), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setId(100L);
            b.setCreatedAt(LocalDateTime.now());
            return b;
        });

        BookingResponse response = bookingService.requestBooking(request, "student@campus.com");

        assertNotNull(response);
        assertEquals("PENDING", response.getStatus());
        assertEquals("Room 101", response.getRoomName());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void requestBooking_shouldFail_whenOverlapExists() {
        BookingRequest request = new BookingRequest(
                1L,
                LocalDateTime.of(2026, 3, 1, 10, 0),
                LocalDateTime.of(2026, 3, 1, 12, 0),
                "Study session");

        Booking existingBooking = new Booking();
        existingBooking.setId(50L);

        when(userRepository.findByEmail("student@campus.com")).thenReturn(Optional.of(testUser));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(bookingRepository.findOverlappingApprovedBookings(eq(1L), any(), any()))
                .thenReturn(List.of(existingBooking));

        assertThrows(IllegalStateException.class,
                () -> bookingService.requestBooking(request, "student@campus.com"));
    }

    @Test
    void requestBooking_shouldFail_whenEndBeforeStart() {
        BookingRequest request = new BookingRequest(
                1L,
                LocalDateTime.of(2026, 3, 1, 14, 0),
                LocalDateTime.of(2026, 3, 1, 10, 0), // before start
                "Study session");

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.requestBooking(request, "student@campus.com"));
    }

    @Test
    void requestBooking_shouldFail_whenDurationExceeds4Hours() {
        BookingRequest request = new BookingRequest(
                1L,
                LocalDateTime.of(2026, 3, 1, 8, 0),
                LocalDateTime.of(2026, 3, 1, 15, 0), // 7 hours
                "Study session");

        when(userRepository.findByEmail("student@campus.com")).thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.requestBooking(request, "student@campus.com"));
    }

    @Test
    void approveBooking_shouldSucceed_whenPending() {
        Booking pendingBooking = new Booking();
        pendingBooking.setId(1L);
        pendingBooking.setUserId(1L);
        pendingBooking.setRoom(testRoom);
        pendingBooking.setStartAt(LocalDateTime.of(2026, 3, 1, 10, 0));
        pendingBooking.setEndAt(LocalDateTime.of(2026, 3, 1, 12, 0));
        pendingBooking.setStatus("PENDING");
        pendingBooking.setCreatedAt(LocalDateTime.now());

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(pendingBooking));
        when(bookingRepository.findOverlappingApprovedBookings(eq(1L), any(), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        BookingResponse response = bookingService.approveBooking(1L);

        assertEquals("APPROVED", response.getStatus());
        verify(notificationService).sendToUser(eq(1L), eq("Booking Approved"), anyString());
    }

    @Test
    void approveBooking_shouldFail_whenAlreadyApproved() {
        Booking approved = new Booking();
        approved.setId(1L);
        approved.setStatus("APPROVED");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(approved));

        assertThrows(IllegalStateException.class,
                () -> bookingService.approveBooking(1L));
    }

    @Test
    void rejectBooking_shouldSucceed_whenPending() {
        Booking pendingBooking = new Booking();
        pendingBooking.setId(1L);
        pendingBooking.setUserId(1L);
        pendingBooking.setRoom(testRoom);
        pendingBooking.setStatus("PENDING");
        pendingBooking.setCreatedAt(LocalDateTime.now());

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(pendingBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        BookingResponse response = bookingService.rejectBooking(1L, "Room needed");

        assertEquals("REJECTED", response.getStatus());
        verify(notificationService).sendToUser(eq(1L), eq("Booking Rejected"), contains("Room needed"));
    }

    @Test
    void cancelBooking_shouldSucceed_whenCreator() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUserId(1L); // owned by testUser (id=1)
        booking.setRoom(testRoom);
        booking.setStatus("PENDING");
        booking.setCreatedAt(LocalDateTime.now());

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findByEmail("student@campus.com")).thenReturn(Optional.of(testUser));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        BookingResponse response = bookingService.cancelBooking(1L, "student@campus.com");

        assertEquals("CANCELLED", response.getStatus());
    }

    @Test
    void cancelBooking_shouldFail_whenNotCreatorOrAdmin() {
        User otherUser = new User("Other", "other@campus.com", "pass", "ROLE_STUDENT");
        otherUser.setId(99L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUserId(1L); // owned by testUser, NOT otherUser
        booking.setRoom(testRoom);
        booking.setStatus("PENDING");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findByEmail("other@campus.com")).thenReturn(Optional.of(otherUser));

        assertThrows(IllegalStateException.class,
                () -> bookingService.cancelBooking(1L, "other@campus.com"));
    }
}
