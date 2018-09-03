package com.level500.ub.ubattendance;

public class StudentCourses {
        private String name;
        private String courseCode;
        private String studentID;
        private int thumbnail;

        public StudentCourses() {
        }

        public StudentCourses(String name, String courseCode, String studentID, int thumbnail) {
            this.name = name;
            this.courseCode = courseCode;
            this.thumbnail = thumbnail;
            this.studentID = studentID;
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

        public String getStudentID() {
            return studentID;
        }

        public void setStudentID(String studentID){
            this.studentID = studentID;
    }

    public int getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(int thumbnail) {
            this.thumbnail = thumbnail;
        }
}
