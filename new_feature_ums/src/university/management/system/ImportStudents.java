package university.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;

public class ImportStudents extends JFrame implements ActionListener {

    JButton importButton;
    JFileChooser fileChooser;

    ImportStudents() {
        setSize(500, 300);
        setLocation(500, 250);
        setLayout(null);

        getContentPane().setBackground(Color.WHITE);

        JLabel heading = new JLabel("Import Student Data");
        heading.setBounds(100, 30, 300, 30);
        heading.setFont(new Font("Tahoma", Font.BOLD, 22));
        add(heading);

        importButton = new JButton("Import CSV");
        importButton.setBounds(150, 100, 200, 40);
        importButton.setBackground(Color.BLUE);
        importButton.setForeground(Color.WHITE);
        importButton.setFont(new Font("Tahoma", Font.BOLD, 16));
        importButton.addActionListener(this);
        add(importButton);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == importButton) {
            importFromCSV();
        }
    }

    public void importFromCSV() {
        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Student CSV File");

        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToImport = fileChooser.getSelectedFile();
            String filePath = fileToImport.getAbsolutePath();

            try (BufferedReader br = new BufferedReader(new FileReader(filePath));
                 Connection conn = new Conn().c;
                 PreparedStatement pstmt = conn.prepareStatement(
                         "INSERT INTO student (name, fname, rollno, dob, address, phone, email, class_x, class_xii, aadhar, course, branch) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

                String line;
                br.readLine(); // Skip header row

                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");

                    if (data.length == 12) {
                        pstmt.setString(1, data[0].trim());  // name
                        pstmt.setString(2, data[1].trim());  // fname
                        pstmt.setString(3, data[2].trim());  // rollno
                        pstmt.setString(4, data[3].trim());  // dob
                        pstmt.setString(5, data[4].trim());  // address
                        pstmt.setString(6, data[5].trim());  // phone
                        pstmt.setString(7, data[6].trim());  // email
                        pstmt.setInt(8, Integer.parseInt(data[7].trim()));  // class_x
                        pstmt.setInt(9, Integer.parseInt(data[8].trim()));  // class_xii
                        pstmt.setString(10, data[9].trim()); // aadhar
                        pstmt.setString(11, data[10].trim()); // course
                        pstmt.setString(12, data[11].trim()); // branch

                        pstmt.addBatch();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid data format in CSV!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                pstmt.executeBatch();

                JOptionPane.showMessageDialog(this, "Student data imported successfully!", "Import Successful", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException | SQLException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error importing student data:\n" + e.getMessage(),
                        "Import Failed", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new ImportStudents();
    }
}
