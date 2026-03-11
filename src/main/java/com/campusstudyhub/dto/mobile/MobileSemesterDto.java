package com.campusstudyhub.dto.mobile;

import java.util.ArrayList;
import java.util.List;

public class MobileSemesterDto {

    private Long id;
    private Integer number;
    private String name;
    private int subjectCount;
    private List<MobileSubjectSummaryDto> subjects = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSubjectCount() {
        return subjectCount;
    }

    public void setSubjectCount(int subjectCount) {
        this.subjectCount = subjectCount;
    }

    public List<MobileSubjectSummaryDto> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<MobileSubjectSummaryDto> subjects) {
        this.subjects = subjects;
    }
}
