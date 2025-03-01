package university.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;

public class ImportTeachers extends JFrame implements ActionListener {
    private JButton importButton;
    private JLabel statusLabel;
    private JFileChooser fileChooser;

    ImportTeachers() {
        setTitle("Import Teachers");
        setSize(400, 200);
        setLayout(new FlowLayout());

        fileChooser = new JFileChooser();
        importButton = new JButton("Select CSV File");
        importButton.addActionListener(this);

        statusLabel = new JLabel("Select a CSV file to import teachers.");

        add(importButton);
        add(statusLabel);

        setVisible(true);
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == importButton) {
            int returnValue = fileChooser.showOpenDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                importTeachersFromCSV(file);
            }
        }
    }

    private void importTeachersFromCSV(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file));
             Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/universitymanagementsystem", "root", "");
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO teacher (name, fname, empid, dob, address, phone, email, class_x, class_xii, aadhar, course, branch) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            String line;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null) {
                if (!headerSkipped) { // Skip the header line
                    headerSkipped = true;
                    continue;
                }
                String[] values = line.split(",");

                if (values.length == 12) {
                    pstmt.setString(1, values[0].trim());  // name
                    pstmt.setString(2, values[1].trim());  // fname
                    pstmt.setString(3, values[2].trim());  // empid
                    pstmt.setString(4, values[3].trim());  // dob
                    pstmt.setString(5, values[4].trim());  // address
                    pstmt.setString(6, values[5].trim());  // phone
                    pstmt.setString(7, values[6].trim());  // email
                    pstmt.setString(8, values[7].trim());  // class_x
                    pstmt.setString(9, values[8].trim());  // class_xii
                    pstmt.setString(10, values[9].trim()); // aadhar
                    pstmt.setString(11, values[10].trim());// course
                    pstmt.setString(12, values[11].trim());// branch

                    pstmt.executeUpdate();
                }
            }

            statusLabel.setText("Teachers imported successfully!");
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new ImportTeachers();
    }
}
