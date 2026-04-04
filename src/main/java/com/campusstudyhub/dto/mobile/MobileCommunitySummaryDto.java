package com.campusstudyhub.dto.mobile;

public class MobileCommunitySummaryDto {

    private long activeLostItems;
    private long activeFoundItems;
    private long openComplaints;
    private long myOpenComplaints;

    public long getActiveLostItems() {
        return activeLostItems;
    }

    public void setActiveLostItems(long activeLostItems) {
        this.activeLostItems = activeLostItems;
    }

    public long getActiveFoundItems() {
        return activeFoundItems;
    }

    public void setActiveFoundItems(long activeFoundItems) {
        this.activeFoundItems = activeFoundItems;
    }

    public long getOpenComplaints() {
        return openComplaints;
    }

    public void setOpenComplaints(long openComplaints) {
        this.openComplaints = openComplaints;
    }

    public long getMyOpenComplaints() {
        return myOpenComplaints;
    }

    public void setMyOpenComplaints(long myOpenComplaints) {
        this.myOpenComplaints = myOpenComplaints;
    }
}
