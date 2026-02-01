# Student Course Scheduler

## Overview
The Student Course Scheduler is a Java-based command-line application that manages students, courses, and academic schedules using a persistent relational database. The system allows users to create student and course records, enroll students in courses, and perform various queries related to enrollment and scheduling.

The application uses SQLite as its database, which is automatically created and stored as a `.db` file. This ensures that all data persists between program runs.

---

## Features
- Add new students with a unique email address
- Add new courses with course code, course name, day of the week, and time range
- Enroll students into courses
- View all students enrolled in a specific course
- View all courses a student is enrolled in
- View a student’s schedule for a given day of the week
- Persistent database storage using SQLite

---

## Technologies Used
- **Language:** Java
- **Database:** SQLite (JDBC)
- **Build Tool:** Maven
- **Architecture:** DAO (Data Access Object) pattern
- **Interface:** Command-Line Interface (CLI)

---

## Project Structure
Student-course-scheduler/
├── pom.xml
├── scheduler.db
└── src
└── main
└── java
└── app
├── Main.java
├── Database.java
├── StudentDAO.java
├── CourseDAO.java
└── EnrollmentDAO.java

---

## How to Compile and Run

### Prerequisites
- Java (JDK 17 or higher recommended)
- Maven

### Compile the Project
From the project root directory:
bash 
```
mvn clean compile
```

### Run the Application
bash
```
mvn exec:java
```

---

## Sample Program Output
=== Student Course Scheduler ===
	1.	Add new student
	2.	Add new course
	3.	Enroll student in course
	4.	List students in a course
	5.	List courses for a student
	6.	List a student’s schedule for a day
	7.	Exit
Choose:---

## Database Persistence
The database is stored in a file named `scheduler.db`.  
This file is automatically created on the first successful run of the program and preserves all data between executions.

---

## Assignment Requirements Mapping
- **Allow new students to enroll:** Add Student option
- **Allow new courses to be introduced:** Add Course option
- **Allow students to enroll in courses:** Enrollment option
- **Query students in each course:** List Students in Course
- **Query courses for each student:** List Courses for Student
- **Query student schedule by day:** Schedule by Day option
- **Database persistence:** SQLite database file
- **Error-free execution:** Validated inputs and structured queries

---

## Author
Muskan Mehrotra
