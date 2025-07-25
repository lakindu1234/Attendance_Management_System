package model;

import java.sql.Date;

public class Attendance {
    private int id;
    private int studentId;
    private Date date;
    private boolean status;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
}