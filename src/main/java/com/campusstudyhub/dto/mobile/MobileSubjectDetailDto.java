package com.campusstudyhub.dto.mobile;

import java.util.ArrayList;
import java.util.List;

public class MobileSubjectDetailDto extends MobileSubjectSummaryDto {

    private List<ResourceItemDto> notes = new ArrayList<>();
    private List<ResourceItemDto> papers = new ArrayList<>();
    private List<ResourceItemDto> videos = new ArrayList<>();

    public List<ResourceItemDto> getNotes() {
        return notes;
    }

    public void setNotes(List<ResourceItemDto> notes) {
        this.notes = notes;
    }

    public List<ResourceItemDto> getPapers() {
        return papers;
    }

    public void setPapers(List<ResourceItemDto> papers) {
        this.papers = papers;
    }

    public List<ResourceItemDto> getVideos() {
        return videos;
    }

    public void setVideos(List<ResourceItemDto> videos) {
        this.videos = videos;
    }
}
