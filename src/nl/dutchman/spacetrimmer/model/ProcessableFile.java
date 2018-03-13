package nl.dutchman.spacetrimmer.model;

import java.io.File;

public class ProcessableFile
{
    private File file;
    private long fileSize;

    public ProcessableFile(File file, long fileSize)
    {
        this.file = file;
        this.fileSize = fileSize;
    }

    public File getFile() { return file; }
    public long getFileSize() { return fileSize; }
}
