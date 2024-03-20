package by.bsuir.frame;

import by.bsuir.service.ciphers.CipherAction;
import by.bsuir.service.files.FilesHandling;
import by.bsuir.service.ciphers.GeffeCipherImpl;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Frame extends JFrame {

    private String path;
    private final JLabel selectedFileLabel = new JLabel("");
    private final JTextField firstLFSR = new JTextField("11101011101110111010101",39);
    private final JTextField secondLFSR = new JTextField("1011111110101011101011101110101",39);
    private final JTextField thirdLFSR = new JTextField("101110101111111011101011101110111010101",39);

    public Frame(String title) throws HeadlessException {
        this.setTitle(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setConfig();
        Container container = lfsrInputFields();
        JButton selectionFileButton = fileSelectionComponents(container);
        cipherActionButtons(selectionFileButton, container);
        pack();
    }

    /**
     *  Метод для создания окна приложения
     */
    private void setConfig() {

        // установить размер окна
        setSize(600, 350);

        // задание условия на закрытие приложения
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     *  Отрисовка полей ввода начальных ключей LFSR1, LFSR2, LFSR3
     */
    private Container lfsrInputFields() {
        Container container = this.getContentPane();
        container.setLayout(new GridLayout(6,1));

        JPanel firstPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel firstLabel = new JLabel("First LFSR:      ");

        JPanel secondPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel secondLabel = new JLabel("Second LFSR:");

        JPanel thirdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel thirdLabel = new JLabel("Third LFSR:     ");

        firstPanel.add(firstLabel);
        firstPanel.add(firstLFSR);
        container.add(firstPanel);

        secondPanel.add(secondLabel);
        secondPanel.add(secondLFSR);
        container.add(secondPanel);


        thirdPanel.add(thirdLabel);
        thirdPanel.add(thirdLFSR);
        container.add(thirdPanel);

        return container;
    }

    /**
     *  Отрисовка полей, информирующих о выбранном файле
     */
    private JButton fileSelectionComponents(Container container) {
        JLabel chosenFileLabel = new JLabel("Chosen file:");
        selectedFileLabel.setText("File is not selected.");
        selectedFileLabel.setVerticalAlignment(JLabel.CENTER);
        JButton selectionFileButton = new JButton("Select source file");

        JPanel filePanel = new JPanel();
        filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.X_AXIS));
        filePanel.add(chosenFileLabel);
        filePanel.add(Box.createRigidArea(new Dimension(10, 10)));
        filePanel.add(selectedFileLabel);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        selectionFileButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, selectionFileButton.getPreferredSize().height));

        container.add(filePanel);
        buttonsPanel.add(selectionFileButton);
        container.add(buttonsPanel);

        return selectionFileButton;
    }

    /**
     *  Отрисовка кнопок режима работы программы (шифрование/дешифрование)
     */
    private void cipherActionButtons(JButton selectionFileButton, Container container) {
        selectionFileButton.addActionListener(e -> fileSelectionChooser());
        JButton encryptionButton = new JButton("Encryption");
        JButton decryptionButton = new JButton("Decryption");

        encryptionButton.addActionListener(e -> cipherActionHandler(CipherAction.ENCRYPTION));
        decryptionButton.addActionListener(e -> cipherActionHandler(CipherAction.DECRYPTION));

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

        encryptionButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, encryptionButton.getPreferredSize().height));
        decryptionButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, decryptionButton.getPreferredSize().height));

        buttonsPanel.add(encryptionButton);
        buttonsPanel.add(decryptionButton);
        container.add(buttonsPanel);
    }

    /**
     *  Отрисовка селектора файла из каталога
     */
    public void fileSelectionChooser() {
        JFileChooser fileOpen = new JFileChooser();
        fileOpen.setCurrentDirectory(new File(System.getProperty("user.dir")));
        if (fileOpen.showDialog(null, "Select source file") == JFileChooser.APPROVE_OPTION) {
            File file = fileOpen.getSelectedFile();
            path = file.getAbsolutePath();
            selectedFileLabel.setText(file.getName());
        }
    }

    /**
     *  Метод для управления режимом работы программы (шифрование/дешифрование)
     */
    public void cipherActionHandler(CipherAction cipherAction) {

        FilesHandling filesHandling = new FilesHandling(
                new GeffeCipherImpl(
                        firstLFSR.getText(),
                        secondLFSR.getText(),
                        thirdLFSR.getText()));
        try {

            String[] array = {};
            String actionType = "";

            if (cipherAction.equals(CipherAction.ENCRYPTION)) {
                array = filesHandling.encryption(path);
                actionType = "Encrypted";

            } else if (cipherAction.equals(CipherAction.DECRYPTION)) {
                array = filesHandling.decryption(path);
                actionType = "Decrypted";
            }

            String geffeKey = array[0];
            String lfsr1Key = array[1];
            String lfsr2Key = array[2];
            String lfsr3Key = array[3];

            JOptionPane.showMessageDialog(null,
                    "Geffe's Key: " + cutString(geffeKey)
                            + "\nLFSR1: " + cutString(lfsr1Key)
                            + "\nLFSR2: " + cutString(lfsr2Key)
                            + "\nLFSR3: " + cutString(lfsr3Key),
                            actionType,
                            JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException exception) {
            JOptionPane.showMessageDialog(null, "File not found", "Some happend :(", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage(), "Some happend :(", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     *  Метод для "обрезки" длинного ключа при его длине более 100 символов (при выводе на UI)
     */
    private String cutString(String originalString) {

        if (originalString.length() > 100) {
            return originalString.substring(0, 100) + "...";
        }

        return originalString;
    }
}