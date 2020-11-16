package com.thinktechnologies.handler.file;

public class FilePair
{
    String macFilename;
    FileType macFileType;

    String windowsFilename;
    FileType windowsFileType;

    public String getMacFilename()
    {
        return macFilename;
    }

    public FileType getMacFileType()
    {
        return macFileType;
    }

    public String getWindowsFilename()
    {
        return windowsFilename;
    }

    public FileType getWindowsFileType()
    {
        return windowsFileType;
    }

    public boolean isCompletePair()
    {
        return !this.macFilename.isEmpty() && !this.windowsFilename.isEmpty();
    }
}
