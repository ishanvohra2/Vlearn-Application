package com.theindiecorp.vlearn.data;

import java.util.ArrayList;

public class Course {

    private String nameOfCourse, courseDescription, imgPath, type, category;
    private String publishDate, authorId;
    private String courseId, keyPoints;
    private int enrollmentNo;
    private Boolean isPlayList = false;
    private ArrayList<String> topics;
    private ArrayList<String> playlistItems;

    public Boolean getPlayList() {
        return isPlayList;
    }

    public void setPlayList(Boolean playList) {
        isPlayList = playList;
    }

    public ArrayList<String> getPlaylistItems() {
        return playlistItems;
    }

    public void setPlaylistItems(ArrayList<String> playlistItems) {
        this.playlistItems = playlistItems;
    }

    public ArrayList<String> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<String> topics) {
        this.topics = topics;
    }

    public String getKeyPoints() {
        return keyPoints;
    }

    public void setKeyPoints(String keyPoints) {
        this.keyPoints = keyPoints;
    }

    public int getEnrollmentNo() {
        return enrollmentNo;
    }

    public void setEnrollmentNo(int enrollmentNo) {
        this.enrollmentNo = enrollmentNo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNameOfCourse() {
        return nameOfCourse;
    }

    public String getType() {
        return type;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNameOfCourse(String nameOfCourse) {
        this.nameOfCourse = nameOfCourse;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
