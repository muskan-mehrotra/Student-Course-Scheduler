package app;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

    private static final Pattern TIME_24H = Pattern.compile("^(?:[01]\\d|2[0-3]):[0-5]\\d$");
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private static final StudentDAO studentDAO = new StudentDAO();
    private static final CourseDAO courseDAO = new CourseDAO();
    private static final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    public static void main(String[] args) {
        Database.init(); // persistence + auto schema creation

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                printMenu();
                String choice = sc.nextLine().trim();

                switch (choice) {
                    case "1" -> addStudent(sc);
                    case "2" -> addCourse(sc);
                    case "3" -> enroll(sc);
                    case "4" -> listStudentsInCourse(sc);
                    case "5" -> listCoursesForStudent(sc);
                    case "6" -> listScheduleForStudentOnDay(sc);
                    case "0" -> {
                        System.out.println("Exiting. Database saved in scheduler.db");
                        return;
                    }
                    default -> System.out.println("Invalid option. Try again.");
                }
                System.out.println();
            }
        }
    }

    private static void printMenu() {
        System.out.println("=== Student Course Scheduler ===");
        System.out.println("1) Add new student");
        System.out.println("2) Add new course");
        System.out.println("3) Enroll student in course");
        System.out.println("4) List students in a course");
        System.out.println("5) List courses for a student");
        System.out.println("6) List a student's schedule for a day");
        System.out.println("0) Exit");
        System.out.print("Choose: ");
    }

    // --- Option 1 ---
    private static void addStudent(Scanner sc) {
        try {
            String email = ask(sc, "Student email: ");
            if (!EMAIL.matcher(email).matches()) {
                System.out.println("Invalid email format.");
                return;
            }
            String first = ask(sc, "First name: ");
            String last = ask(sc, "Last name: ");

            int id = studentDAO.addStudent(email, first, last);
            System.out.println("Created student with ID: " + id);
        } catch (SQLException e) {
            System.out.println("Could not add student: " + e.getMessage());
        }
    }

    // --- Option 2 ---
    private static void addCourse(Scanner sc) {
        try {
            String code = ask(sc, "Course code (e.g., CS101): ").toUpperCase();
            String name = ask(sc, "Course name: ");
            String day = askDay(sc);
            String start = askTime(sc, "Start time (HH:MM 24h): ");
            String end = askTime(sc, "End time (HH:MM 24h): ");

            // simple check: end should be after start (lexicographically works for HH:MM)
            if (end.compareTo(start) <= 0) {
                System.out.println("End time must be after start time.");
                return;
            }

            int id = courseDAO.addCourse(code, name, day, start, end);
            System.out.println("Created course with ID: " + id);
        } catch (SQLException e) {
            System.out.println("Could not add course: " + e.getMessage());
        }
    }

    // --- Option 3 ---
    private static void enroll(Scanner sc) {
        try {
            String email = ask(sc, "Student email: ");
            Integer studentId = studentDAO.findStudentIdByEmail(email);
            if (studentId == null) {
                System.out.println("No student found with that email.");
                return;
            }

            String code = ask(sc, "Course code: ");
            Integer courseId = courseDAO.findCourseIdByCode(code);
            if (courseId == null) {
                System.out.println("No course found with that code.");
                return;
            }

            enrollmentDAO.enrollStudentInCourse(studentId, courseId);
            System.out.println("Enrolled student " + studentId + " in course " + courseId);
        } catch (SQLException e) {
            System.out.println("Could not enroll: " + e.getMessage());
            System.out.println("(Tip: if it says UNIQUE constraint failed, the student is already enrolled.)");
        }
    }

    // --- Option 4 ---
    private static void listStudentsInCourse(Scanner sc) {
        try {
            String code = ask(sc, "Course code: ");
            Integer courseId = courseDAO.findCourseIdByCode(code);
            if (courseId == null) {
                System.out.println("No course found with that code.");
                return;
            }

            List<String> students = studentDAO.listStudentsInCourse(courseId);
            if (students.isEmpty()) {
                System.out.println("No students enrolled in " + code.toUpperCase());
                return;
            }

            System.out.println("Students in " + code.toUpperCase() + ":");
            students.forEach(s -> System.out.println(" - " + s));
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
    }

    // --- Option 5 ---
    private static void listCoursesForStudent(Scanner sc) {
        try {
            String email = ask(sc, "Student email: ");
            Integer studentId = studentDAO.findStudentIdByEmail(email);
            if (studentId == null) {
                System.out.println("No student found with that email.");
                return;
            }

            List<String> courses = courseDAO.listCoursesForStudent(studentId);
            if (courses.isEmpty()) {
                System.out.println("Student is not enrolled in any courses.");
                return;
            }

            System.out.println("Courses for " + email + ":");
            courses.forEach(c -> System.out.println(" - " + c));
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
    }

    // --- Option 6 ---
    private static void listScheduleForStudentOnDay(Scanner sc) {
        try {
            String email = ask(sc, "Student email: ");
            Integer studentId = studentDAO.findStudentIdByEmail(email);
            if (studentId == null) {
                System.out.println("No student found with that email.");
                return;
            }

            String day = askDay(sc);
            List<String> schedule = courseDAO.listCoursesForStudentOnDay(studentId, day);

            if (schedule.isEmpty()) {
                System.out.println("No classes on " + day + " for " + email);
                return;
            }

            System.out.println("Schedule for " + email + " on " + day + ":");
            schedule.forEach(x -> System.out.println(" - " + x));
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
    }

    // --- Input helpers ---
    private static String ask(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static String askTime(Scanner sc, String prompt) {
        String t = ask(sc, prompt);
        if (!TIME_24H.matcher(t).matches()) {
            System.out.println("Invalid time. Must be HH:MM in 24-hour format (e.g., 09:30, 14:05).");
            return askTime(sc, prompt);
        }
        return t;
    }

    private static String askDay(Scanner sc) {
        String day = ask(sc, "Day of week (MON/TUE/WED/THU/FRI/SAT/SUN): ").toUpperCase();
        return switch (day) {
            case "MON","TUE","WED","THU","FRI","SAT","SUN" -> day;
            default -> {
                System.out.println("Invalid day. Use MON/TUE/WED/THU/FRI/SAT/SUN.");
                yield askDay(sc);
            }
        };
    }
}