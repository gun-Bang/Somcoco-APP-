package com.example.somcoco.searchcampus;

public class CampusCarouselItem {
    int imgPriority;
    String carouselImg;

    public CampusCarouselItem(int imgPriority, String carouselImg) {
        this.imgPriority = imgPriority;
        this.carouselImg = carouselImg;
    }

    public int getImgPriority() {
        return imgPriority;
    }

    public void setImgPriority(int imgPriority) {
        this.imgPriority = imgPriority;
    }

    public String getCarouselImg() {
        return carouselImg;
    }

    public void setCarouselImg(String carouselImg) {
        this.carouselImg = carouselImg;
    }
}
