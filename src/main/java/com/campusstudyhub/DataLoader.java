package com.campusstudyhub;

import com.campusstudyhub.entity.Room;
import com.campusstudyhub.entity.Semester;
import com.campusstudyhub.entity.Subject;
import com.campusstudyhub.entity.VideoLink;
import com.campusstudyhub.entity.User;
import com.campusstudyhub.repository.RoomRepository;
import com.campusstudyhub.repository.SemesterRepository;
import com.campusstudyhub.repository.SubjectRepository;
import com.campusstudyhub.repository.VideoLinkRepository;
import com.campusstudyhub.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Data loader to seed initial data on application startup.
 * 
 * Uses ApplicationReadyEvent to ensure schema is created before seeding.
 * 
 * Admin credentials are configured via application.properties:
 * - app.admin.email: Admin email address (default: admin@campus.com)
 * - app.admin.password: Admin password (MUST be changed before production!)
 * - app.admin.name: Admin display name
 */
@Component
@ConditionalOnProperty(name = "app.dataloader.enabled", havingValue = "true", matchIfMissing = true)
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final SemesterRepository semesterRepository;
    private final SubjectRepository subjectRepository;
    private final VideoLinkRepository videoLinkRepository;
    private final RoomRepository roomRepository;
    private final UserService userService;

    // Admin configuration from properties
    @Value("${app.admin.email:admin@campus.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.admin.name:Campus Admin}")
    private String adminName;

    public DataLoader(SemesterRepository semesterRepository,
            SubjectRepository subjectRepository,
            VideoLinkRepository videoLinkRepository,
            RoomRepository roomRepository,
            UserService userService) {
        this.semesterRepository = semesterRepository;
        this.subjectRepository = subjectRepository;
        this.videoLinkRepository = videoLinkRepository;
        this.roomRepository = roomRepository;
        this.userService = userService;
    }

    /**
     * Runs after the application is fully ready (schema created, beans
     * initialized).
     * This ensures Hibernate has created all tables before we try to access them.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void onApplicationReady() {
        log.info("Starting data seeding (ApplicationReadyEvent)...");

        // Create admin user using configured credentials
        createAdmin();

        // Create semesters and subjects only if not already present
        if (semesterRepository.count() == 0) {
            seedSemesters();
            log.info("Semesters and subjects seeded successfully!");
        } else {
            log.info("Semesters already exist, skipping seeding.");
        }

        // Seed rooms if not already present
        if (roomRepository.count() == 0) {
            seedRooms();
            log.info("Rooms seeded successfully!");
        } else {
            log.info("Rooms already exist, skipping seeding.");
        }

        log.info("Data seeding completed!");
    }

    private void createAdmin() {
        userService.createAdminIfNotPresent(adminEmail, adminPassword, adminName);
    }

    private void seedSemesters() {
        // Semester 1
        Semester sem1 = createSemester(1, "Semester 1");
        createSubject("Programming Fundamentals", "CS101", "Introduction to programming using C", sem1);
        createSubject("Mathematics I", "MA101", "Calculus and Linear Algebra", sem1);
        createSubject("Physics", "PH101", "Engineering Physics", sem1);
        createSubject("English Communication", "EN101", "Technical Communication Skills", sem1);

        // Semester 2
        Semester sem2 = createSemester(2, "Semester 2");
        Subject ds = createSubject("Data Structures", "CS201", "Arrays, Linked Lists, Trees, Graphs", sem2);
        createSubject("Object Oriented Programming", "CS202", "OOP concepts using Java/C++", sem2);
        createSubject("Mathematics II", "MA201", "Discrete Mathematics and Probability", sem2);
        createSubject("Digital Logic Design", "EC201", "Boolean Algebra, Logic Gates, Circuits", sem2);

        // Add sample video for Data Structures
        addSampleVideo(ds);

        // Semester 3
        Semester sem3 = createSemester(3, "Semester 3");
        Subject dbms = createSubject("Database Management Systems", "CS301", "SQL, Normalization, Transactions", sem3);
        createSubject("Operating Systems", "CS302", "Process Management, Memory, File Systems", sem3);
        createSubject("Computer Organization", "CS303", "CPU Architecture, Memory Hierarchy", sem3);
        createSubject("Design and Analysis of Algorithms", "CS304", "Algorithm complexity, Sorting, Graph algorithms",
                sem3);

        // Add sample video for DBMS
        addSampleVideoForDBMS(dbms);

        // Semester 4
        Semester sem4 = createSemester(4, "Semester 4");
        createSubject("Computer Networks", "CS401", "OSI Model, TCP/IP, Routing", sem4);
        createSubject("Software Engineering", "CS402", "SDLC, Agile, UML", sem4);
        createSubject("Theory of Computation", "CS403", "Automata, Regular Languages, Turing Machines", sem4);
        createSubject("Microprocessors", "CS404", "8086 Architecture, Assembly Programming", sem4);

        // Semester 5
        Semester sem5 = createSemester(5, "Semester 5");
        createSubject("Web Technologies", "CS501", "HTML, CSS, JavaScript, Web Frameworks", sem5);
        createSubject("Compiler Design", "CS502", "Lexical Analysis, Parsing, Code Generation", sem5);
        createSubject("Machine Learning", "CS503", "Supervised and Unsupervised Learning", sem5);
        createSubject("Information Security", "CS504", "Cryptography, Network Security", sem5);

        // Semester 6
        Semester sem6 = createSemester(6, "Semester 6");
        createSubject("Artificial Intelligence", "CS601", "Search, Knowledge Representation, NLP", sem6);
        createSubject("Cloud Computing", "CS602", "Virtualization, AWS, Azure", sem6);
        createSubject("Mobile App Development", "CS603", "Android/iOS Development", sem6);
        createSubject("Big Data Analytics", "CS604", "Hadoop, Spark, Data Mining", sem6);

        // Semester 7
        Semester sem7 = createSemester(7, "Semester 7");
        createSubject("Deep Learning", "CS701", "Neural Networks, CNN, RNN", sem7);
        createSubject("Blockchain Technology", "CS702", "Distributed Ledgers, Smart Contracts", sem7);
        createSubject("Internet of Things", "CS703", "Sensors, Embedded Systems, IoT Platforms", sem7);

        // Semester 8
        Semester sem8 = createSemester(8, "Semester 8");
        createSubject("Project Work", "CS801", "Final Year Project", sem8);
        createSubject("Professional Ethics", "HS801", "Ethics in Computing", sem8);
    }

    private Semester createSemester(int number, String name) {
        Semester semester = new Semester(number, name);
        return semesterRepository.save(semester);
    }

    private Subject createSubject(String name, String code, String description, Semester semester) {
        Subject subject = new Subject(name, code, description, semester);
        return subjectRepository.save(subject);
    }

    private void addSampleVideo(Subject subject) {
        User admin = userService.findByEmail(adminEmail)
                .orElse(null);

        if (admin != null) {
            VideoLink video = new VideoLink();
            video.setTitle("Introduction to Data Structures");
            video.setYoutubeUrl("https://www.youtube.com/watch?v=RBSGKlAvoiM");
            video.setDescription("Complete tutorial on Data Structures for beginners");
            video.setSubject(subject);
            video.setAddedBy(admin);
            videoLinkRepository.save(video);
            log.info("Sample video added for: {}", subject.getName());
        }
    }

    private void seedRooms() {
        Room room101 = new Room("Room 101", 30, "Main Block", "1", "101");
        room101.setResources("{\"projector\":true,\"whiteboard\":true,\"ac\":true}");
        roomRepository.save(room101);

        Room room102 = new Room("Room 102", 50, "Main Block", "1", "102");
        room102.setResources("{\"projector\":true,\"whiteboard\":true,\"ac\":true,\"smartBoard\":true}");
        roomRepository.save(room102);

        Room libraryRoom = new Room("Library Study Room", 10, "Library", "Ground", "LSR-1");
        libraryRoom.setResources("{\"whiteboard\":true,\"powerOutlets\":8}");
        roomRepository.save(libraryRoom);
    }

    private void addSampleVideoForDBMS(Subject subject) {
        User admin = userService.findByEmail(adminEmail)
                .orElse(null);

        if (admin != null) {
            VideoLink video = new VideoLink();
            video.setTitle("DBMS Complete Course");
            video.setYoutubeUrl("https://www.youtube.com/watch?v=IoL9Ve2SRwQ");
            video.setDescription("Database Management Systems full tutorial");
            video.setSubject(subject);
            video.setAddedBy(admin);
            videoLinkRepository.save(video);
            log.info("Sample video added for: {}", subject.getName());
        }
    }
}
