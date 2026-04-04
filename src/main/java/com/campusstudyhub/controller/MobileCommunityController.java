package com.campusstudyhub.controller;

import com.campusstudyhub.dto.mobile.MobileCommunitySummaryDto;
import com.campusstudyhub.dto.mobile.MobileComplaintCreateRequest;
import com.campusstudyhub.dto.mobile.MobileComplaintDto;
import com.campusstudyhub.dto.mobile.MobileLostFoundCreateRequest;
import com.campusstudyhub.dto.mobile.MobileLostFoundDto;
import com.campusstudyhub.entity.Complaint;
import com.campusstudyhub.entity.LostFoundItem;
import com.campusstudyhub.repository.ComplaintRepository;
import com.campusstudyhub.repository.LostFoundRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/mobile/community")
public class MobileCommunityController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private final ComplaintRepository complaintRepository;
    private final LostFoundRepository lostFoundRepository;

    public MobileCommunityController(ComplaintRepository complaintRepository, LostFoundRepository lostFoundRepository) {
        this.complaintRepository = complaintRepository;
        this.lostFoundRepository = lostFoundRepository;
    }

    @GetMapping("/summary")
    public MobileCommunitySummaryDto summary(Authentication authentication) {
        String email = authentication.getName();
        List<Complaint> complaints = complaintRepository.findAllByOrderByCreatedAtDesc();
        List<LostFoundItem> items = lostFoundRepository.findAllByOrderByCreatedAtDesc();

        MobileCommunitySummaryDto dto = new MobileCommunitySummaryDto();
        dto.setActiveLostItems(items.stream().filter(item -> isOpen(item.getStatus()) && "LOST".equalsIgnoreCase(item.getType())).count());
        dto.setActiveFoundItems(items.stream().filter(item -> isOpen(item.getStatus()) && "FOUND".equalsIgnoreCase(item.getType())).count());
        dto.setOpenComplaints(complaints.stream().filter(complaint -> isComplaintOpen(complaint.getStatus())).count());
        dto.setMyOpenComplaints(complaints.stream()
                .filter(complaint -> isComplaintOpen(complaint.getStatus()) && email.equalsIgnoreCase(complaint.getSubmittedBy()))
                .count());
        return dto;
    }

    @GetMapping("/complaints")
    public List<MobileComplaintDto> complaints(
            Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "false") boolean mine) {
        String email = authentication.getName();
        return complaintRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(complaint -> !mine || email.equalsIgnoreCase(complaint.getSubmittedBy()))
                .filter(complaint -> status == null || status.isBlank() || status.equalsIgnoreCase(complaint.getStatus()))
                .map(complaint -> toComplaintDto(complaint, email))
                .toList();
    }

    @PostMapping("/complaints")
    public ResponseEntity<MobileComplaintDto> createComplaint(
            Authentication authentication,
            @Valid @RequestBody MobileComplaintCreateRequest request) {
        Complaint complaint = new Complaint();
        complaint.setTitle(request.getTitle().trim());
        complaint.setDescription(request.getDescription().trim());
        complaint.setCategory(request.getCategory().trim().toUpperCase(Locale.ROOT));
        complaint.setStatus("OPEN");
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setSubmittedBy(authentication.getName());
        Complaint saved = complaintRepository.save(complaint);
        return ResponseEntity.status(HttpStatus.CREATED).body(toComplaintDto(saved, authentication.getName()));
    }

    @PutMapping("/complaints/{id}/resolve")
    public MobileComplaintDto resolveComplaint(@PathVariable Long id, Authentication authentication) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Complaint not found"));
        assertOwnerOrAdmin(authentication, complaint.getSubmittedBy());
        complaint.setStatus("RESOLVED");
        complaint.setResolvedAt(LocalDateTime.now());
        return toComplaintDto(complaintRepository.save(complaint), authentication.getName());
    }

    @GetMapping("/lost-found")
    public List<MobileLostFoundDto> lostFound(
            Authentication authentication,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "false") boolean mine) {
        String email = authentication.getName();
        return lostFoundRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(item -> !mine || email.equalsIgnoreCase(item.getPostedBy()))
                .filter(item -> type == null || type.isBlank() || type.equalsIgnoreCase(item.getType()))
                .map(item -> toLostFoundDto(item, email))
                .toList();
    }

    @PostMapping("/lost-found")
    public ResponseEntity<MobileLostFoundDto> createLostFound(
            Authentication authentication,
            @Valid @RequestBody MobileLostFoundCreateRequest request) {
        LostFoundItem item = new LostFoundItem();
        item.setTitle(request.getTitle().trim());
        item.setDescription(trimToNull(request.getDescription()));
        item.setType(request.getType().trim().toUpperCase(Locale.ROOT));
        item.setLocation(trimToNull(request.getLocation()));
        item.setContactInfo(trimToNull(request.getContactInfo()));
        item.setImageUrl(trimToNull(request.getImageUrl()));
        item.setStatus("OPEN");
        item.setCreatedAt(LocalDateTime.now());
        item.setPostedBy(authentication.getName());
        LostFoundItem saved = lostFoundRepository.save(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(toLostFoundDto(saved, authentication.getName()));
    }

    @PutMapping("/lost-found/{id}/resolve")
    public MobileLostFoundDto resolveLostFound(@PathVariable Long id, Authentication authentication) {
        LostFoundItem item = lostFoundRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Lost & found item not found"));
        assertOwnerOrAdmin(authentication, item.getPostedBy());
        item.setStatus("RESOLVED");
        return toLostFoundDto(lostFoundRepository.save(item), authentication.getName());
    }

    private MobileComplaintDto toComplaintDto(Complaint complaint, String currentUserEmail) {
        MobileComplaintDto dto = new MobileComplaintDto();
        dto.setId(complaint.getId());
        dto.setTitle(complaint.getTitle());
        dto.setDescription(complaint.getDescription());
        dto.setCategory(complaint.getCategory());
        dto.setStatus(complaint.getStatus());
        dto.setAdminResponse(complaint.getAdminResponse());
        dto.setSubmittedBy(complaint.getSubmittedBy());
        dto.setCreatedAt(formatDateTime(complaint.getCreatedAt()));
        dto.setResolvedAt(formatDateTime(complaint.getResolvedAt()));
        dto.setMine(currentUserEmail.equalsIgnoreCase(complaint.getSubmittedBy()));
        return dto;
    }

    private MobileLostFoundDto toLostFoundDto(LostFoundItem item, String currentUserEmail) {
        MobileLostFoundDto dto = new MobileLostFoundDto();
        dto.setId(item.getId());
        dto.setTitle(item.getTitle());
        dto.setDescription(item.getDescription());
        dto.setType(item.getType());
        dto.setLocation(item.getLocation());
        dto.setContactInfo(item.getContactInfo());
        dto.setImageUrl(item.getImageUrl());
        dto.setStatus(item.getStatus());
        dto.setPostedBy(item.getPostedBy());
        dto.setCreatedAt(formatDateTime(item.getCreatedAt()));
        dto.setMine(currentUserEmail.equalsIgnoreCase(item.getPostedBy()));
        return dto;
    }

    private void assertOwnerOrAdmin(Authentication authentication, String ownerEmail) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN") || authority.getAuthority().equals("ADMIN"));
        if (!isAdmin && !authentication.getName().equalsIgnoreCase(ownerEmail)) {
            throw new ResponseStatusException(FORBIDDEN, "You can only update your own posts");
        }
    }

    private boolean isOpen(String status) {
        return status != null && !"RESOLVED".equalsIgnoreCase(status) && !"CLOSED".equalsIgnoreCase(status);
    }

    private boolean isComplaintOpen(String status) {
        return status != null && !"RESOLVED".equalsIgnoreCase(status) && !"CLOSED".equalsIgnoreCase(status);
    }

    private String formatDateTime(LocalDateTime value) {
        return value == null ? null : DATE_TIME_FORMATTER.format(value);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
