import java.util.*;
import java.io.*;

class University2{
    private static HashMap<String, User> users = new HashMap<>();
    private static final String STUDENT_DATA_FILE = "student_data.txt";
    private static final String TEACHER_DATA_FILE = "teacher_data.txt";
    private static User signedUpUser; 

    public static void main(String[] args) {
        loadUserData();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println();
            System.out.println("------------------- Welcome to University Management System -------------------");
            System.out.println();

            System.out.println("1. Sign Up");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    signUpFlow(scanner);
                    break;
                case 2:
                    loginFlow(scanner);
                    break;
                case 3:
                    saveUserData(); 
                    System.out.println("Exiting program...");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }

    private static void signUpFlow(Scanner scanner) {
        System.out.println("Enter your role (student, teacher):");
        String role = scanner.nextLine().toLowerCase();

        switch (role) {
            case "student":
                signUp("student", scanner);
                break;
            case "teacher":
                signUp("teacher", scanner);
                break;
            case "hod":
                signUp("hod", scanner);
                break;
            default:
                System.out.println("Invalid role. Please try again.");
        }
    }

    private static void loginFlow(Scanner scanner) {
        System.out.println("Enter your role (student, teacher):");
        String role = scanner.nextLine().toLowerCase();

        switch (role) {
            case "student":
                login("student", scanner);
                if (signedUpUser != null) {
                    updateDetails_student(signedUpUser, scanner);
                    viewMarks(signedUpUser);
                }
                break;
            case "teacher":
                login("teacher", scanner);
                if(signedUpUser!=null)
                {
                    updateDetails_teacher(signedUpUser,scanner);
                }
                break;
            case "hod":
                hodLogin(scanner);
                break;
            default:
                System.out.println("Invalid role. Please try again.");
        }
    }

    private static void loadUserData() {
        loadUserDataFromFile(STUDENT_DATA_FILE);
        loadUserDataFromFile(TEACHER_DATA_FILE);
    }

