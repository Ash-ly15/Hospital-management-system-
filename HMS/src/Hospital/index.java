package Hospital;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class index extends JFrame implements ActionListener {
    JFrame f;
    JLabel l1, l2, l3;
    JButton b1, b2, b3, b4;

    public index() {
        f = new JFrame("Index Page");
        f.setBackground(Color.WHITE);
        f.setLayout(null);

        l1 = new JLabel();
        l1.setBounds(0, 0, 800, 570);
        l1.setLayout(null);

        ImageIcon img = new ImageIcon(ClassLoader.getSystemResource("Hospital/icons/firstpage.png"));
        if (img != null) {
            Image i1 = img.getImage().getScaledInstance(800, 570, Image.SCALE_SMOOTH);
            ImageIcon img1 = new ImageIcon(i1);
            l1.setIcon(img1);
        } else {
            System.out.println("Image not found.");
        }

        l2 = new JLabel("Amity Medical Centre");
        l2.setBounds(70, 315, 500, 30);
        l2.setFont(new Font("Times New Roman", Font.BOLD, 30));
        l2.setForeground(Color.DARK_GRAY);
        l1.add(l2);

        l3 = new JLabel("Provides best quality healthcare.");
        l3.setBounds(115, 340, 500, 30);
        l3.setFont(new Font("Times New Roman", Font.ITALIC, 13));
        l3.setForeground(Color.DARK_GRAY);
        l1.add(l3);

        b1 = new JButton("Doctor");
        b1.setBounds(50, 390, 150, 40);
        b1.setFont(new Font("Times New Roman", Font.BOLD, 13));
        b1.setBackground(Color.DARK_GRAY);
        b1.setForeground(Color.PINK);
        l1.add(b1);

        b2 = new JButton("Patient");
        b2.setBounds(220, 390, 150, 40);
        b2.setFont(new Font("Times New Roman", Font.BOLD, 13));
        b2.setBackground(Color.DARK_GRAY);
        b2.setForeground(Color.PINK);
        l1.add(b2);

        b3 = new JButton("Receptionist");
        b3.setBounds(50, 450, 150, 40);
        b3.setFont(new Font("Times New Roman", Font.BOLD, 13));
        b3.setBackground(Color.DARK_GRAY);
        b3.setForeground(Color.PINK);
        l1.add(b3);

        b4 = new JButton("Admin");
        b4.setBounds(220, 450, 150, 40);
        b4.setFont(new Font("Times New Roman", Font.BOLD, 13));
        b4.setBackground(Color.DARK_GRAY);
        b4.setForeground(Color.PINK);
        l1.add(b4);

        b1.addActionListener(this);
        b2.addActionListener(this);
        b3.addActionListener(this);
        b4.addActionListener(this);

        f.add(l1);
        f.setSize(800, 570);
        f.setLocation(300, 100);
        f.setVisible(true);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == b1) {
            f.setVisible(false);
            new LoginPage("Doctor");
        }
        if (ae.getSource() == b2) {
            f.setVisible(false);
            new LoginPage("Patient");
        }
        if (ae.getSource() == b3) {
            f.setVisible(false);
            new LoginPage("Receptionist");
        }
        if (ae.getSource() == b4) {
            f.setVisible(false);
            new LoginPage("Admin");
        }
    }

    public static void main(String[] args) {
        new index();
    }
}
