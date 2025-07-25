package dao;

import model.Attendance;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {
    public void markAttendance(Attendance att) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO attendance (student_id, date, status) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, att.getStudentId());
            stmt.setDate(2, att.getDate());
            stmt.setBoolean(3, att.isStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Attendance> getAttendanceByStudent(int studentId) {
        List<Attendance> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM attendance WHERE student_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Attendance att = new Attendance();
                att.setId(rs.getInt("id"));
                att.setStudentId(rs.getInt("student_id"));
                att.setDate(rs.getDate("date"));
                att.setStatus(rs.getBoolean("status"));
                list.add(att);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