    private static void loadUserDataFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length != 9) {
                    System.out.println("Invalid user data format: " + line);
                    continue; 
                }
                String email = userData[0];
                User user = new User(userData[1], userData[2], userData[3], Integer.parseInt(userData[4]),
                        userData[5], userData[6], userData[7], userData[8]);
                users.put(email, user);
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading user data from file " + filename + ": " + e.getMessage());
        }
    }
    private static void loadUserDataFrom1(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length != 8) {
                    System.out.println("Invalid user data format: " + line);
                    continue; // Skip this line and move to the next one
                }
                String email = userData[0];
                User user = new User(userData[1], userData[2], userData[3],userData[4],userData[5],
                        userData[6], userData[7]);
                users.put(email, user);
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading user data from file " + filename + ": " + e.getMessage());
        }
    }

    private static void saveUserData() {
        saveUserDataToFile(users.values(), STUDENT_DATA_FILE, "student");
        saveUserDataToFile(users.values(), TEACHER_DATA_FILE, "teacher");

    }

    private static void saveUserDataToFile(Collection<User> userList, String filename, String role) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (User user : userList) {
                if (user.getRole().equals(role)) {
                    bw.write(user.toCsv());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving user data to file " + filename + ": " + e.getMessage());
        }
    }

    private static void signUp(String role, Scanner scanner) {
        System.out.print("Enter your full name: ");
    String fullName = scanner.nextLine().trim();
    
    // Check if the full name contains more than one word
    if (fullName.split("\\s+").length < 2) {
        System.out.println("Please enter your full name.");
        return; 
    }
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
    
        // Generate email ID
        String email = generateEmail(fullName);
        System.out.println("Sign up successful.");
        System.out.println("Your Email id is: " + email);
        System.out.println();
        System.out.println("Enter Your Details Below: ");
    
        // Additional details for students
        if (role.equals("student")) {
            System.out.print("Enter your current semester: ");
            int currentSemester = scanner.nextInt();
            scanner.nextLine(); 
            System.out.print("Enter your roll number: ");
            String rollNo = scanner.nextLine();
            System.out.print("Enter your batch: ");
            String batch = scanner.nextLine();
            System.out.print("Enter your branch: ");
            String branch = scanner.nextLine();
    
            String studentId = generateStudentId(branch);
            signedUpUser = new User(fullName, password, role, currentSemester, rollNo, batch, branch, studentId);
            users.put(email, signedUpUser);
            saveUserData(); 
            System.out.println("Your student ID is: " + studentId);
            displaySignUpDetails();
            System.out.println();
        } else if (role.equals("teacher")) {
            // Ask for further details for teachers
            System.out.print("Enter your department: ");
            String department = scanner.nextLine();
            System.out.print("Enter your contact number: ");
            String contactNumber = scanner.nextLine();
            System.out.print("Enter your qualification (degree): ");
            String qualification = scanner.nextLine();
    
            // Generate teacher ID
            String teacherId = generateTeacherId(department);
            System.out.println("Your teacher ID is: " + teacherId);
    
            signedUpUser = new User(fullName, password, role, department,contactNumber, qualification, teacherId);
            signedUpUser.setDepartment(department);
            signedUpUser.setContactNumber(contactNumber);
            signedUpUser.setQualification(qualification);
            signedUpUser.setEmail(email);
            signedUpUser.setTeacherId(teacherId); 
    
            users.put(email, signedUpUser);
            saveUserData(); // Save user data after sign up
            displayTeacherDetails(signedUpUser);
            System.out.println();
        } 
        
        else {
            // Sign up for other roles
            signedUpUser = new User(fullName, password, role);
            users.put(email, signedUpUser);
            saveUserData(); // Save user data after sign up
            System.out.println();
        }
    }

    private static void displaySignUpDetails() {
        if (signedUpUser != null) {
            System.out.println();
            System.out.println("-------------------------------------------------------------------------- Sign-Up Details --------------------------------------------------------------------------");
            System.out.println();
            System.out.printf("%-30s %-40s %-20s %-20s %-20s %-20s %-20s\n",
                    "Full Name", "Email", "ID", "Semester", "Roll Number", "Batch", "Branch");
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");

            System.out.printf("%-30s %-40s %-20s %-20d %-20s %-20s %-20s\n",
                    signedUpUser.getFullName(), signedUpUser.getEmail(), signedUpUser.getStudentId(), signedUpUser.getCurrentSemester(),
                    signedUpUser.getRollNo(), signedUpUser.getBatch(), signedUpUser.getBranch());
            System.out.println();
            System.out.println("Login to update your details");
        } else {
            System.out.println("No user signed up in this session.");
        }
    }
    private static void displayTeacherDetails(User teacher) {
        if (teacher != null) {
            System.out.println();
            System.out.println("-------------------------------------------------------------------------- Teacher Details --------------------------------------------------------------------------");
            System.out.println();
            System.out.printf("%-30s %-40s %-20s %-20s %-20s %-20s\n",
                    "Full Name", "Email", "Department", "Contact Number", "Qualification", "Teacher ID");
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    
            System.out.printf("%-30s %-40s %-20s %-20s %-20s %-20s\n",
                    teacher.getFullName(), teacher.getEmail(), teacher.getDepartment(), teacher.getContactNumber(), teacher.getQualification(),teacher.getTeacherId());
            System.out.println();
            System.out.println("Login to update your details");
        } else {
            System.out.println("No teacher signed up in this session.");
        }
    }
    
    private static String generateTeacherId(String department) {
        // Extract the first three letters from the department name
        String prefix = department.substring(0, Math.min(department.length(), 3)).toUpperCase();
        // Add some random numbers to make it unique
        Random random = new Random();
        int randomNumber = random.nextInt(9000) + 1000;
        return prefix + randomNumber;
    }
   
    private static String generateEmail(String fullName) {
        String username = generateUsername(fullName);
        return username + "@ljku.edu.in";
    }

    private static String generateUsername(String fullName) {
    String[] names = fullName.split(" ");
    if (names.length >= 2) {
        String firstName = names[0].toLowerCase();
        String lastName = names[names.length - 2].toLowerCase(); // Changed to use the last part of the name
        String username = firstName + "." + lastName;
        // Check if the username already exists
        if (users.containsKey(username)) {
            // Append a unique identifier to make it unique
            int uniqueIdentifier = 1;
            while (users.containsKey(username + uniqueIdentifier)) {
                uniqueIdentifier++;
            }
            return username + uniqueIdentifier;
        } else {
            return username;
        }
    } else {
        // If fullName doesn't contain at least two parts, use a default username
        return "default_username";
    }
}

    private static String generateStudentId(String branch) {
        Random random = new Random();
        int randomNum = random.nextInt(9000) + 1000; // Generate a 4-digit random number
        return branch.toUpperCase() + randomNum;
    }

    private static void updateDetails_student(User user, Scanner scanner) {
        System.out.println("Updating user details...");

        // Prompt the user to enter new details
        System.out.print("Enter your full name: ");
        String fullName = scanner.nextLine();
        System.out.print("Enter your current semester: ");
        int currentSemester = scanner.nextInt();
        scanner.nextLine(); // Consume newline character
        System.out.print("Enter your roll number: ");
        String rollNo = scanner.nextLine();
        System.out.print("Enter your batch: ");
        String batch = scanner.nextLine();
        System.out.print("Enter your branch: ");
        String branch = scanner.nextLine();

        // Update the user object with new details
        user.setFullName(fullName);
        user.setCurrentSemester(currentSemester);
        user.setRollNo(rollNo);
        user.setBatch(batch);
        user.setBranch(branch);

        // Display updated details
        System.out.println("User details updated successfully:");
        System.out.println("Full Name: " + user.getFullName());
        System.out.println("Current Semester: " + user.getCurrentSemester());
        System.out.println("Roll Number: " + user.getRollNo());
        System.out.println("Batch: " + user.getBatch());
        System.out.println("Branch: " + user.getBranch());
    }
    private static void updateDetails_teacher(User user, Scanner scanner) {
        System.out.println("Updating user details...");
    
        // Prompt the user to enter new details
        System.out.print("Enter your full name: ");
        String fullName = scanner.nextLine();
        System.out.print("Enter your department: ");
        String department = scanner.nextLine();
        System.out.print("Enter your contact number: ");
        String contactNumber = scanner.nextLine();
        System.out.print("Enter your qualification: ");
        String qualification = scanner.nextLine();
    
        // Update the user object with new details
        user.setFullName(fullName);
        user.setDepartment(department);
        user.setContactNumber(contactNumber);
        user.setQualification(qualification);
    
        // Display updated details in table form
        System.out.println();
        System.out.println("-------------------------------------------------------------------------- Updated Teacher Details --------------------------------------------------------------------------");
        System.out.println();
        System.out.printf("%-30s %-40s %-20s %-20s %-20s\n",
                "Full Name", "Email", "Department", "Contact Number", "Qualification");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    
        System.out.printf("%-30s %-40s %-20s %-20s %-20s\n",
                user.getFullName(), user.getEmail(), user.getDepartment(), user.getContactNumber(), user.getQualification());
        System.out.println();
    }
    
    private static void searchStudentById(Scanner scanner) {
        System.out.print("Enter student ID to search: ");
        String searchId = scanner.nextLine();
    
        boolean found = false;
        for (User user : users.values()) {
            if (user.getRole().equals("student") && user.getStudentId().equals(searchId)) {
                found = true;
                System.out.println("-------------------------------------------------------------------------- Student Details --------------------------------------------------------------------------");
                System.out.println();
            System.out.printf("%-30s %-40s %-20s %-20s %-20s %-20s %-20s\n",
                    "Full Name", "Email", "ID", "Semester", "Roll Number", "Batch", "Branch");
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");

            System.out.printf("%-30s %-40s %-20s %-20d %-20s %-20s %-20s\n",
                    user.getFullName(), user.getEmail(), user.getStudentId(), user.getCurrentSemester(),
                    user.getRollNo(), user.getBatch(), user.getBranch());
            System.out.println();
                break;
                
            }
        }
    
        if (!found) {
            System.out.println("Student with ID " + searchId + " not found.");
        }
    }
    private static User searchStudentById1(Scanner scanner) {
        System.out.print("Enter student ID to search: ");
        String searchId = scanner.nextLine();

        for (User user : users.values()) {
            if (user.getRole().equals("student") && user.getStudentId().equals(searchId)) {
                return user;
            }
        }

        System.out.println("Student with ID " + searchId + " not found.");
        return null;
    }

    private static void giveMarksToStudent() {
        Scanner scanner = new Scanner(System.in);
        User student = searchStudentById1(scanner);
        HashMap<String, Integer> grades = new HashMap<>();
    
        if (student != null) {
            System.out.println("Hello Teacher!");
    
            // Prompt the teacher to give marks for each subject
            System.out.println("Enter marks for Design and Analysis of Algorithm:");
            int daaMarks = Integer.parseInt(scanner.nextLine());
    
            System.out.println("Enter marks for Software Engineering:");
            int seMarks = Integer.parseInt(scanner.nextLine());
    
            System.out.println("Enter marks for Mini Project:");
            int miniProjectMarks = Integer.parseInt(scanner.nextLine());
    
            System.out.println("Enter marks for Engineering Aptitude:");
            int engAptitudeMarks = Integer.parseInt(scanner.nextLine());
    
            System.out.println("Enter marks for Technical English-1:");
            int technicalEnglishMarks = Integer.parseInt(scanner.nextLine());
    
            // Store the marks in a HashMap
            grades.put("Design and Analysis of Algorithm", daaMarks);
            grades.put("Software Engineering", seMarks);
            grades.put("Mini Project", miniProjectMarks);
            grades.put("Engineering Aptitude", engAptitudeMarks);
            grades.put("Technical English-1", technicalEnglishMarks);
    
            // Save marks to file
            saveMarksToFile(student.getStudentId(), grades); // <-- Save marks to file
    
            System.out.println("Marks have been updated successfully!");
            student.setGrades(grades);
        }
    }
    

    // private static void viewstu(){
    //     data=[CE3797,CE3772]
    //     for i in range data:
    //     viewMarks(User student)
    // }

    private static void viewMarks(User student) {
        System.out.println("------------------------------------------------------------------------------");
        System.out.printf("%-50s %-20s %-20s %-20s\n", "Subject", "Marks Obtained", "Total Marks", "Credits");
        System.out.println("------------------------------------------------------------------------------");
    
        // Load marks from file
        HashMap<String, Integer> grades = loadMarksFromFile(student.getStudentId()); // <-- Load marks from file
    
        if (grades != null) {
            int totalObtainedMarks = 0;
            int totalPossibleMarks = 0;
    
            for (Map.Entry<String, Integer> entry : grades.entrySet()) {
                String subject = entry.getKey();
                int marks = entry.getValue();
    
                // Print marks for each subject
                System.out.printf("%-50s %-20d %-20d %-20d\n", subject, marks, 100, calculateCredits(subject));
    
                // Calculate total obtained marks
                totalObtainedMarks += marks;
    
                // Calculate total possible marks
                totalPossibleMarks += 100; // Assuming total marks for each subject is 100
            }
    
            // Calculate total percentage
            double percentage = calculatePercentage(totalObtainedMarks, totalPossibleMarks);
    
            System.out.println("------------------------------------------------------------------------------");
            System.out.printf("Total Percentage: %.2f%%\n", percentage);
            System.out.println("------------------------------------------------------------------------------");
        } else {
            System.out.println("No marks available.");
        }
    }
    
   
    private static double calculatePercentage(int obtainedMarks, int totalMarks) {
        return (double) obtainedMarks / totalMarks * 100;
    }

    private static int calculateCredits(String subject) {
        switch (subject) {
            case "Design and Analysis of Algorithm":
                return 5;
            case "Software Engineering":
                return 4;
            case "Mini Project":
                return 2;
            case "Engineering Aptitude":
                return 3;
            case "Technical English-1":
                return 1;
            default:
                return 0; // If subject not found, return 0 credits
        }
    }

    private static int getTotalMarksForSubject(String subject) {
        switch (subject) {
            case "Design and Analysis of Algorithm":
            case "Software Engineering":
                return 100; // Assuming total marks for these subjects are 100
            case "Mini Project":
                return 100; // Assuming total marks for mini project are also 100
            case "Engineering Aptitude":
            case "Technical English-1":
                return 100; // Assuming total marks for these subjects are 100
            default:
                return 0; // Default case: subject not found or not applicable
        }
    }

    private static final String MARKS_FILE = "student_marks.txt";

