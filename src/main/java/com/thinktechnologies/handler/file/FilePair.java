package com.thinktechnologies.handler.file;

public class FilePair
{
    String macFilename;
    String windowsFilename;

    public FilePair(String macFilename, String windowsFilename)
    {
        this.macFilename = macFilename == null ? "" : macFilename;
        this.windowsFilename = windowsFilename == null ? "" : windowsFilename;
    }

    public boolean isCompletePair()
    {
        return !this.macFilename.isEmpty() && !this.windowsFilename.isEmpty();
    }
}
