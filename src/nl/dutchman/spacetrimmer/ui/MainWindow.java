package nl.dutchman.spacetrimmer.ui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame
{
    public static final int WIDTH = 800, HEIGHT = 600;

    private JPanel contentPane;
    private JPanel consoleContainer;
    private JScrollPane consoleScrollContainer;
    private JTextPane consoleArea;
    private JPanel interfaceContainer;
    private JPanel directoryContainer;
    private JPanel optionsContainer;
    private JPanel utilityContainer;
    private JButton directoryButton;
    private JTextField directoryField;
    private JPanel processContainer;
    private JButton processButton;
    private JPanel progressContainer;
    private JPanel statusContainer;
    private JLabel statusLabel;
    private JLabel statusIcon;
    private JLabel threadsLabel;
    private JLabel currentLabel;
    private JProgressBar progressBar;
    private JCheckBox endTrimCheckbox;
    private JCheckBox emptyLineCheckBox;
    private JPanel fileFormatContainer;
    private JLabel fileFormatLabel;
    private JTextField fileFormatField;

    public JTextPane getConsoleArea() { return consoleArea; }
    public JButton getDirectoryButton() { return directoryButton; }
    public JTextField getDirectoryField() { return directoryField; }
    public JButton getProcessButton() { return processButton; }
    public JLabel getStatusLabel() { return statusLabel; }
    public JLabel getStatusIcon() { return statusIcon; }
    public JLabel getThreadsLabel() { return threadsLabel; }
    public JLabel getCurrentLabel() { return currentLabel; }
    public JProgressBar getProgressBar() { return progressBar; }
    public JCheckBox getEndTrimCheckbox() { return endTrimCheckbox; }
    public JCheckBox getEmptyLineCheckBox() { return emptyLineCheckBox; }
    public JTextField getFileFormatField() { return fileFormatField; }

    public MainWindow()
    {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(WIDTH, HEIGHT);
        this.setLocationRelativeTo(null);
        this.setContentPane(contentPane);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource("/nl/dutchman/spacetrimmer/resources/icon.png")));
        this.setTitle("Space Trimmer - Remove trailing spaces/empty lines");
        this.setResizable(false);

        this.consoleArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        this.consoleArea.setEditable(false);

        this.endTrimCheckbox.setSelected(true);
        this.emptyLineCheckBox.setSelected(true);
    }
}
