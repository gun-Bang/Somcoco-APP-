package com.example.somcoco.tour;

public class FacilityInfoItem {
    String facilityName;
    String facilityImg;
    String facilityInfoKr;
    String facilityInfoEn;
    String facilityAudio;

    public FacilityInfoItem(String facilityName, String facilityImg, String facilityInfoKr, String facilityInfoEn, String facilityAudio) {
        this.facilityName = facilityName;
        this.facilityImg = facilityImg;
        this.facilityInfoKr = facilityInfoKr;
        this.facilityInfoEn = facilityInfoEn;
        this.facilityAudio = facilityAudio;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityImg() {
        return facilityImg;
    }

    public void setFacilityImg(String facilityImg) {
        this.facilityImg = facilityImg;
    }

    public String getFacilityInfoKr() {
        return facilityInfoKr;
    }

    public void setFacilityInfoKr(String facilityInfoKr) {
        this.facilityInfoKr = facilityInfoKr;
    }

    public String getFacilityInfoEn() {
        return facilityInfoEn;
    }

    public void setFacilityInfoEn(String facilityInfoEn) {
        this.facilityInfoEn = facilityInfoEn;
    }

    public String getFacilityAudio() {
        return facilityAudio;
    }

    public void setFacilityAudio(String facilityAudio) {
        this.facilityAudio = facilityAudio;
    }
}
