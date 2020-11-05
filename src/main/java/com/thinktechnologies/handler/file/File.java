package com.thinktechnologies.handler.file;

public class File
{
    private final String path;

    public File(String path)
    {
        this.path = path.endsWith("/") || path.isEmpty() ? path.substring(0, path.length() - 1) : path;
    }

    public String getPath()
    {
        return path;
    }

    public String getExtension()
    {
        String filename = getPath();
        return filename.substring(filename.indexOf('.') + 1);
    }

    public String getName()
    {
        return path;
    }
}
