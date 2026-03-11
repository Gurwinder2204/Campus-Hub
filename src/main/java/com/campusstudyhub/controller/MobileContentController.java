package com.campusstudyhub.controller;

import com.campusstudyhub.dto.SubjectDto;
import com.campusstudyhub.dto.mobile.MobileDashboardDto;
import com.campusstudyhub.dto.mobile.MobileRoomDto;
import com.campusstudyhub.dto.mobile.MobileSemesterDto;
import com.campusstudyhub.dto.mobile.MobileSubjectDetailDto;
import com.campusstudyhub.dto.mobile.MobileSubjectSummaryDto;
import com.campusstudyhub.dto.mobile.MobileUserDto;
import com.campusstudyhub.dto.mobile.ResourceItemDto;
import com.campusstudyhub.entity.Note;
import com.campusstudyhub.entity.QuestionPaper;
import com.campusstudyhub.entity.Room;
import com.campusstudyhub.entity.Semester;
import com.campusstudyhub.entity.Subject;
import com.campusstudyhub.entity.User;
import com.campusstudyhub.entity.VideoLink;
import com.campusstudyhub.repository.NoteRepository;
import com.campusstudyhub.repository.QuestionPaperRepository;
import com.campusstudyhub.repository.RoomRepository;
import com.campusstudyhub.repository.SubjectRepository;
import com.campusstudyhub.repository.VideoLinkRepository;
import com.campusstudyhub.service.StudyTaskService;
import com.campusstudyhub.service.SubjectService;
import com.campusstudyhub.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/mobile")
public class MobileContentController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private final SubjectService subjectService;
    private final SubjectRepository subjectRepository;
    private final NoteRepository noteRepository;
    private final QuestionPaperRepository questionPaperRepository;
    private final VideoLinkRepository videoLinkRepository;
    private final RoomRepository roomRepository;
    private final UserService userService;
    private final StudyTaskService studyTaskService;

    public MobileContentController(SubjectService subjectService, SubjectRepository subjectRepository,
            NoteRepository noteRepository, QuestionPaperRepository questionPaperRepository,
            VideoLinkRepository videoLinkRepository, RoomRepository roomRepository, UserService userService,
            StudyTaskService studyTaskService) {
        this.subjectService = subjectService;
        this.subjectRepository = subjectRepository;
        this.noteRepository = noteRepository;
        this.questionPaperRepository = questionPaperRepository;
        this.videoLinkRepository = videoLinkRepository;
        this.roomRepository = roomRepository;
        this.userService = userService;
        this.studyTaskService = studyTaskService;
    }

    @GetMapping("/dashboard")
    public MobileDashboardDto dashboard(org.springframework.security.core.Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());
        List<Semester> semesters = subjectService.getAllSemesters().stream()
                .sorted(Comparator.comparing(Semester::getNumber))
                .toList();

        MobileDashboardDto response = new MobileDashboardDto();
        response.setUser(new MobileUserDto(user.getId(), user.getFullName(), user.getEmail(), user.getRole()));
        response.setPendingTasks(studyTaskService.countPendingTasks(authentication.getName()));
        response.setTotalSemesters(semesters.size());
        response.setTotalSubjects(subjectRepository.count());
        response.setFeaturedSemesters(semesters.stream()
                .limit(3)
                .map(this::toSemesterDto)
                .toList());
        return response;
    }

    @GetMapping("/semesters")
    public List<MobileSemesterDto> semesters() {
        return subjectService.getAllSemesters().stream()
                .sorted(Comparator.comparing(Semester::getNumber))
                .map(this::toSemesterDto)
                .toList();
    }

    @GetMapping("/semesters/{id}")
    public MobileSemesterDto semester(@PathVariable Long id) {
        return toSemesterDto(subjectService.getSemester(id));
    }

    @GetMapping("/subjects/{id}")
    public MobileSubjectDetailDto subject(@PathVariable Long id) {
        Subject subject = subjectRepository.findDetailedById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Subject not found: " + id));

        MobileSubjectDetailDto dto = new MobileSubjectDetailDto();
        dto.setId(subject.getId());
        dto.setName(subject.getName());
        dto.setCode(subject.getCode());
        dto.setDescription(subject.getDescription());
        dto.setSemesterNumber(subject.getSemester().getNumber());
        dto.setNotesCount(noteRepository.countBySubjectId(subject.getId()));
        dto.setPapersCount(questionPaperRepository.countBySubjectId(subject.getId()));
        dto.setVideosCount(videoLinkRepository.countBySubjectId(subject.getId()));
        dto.setNotes(noteRepository.findBySubjectIdOrderByUploadedAtDesc(subject.getId()).stream().map(this::toNoteItem).toList());
        dto.setPapers(questionPaperRepository.findBySubjectIdOrderByYearDesc(subject.getId()).stream().map(this::toPaperItem)
                .toList());
        dto.setVideos(videoLinkRepository.findBySubjectIdOrderByAddedAtDesc(subject.getId()).stream().map(this::toVideoItem)
                .toList());
        return dto;
    }

    @GetMapping("/search")
    public List<MobileSubjectSummaryDto> search(@RequestParam("q") String query) {
        return subjectService.searchSubjects(query).stream().map(this::toSubjectSummary).toList();
    }

    @GetMapping("/rooms")
    public List<MobileRoomDto> rooms() {
        return roomRepository.findAll().stream()
                .sorted(Comparator.comparing(Room::getBuilding).thenComparing(Room::getName))
                .map(this::toRoomDto)
                .toList();
    }

    private MobileSemesterDto toSemesterDto(Semester semester) {
        List<SubjectDto> subjects = subjectService.listBySemester(semester.getId());
        MobileSemesterDto dto = new MobileSemesterDto();
        dto.setId(semester.getId());
        dto.setNumber(semester.getNumber());
        dto.setName(semester.getName());
        dto.setSubjectCount(subjects.size());
        dto.setSubjects(subjects.stream().map(this::toSubjectSummary).toList());
        return dto;
    }

    private MobileSubjectSummaryDto toSubjectSummary(SubjectDto subject) {
        MobileSubjectSummaryDto dto = new MobileSubjectSummaryDto();
        dto.setId(subject.getId());
        dto.setName(subject.getName());
        dto.setCode(subject.getCode());
        dto.setDescription(subject.getDescription());
        dto.setSemesterNumber(subject.getSemesterNumber());
        dto.setNotesCount(subject.getNotesCount());
        dto.setPapersCount(subject.getPapersCount());
        dto.setVideosCount(subject.getVideosCount());
        return dto;
    }

    private ResourceItemDto toNoteItem(Note note) {
        ResourceItemDto dto = new ResourceItemDto();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setSubtitle(note.getUploadedAt() != null ? DATE_TIME_FORMATTER.format(note.getUploadedAt()) : null);
        dto.setDescription(note.getOriginalFileName());
        dto.setUrl("/files/notes/" + note.getId() + "/download");
        dto.setType("NOTE");
        return dto;
    }

    private ResourceItemDto toPaperItem(QuestionPaper paper) {
        ResourceItemDto dto = new ResourceItemDto();
        dto.setId(paper.getId());
        dto.setTitle(paper.getTitle());
        dto.setSubtitle(paper.getYear() != null ? "Year " + paper.getYear() : null);
        dto.setDescription(paper.getOriginalFileName());
        dto.setUrl("/files/papers/" + paper.getId() + "/download");
        dto.setType("PAPER");
        return dto;
    }

    private ResourceItemDto toVideoItem(VideoLink videoLink) {
        ResourceItemDto dto = new ResourceItemDto();
        dto.setId(videoLink.getId());
        dto.setTitle(videoLink.getTitle());
        dto.setSubtitle(videoLink.getAddedAt() != null ? DATE_TIME_FORMATTER.format(videoLink.getAddedAt()) : null);
        dto.setDescription(videoLink.getDescription());
        dto.setUrl(videoLink.getYoutubeUrl());
        dto.setType("VIDEO");
        return dto;
    }

    private MobileRoomDto toRoomDto(Room room) {
        MobileRoomDto dto = new MobileRoomDto();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setCapacity(room.getCapacity());
        dto.setBuilding(room.getBuilding());
        dto.setFloor(room.getFloor());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setResources(room.getResources());
        return dto;
    }
}
