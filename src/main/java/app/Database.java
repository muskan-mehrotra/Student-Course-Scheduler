package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database helper:
 * - Uses SQLite file "scheduler.db" in the project root (same folder where you run the program)
 * - Initializes tables if they don't exist (safe to run every time)
 */
public class Database {
    private static final String DB_URL = "jdbc:sqlite:scheduler.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void init() {
        // Enable FK constraints and create schema
        String pragma = "PRAGMA foreign_keys = ON;";

        String createStudents = """
            CREATE TABLE IF NOT EXISTS students (
                student_id INTEGER PRIMARY KEY AUTOINCREMENT,
                email TEXT NOT NULL UNIQUE,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL
            );
        """;

        String createCourses = """
            CREATE TABLE IF NOT EXISTS courses (
                course_id INTEGER PRIMARY KEY AUTOINCREMENT,
                course_code TEXT NOT NULL UNIQUE,
                course_name TEXT NOT NULL,
                day_of_week TEXT NOT NULL CHECK(day_of_week IN ('MON','TUE','WED','THU','FRI','SAT','SUN')),
                start_time TEXT NOT NULL,  -- HH:MM
                end_time   TEXT NOT NULL   -- HH:MM
            );
        """;

        String createEnrollments = """
            CREATE TABLE IF NOT EXISTS enrollments (
                enrollment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER NOT NULL,
                course_id INTEGER NOT NULL,
                UNIQUE(student_id, course_id),
                FOREIGN KEY(student_id) REFERENCES students(student_id) ON DELETE CASCADE,
                FOREIGN KEY(course_id) REFERENCES courses(course_id) ON DELETE CASCADE
            );
        """;

        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute(pragma);
            s.execute(createStudents);
            s.execute(createCourses);
            s.execute(createEnrollments);
        } catch (SQLException e) {
            System.err.println("DB init failed: " + e.getMessage());
        }
    }
}