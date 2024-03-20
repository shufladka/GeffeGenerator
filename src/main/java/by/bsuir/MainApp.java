package by.bsuir;

import by.bsuir.frame.Frame;

import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {

        // create an instance of the MainFrame class and set the window name
        Frame frame = new Frame("Geffe Generator");

        // set the icon for the frame
        frame.setIconImage(new ImageIcon("src/main/resources/bsuir-icon.png").getImage());

        // make the window visible
        frame.setVisible(true);

        // align the window to the center
        frame.setLocationRelativeTo(null);

        frame.setResizable(false);
    }
}