private static void saveMarksToFile(String studentId, HashMap<String, Integer> grades) {
    try {
        FileWriter writer = new FileWriter(MARKS_FILE, true); 
        for (Map.Entry<String, Integer> entry : grades.entrySet()) {
            writer.write(studentId + "," + entry.getKey() + "," + entry.getValue() + "\n");
        }
        writer.close();
        System.out.println("Marks have been saved successfully.");
    } catch (IOException e) {
        System.out.println("An error occurred while saving marks: " + e.getMessage());
    }
}

private static HashMap<String, Integer> loadMarksFromFile(String studentId) {
    HashMap<String, Integer> grades = new HashMap<>();
    try {
        File file = new File(MARKS_FILE);
        if (file.exists()) {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[0].equals(studentId)) {
                    grades.put(parts[1], Integer.parseInt(parts[2]));
                }
            }
            scanner.close();
            System.out.println("Marks have been loaded successfully.");
        } else {
            System.out.println("Marks file not found.");
        }
    } catch (IOException e) {
        System.out.println("An error occurred while loading marks: " + e.getMessage());
    }
    return grades;
}

private static HashMap<String, Integer> loadMarksFile() {
    HashMap<String, Integer> grades1 = new HashMap<>();
    try {
        File file = new File(MARKS_FILE);
        if (file.exists()) {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    grades1.put(parts[1], Integer.parseInt(parts[2]));
                }
            }
            scanner.close();
            System.out.println("Marks have been loaded successfully.");
        } else {
            System.out.println("Marks file not found.");
        }
    } catch (IOException e) {
        System.out.println("An error occurred while loading marks: " + e.getMessage());
    }
    return grades1;
}

