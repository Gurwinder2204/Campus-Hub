package com.campusstudyhub.dto.mobile;

public class MobileSubjectSummaryDto {

    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer semesterNumber;
    private long notesCount;
    private long papersCount;
    private long videosCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSemesterNumber() {
        return semesterNumber;
    }

    public void setSemesterNumber(Integer semesterNumber) {
        this.semesterNumber = semesterNumber;
    }

    public long getNotesCount() {
        return notesCount;
    }

    public void setNotesCount(long notesCount) {
        this.notesCount = notesCount;
    }

    public long getPapersCount() {
        return papersCount;
    }

    public void setPapersCount(long papersCount) {
        this.papersCount = papersCount;
    }

    public long getVideosCount() {
        return videosCount;
    }

    public void setVideosCount(long videosCount) {
        this.videosCount = videosCount;
    }
}
