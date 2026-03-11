package com.campusstudyhub.dto.mobile;

import java.util.ArrayList;
import java.util.List;

public class MobileDashboardDto {

    private MobileUserDto user;
    private long pendingTasks;
    private long totalSemesters;
    private long totalSubjects;
    private List<MobileSemesterDto> featuredSemesters = new ArrayList<>();

    public MobileUserDto getUser() {
        return user;
    }

    public void setUser(MobileUserDto user) {
        this.user = user;
    }

    public long getPendingTasks() {
        return pendingTasks;
    }

    public void setPendingTasks(long pendingTasks) {
        this.pendingTasks = pendingTasks;
    }

    public long getTotalSemesters() {
        return totalSemesters;
    }

    public void setTotalSemesters(long totalSemesters) {
        this.totalSemesters = totalSemesters;
    }

    public long getTotalSubjects() {
        return totalSubjects;
    }

    public void setTotalSubjects(long totalSubjects) {
        this.totalSubjects = totalSubjects;
    }

    public List<MobileSemesterDto> getFeaturedSemesters() {
        return featuredSemesters;
    }

    public void setFeaturedSemesters(List<MobileSemesterDto> featuredSemesters) {
        this.featuredSemesters = featuredSemesters;
    }
}
