package nl.dutchman.spacetrimmer.utils;

import javax.swing.*;
import java.awt.*;

public class DialogManager
{
    public void displayInfo(String title, String message)
    {
        displayFrame(title, message, JOptionPane.INFORMATION_MESSAGE);
    }

    public void displayWarning(String title, String message)
    {
        displayFrame(title, message, JOptionPane.WARNING_MESSAGE);
    }

    public void displayError(String title, String message)
    {
        displayFrame(title, message, JOptionPane.ERROR_MESSAGE);
    }

    private void displayFrame(String title, String message, int msgType)
    {
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(null, message, title, msgType);
    }
}
