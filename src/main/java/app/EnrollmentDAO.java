package app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class EnrollmentDAO {

    public void enrollStudentInCourse(int studentId, int courseId) throws SQLException {
        String sql = "INSERT INTO enrollments(student_id, course_id) VALUES (?, ?)";

        try (Connection c = Database.getConnection();
             Statement s = c.createStatement();
             PreparedStatement ps = c.prepareStatement(sql)) {

            // Enable foreign keys for SQLite
            s.execute("PRAGMA foreign_keys = ON;");

            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.executeUpdate();
        }
    }
}