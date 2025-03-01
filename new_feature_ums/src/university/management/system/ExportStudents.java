package university.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import net.proteanit.sql.DbUtils;

public class ExportStudents extends JFrame implements ActionListener {

    JTable table;
    JButton exportButton;

    ExportStudents() {
        setSize(1000, 700);
        setLocation(250, 50);
        setLayout(null);

        getContentPane().setBackground(Color.WHITE);

        JLabel heading = new JLabel("Student Details");
        heading.setBounds(50, 10, 400, 30);
        heading.setFont(new Font("Tahoma", Font.BOLD, 30));
        add(heading);

        table = new JTable();
        loadStudentData();

        JScrollPane jsp = new JScrollPane(table);
        jsp.setBounds(0, 60, 1000, 550);
        add(jsp);

        exportButton = new JButton("Export to CSV");
        exportButton.setBounds(400, 620, 200, 40);
        exportButton.setBackground(Color.BLUE);
        exportButton.setForeground(Color.WHITE);
        exportButton.setFont(new Font("Tahoma", Font.BOLD, 16));
        exportButton.addActionListener(this);
        add(exportButton);

        setVisible(true);
    }

    private void loadStudentData() {
        try {
            Conn c = new Conn();
            ResultSet rs = c.s.executeQuery("SELECT * FROM student");
            table.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading student data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == exportButton) {
            exportToCSV();
        }
    }

    public void exportToCSV() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save Student Data");
    fileChooser.setSelectedFile(new File("students.csv"));

    int userSelection = fileChooser.showSaveDialog(this);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File fileToSave = fileChooser.getSelectedFile();
        String filePath = fileToSave.getAbsolutePath();

        if (!filePath.endsWith(".csv")) {
            filePath += ".csv";
        }

        try (FileWriter writer = new FileWriter(filePath);
             Connection conn = new Conn().c;
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM student")) {

            // Get column names dynamically
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Writing the header
            for (int i = 1; i <= columnCount; i++) {
                writer.append(metaData.getColumnName(i));
                if (i < columnCount) writer.append(",");
            }
            writer.append("\n");

            // Writing rows
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    writer.append(rs.getString(i));
                    if (i < columnCount) writer.append(",");
                }
                writer.append("\n");
            }

            JOptionPane.showMessageDialog(this, "Student data exported successfully to:\n" + filePath,
                                          "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error exporting student data:\n" + e.getMessage(),
                                          "Export Failed", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}


    public static void main(String[] args) {
        new ExportStudents();
    }
}

