package nl.dutchman.spacetrimmer.ui;

import nl.dutchman.spacetrimmer.model.ProcessableFile;
import nl.dutchman.spacetrimmer.utils.DialogManager;
import nl.dutchman.spacetrimmer.utils.MessageConsole;
import nl.dutchman.spacetrimmer.utils.ProcessInformation;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WindowController
{
    private MainWindow mainWindow;
    private DialogManager dialogManager;
    private int threadsToUse;
    private ProcessInformation processInformation;
    private ProcessInformation[] stepInformation = new ProcessInformation[2];
    private int filesToProcess, filesProcessed;
    private long lengthToProcess, lengthProcessed;

    public WindowController()
    {
        threadsToUse = Runtime.getRuntime().availableProcessors();
        dialogManager = new DialogManager();
        setLookAndFeel();
        initializeComponents();
        initializeListeners();
    }

    private void setStatus(int step)
    {
        if (step == 0)
        {
            filesProcessed = 0;
            filesToProcess = 0;
            lengthProcessed = 0;
            lengthToProcess = 0;
            this.mainWindow.getProgressBar().setValue(0);
            this.mainWindow.getStatusIcon().setIcon(new ImageIcon(WindowController.class.getResource("/nl/dutchman/spacetrimmer/resources/processing.gif")));
            this.mainWindow.getStatusLabel().setText("Status: PROCESSING");
            this.mainWindow.getThreadsLabel().setText("Analyzing directory");
            this.mainWindow.getCurrentLabel().setText("Processed 0,0 kB of 0,0 kB. Files processed: 0 out of 0");
            this.mainWindow.getProcessButton().setEnabled(false);
        }
        else if (step == 1)
            this.mainWindow.getThreadsLabel().setText("Processing in parallel mode. Threads being used: " + threadsToUse);
        else if (step == -1)
        {
            this.mainWindow.getStatusIcon().setIcon(new ImageIcon(WindowController.class.getResource("/nl/dutchman/spacetrimmer/resources/ready.png")));
            this.mainWindow.getStatusLabel().setText("Status: READY");
            this.mainWindow.getThreadsLabel().setText("Currently idle. Awaiting for input");
            this.mainWindow.getProcessButton().setEnabled(true);
        }
    }

    private void initializeProcess(String directory, String extensionStr, boolean trimSpaces, boolean trimLines)
    {
        setStatus(0);

        processInformation = new ProcessInformation();
        processInformation.start();
        System.out.println("Process starting at " + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now()));

        boolean noExtensions = extensionStr == null || extensionStr.trim().isEmpty();
        List<String> extensions;
        if (noExtensions)
            extensions = new ArrayList<>();
        else
            extensions = Arrays.asList(extensionStr.split(","));

        ExecutorService processService = Executors.newSingleThreadExecutor();
        processService.submit(() -> {
            try
            {

                BiFunction<String, List<String>, Boolean> endsWithAny = (dirName, exts) -> {
                    try
                    {
                        return exts.contains(dirName.substring(dirName.lastIndexOf('.') + 1));
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        return false;
                    }
                };
                System.out.println("Step 0 --> Gathering files to modify");
                stepInformation[0] = new ProcessInformation();
                stepInformation[0].start();
                int c = 0;
                List<ProcessableFile> processableFiles = Files.walk(Paths.get(directory)).
                        map(p -> new ProcessableFile(p.toFile(), p.toFile().length())).
                        filter(p -> !p.getFile().isDirectory()).
                        filter(noExtensions ? p -> true : p -> endsWithAny.apply(p.getFile().getAbsolutePath(), extensions)).
                        collect(Collectors.toList());
                stepInformation[0].end();

                filesToProcess = processableFiles.size();
                lengthToProcess = processableFiles.stream().map(p -> p.getFileSize()).mapToInt(i -> i.intValue()).sum();

                System.out.println("Step 0 --> Files gathered in " + stepInformation[0].getTime() + "ms");

                ExecutorService executorService = Executors.newFixedThreadPool(threadsToUse);

                setStatus(1);

                System.out.println("Step 1 --> Processing files. This may take a while...");
                System.out.println("Settings >- trimSpaces: " + trimSpaces + " | trimLines: " + trimLines);
                this.mainWindow.getCurrentLabel().setText("Processed 0.0 kB of " + readableFileSize(lengthToProcess) + ". Files processed: 0 out of " + filesToProcess);
                stepInformation[1] = new ProcessInformation();
                stepInformation[1].start();

                for (ProcessableFile file : processableFiles)
                {
                    executorService.submit(() -> {
                        try
                        {
                            List<String> lineFiles = new ArrayList<>();
                            for (String line : Files.readAllLines(file.getFile().toPath(), StandardCharsets.UTF_8))
                            {
                                if (trimSpaces)
                                    line = line.replaceAll("\\s+$", "");

                                lineFiles.add(line);
                            }

                            if (trimLines)
                            {
                                for (int i = lineFiles.size() - 3; i >= 0; i--)
                                {
                                    if (lineFiles.get(i).trim().isEmpty() &&
                                            lineFiles.get(i+1).trim().isEmpty() &&
                                            lineFiles.get(i+2).trim().isEmpty())
                                        lineFiles.remove(i);
                                }
                            }

                            Files.write(file.getFile().toPath(), lineFiles);
                        }
                        catch (IOException e)
                        {
                            System.err.println("Could not process " + file.getFile().getAbsolutePath());
                            e.printStackTrace();
                        }

                        synchronized (this)
                        {
                            filesProcessed++;
                            lengthProcessed += file.getFileSize();

                            this.mainWindow.getProgressBar().setValue((int) (lengthProcessed / (lengthToProcess / 100)));
                            this.mainWindow.getCurrentLabel().setText("Processed " + readableFileSize(lengthProcessed) + " of "
                                    + readableFileSize(lengthToProcess) + ". Files processed: " + filesProcessed + " out of " + filesToProcess);
                        }
                    });
                }

                executorService.shutdown();

                try
                {
                    executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                }
                catch (InterruptedException e)
                {
                    dialogManager.displayError("ERR5 - Awaiting process", "The process has been interrupted");
                    e.printStackTrace();
                }
                finally
                {
                    stepInformation[1].end();
                    System.out.println("Step 1 --> Files processed in " + stepInformation[1].getTime() + "ms");
                    processInformation.end();
                    System.out.println("Process finished succesfully in " + processInformation.getTime() + "ms");
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                dialogManager.displayError("ERR4 - Process Error", "Could not finish processing.\n" +
                        "Please check console for further information");
                return;
            }
            finally
            {
                setStatus(-1);
                dialogManager.displayInfo("INF1 - Finished process", "Finish processing " + filesProcessed + " files");
            }
        });
    }

    private String readableFileSize(long size)
    {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private void setLookAndFeel()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException | ClassNotFoundException e)
        {
            dialogManager.displayError("ERR1 - Look and Feel", "Could not set application's look and feel");
            e.printStackTrace();
        }
    }

    public void display()
    {
        this.mainWindow.setVisible(true);
    }

    private void initializeComponents()
    {
        this.mainWindow = new MainWindow();
        MessageConsole console = new MessageConsole(this.mainWindow.getConsoleArea());
        console.redirectOut(null, System.out);
        console.redirectErr(Color.RED, null);
        console.setMessageLines(1000);
    }

    private void initializeListeners()
    {
        setExtensionsDocumentFilter();
        addFileChooserListener();
        addProcessListener();
    }

    private void setExtensionsDocumentFilter()
    {
        Pattern extensionPattern = Pattern.compile("[a-zA-Z0-9,]*");
        AbstractDocument document = (AbstractDocument) this.mainWindow.getFileFormatField().getDocument();
        document.setDocumentFilter(new DocumentFilter(){
            @Override
            public void replace(DocumentFilter.FilterBypass byPass, int offset, int length, String text, AttributeSet attributes) throws BadLocationException
            {
                Matcher matcher = extensionPattern.matcher(text);
                if (matcher.matches())
                    super.replace(byPass, offset, length, text, attributes);
            }
        });
    }

    private void addFileChooserListener()
    {
        JButton fileChooserButton = mainWindow.getDirectoryButton();
        fileChooserButton.addActionListener(actionListener  -> {
            JTextField fileChooserField = mainWindow.getDirectoryField();
            String startPath = (!fileChooserField.getText().isEmpty() &&
                                Files.isDirectory(Paths.get(fileChooserField.getText()))) ?
                fileChooserField.getText() : System.getProperty("user.home");
            JFileChooser fileChooser = new JFileChooser(startPath);
            fileChooser.setDialogTitle("Select a directory");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fileChooser.showDialog(mainWindow, "Select directory") == JFileChooser.APPROVE_OPTION)
            {
                fileChooserField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
    }

    private void addProcessListener()
    {
        JButton processButton = mainWindow.getProcessButton();
        processButton.addActionListener(actionListener -> {
            JTextField fileChooserField = mainWindow.getDirectoryField();
            JTextField fileExtensionField = mainWindow.getFileFormatField();
            String selectedDirectory = fileChooserField.getText();
            String selectedExtensions = fileExtensionField.getText();
            boolean trimSpaces = mainWindow.getEndTrimCheckbox().isSelected();
            boolean trimLines = mainWindow.getEmptyLineCheckBox().isSelected();

            // Cleaning up empty extensions here.
            while (selectedExtensions.contains(",,"))
                selectedExtensions = selectedExtensions.replace(",,", ",");
            if (selectedExtensions.startsWith(","))
                selectedExtensions = selectedExtensions.substring(1);
            if (selectedExtensions.endsWith(","))
                selectedExtensions = selectedExtensions.substring(0, selectedExtensions.length() - 1);

            if (selectedDirectory == null || selectedDirectory.trim().isEmpty())
            {
                dialogManager.displayError("ERR2 - No directory", "No directory has been selected");
                return;
            }
            if (!Files.isDirectory(Paths.get(selectedDirectory)))
            {
                dialogManager.displayError("ERR3 - Invalid directory", "Selected directory is invalid");
                return;
            }
            if (selectedExtensions == null || selectedExtensions.trim().isEmpty())
            {
                dialogManager.displayError("ERR6 - No extensions selected", "No extensions have been selected.");
                return;
            }
            initializeProcess(selectedDirectory, selectedExtensions, trimSpaces, trimLines);
        });
    }
}
