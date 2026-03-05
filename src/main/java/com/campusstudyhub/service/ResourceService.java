package com.campusstudyhub.service;

import com.campusstudyhub.dto.*;
import com.campusstudyhub.entity.*;
import com.campusstudyhub.exception.ResourceNotFoundException;
import com.campusstudyhub.repository.*;
import com.campusstudyhub.util.FileStorageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for resource (notes, papers, videos) management.
 */
@Service
@Transactional
public class ResourceService {

    private static final Logger log = LoggerFactory.getLogger(ResourceService.class);

    private final NoteRepository noteRepository;
    private final QuestionPaperRepository questionPaperRepository;
    private final VideoLinkRepository videoLinkRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final FileStorageUtil fileStorageUtil;
    private final AnalyticsService analyticsService;

    public ResourceService(NoteRepository noteRepository,
            QuestionPaperRepository questionPaperRepository,
            VideoLinkRepository videoLinkRepository,
            SubjectRepository subjectRepository,
            UserRepository userRepository,
            FileStorageUtil fileStorageUtil,
            AnalyticsService analyticsService) {
        this.noteRepository = noteRepository;
        this.questionPaperRepository = questionPaperRepository;
        this.videoLinkRepository = videoLinkRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.fileStorageUtil = fileStorageUtil;
        this.analyticsService = analyticsService;
    }

    // ============== NOTES ==============

