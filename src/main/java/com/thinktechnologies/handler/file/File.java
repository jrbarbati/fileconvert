package com.thinktechnologies.handler.file;

import java.util.Objects;

public class File
{
    private final String filename;
    private final String path;
    private final Integer id;

    public File(String filename)
    {
        this(filename, "");
    }

    public File(String filename, String path)
    {
        this.filename = filename;
        this.path = path.endsWith("/") || path.isEmpty() ? path : path + "/";
        this.id = Objects.hash(filename);
    }

    public String getFilename()
    {
        return filename;
    }

    public String getPath()
    {
        return path;
    }

    public int getId()
    {
        return id;
    }

    public String getExtension()
    {
        String filename = getFilename();
        return filename.substring(filename.indexOf('.') + 1);
    }

    /**
     * @return the path concatenated with the filename
     */
    public String getFullName()
    {
        return path + filename;
    }
}