// private static void sortAndDisplayStudents(List<User> students) {
//     Collections.sort(students, Comparator.comparingDouble(User::getPercentage).reversed());

//     System.out.println("Sorted students by percentage:");
//     for (User student : User) {
//         System.out.printf("ID: %s, Name: %s, Marks: %d, Percentage: %.2f%%\n",
//                 student.id, student.name, student.marks, student.getPercentage());
//     }
// }
// }

private static void viewStudents(int option) {
    Scanner scanner = new Scanner(System.in);
    switch (option) {
        case 1:
            System.out.print("Enter semester to search students: ");
            int semester = scanner.nextInt();
            viewStudentsBySemester(semester);
            break;
        case 2:
            System.out.print("Enter branch: ");
            String branch = scanner.next();
            System.out.print("Enter semester: ");
            semester = scanner.nextInt();
            viewStudentsBySemesterAndBranch(semester, branch);
            break;
        case 3:
            viewAllStudents();
            break;
        default:
            System.out.println("Invalid option!");
    }
}

private static void viewStudentsBySemester(int semester) {
    System.out.println("Students in semester " + semester + ":");
    System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    System.out.printf("%-30s %-40s %-20s %-20s %-20s %-20s %-20s\n",
            "Full Name", "Email", "ID", "Semester", "Roll Number", "Batch", "Branch");
    System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");

    for (User user : users.values()) {
        if (user.getRole().equals("student") && user.getCurrentSemester() == semester) {
            System.out.printf("%-30s %-40s %-20s %-20d %-20s %-20s %-20s\n",
                    user.getFullName(), user.getEmail(), user.getStudentId(), user.getCurrentSemester(),
                    user.getRollNo(), user.getBatch(), user.getBranch());
        }
    }
    System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");
}

