import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.toedter.calendar.JDateChooser;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StylishHealthcareGUI extends JFrame {
	private JTable table;
	private int loggedInDoctorId = -1; 
    private JComboBox<String> roleComboBox;
    private JLabel descriptionLabel;
    private JLabel roleIconLabel;
    private JPanel backgroundPanel;
    

    public StylishHealthcareGUI() {
        setTitle("Healthcare System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Background panel with image
        backgroundPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon bgImage = new ImageIcon("D:\\\\AOOP\\\\background.jpg");
                g.drawImage(bgImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        // Top navigation bar
        JPanel navBar = new JPanel();
        navBar.setBackground(new Color(0, 76, 153));
        navBar.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 10));

        String[] navItems = {"HOME", "ABOUT", "CONTACT"};
        for (String item : navItems) {
            JButton btn = new JButton(item);
            btn.setBackground(new Color(0, 102, 204));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(90, 35));

            btn.addActionListener(e -> {
                if (item.equals("ABOUT")) {
                    showAboutInfo();
                } else if (item.equals("HOME")) {
                    dispose();
                    new StylishHealthcareGUI(); // reload home
                } else if (item.equals("CONTACT")) {
                	showContactPage();
                }
            });

            navBar.add(btn);
        }

        backgroundPanel.add(navBar, BorderLayout.NORTH);

        // Main role selection card
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(new Color(255, 255, 255, 230));
        cardPanel.setPreferredSize(new Dimension(400, 300));
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("SELECT YOUR ROLE");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(title);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        roleComboBox = new JComboBox<>(new String[]{"-- Select Role --", "Admin", "Doctor", "Patient", "Staff"});
        roleComboBox.setMaximumSize(new Dimension(250, 30));
        roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardPanel.add(roleComboBox);
        roleComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        roleIconLabel = new JLabel(new ImageIcon("admin_icon.png")); // Default icon
        roleIconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(roleIconLabel);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        descriptionLabel = new JLabel("Admins manage user access and oversee the system.");
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(descriptionLabel);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton nextButton = new JButton("NEXT");
        nextButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nextButton.setBackground(new Color(30, 144, 255));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
        nextButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        nextButton.addActionListener(e -> {
            String selectedRole = (String) roleComboBox.getSelectedItem();

            if (selectedRole.equals("-- Select Role --")) {
                JOptionPane.showMessageDialog(this, "Please select a role.");
                return;
            }

            if ("Admin".equals(selectedRole)||  "Doctor".equals(selectedRole) ||"Staff".equals(selectedRole) || "Patient".equals(selectedRole)) {
                showAdminLogin(selectedRole);  // Pass role to login screen
            } else if ("check".equals(selectedRole)) {
                showPatientForm();
            }
        });
        cardPanel.add(nextButton);

        roleComboBox.addActionListener(e -> updateRoleInfo());
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(cardPanel);

        backgroundPanel.add(centerWrapper, BorderLayout.CENTER);
        add(backgroundPanel);
        setVisible(true);
    }

    private void updateRoleInfo() {
        String selected = (String) roleComboBox.getSelectedItem();
        switch (selected) {
            case "-- Select Role --":
            	descriptionLabel.setText("Please select your role from the dropdown.");
                roleIconLabel.setIcon(null); // Or a default/help icon if you want
                break;
            case "Doctor":
                descriptionLabel.setText("Doctors view and update patient health records.");
                roleIconLabel.setIcon(new ImageIcon("doctor_icon.png"));
                break;
            case "Patient":
                descriptionLabel.setText("Patients can view their health data and book appointments.");
                roleIconLabel.setIcon(new ImageIcon("patient_icon.png"));
                break;
            case "Staff":
            	descriptionLabel.setText("Staff assist in daily hospital operations.");
            	break;
            case "Admin":
                descriptionLabel.setText("Admins manage user access and oversee the system.");
                roleIconLabel.setIcon(new ImageIcon("admin_icon.png"));
                break;
           default:
                descriptionLabel.setText("Please select your role from the dropdown.");
                roleIconLabel.setIcon(null); // Or a default/help icon if you want
                break;
        }
    }
    private void showPatientForm() {
        JFrame frame = new JFrame("Book Appointment");
        frame.setSize(500, 600);
        frame.setLocationRelativeTo(this);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(11, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(230, 245, 255));

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Color labelColor = new Color(0, 51, 102);

        // Doctor selection
        JLabel doctorLabel = new JLabel("Select Doctor:");
        doctorLabel.setFont(labelFont); doctorLabel.setForeground(labelColor);
        JComboBox<String> doctorBox = new JComboBox<>();
        Map<String, Integer> doctorMap = new HashMap<>(); // doctor name -> ID

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");

      //      Map<String, Integer> doctorMap = new HashMap<>();
            PreparedStatement stmt = conn.prepareStatement("SELECT id, name, specialization FROM doctors");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String specialization = rs.getString("specialization");

                String displayName = name + " - " + specialization;
                doctorBox.addItem(displayName);
                doctorMap.put(displayName, id);
            }

            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            doctorBox.addItem("Error loading doctors");
        }

        // Problem input
        JLabel problemLabel = new JLabel("Problem:");
        problemLabel.setFont(labelFont); problemLabel.setForeground(labelColor);
        JTextField problemField = new JTextField();

        // Patient info fields
        JLabel nameLabel = new JLabel("Name:"); nameLabel.setFont(labelFont); nameLabel.setForeground(labelColor);
        JTextField nameField = new JTextField();

        JLabel ageLabel = new JLabel("Age:"); ageLabel.setFont(labelFont); ageLabel.setForeground(labelColor);
        JTextField ageField = new JTextField();

        JLabel genderLabel = new JLabel("Gender:"); genderLabel.setFont(labelFont); genderLabel.setForeground(labelColor);
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});

        JLabel phoneLabel = new JLabel("Phone:"); phoneLabel.setFont(labelFont); phoneLabel.setForeground(labelColor);
        JTextField phoneField = new JTextField();

        JLabel emailLabel = new JLabel("Email:"); emailLabel.setFont(labelFont); emailLabel.setForeground(labelColor);
        JTextField emailField = new JTextField();

        JLabel dobLabel = new JLabel("Date of Birth:");
        dobLabel.setFont(labelFont); dobLabel.setForeground(labelColor);
        JDateChooser dobChooser = new JDateChooser();
        dobChooser.setDateFormatString("yyyy-MM-dd");
        dobChooser.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton submitBtn = new JButton("SUBMIT");
        submitBtn.setBackground(new Color(0, 102, 204));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton backBtn = new JButton("BACK");
        backBtn.setBackground(new Color(192, 57, 43));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Add components
        panel.add(doctorLabel); panel.add(doctorBox);
        panel.add(problemLabel); panel.add(problemField);
        panel.add(nameLabel); panel.add(nameField);
        panel.add(ageLabel); panel.add(ageField);
        panel.add(genderLabel); panel.add(genderBox);
        panel.add(phoneLabel); panel.add(phoneField);
        panel.add(emailLabel); panel.add(emailField);
        panel.add(dobLabel); panel.add(dobChooser);
        panel.add(submitBtn); panel.add(backBtn);

        // Submit action
        submitBtn.addActionListener(e -> {
            try {
                String doctorName = (String) doctorBox.getSelectedItem();
                Integer doctorId = doctorMap.get(doctorName);
                String problem = problemField.getText();
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                String gender = (String) genderBox.getSelectedItem();
                String phone = phoneField.getText();
                String email = emailField.getText();
                Date dob = dobChooser.getDate();
                Date today = new Date();

                if (problem.isEmpty() || name.isEmpty()  || phone.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill all fields.");
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dobStr = sdf.format(dob);
                String todayStr = sdf.format(today);

                Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");

                String sql = "INSERT INTO appointments (patient_name, age, gender, phone, email, dob, problem, doctor_id, booking_date) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setInt(2, age);
                stmt.setString(3, gender);
                stmt.setString(4, phone);
                stmt.setString(5, email);
                stmt.setString(6, dobStr);
                stmt.setString(7, problem);
                stmt.setInt(8, doctorId);
                stmt.setString(9, todayStr);

                stmt.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(frame, "Appointment booked successfully!");
                frame.dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error saving appointment.");
            }
        });

        backBtn.addActionListener(e -> frame.dispose());

        frame.add(panel);
        frame.setVisible(true);
    }
    
    private void showAdminLogin(String role) {
        JFrame loginFrame = new JFrame(role + " Login");
        loginFrame.setSize(400, 350);
        loginFrame.setLocationRelativeTo(this);
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(0, 128, 192));

        JLabel title = new JLabel(role.toUpperCase() + " LOGIN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBounds(130, 20, 200, 30);
        title.setForeground(Color.WHITE);
        panel.add(title);

        JLabel userLabel = new JLabel("User ID:");
        userLabel.setBounds(50, 70, 100, 25);
        userLabel.setForeground(Color.WHITE);
        panel.add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(150, 70, 180, 25);
        panel.add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 110, 100, 25);
        passLabel.setForeground(Color.WHITE);
        panel.add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(150, 110, 180, 25);
        panel.add(passField);

        JLabel warningLabel = new JLabel();
        warningLabel.setBounds(100, 140, 250, 20);
        warningLabel.setForeground(Color.RED);
        panel.add(warningLabel);

        JButton submitBtn = new JButton("SUBMIT");
        submitBtn.setBounds(150, 170, 90, 30);
        panel.add(submitBtn);

        JButton backBtn = new JButton("BACK");
        backBtn.setBounds(250, 170, 80, 30);
        panel.add(backBtn);

        JButton forgotBtn = new JButton("Forgot Password?");
        forgotBtn.setBounds(130, 210, 150, 25);
        forgotBtn.setFocusPainted(false);
        panel.add(forgotBtn);
        
     // --- NEW: Sign Up Button ---
        JButton signupBtn = new JButton("Sign Up");
        signupBtn.setBounds(130, 240, 150, 25);
        signupBtn.setFocusPainted(false);
        panel.add(signupBtn);

        // LOGIN LOGIC
        submitBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                warningLabel.setText("User ID and Password are required!");
            } else {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");

                    String sql = "SELECT * FROM users WHERE user_id = ? AND password = ? AND role = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, user);
                    stmt.setString(2, pass);
                    stmt.setString(3, role);

                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        JOptionPane.showMessageDialog(loginFrame, "Login successful..!");
                        loginFrame.dispose();

                        if (role.equals("Admin")) showAdminDashboard();
                       // else if (role.equals("Doctor")) showDoctorDashboard();
                        else if (role.equals("Doctor")) {
                            loggedInDoctorId = getDoctorIdFromUserId(user);  // user is the email
                            showDoctorDashboard();
                        }
                        else if (role.equals("Staff")) showStaffDashboard();
                        else if (role.equals("Patient")) {
                            showPatientDashboard(user); // Pass user email (user_id)
                        }
                    } else {
                        warningLabel.setText("Invalid credentials or role mismatch!");
                    }

                    conn.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    warningLabel.setText("Database error!");
                }
            }
        });

        // FORGOT PASSWORD
        forgotBtn.addActionListener(e -> {
            JFrame resetFrame = new JFrame("Reset Password");
            resetFrame.setSize(400, 250);
            resetFrame.setLocationRelativeTo(loginFrame);
            resetFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel resetPanel = new JPanel(null);
            resetPanel.setBackground(new Color(204, 229, 255));

            JLabel userIdLabel = new JLabel("User ID:");
            userIdLabel.setBounds(50, 30, 100, 25);
            resetPanel.add(userIdLabel);

            JTextField userIdField = new JTextField();
            userIdField.setBounds(150, 30, 180, 25);
            resetPanel.add(userIdField);

            JLabel newPassLabel = new JLabel("New Password:");
            newPassLabel.setBounds(50, 70, 100, 25);
            resetPanel.add(newPassLabel);

            JPasswordField newPassField = new JPasswordField();
            newPassField.setBounds(150, 70, 180, 25);
            resetPanel.add(newPassField);

            JButton resetBtn = new JButton("RESET");
            resetBtn.setBounds(150, 120, 100, 30);
            resetPanel.add(resetBtn);

            resetBtn.addActionListener(ev -> {
                String userId = userIdField.getText();
                String newPass = new String(newPassField.getPassword());

                if (userId.isEmpty() || newPass.isEmpty()) {
                    JOptionPane.showMessageDialog(resetFrame, "Please fill all fields.");
                    return;
                }

                try {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");

                    String sql = "UPDATE users SET password = ? WHERE user_id = ? AND role = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, newPass);
                    stmt.setString(2, userId);
                    stmt.setString(3, role);

                    int updated = stmt.executeUpdate();
                    conn.close();

                    if (updated > 0) {
                        JOptionPane.showMessageDialog(resetFrame, "Password reset successfully!");
                        resetFrame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(resetFrame, "User not found or role mismatch.");
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(resetFrame, "Error while resetting password.");
                }
            });

            resetFrame.add(resetPanel);
            resetPanel.setLayout(null);
            resetFrame.setVisible(true);
        });
        
        // Sign Up Button Action
        signupBtn.addActionListener(e -> {
            if (role.equals("Patient")) {
                showPatientSignupForm();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Sign Up is only available for Patients.");
            }
        });

        backBtn.addActionListener(e -> loginFrame.dispose());

        loginFrame.add(panel);
        loginFrame.setVisible(true);
    
}
    private int getDoctorIdFromUserId(String userId) {
        int doctorId = -1;
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/hms", "root", "Rowdy@08");
            String sql = "SELECT id FROM doctors WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            //stmt.setString(1, userEmail);
            //ResultSet rs = stmt.executeQuery();
            stmt.setString(1, userId + "@hospital.com");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                doctorId = rs.getInt("id");
            }
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return doctorId;
    }
    private void showPatientDashboard(String patientEmail) {
        JFrame dashboard = new JFrame("Patient Dashboard");
        dashboard.setSize(600, 350);
        dashboard.setLocationRelativeTo(null);
        dashboard.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(224, 255, 255)); // Light cyan

        String[] options = {"Book Appointment", "View My Prescriptions", "Help", "Logout"};

        for (String option : options) {
            JButton btn = new JButton(option);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setBackground(option.equals("Logout") ? new Color(255, 102, 102) : new Color(0, 153, 204));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);

            if (option.equals("Logout")) {
                btn.addActionListener(e -> dashboard.dispose());
            } else if (option.equals("Book Appointment")) {
                btn.addActionListener(e -> showPatientForm()); // Already exists
            } else if (option.equals("View My Prescriptions")) {
                btn.addActionListener(e -> showPatientPrescriptions(patientEmail));
            } else if (option.equals("Help")) {
                JOptionPane.showMessageDialog(dashboard,
                    "Need help?\nContact support: help@hospital.com");
            }

            panel.add(btn);
        }

        dashboard.add(panel);
        dashboard.setVisible(true);
    }
    private void showPatientPrescriptions(String email) {
        JFrame frame = new JFrame("My Prescriptions");
        frame.setSize(750, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] cols = {"Doctor", "Problem", "Prescription", "Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");

            String sql = "SELECT d.name AS doctor_name, a.problem, p.prescription_text, p.prescribed_on " +
                         "FROM prescriptions p " +
                         "JOIN appointments a ON p.appointment_id = a.id " +
                         "JOIN doctors d ON a.doctor_id = d.id " +
                         "WHERE a.email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("doctor_name"),
                    rs.getString("problem"),
                    rs.getString("prescription_text"),
                    rs.getDate("prescribed_on")
                });
            }

            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading prescriptions.");
        }

        frame.add(scroll);
        frame.setVisible(true);
    }
    private void showAdminDashboard() {
        JFrame dashboard = new JFrame("Admin Dashboard");
        dashboard.setSize(900, 400);
        dashboard.setLocationRelativeTo(null);
        dashboard.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel backgroundPanel = new JPanel(new BorderLayout());
        backgroundPanel.setBackground(new Color(0, 76, 153)); // Deep blue background

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 3, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        panel.setBackground(new Color(0, 76, 153)); // Match outer background

        String[] buttons = {
            "Manage Doctors",
            "Manage Staff",
            "Manage Patients",
            "Appointments",
            "Reports",
            "Logout"
        };

        Color mainButtonColor = new Color(0, 153, 204);      // Sky blue
        Color logoutButtonColor = new Color(231, 76, 60);     // Red

        for (int i = 0; i < buttons.length; i++) {
            JButton btn = new JButton(buttons[i]);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btn.setBackground(buttons[i].equals("Logout") ? logoutButtonColor : mainButtonColor);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            panel.add(btn);

            final int index = i;

            if (buttons[i].equals("Logout")) {
                btn.addActionListener(e -> dashboard.dispose());
            } else if (buttons[i].equals("Manage Doctors")) {
                btn.addActionListener(e -> showDoctorTable());
            } else if (buttons[i].equals("Manage Staff")) {
                btn.addActionListener(e -> showStaffTable());
            } else if (buttons[i].equals("Manage Patients")) {
                btn.addActionListener(e -> showPatientTable());
            } else if (buttons[i].equals("Appointments")) {
               btn.addActionListener(e -> showAppointmentsTable());
            }
            else if (buttons[i].equals("Reports")) {
                btn.addActionListener(e -> showReports());
            }
            
        }

        backgroundPanel.add(panel, BorderLayout.CENTER);
        dashboard.setContentPane(backgroundPanel);
        dashboard.setVisible(true);
    }
    
    private void showAppointmentsTable() {
        JFrame frame = new JFrame("All Appointments");
        frame.setSize(1000, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] columns = {"ID", "Patient Name", "Age", "Gender", "Phone", "Email", "DOB", "Problem", "Doctor Name", "Booking Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");

            String sql = "SELECT a.id, a.patient_name, a.age, a.gender, a.phone, a.email, a.dob, a.problem, d.name AS doctor_name, a.booking_date " +
                         "FROM appointments a JOIN doctors d ON a.doctor_id = d.id";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("patient_name"),
                    rs.getInt("age"),
                    rs.getString("gender"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getDate("dob"),
                    rs.getString("problem"),
                    rs.getString("doctor_name"),
                    rs.getDate("booking_date")
                });
            }

            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading appointments.");
        }

        frame.add(scrollPane);
        frame.setVisible(true);
    }
    
    private void showReports() {
        JFrame frame = new JFrame("Healthcare Reports");
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();

        tabs.add("Patients per Day", getPatientsPerDayPanel());
        tabs.add("Doctor-wise Count", getDoctorWiseReportPanel());
        tabs.add("Gender-wise Count", getGenderWiseReportPanel());
        tabs.add("Monthly Summary", getMonthlySummaryPanel());
        tabs.add("Filter by Date", getDateRangeReportPanel());

        frame.add(tabs);
        frame.setVisible(true);
    }
    private JPanel getPatientsPerDayPanel() {
        String[] columns = {"Date", "No. of Patients"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");
            String sql = "SELECT booking_date, COUNT(*) AS total FROM appointments GROUP BY booking_date ORDER BY booking_date DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getDate("booking_date"),
                    rs.getInt("total")
                });
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JPanel(new BorderLayout()) {{ add(new JScrollPane(table), BorderLayout.CENTER); }};
    }
    private JPanel getDoctorWiseReportPanel() {
        String[] columns = {"Doctor Name", "Total Patients"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");
            String sql = "SELECT d.name AS doctor_name, COUNT(a.id) AS total_patients FROM appointments a " +
                         "JOIN doctors d ON a.doctor_id = d.id GROUP BY a.doctor_id ORDER BY total_patients DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("doctor_name"),
                    rs.getInt("total_patients")
                });
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JPanel(new BorderLayout()) {{ add(new JScrollPane(table), BorderLayout.CENTER); }};
    }
    private JPanel getGenderWiseReportPanel() {
        String[] columns = {"Gender", "Total Patients"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");
            String sql = "SELECT gender, COUNT(*) AS total FROM appointments GROUP BY gender";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("gender"),
                    rs.getInt("total")
                });
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JPanel(new BorderLayout()) {{ add(new JScrollPane(table), BorderLayout.CENTER); }};
    }
    private JPanel getMonthlySummaryPanel() {
        String[] columns = {"Month", "Total Appointments"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");
            String sql = "SELECT DATE_FORMAT(booking_date, '%Y-%m') AS month, COUNT(*) AS total " +
                         "FROM appointments GROUP BY month ORDER BY month DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("month"),
                    rs.getInt("total")
                });
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JPanel(new BorderLayout()) {{ add(new JScrollPane(table), BorderLayout.CENTER); }};
    }
    private JPanel getDateRangeReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout());

        JLabel fromLabel = new JLabel("From:");
        JDateChooser fromDate = new JDateChooser();
        fromDate.setDateFormatString("yyyy-MM-dd");

        JLabel toLabel = new JLabel("To:");
        JDateChooser toDate = new JDateChooser();
        toDate.setDateFormatString("yyyy-MM-dd");

        JButton filterBtn = new JButton("Filter");

        String[] columns = {"Date", "Patients"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        filterBtn.addActionListener(e -> {
            model.setRowCount(0);
            Date from = fromDate.getDate();
            Date to = toDate.getDate();

            if (from == null || to == null) {
                JOptionPane.showMessageDialog(panel, "Please select both dates.");
                return;
            }

            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");
                String sql = "SELECT booking_date, COUNT(*) AS total FROM appointments WHERE booking_date BETWEEN ? AND ? GROUP BY booking_date";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setDate(1, new java.sql.Date(from.getTime()));
                stmt.setDate(2, new java.sql.Date(to.getTime()));
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getDate("booking_date"),
                        rs.getInt("total")
                    });
                }
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        top.add(fromLabel); top.add(fromDate);
        top.add(toLabel); top.add(toDate);
        top.add(filterBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }
    
    private void showDoctorTable() {
        JFrame frame = new JFrame("Manage Doctors");
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);

        // Add button to Add a new doctor
        JButton addDoctorBtn = new JButton("Add Doctor");
        addDoctorBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addDoctorBtn.setBackground(new Color(0, 102, 204));
        addDoctorBtn.setForeground(Color.WHITE);
        addDoctorBtn.setFocusPainted(false);
        addDoctorBtn.setPreferredSize(new Dimension(120, 40));
        
        addDoctorBtn.addActionListener(e -> showAddDoctorForm(frame)); // Open Add Doctor Form
        
        // Create table model
        String[] columns = {"ID", "Name", "Specialization", "Phone", "Email", "Edit", "Delete"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");
            String sql = "SELECT * FROM doctors";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("specialization"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    "Edit",
                    "Delete"
                });
            }
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading doctor data.");
        }

        // Add Edit/Delete buttons
        table.getColumn("Edit").setCellRenderer(new ButtonRenderer());
        table.getColumn("Edit").setCellEditor(new ButtonEditor(new JCheckBox(), "Edit", "doctor"));

        table.getColumn("Delete").setCellRenderer(new ButtonRenderer());
        table.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), "Delete", "doctor"));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(addDoctorBtn, BorderLayout.NORTH);  // Add button to the panel
        panel.add(scrollPane, BorderLayout.CENTER);   // Add the table to the panel

        frame.add(panel);
        frame.setVisible(true);
    }
    
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private String actionType;
        private String tableType;
        private JTable tableRef;
        private JButton button;

        public ButtonEditor(JCheckBox checkBox, String actionType, String tableType) {
            super(checkBox);
            this.actionType = actionType;
            this.tableType = tableType;
            button = new JButton();
            button.setOpaque(true);

            button.addActionListener(e -> {
                int row = tableRef.getSelectedRow();
                if (actionType.equals("Edit")) {
                    if (tableType.equals("doctor")) {
                        editDoctor(row);
                    } else if (tableType.equals("staff")) {
                        editStaff(row);
                    }
                } else if (actionType.equals("Delete")) {
                    if (tableType.equals("doctor")) {
                        deleteDoctor(row);
                    } else if (tableType.equals("staff")) {
                        deleteStaff(row);
                    }
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.tableRef = table;
            button.setText(actionType);
            return button;
        }

        public Object getCellEditorValue() {
            return actionType;
        }
    
    
        private void editDoctor(int row) {
            int doctorId = (int) table.getValueAt(row, 0); // Doctor ID

            JFrame frame = new JFrame("Edit Doctor");
            frame.setSize(500, 400);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
            panel.setBackground(new Color(230, 245, 255));

            // Fields
            JTextField nameField = new JTextField();
            JTextField specializationField = new JTextField();
            JTextField phoneField = new JTextField();
            JTextField emailField = new JTextField();

            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");
                String sql = "SELECT * FROM doctors WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, doctorId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    nameField.setText(rs.getString("name"));
                    specializationField.setText(rs.getString("specialization"));
                    phoneField.setText(rs.getString("phone"));
                    emailField.setText(rs.getString("email"));
                }
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error loading doctor data.");
            }

            panel.add(new JLabel("Name:")); panel.add(nameField);
            panel.add(new JLabel("Specialization:")); panel.add(specializationField);
            panel.add(new JLabel("Phone:")); panel.add(phoneField);
            panel.add(new JLabel("Email:")); panel.add(emailField);

            JButton submitBtn = new JButton("Update");
            submitBtn.setBackground(new Color(0, 102, 204));
            submitBtn.setForeground(Color.WHITE);

            submitBtn.addActionListener(e -> {
                String name = nameField.getText();
                String specialization = specializationField.getText();
                String phone = phoneField.getText();
                String email = emailField.getText();

                try {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");
                    String sql = "UPDATE doctors SET name = ?, specialization = ?, phone = ?, email = ? WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, name);
                    stmt.setString(2, specialization);
                    stmt.setString(3, phone);
                    stmt.setString(4, email);
                    stmt.setInt(5, doctorId);

                    stmt.executeUpdate();
                    conn.close();

                    JOptionPane.showMessageDialog(frame, "Doctor updated successfully!");
                    frame.dispose();
                    showDoctorTable(); // Refresh
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Update failed.");
                }
            });

            panel.add(submitBtn);
            frame.add(panel);
            frame.setVisible(true);
        }

        private void deleteDoctor(int row) {
            int doctorId = (int) table.getValueAt(row, 0); // Doctor ID
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this doctor?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");
                    String sql = "DELETE FROM doctors WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, doctorId);
                    stmt.executeUpdate();
                    conn.close();

                    JOptionPane.showMessageDialog(null, "Doctor deleted successfully.");
                    showDoctorTable(); // Refresh
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error deleting doctor.");
                }
            }
        }
    }
    
    private void showAddDoctorForm(JFrame parentFrame) {
        JFrame frame = new JFrame("Add Doctor");
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(parentFrame);  // Position relative to parent frame
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(230, 245, 255));

        // Doctor name, specialization, phone, email
        panel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Specialization:"));
        JTextField specializationField = new JTextField();
        panel.add(specializationField);

        panel.add(new JLabel("Phone:"));
        JTextField phoneField = new JTextField();
        panel.add(phoneField);

        panel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        panel.add(emailField);

        JButton submitBtn = new JButton("SUBMIT");
        submitBtn.setBackground(new Color(0, 102, 204));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        submitBtn.addActionListener(e -> {
            String name = nameField.getText();
            String specialization = specializationField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();

            if (name.isEmpty()||specialization.isEmpty()||phone.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields.");
                return;
            }

            try {
                // Insert new doctor into the database
                Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");
                String sql = "INSERT INTO doctors (name, specialization, phone, email) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, specialization);
                stmt.setString(3, phone);
                stmt.setString(4, email);

                stmt.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(frame, "Doctor added successfully!");
                frame.dispose();
                showDoctorTable(); // Refresh the doctor table
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error saving doctor.");
            }
        });

        panel.add(submitBtn);
        frame.add(panel);
        frame.setVisible(true);
    }
    
    
    private void showStaffTable() {
        JFrame frame = new JFrame("Manage Staff");
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);

        JButton addStaffBtn = new JButton("Add Staff");
        addStaffBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addStaffBtn.setBackground(new Color(0, 102, 204));
        addStaffBtn.setForeground(Color.WHITE);
        addStaffBtn.setFocusPainted(false);
        addStaffBtn.setPreferredSize(new Dimension(120, 40));

        addStaffBtn.addActionListener(e -> showAddStaffForm(frame));

        String[] columns = {"ID", "Name", "Position", "Department", "Phone", "Email", "Edit", "Delete"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");
            String sql = "SELECT * FROM staff";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("position"),
                    rs.getString("department"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    "Edit",
                    "Delete"
                });
            }
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading staff data.");
        }
        
        table.getColumn("Edit").setCellRenderer(new ButtonRenderer());
        table.getColumn("Edit").setCellEditor(new ButtonEditor(new JCheckBox(), "Edit", "staff"));

        table.getColumn("Delete").setCellRenderer(new ButtonRenderer());
        table.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), "Delete", "staff"));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(addStaffBtn, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        frame.add(panel);
        frame.setVisible(true);
    }
    
    private void showAddStaffForm(JFrame parent) {
        JFrame frame = new JFrame("Add New Staff");
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JTextField nameField = new JTextField();
        JTextField positionField = new JTextField();
        JTextField deptField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();

        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Position:")); panel.add(positionField);
        panel.add(new JLabel("Department:")); panel.add(deptField);
        panel.add(new JLabel("Phone:")); panel.add(phoneField);
        panel.add(new JLabel("Email:")); panel.add(emailField);

        JButton submitBtn = new JButton("SUBMIT");
        submitBtn.addActionListener(e -> {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");
                String sql = "INSERT INTO staff (name, position, department, phone, email) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nameField.getText());
                stmt.setString(2, positionField.getText());
                stmt.setString(3, deptField.getText());
                stmt.setString(4, phoneField.getText());
                stmt.setString(5, emailField.getText());
                stmt.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(frame, "Staff added successfully!");
                frame.dispose();
                showStaffTable();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error saving staff.");
            }
        });

        panel.add(submitBtn);
        frame.add(panel);
        frame.setVisible(true);
    }
    
    private void editStaff(int row) {
        int staffId = (int) table.getValueAt(row, 0);

        JFrame frame = new JFrame("Edit Staff");
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JTextField nameField = new JTextField();
        JTextField positionField = new JTextField();
        JTextField deptField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM staff WHERE id = ?");
            stmt.setInt(1, staffId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                positionField.setText(rs.getString("position"));
                deptField.setText(rs.getString("department"));
                phoneField.setText(rs.getString("phone"));
                emailField.setText(rs.getString("email"));
            }
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading data.");
        }

        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Position:")); panel.add(positionField);
        panel.add(new JLabel("Department:")); panel.add(deptField);
        panel.add(new JLabel("Phone:")); panel.add(phoneField);
        panel.add(new JLabel("Email:")); panel.add(emailField);

        JButton updateBtn = new JButton("UPDATE");
        updateBtn.addActionListener(e -> {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");
                PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE staff SET name=?, position=?, department=?, phone=?, email=? WHERE id=?");
                stmt.setString(1, nameField.getText());
                stmt.setString(2, positionField.getText());
                stmt.setString(3, deptField.getText());
                stmt.setString(4, phoneField.getText());
                stmt.setString(5, emailField.getText());
                stmt.setInt(6, staffId);
                stmt.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(frame, "Staff updated successfully!");
                frame.dispose();
                showStaffTable();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error updating staff.");
            }
        });

        panel.add(updateBtn);
        frame.add(panel);
        frame.setVisible(true);
    }
    private void deleteStaff(int row) {
        int staffId = (int) table.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(null, "Delete this staff member?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM staff WHERE id = ?");
                stmt.setInt(1, staffId);
                stmt.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(null, "Staff deleted.");
                showStaffTable();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting staff.");
            }
        }
    }
    
    private void showPatientTable() {
        JFrame frame = new JFrame("Manage Patients");
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);

        // Table columns
        String[] columns = {"ID", "Name", "Age", "Gender", "Phone", "Email", "Doctor", "Problem", "Booking Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Fetch patients from DB and populate the table
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");
            String sql = "SELECT a.id, a.patient_name, a.age, a.gender, a.phone, a.email, a.dob, a.problem, d.name AS doctor_name, a.booking_date " +
                    "FROM appointments a JOIN doctors d ON a.doctor_id = d.id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("patient_name"),
                    rs.getInt("age"),
                    rs.getString("gender"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("doctor_name"),
                    rs.getString("problem"),
                    rs.getDate("booking_date"),
                });
            }
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading patient data.");
        }

        // Add Edit/Delete buttons in the Actions column
        //table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        //table.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox()));

        frame.add(scrollPane);
        frame.setVisible(true);
    }
    
    private void showStaffDashboard() {
        JFrame dashboard = new JFrame("Staff Dashboard");
        dashboard.setSize(900, 400);
        dashboard.setLocationRelativeTo(null);
        dashboard.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel backgroundPanel = new JPanel(new BorderLayout());
        backgroundPanel.setBackground(new Color(51, 102, 153)); // Blue-gray background

        JPanel panel = new JPanel(new GridLayout(2, 3, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        panel.setBackground(new Color(51, 102, 153)); // Match background

        String[] buttons = {
            "Check Schedules",
            "Assist Doctors",
            "Manage Supplies",
            "Update Records",
            "Help Desk",
            "Logout"
        };

        Color mainColor = new Color(0, 153, 255);       // Light blue
        Color logoutColor = new Color(192, 57, 43);     // Red

        for (int i = 0; i < buttons.length; i++) {
            JButton btn = new JButton(buttons[i]);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btn.setBackground(buttons[i].equals("Logout") ? logoutColor : mainColor);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            panel.add(btn);

            final int index = i;

            if (buttons[i].equals("Logout")) {
                btn.addActionListener(e -> dashboard.dispose());
            } else {
                btn.addActionListener(e ->
                    JOptionPane.showMessageDialog(dashboard, buttons[index] + " (feature coming soon!)"));
            }
        }

        backgroundPanel.add(panel, BorderLayout.CENTER);
        dashboard.setContentPane(backgroundPanel);
        dashboard.setVisible(true);
    }
    
    private void showDoctorDashboard() {
        JFrame dashboard = new JFrame("Doctor Dashboard");
        dashboard.setSize(900, 400);
        dashboard.setLocationRelativeTo(null);
        dashboard.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel backgroundPanel = new JPanel(new BorderLayout());
        backgroundPanel.setBackground(new Color(0, 102, 102)); // Teal background

        JPanel panel = new JPanel(new GridLayout(2, 3, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        panel.setBackground(new Color(0, 102, 102));

        String[] buttons = {
            "My Appointments",
            "Patient History",
            "Write Prescription",
            "My Profile",
            "Help",
            "Logout"
        };

        Color mainColor = new Color(0, 153, 153);      // Light teal
        Color logoutColor = new Color(192, 57, 43);    // Red

        for (int i = 0; i < buttons.length; i++) {
            JButton btn = new JButton(buttons[i]);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btn.setBackground(buttons[i].equals("Logout") ? logoutColor : mainColor);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            panel.add(btn);

            final int index = i;

            switch (buttons[i]) {
                case "Logout":
                    btn.addActionListener(e -> dashboard.dispose());
                    break;
                case "My Appointments":
                    btn.addActionListener(e -> showDoctorAppointments(loggedInDoctorId));
                    break;
                case "My Profile":
                    btn.addActionListener(e -> showDoctorProfile(loggedInDoctorId));
                    System.out.println(loggedInDoctorId);
                    break;
                case "Help":
                    btn.addActionListener(e -> showDoctorHelp());
                    break;
                case "Write Prescription":
                    btn.addActionListener(e -> showWritePrescription(loggedInDoctorId));
                    break;
                case "Patient History":
                    btn.addActionListener(e -> showPatientHistory(loggedInDoctorId));
                    break;
                default:
                    btn.addActionListener(e -> JOptionPane.showMessageDialog(dashboard, buttons[index] + " (feature coming soon!)"));
                    break;
            }
        }

        backgroundPanel.add(panel, BorderLayout.CENTER);
        dashboard.setContentPane(backgroundPanel);
        dashboard.setVisible(true);
    }
    private void showWritePrescription(int doctorId) {
        JFrame frame = new JFrame("Write Prescription");
        frame.setSize(600, 450);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(245, 255, 250));

        JComboBox<String> appointmentBox = new JComboBox<>();
        Map<String, Integer> appointmentMap = new HashMap<>();

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/hms", "root", "Rowdy@08");
            String sql = "SELECT id, patient_name FROM appointments WHERE doctor_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String patient = rs.getString("patient_name");
                String display = "ID: " + id + " - " + patient;
                appointmentBox.addItem(display);
                appointmentMap.put(display, id);
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTextArea prescriptionArea = new JTextArea();
        prescriptionArea.setLineWrap(true);
        prescriptionArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(prescriptionArea);

        JButton saveBtn = new JButton("Save Prescription");
        saveBtn.setBackground(new Color(0, 153, 0));
        saveBtn.setForeground(Color.WHITE);

        saveBtn.addActionListener(e -> {
            String selected = (String) appointmentBox.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(frame, "Select an appointment.");
                return;
            }

            int appointmentId = appointmentMap.get(selected);
            String prescriptionText = prescriptionArea.getText();
            if (prescriptionText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Enter prescription text.");
                return;
            }

            String patientName = selected.split(" - ")[1];
            Date today = new Date();
            java.sql.Date sqlDate = new java.sql.Date(today.getTime());

            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");
                String insert = "INSERT INTO prescriptions (appointment_id, doctor_id, patient_name, prescription_text, prescribed_on) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(insert);
                stmt.setInt(1, appointmentId);
                stmt.setInt(2, doctorId);
                stmt.setString(3, patientName);
                stmt.setString(4, prescriptionText);
                stmt.setDate(5, sqlDate);
                stmt.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(frame, "Prescription saved.");
                frame.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error saving prescription.");
            }
        });

        panel.add(new JLabel("Select Appointment:")); panel.add(appointmentBox);
        panel.add(new JLabel("Prescription:")); panel.add(scroll);
        panel.add(new JLabel()); panel.add(saveBtn);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void showPatientHistory(int doctorId) {
        JFrame frame = new JFrame("Patient History");
        frame.setSize(700, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] cols = {"Patient", "Prescription", "Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");
            String sql = "SELECT patient_name, prescription_text, prescribed_on FROM prescriptions WHERE doctor_id = ? ORDER BY prescribed_on DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("patient_name"),
                    rs.getString("prescription_text"),
                    rs.getDate("prescribed_on")
                });
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading history.");
        }

        frame.add(scroll);
        frame.setVisible(true);
    }

    private void showDoctorHelp() {
        JFrame frame = new JFrame("Doctor Help Guide");
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 255, 250)); // mint green

        JLabel title = new JLabel("How to Use the Doctor Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(0, 102, 102));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JTextArea helpText = new JTextArea(
            "Welcome, Doctor!\n\n" +
            "Here's a quick guide to help you use the dashboard:\n\n" +
            "• My Appointments: View all appointments booked by patients assigned to you.\n" +
            "• Patient History: Review past patient records and prescriptions.\n" +
            "• Write Prescription: Fill prescriptions for patients post consultation.\n" +
            "• My Profile: View your profile, contact info, and specialization.\n" +
            "• Help: Access this guide anytime.\n\n" +
            "For any support, contact the system admin at:\n" +
            "Email: admin@hospital.com\nPhone: +91-9876543210\n\n" +
            "Thank you for your dedication to patient care!"
        );
        helpText.setWrapStyleWord(true);
        helpText.setLineWrap(true);
        helpText.setEditable(false);
        helpText.setOpaque(false);
        helpText.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        helpText.setForeground(Color.DARK_GRAY);
        helpText.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JScrollPane scrollPane = new JScrollPane(helpText);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(255, 102, 102));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeBtn.setFocusPainted(false);
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.setPreferredSize(new Dimension(100, 40));
        closeBtn.addActionListener(e -> frame.dispose());

        panel.add(title);
        panel.add(scrollPane);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(closeBtn);

        frame.add(panel);
        frame.setVisible(true);
    }

    
    private void showDoctorAppointments(int doctorId) {
        JFrame frame = new JFrame("My Appointments");
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);

        String[] columns = {"Patient Name", "Problem", "Phone", "Email", "Booking Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");
            String sql = "SELECT patient_name, problem, phone, email, booking_date FROM appointments WHERE doctor_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("patient_name"),
                    rs.getString("problem"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getDate("booking_date")
                });
            }
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading appointments.");
        }

        frame.add(scrollPane);
        frame.setVisible(true);
    }
    
    private void showDoctorProfile(int doctorId) {
        JFrame frame = new JFrame("My Profile");
        frame.setSize(450, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(230, 245, 255)); // light blue

        JLabel header = new JLabel("Doctor Profile", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setForeground(new Color(0, 102, 204));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(header);

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");
            String sql = "SELECT name, specialization, phone, email FROM doctors WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                panel.add(createProfileLabel("Name:", rs.getString("name")));
                panel.add(createProfileLabel("Specialization:", rs.getString("specialization")));
                panel.add(createProfileLabel("Phone:", rs.getString("phone")));
                panel.add(createProfileLabel("Email:", rs.getString("email")));
            } else {
                JOptionPane.showMessageDialog(frame, "Doctor profile not found.");
            }

            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading profile.");
        }

        JButton backBtn = new JButton("Close");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setBackground(new Color(255, 102, 102));  // light red
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> frame.dispose());

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(backBtn);

        frame.add(panel);
        frame.setVisible(true);
    }
   
    
    private JPanel createProfileLabel(String label, String value) {
        JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line.setBackground(new Color(230, 245, 255));

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        fieldLabel.setForeground(new Color(0, 76, 153));

        JLabel fieldValue = new JLabel(value);
        fieldValue.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        fieldValue.setForeground(Color.DARK_GRAY);

        line.add(fieldLabel);
        line.add(fieldValue);

        return line;
    }

    

    private void showAboutInfo() {
        backgroundPanel.removeAll();
        backgroundPanel.setLayout(new BorderLayout());

        // NavBar again
        JPanel navBar = new JPanel();
        navBar.setBackground(new Color(0, 76, 153));
        navBar.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 10));

        String[] navItems = {"HOME", "ABOUT", "CONTACT"};
        for (String item : navItems) {
            JButton btn = new JButton(item);
            btn.setBackground(new Color(0, 102, 204));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(90, 35));

            btn.addActionListener(e -> {
                if (item.equals("ABOUT")) {
                    showAboutInfo();
                } else if (item.equals("HOME")) {
                    dispose();
                    new StylishHealthcareGUI();
                } else if (item.equals("CONTACT")) {
                	showContactPage();
                }
            });

            navBar.add(btn);
        }

        backgroundPanel.add(navBar, BorderLayout.NORTH);

        // Wide About Panel
        JPanel aboutPanel = new JPanel(new BorderLayout());
        aboutPanel.setBackground(new Color(255, 255, 255, 230));
        aboutPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel heading = new JLabel("About Our Healthcare System", JLabel.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        aboutPanel.add(heading, BorderLayout.NORTH);

        JTextArea aboutText = new JTextArea(
            "A great hospital stands out for its commitment to providing top-notch healthcare, "
          + "combined with a welcoming and compassionate environment. Its infrastructure is well-equipped "
          + "with the latest medical technologies, ensuring accurate diagnoses and effective treatments.\n\n"
          + "What truly sets a hospital apart is its emphasis on patient-centered care, where every individual "
          + "is treated with dignity and empathy. From clean and organized facilities to streamlined processes, "
          + "the goal is always to prioritize the comfort and well-being of patients.\n\n"
          + "The staff, from doctors and nurses to administrative teams, play a vital role in the hospital's success. "
          + "Their dedication, expertise, and teamwork reflect their unwavering commitment to saving lives and "
          + "improving health outcomes."
        );
        aboutText.setWrapStyleWord(true);
        aboutText.setLineWrap(true);
        aboutText.setEditable(false);
        aboutText.setOpaque(false);
        aboutText.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        aboutText.setMargin(new Insets(10, 20, 10, 20));

        JScrollPane scrollPane = new JScrollPane(aboutText);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        aboutPanel.add(scrollPane, BorderLayout.CENTER);

        JButton backBtn = new JButton("BACK");
        backBtn.setBackground(new Color(30, 144, 255));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setPreferredSize(new Dimension(100, 35));
        backBtn.addActionListener(e -> {
            dispose();
            new StylishHealthcareGUI();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(backBtn);

        aboutPanel.add(bottomPanel, BorderLayout.SOUTH);

        backgroundPanel.add(aboutPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
        add(backgroundPanel);
    }
    private void showPatientSignupForm() {
        JFrame frame = new JFrame("Patient Signup");
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        JButton signupBtn = new JButton("Sign Up");
        JButton backBtn = new JButton("Back");

        panel.add(emailLabel); panel.add(emailField);
        panel.add(passLabel); panel.add(passField);
        panel.add(signupBtn); panel.add(backBtn);

        signupBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String role = "Patient";

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields.");
                return;
            }

            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/hms", "root", "Rowdy@08");

                // Check if user already exists
                PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM users WHERE user_id = ?");
                checkStmt.setString(1, email);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(frame, "User already exists. Try logging in.");
                    conn.close();
                    return;
                }

                // Insert new patient into users table
                String sql = "INSERT INTO users (user_id, password, role) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, email);
                stmt.setString(2, password);
                stmt.setString(3, role);
                stmt.executeUpdate();

                conn.close();
                JOptionPane.showMessageDialog(frame, "Signup successful! Please login.");
                frame.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error during signup.");
            }
        });

        backBtn.addActionListener(e -> frame.dispose());

        frame.add(panel);
        frame.setVisible(true);
    }
    private void showContactPage() {
        backgroundPanel.removeAll();
        backgroundPanel.setLayout(new BorderLayout());

        // Reuse nav bar
        JPanel navBar = new JPanel();
        navBar.setBackground(new Color(0, 76, 153));
        navBar.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 10));
        String[] navItems = {"HOME", "ABOUT", "CONTACT"};

        for (String item : navItems) {
            JButton btn = new JButton(item);
            btn.setBackground(new Color(0, 102, 204));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(90, 35));

            btn.addActionListener(e -> {
                if (item.equals("ABOUT")) {
                    showAboutInfo();
                } else if (item.equals("HOME")) {
                    dispose();
                    new StylishHealthcareGUI();
                } else if (item.equals("CONTACT")) {
                    showContactPage();
                }
            });

            navBar.add(btn);
        }

        backgroundPanel.add(navBar, BorderLayout.NORTH);

        // Contact Panel
        JPanel contactPanel = new JPanel();
        contactPanel.setLayout(new BoxLayout(contactPanel, BoxLayout.Y_AXIS));
        contactPanel.setBackground(new Color(173, 216, 230)); // Light blue
        contactPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel heading = new JLabel("Contact Us");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        contactPanel.add(heading);
        contactPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel email = new JLabel("Email: contact@healthcare.com");
        JLabel phone = new JLabel("Phone: +1 234 567 890");
        JLabel address = new JLabel("Address: 123 Healthcare Street, City, Country");

        JLabel[] infoLabels = {email, phone, address};
        for (JLabel lbl : infoLabels) {
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            contactPanel.add(lbl);
            contactPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JLabel teamTitle = new JLabel("Team Members");
        teamTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        teamTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        contactPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contactPanel.add(teamTitle);
        contactPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        String[] team = {
            "LALITHYA BANKA - 2310030101",
            "SATHWIK RAM - 2310030021",
            "SANIA BASHA - 2310030404",
            "RAKESH GEDALA - 2310030203"
        };

        for (String member : team) {
            JLabel lbl = new JLabel("• " + member);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            contactPanel.add(lbl);
            contactPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        contactPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton backButton = new JButton("BACK");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setBackground(new Color(0, 102, 204));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(100, 35));
        backButton.addActionListener(e -> {
            dispose();
            new StylishHealthcareGUI();
        });

        contactPanel.add(backButton);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(135, 206, 250)); // Slightly different blue
        wrapper.add(contactPanel);

        backgroundPanel.add(wrapper, BorderLayout.CENTER);
        revalidate();
        repaint();
        add(backgroundPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StylishHealthcareGUI::new);
    }
}