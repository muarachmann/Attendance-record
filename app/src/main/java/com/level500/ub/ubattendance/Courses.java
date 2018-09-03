package com.level500.ub.ubattendance;

public class Courses {
        private String name;
        private String courseCode;
        private String lecturerID;
        private int thumbnail;

        public Courses() {
        }

        public Courses(String name, String courseCode,String lecturerID, int thumbnail) {
            this.name = name;
            this.courseCode = courseCode;
            this.thumbnail = thumbnail;
            this.lecturerID = lecturerID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCourseCode() {
            return courseCode;
        }

        public void setCourseCode(String courseCode) {
            this.courseCode = courseCode;
        }

        public String getLecturerID() {
            return lecturerID;
        }

        public void setLecturerID(String lecturerID){
            this.lecturerID = lecturerID;
    }

    public int getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(int thumbnail) {
            this.thumbnail = thumbnail;
        }
}