private static void viewStudentsBySemesterAndBranch(int semester, String branch) {
    System.out.println("Students in semester " + semester + " and branch " + branch + ":");
    System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    System.out.printf("%-30s %-40s %-20s %-20s %-20s %-20s %-20s\n",
            "Full Name", "Email", "ID", "Semester", "Roll Number", "Batch", "Branch");
    System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");

    for (User user : users.values()) {
        if (user.getRole().equals("student") && user.getCurrentSemester() == semester && user.getBranch().equalsIgnoreCase(branch)) {
            System.out.printf("%-30s %-40s %-20s %-20d %-20s %-20s %-20s\n",
                    user.getFullName(), user.getEmail(), user.getStudentId(), user.getCurrentSemester(),
                    user.getRollNo(), user.getBatch(), user.getBranch());
        }
    }
    System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");
}

private static void viewAllStudents() {
    System.out.println("All students:");
    System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    System.out.printf("%-30s %-40s %-20s %-20s %-20s %-20s %-20s\n",
            "Full Name", "Email", "ID", "Semester", "Roll Number", "Batch", "Branch");
    System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");

    for (User user : users.values()) {
        if (user.getRole().equals("student")) {
            System.out.printf("%-30s %-40s %-20s %-20d %-20s %-20s %-20s\n",
                    user.getFullName(), user.getEmail(), user.getStudentId(), user.getCurrentSemester(),
                    user.getRollNo(), user.getBatch(), user.getBranch());
        }
    }

    System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");
}
private static void searchTeacherById(Scanner scanner) {
    System.out.print("Enter teacher ID to search: ");
    String searchId = scanner.nextLine();

    boolean found = false;
    for (User user : users.values()) {
        if (user.getRole().equals("teacher") && user.getTeacherId() != null && user.getTeacherId().equals(searchId)) {
            found = true;
            displayTeacherDetails(user);
            break;
        }
    }

    if (!found) {
        System.out.println("Teacher with ID " + searchId + " not found.");
    }
}

