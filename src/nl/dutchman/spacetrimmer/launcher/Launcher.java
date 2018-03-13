package nl.dutchman.spacetrimmer.launcher;

import nl.dutchman.spacetrimmer.ui.WindowController;

public class Launcher
{
    public static void main(String[] args)
    {
        try
        {
            WindowController windowController = new WindowController();
            windowController.display();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
