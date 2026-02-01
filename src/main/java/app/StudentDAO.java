package app;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public int addStudent(String email, String first, String last) throws SQLException {
        String sql = "INSERT INTO students(email, first_name, last_name) VALUES(?,?,?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, email.trim());
            ps.setString(2, first.trim());
            ps.setString(3, last.trim());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Could not create student.");
    }

    public Integer findStudentIdByEmail(String email) throws SQLException {
        String sql = "SELECT student_id FROM students WHERE email = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("student_id");
            }
        }
        return null;
    }

    public List<String> listStudentsInCourse(int courseId) throws SQLException {
        String sql = """
            SELECT s.student_id, s.first_name, s.last_name, s.email
            FROM students s
            JOIN enrollments e ON e.student_id = s.student_id
            WHERE e.course_id = ?
            ORDER BY s.last_name, s.first_name
        """;

        List<String> out = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(
                        rs.getInt("student_id") + ": " +
                        rs.getString("first_name") + " " +
                        rs.getString("last_name") + " (" +
                        rs.getString("email") + ")"
                    );
                }
            }
        }
        return out;
    }
}