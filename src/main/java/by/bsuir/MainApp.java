package by.bsuir;

import by.bsuir.frame.AppWindow;

import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {

        // create an instance of the AppWindow class and set the window name
        AppWindow appWindow = new AppWindow("Geffe Generator");

        // set the icon for the window
        appWindow.setIconImage(new ImageIcon("src/main/resources/bsuir-icon.png").getImage());

        // make the window visible
        appWindow.setVisible(true);

        // align the window to the center
        appWindow.setLocationRelativeTo(null);

        appWindow.setResizable(false);
    }
}
