package app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public int addCourse(String code, String name, String day, String start, String end)
            throws SQLException {

        String sql =
            "INSERT INTO courses(course_code, course_name, day_of_week, start_time, end_time) " +
            "VALUES(?,?,?,?,?)";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, code.trim().toUpperCase());
            ps.setString(2, name.trim());
            ps.setString(3, day.trim().toUpperCase());
            ps.setString(4, start.trim());
            ps.setString(5, end.trim());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Could not create course.");
    }

    public Integer findCourseIdByCode(String code) throws SQLException {
        String sql = "SELECT course_id FROM courses WHERE course_code = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, code.trim().toUpperCase());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("course_id");
                }
            }
        }

        return null;
    }

    public List<String> listCoursesForStudent(int studentId) throws SQLException {
        String sql =
            "SELECT c.course_code, c.course_name, c.day_of_week, c.start_time, c.end_time " +
            "FROM courses c " +
            "JOIN enrollments e ON e.course_id = c.course_id " +
            "WHERE e.student_id = ? " +
            "ORDER BY c.day_of_week, c.start_time";

        List<String> out = new ArrayList<>();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(
                        rs.getString("course_code") + " — " +
                        rs.getString("course_name") + " (" +
                        rs.getString("day_of_week") + " " +
                        rs.getString("start_time") + "-" +
                        rs.getString("end_time") + ")"
                    );
                }
            }
        }

        return out;
    }

    public List<String> listCoursesForStudentOnDay(int studentId, String day)
            throws SQLException {

        String sql =
            "SELECT c.course_code, c.course_name, c.start_time, c.end_time " +
            "FROM courses c " +
            "JOIN enrollments e ON e.course_id = c.course_id " +
            "WHERE e.student_id = ? AND c.day_of_week = ? " +
            "ORDER BY c.start_time";

        List<String> out = new ArrayList<>();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setString(2, day.trim().toUpperCase());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(
                        rs.getString("course_code") + " — " +
                        rs.getString("course_name") + " (" +
                        rs.getString("start_time") + "-" +
                        rs.getString("end_time") + ")"
                    );
                }
            }
        }

        return out;
    }
}