    /**
     * Upload a new note.
     * 
     * @param dto           the upload data
     * @param file          the PDF file
     * @param uploaderEmail the uploader's email
     * @return the created note DTO
     */
    public NoteDto uploadNote(ResourceUploadDto dto, MultipartFile file, String uploaderEmail) throws IOException {
        log.info("Uploading note: {} for subject {}", dto.getTitle(), dto.getSubjectId());

        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", dto.getSubjectId()));

        User uploader = userRepository.findByEmail(uploaderEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", uploaderEmail));

        // Store file
        String storedFileName = fileStorageUtil.storeNoteFile(file);
        Path filePath = fileStorageUtil.getNoteFilePath(storedFileName);

        // Create note entity
        Note note = new Note();
        note.setTitle(dto.getTitle());
        note.setOriginalFileName(file.getOriginalFilename());
        note.setStoredFileName(storedFileName);
        note.setFilePath(filePath.toString());
        note.setFileSize(file.getSize());
        note.setUploadedBy(uploader);
        note.setSubject(subject);

        note = noteRepository.save(note);
        log.info("Note uploaded: {} with ID {}", note.getTitle(), note.getId());

        analyticsService.trackEvent("resource_upload",
                java.util.Map.of("type", "note", "id", note.getId(), "title", note.getTitle(), "subjectId",
                        subject.getId()),
                uploaderEmail);

        return toNoteDto(note);
    }

    /**
     * Get a note by ID.
     */
    @Transactional(readOnly = true)
    public NoteDto getNote(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", id));
        return toNoteDto(note);
    }

    /**
     * Get note entity by ID.
     */
    @Transactional(readOnly = true)
    public Note getNoteEntity(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", id));
    }

    /**
     * List notes by subject.
     */
    @Transactional(readOnly = true)
    public List<NoteDto> listNotesBySubject(Long subjectId) {
        return noteRepository.findBySubjectIdOrderByUploadedAtDesc(subjectId).stream()
                .map(this::toNoteDto)
                .collect(Collectors.toList());
    }

    /**
     * Download a note file.
     */
    @Transactional(readOnly = true)
    public Resource downloadNoteFile(Long noteId) {
        Note note = getNoteEntity(noteId);
        return loadFileAsResource(fileStorageUtil.getNoteFilePath(note.getStoredFileName()));
    }

    /**
     * Delete a note.
     */
    public void deleteNote(Long noteId) {
        log.info("Deleting note: {}", noteId);
        Note note = getNoteEntity(noteId);

        // Delete file
        boolean fileDeleted = fileStorageUtil.deleteNoteFile(note.getStoredFileName());
        if (!fileDeleted) {
            log.warn("File not found or could not be deleted: {}", note.getStoredFileName());
        }

        // Delete DB record
        noteRepository.delete(note);
        log.info("Note deleted: {}", noteId);
    }

    // ============== QUESTION PAPERS ==============

    /**
     * Upload a new question paper.
     */
    public QuestionPaperDto uploadPaper(ResourceUploadDto dto, MultipartFile file, String uploaderEmail)
            throws IOException {
        log.info("Uploading paper: {} for subject {}", dto.getTitle(), dto.getSubjectId());

        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", dto.getSubjectId()));

        User uploader = userRepository.findByEmail(uploaderEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", uploaderEmail));

        // Store file
        String storedFileName = fileStorageUtil.storePaperFile(file);
        Path filePath = fileStorageUtil.getPaperFilePath(storedFileName);

        // Create paper entity
        QuestionPaper paper = new QuestionPaper();
        paper.setTitle(dto.getTitle());
        paper.setYear(dto.getYear());
        paper.setOriginalFileName(file.getOriginalFilename());
        paper.setStoredFileName(storedFileName);
        paper.setFilePath(filePath.toString());
        paper.setFileSize(file.getSize());
        paper.setUploadedBy(uploader);
        paper.setSubject(subject);

        paper = questionPaperRepository.save(paper);
        log.info("Paper uploaded: {} with ID {}", paper.getTitle(), paper.getId());

        analyticsService.trackEvent("resource_upload",
                java.util.Map.of("type", "paper", "id", paper.getId(), "title", paper.getTitle(), "subjectId",
                        subject.getId()),
                uploaderEmail);

        return toPaperDto(paper);
    }

    /**
     * Get a question paper by ID.
     */
    @Transactional(readOnly = true)
    public QuestionPaperDto getPaper(Long id) {
        QuestionPaper paper = questionPaperRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionPaper", "id", id));
        return toPaperDto(paper);
    }

    /**
     * Get paper entity by ID.
     */
    @Transactional(readOnly = true)
    public QuestionPaper getPaperEntity(Long id) {
        return questionPaperRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionPaper", "id", id));
    }

    /**
     * List papers by subject.
     */
    @Transactional(readOnly = true)
    public List<QuestionPaperDto> listPapersBySubject(Long subjectId) {
        return questionPaperRepository.findBySubjectIdOrderByYearDesc(subjectId).stream()
                .map(this::toPaperDto)
                .collect(Collectors.toList());
    }

    /**
     * Download a paper file.
     */
    @Transactional(readOnly = true)
    public Resource downloadPaperFile(Long paperId) {
        QuestionPaper paper = getPaperEntity(paperId);
        return loadFileAsResource(fileStorageUtil.getPaperFilePath(paper.getStoredFileName()));
    }

    /**
     * Delete a question paper.
     */
    public void deletePaper(Long paperId) {
        log.info("Deleting paper: {}", paperId);
        QuestionPaper paper = getPaperEntity(paperId);

        // Delete file
        boolean fileDeleted = fileStorageUtil.deletePaperFile(paper.getStoredFileName());
        if (!fileDeleted) {
            log.warn("File not found or could not be deleted: {}", paper.getStoredFileName());
        }

        // Delete DB record
        questionPaperRepository.delete(paper);
        log.info("Paper deleted: {}", paperId);
    }

    // ============== VIDEO LINKS ==============

    /**
     * Add a new video link.
     */
    public VideoLinkDto addVideoLink(VideoLinkDto dto, String addedByEmail) {
        log.info("Adding video: {} for subject {}", dto.getTitle(), dto.getSubjectId());

        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", dto.getSubjectId()));

        User addedBy = userRepository.findByEmail(addedByEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", addedByEmail));

        VideoLink video = new VideoLink();
        video.setTitle(dto.getTitle());
        video.setYoutubeUrl(dto.getYoutubeUrl());
        video.setDescription(dto.getDescription());
        video.setSubject(subject);
        video.setAddedBy(addedBy);

        video = videoLinkRepository.save(video);
        log.info("Video added: {} with ID {}", video.getTitle(), video.getId());

        analyticsService.trackEvent("resource_upload",
                java.util.Map.of("type", "video", "id", video.getId(), "title", video.getTitle(), "subjectId",
                        subject.getId()),
                addedByEmail);

        return toVideoDto(video);
    }

    /**
     * Get a video link by ID.
     */
    @Transactional(readOnly = true)
    public VideoLinkDto getVideo(Long id) {
        VideoLink video = videoLinkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VideoLink", "id", id));
        return toVideoDto(video);
    }

    /**
     * List videos by subject.
     */
    @Transactional(readOnly = true)
    public List<VideoLinkDto> listVideosBySubject(Long subjectId) {
        return videoLinkRepository.findBySubjectIdOrderByAddedAtDesc(subjectId).stream()
                .map(this::toVideoDto)
                .collect(Collectors.toList());
    }

    /**
     * Delete a video link.
     */
    public void deleteVideo(Long videoId) {
        log.info("Deleting video: {}", videoId);
        VideoLink video = videoLinkRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("VideoLink", "id", videoId));
        videoLinkRepository.delete(video);
        log.info("Video deleted: {}", videoId);
    }

    // ============== HELPER METHODS ==============

    private Resource loadFileAsResource(Path filePath) {
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new ResourceNotFoundException("File not found: " + filePath);
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("File not found: " + filePath);
        }
    }

    private NoteDto toNoteDto(Note note) {
        NoteDto dto = new NoteDto();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setOriginalFileName(note.getOriginalFileName());
        dto.setFileSize(note.getFileSize());
        dto.setUploadedAt(note.getUploadedAt());
        dto.setUploadedByName(note.getUploadedBy().getFullName());
        dto.setSubjectId(note.getSubject().getId());
        dto.setSubjectName(note.getSubject().getName());
        return dto;
    }

    private QuestionPaperDto toPaperDto(QuestionPaper paper) {
        QuestionPaperDto dto = new QuestionPaperDto();
        dto.setId(paper.getId());
        dto.setTitle(paper.getTitle());
        dto.setYear(paper.getYear());
        dto.setOriginalFileName(paper.getOriginalFileName());
        dto.setFileSize(paper.getFileSize());
        dto.setUploadedAt(paper.getUploadedAt());
        dto.setUploadedByName(paper.getUploadedBy().getFullName());
        dto.setSubjectId(paper.getSubject().getId());
        dto.setSubjectName(paper.getSubject().getName());
        return dto;
    }

    private VideoLinkDto toVideoDto(VideoLink video) {
        VideoLinkDto dto = new VideoLinkDto();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setYoutubeUrl(video.getYoutubeUrl());
        dto.setThumbnailUrl(video.getThumbnailUrl());
        dto.setEmbedUrl(video.getEmbedUrl());
        dto.setDescription(video.getDescription());
        dto.setSubjectId(video.getSubject().getId());
        dto.setSubjectName(video.getSubject().getName());
        dto.setAddedByName(video.getAddedBy().getFullName());
        dto.setAddedAt(video.getAddedAt());
        return dto;
    }
}