// public double getPercentage() {
//     if (grades == null || grades.isEmpty()) {
//         return 0.0;
//     }
//     int totalMarksObtained = grades.values().stream().mapToInt(Integer::intValue).sum();
//     int totalMarksPossible = grades.size() * 100; // Assuming each subject has a total of 100 marks
//     return (totalMarksObtained / (double) totalMarksPossible) * 100;
// }
// }
// private static void viewSortedStudentList() {
//     List<User> studentList = new ArrayList<>();
//     for (User user : users.values()) {
//         if (user.getRole().equals("student")) {
//             studentList.add(user);
//         }
//     }

//     // Sort students by their percentage in ascending order
//     studentList.sort(Comparator.comparingDouble(User::getPercentage));

//     System.out.println("Student list sorted by percentage (ascending order):");
//     System.out.printf("%-30s %-40s %-20s %-20s\n", "Full Name", "Email", "ID", "Percentage");
//     for (User student : studentList) {
//         System.out.printf("%-30s %-40s %-20s %-20.2f\n",
//                 student.getFullName(), student.getEmail(), student.getStudentId(), student.getPercentage());
//     }
// }

    private static void login(String role, Scanner scanner) {
        System.out.print("Enter your email: ");
        String email = scanner.nextLine().toLowerCase();
        if (!users.containsKey(email)) {
            System.out.println("User does not exist.");
            System.out.println();
            return;
        }
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        User user = users.get(email);
        if (user.getPassword().equals(password) && user.getRole().equals(role)) {
            System.out.println("Welcome, " + user.getFullName() + "!");

            // Additional actions for students after login
            if (role.equals("student")) {
                int choice;
                do {
                    System.out.println("1. Update details");
                    System.out.println("2. View grades");
                    System.out.println("3. View fees structure");
                    System.out.println("4. Update password");
                    System.out.println("5. Search Student");
                    System.out.println("6. View Courses");
                    System.out.println("7. Logout");
                    System.out.print("Choose an option: ");
                    choice = scanner.nextInt();
                    scanner.nextLine(); 
            
                    switch (choice) {
                        case 1:
                            // Call method to update details
                            updateDetails_student(user, scanner);
                            break;
                        case 2:
                            viewMarks(user);
                            // Call method to view grades
                            break;
                        case 3:
                        System.out.println("--------------------------------- Fees Structure for all Students ---------------------------------");
                        System.out.println();
                        System.out.printf("%-10s %-10s %-12s %-12s %-15s %-23s %-12s\n",
                                "Sr No. ", "Branch", "Tution Fees", "Exam Fess", "MOOC Course", "Digital Media ", "Total");
                        System.out.printf("%-10s %-11s %-11s %-12s %-13s %-23s %-20s\n",
                                "", "", " (A)", " (B)", "Fees(C)", "Subscription Fees(D)", "E=(A+B+C+D)\n");
                        System.out.printf("%-10s %-11s %-11s %-12s %-18s %-20s %-20s\n",
                                "1", "CE", "36500", "3000", "2000", "1000", "42500\n");
                        System.out.printf("%-10s %-11s %-11s %-12s %-18s %-20s %-20s\n",
                                "2", "CSD", "36500", "3000", "2000", "1000", "42500\n");
                        System.out.printf("%-10s %-11s %-11s %-12s %-18s %-20s %-20s\n",
                                "3", "CSE", "36500", "3000", "2000", "1000", "42500\n");
                        System.out.printf("%-10s %-11s %-11s %-12s %-18s %-20s %-20s\n",
                                "4", "IT", "36500", "3000", "2000", "1000", "42500\n");
                        System.out.printf("%-10s %-11s %-11s %-12s %-18s %-20s %-20s\n",
                                "5", "AIML", "36500", "3000", "2000", "1000", "42500\n");
                        System.out.printf("%-10s %-11s %-11s %-12s %-18s %-20s %-20s\n",
                                "6", "AIDS", "36500", "3000", "2000", "1000", "42500\n");
                        System.out.printf("%-10s %-11s %-11s %-12s %-18s %-20s %-20s\n",
                                "7", "RAI", "36500", "3000", "2000", "1000", "42500\n");
                        System.out.println();
                        System.out.println("The Last day of paying fees is 10-04-2024:");
                        System.out.println("Upto the Date of 20-04-2024        Rs.150/-");
                        System.out.println("Upto the Date of 30-04-2024        Rs.300/-");
                        System.out.println("Upto the Date of 13-05-2024        Rs.500/-");
                        System.out.println();
                        System.out.println("Bank Details for Fees Transfer");
                        System.out.println("Name : L.J. Institute of Engineering and Technology");
                        System.out.println("Bank Name : IDFC FIRST BANK");
                        System.out.println("Account Number : 10064630343");
                        System.out.println("Type of Account : SAVING");
                        System.out.println("IFSC CODE : IDFB0040303");
                        System.out.println("Bank Adderss : First Floor, Shop No. 2,3,4, Shivalik Building, Rambaug Cross Raod, Maninagar, Ahmedabad-380008");
                        break;
                            
                        case 4:
                            System.out.print("Enter your new password: ");
                            String newPassword = scanner.nextLine();
                            user.updatePassword(newPassword);
                            System.out.println("Password updated successfully.");
                            break;
                        case 5:
                            searchStudentById(scanner);
                            break;
                        case 6:
                        System.out.println("-------------------------------------------------- LJU SUBJECT DETAILS --------------------------------------------------");
                        System.out.println();
                            System.out.printf("%-16s %-20s %-20s %-16s %-12s %-18s %-12s\n",
                                    "   Branch ", "Subject Code", "Subject", "Subject Short ", "Credits", "Total Theory","Total practical");
                                    System.out.printf("%-16s %-20s %-24s %-12s %-15s %-20s %-12s\n",
                                    "", "", "", "Name ", "", "Marks","Marks");
                                    System.out.println();
                                    System.out.printf("%-16s %-13s %-32s %-14s %-13s %-18s %-20s\n",
                                    "CE/CSD/CSE/IT", "  017013591", "   Design and Analysis ", "DAA", "5", "100","100");
                                    System.out.printf("%-10s %-21s %-11s %-12s %-18s %-20s %-20s\n",
                                    "AIML/AIDS/RAI", "", "of Algorithms", "", "", "","\n");
                                    System.out.printf("%-16s %-16s %-29s %-14s %-13s %-18s %-20s\n",
                                    "CE/CSD/CSE/IT", "  017013592", "Software Engineering ", "SE", "4", "100","100");
                                    System.out.printf("%-10s %-21s %-11s %-12s %-18s %-20s %-20s\n",
                                    "AIML/AIDS/RAI", "", "", "", "", "","\n");
                                    System.out.printf("%-16s %-16s %-29s %-14s %-13s %-18s %-20s\n",
                                    "CE/CSD/CSE/IT", "  017013593", "Engineering Aptitude", "EA", "3", "100","00");
                                    System.out.printf("%-10s %-21s %-11s %-12s %-18s %-20s %-20s\n",
                                    "AIML/AIDS/RAI", "", "", "", "", "","\n");
                                    System.out.printf("%-16s %-16s %-29s %-14s %-13s %-18s %-20s\n",
                                    "CE/CSD/CSE/IT", "  017013594", "Technical English-1", "TE-1","1", "100","00");
                                    System.out.printf("%-10s %-21s %-11s %-12s %-18s %-20s %-20s\n",
                                    "AIML/AIDS/RAI", "", "", "", "", "","\n");
                                    System.out.printf("%-16s %-16s %-29s %-14s %-13s %-18s %-20s\n",
                                    "CE/CSD/CSE/IT", "  017013595", "Mini Project", "MP", "2", "00","100");
                                    System.out.printf("%-10s %-21s %-11s %-12s %-18s %-20s %-20s\n",
                                    "AIML/AIDS/RAI", "", "", "", "", "","\n");
                                    System.out.println();
                                    break;
                            
                        case 7:
                            break;
                        default:
                            System.out.println("Invalid choice.");
                    }
                } while (choice != 7);
            }
            
            else if (role.equals("teacher")) {
                int choice;
                do {
                    System.out.println();
                    System.out.println("1. Update details");
                    System.out.println("2. Give marks to students");
                    System.out.println("3. View student details");
                    System.out.println("4. Update passowrd");
                    System.out.println("5. Logout");
                    System.out.print("Choose an option: ");
                    choice = scanner.nextInt();
                    scanner.nextLine();
            
                    switch (choice) {
                        case 1:
                            updateDetails_teacher(user, scanner);
                            break;
                        case 2:
                            giveMarksToStudent();
                            break;
                        case 3:
                            searchStudentById(scanner);
                            break;
                        case 4:
                            System.out.print("Enter your new password: ");
                            String newPassword = scanner.nextLine();
                            user.updatePassword(newPassword);
                            System.out.println("Password updated successfully.");
                            break;                        
                        case 5:
                            System.out.println("Logging out...");
                            break;
                        default:
                            System.out.println("Invalid choice.");
                    }
                } while (choice != 6);
            }
             else {
                System.out.println("Incorrect email or password.");
            }
            System.out.println();
            }
        }
            
            private static void hodLogin(Scanner scanner) {
                // Default director credentials
                String hodEmail = "hod@ljku.edu.in";
                String hodPassword = "hod@123";
            
                System.out.print("Enter hod email: ");
                String email = scanner.nextLine().toLowerCase();
                System.out.print("Enter hod password: ");
                String password = scanner.nextLine();
            
                if (hodEmail.equals(email) && hodPassword.equals(password)) {
                    System.out.println("Welcome hod!");
                    // Director actions can be added here
                } else {
                    System.out.println("Invalid hod credentials.");
                }
            }

    // Define the User class here
    static class User {
        private String fullName;
        private String email;
        private String password;
        private String role;
        private int currentSemester;
        private String rollNo;
        private String batch;
        private String branch;
        private String studentId;
        private String teacherId; 
        private String department;
        private String contactNumber; 
        private String qualification;
        private HashMap<String, Integer> grades; 

        public User(String fullName, String password, String role) {
            this.fullName = fullName;
            this.password = password;
            this.role = role;
            generateEmail();
        }

        public User(String fullName, String password, String role, int currentSemester, String rollNo,
                    String batch, String branch, String studentId) {
            this.fullName = fullName;
            this.password = password;
            this.role = role;
            this.currentSemester = currentSemester;
            this.rollNo = rollNo;
            this.batch = batch;
            this.branch = branch;
            this.studentId = studentId;
            generateEmail();
        }
        public User(String fullName, String password, String role,String department,String contactNumber,String qualification,String teacherId) {
            this.fullName = fullName;
            this.password = password;
            this.role = role;
            this.department=department;
            this.contactNumber=contactNumber;
            this.qualification=qualification;
            this.teacherId=teacherId;
            generateEmail();
        }

        private void generateEmail() {
            String username = generateUsername(fullName);
            this.email = username + "@ljku.edu.in";
        }

        public String getEmail() {
            return email;
        }

        public String getStudentId() {
            return studentId;
        }

        public String getFullName() {
            return fullName;
        }

        public String getRole() {
            return role;
        }

        public int getCurrentSemester() {
            return currentSemester;
        }

        public String getRollNo() {
            return rollNo;
        }

        public String getBatch() {
            return batch;
        }

        public String getBranch() {
            return branch;
        }

        public String getPassword() {
            return password;
        }
        public void updatePassword(String newPassword) {
            this.password = newPassword;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public void setCurrentSemester(int currentSemester) {
            this.currentSemester = currentSemester;
        }

        public void setRollNo(String rollNo) {
            this.rollNo = rollNo;
        }

        public void setBatch(String batch) {
            this.batch = batch;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }
        public String getDepartment() {
            return department;
        }
    
        public void setDepartment(String department) {
            this.department = department;
        }
    
        public String getContactNumber() {
            return contactNumber;
        }
    
        public void setContactNumber(String contactNumber) {
            this.contactNumber = contactNumber;
        }
        public String getTeacherId() {
            return teacherId;
        }
    
        public void setTeacherId(String teacherId) {
            this.teacherId = teacherId;
        }
    
        public String getQualification() {
            return qualification;
        }
    
        public void setQualification(String qualification) {
            this.qualification = qualification;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public void setGrades(HashMap<String, Integer> grades) {
            this.grades = grades;
        }
    
        public HashMap<String, Integer> getGrades() {
            return grades;
        }
        public String toCsv() {
            return email + "," + fullName + "," + password + "," + role + "," + currentSemester + "," +
                    rollNo + "," + batch + "," + branch + "," + studentId;
        }
        public String teacherToCsv() {
            return email + "," + fullName + "," + password + "," + role + "," + department + "," +
                    (teacherId != null ? teacherId : "null");
        }
        
    }

 }
