package com.thinktechnologies.handler.file;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FileTest
{
    @Test
    public void file_noPath()
    {
        File file = new File("filename.pages");

        assertEquals("filename.pages", file.getFilename());
        assertEquals("", file.getPath());
        assertEquals("filename.pages", file.getFullName());
    }

    @Test
    public void file()
    {
        File file = new File("filename.pages", "~/tmp/dir");

        assertEquals("filename.pages", file.getFilename());
        assertEquals("~/tmp/dir/", file.getPath());
        assertEquals("~/tmp/dir/filename.pages", file.getFullName());
    }

    @Test
    public void file_pathWithEndingSlash()
    {
        File file = new File("filename.pages", "~/tmp/dir/");

        assertEquals("filename.pages", file.getFilename());
        assertEquals("~/tmp/dir/", file.getPath());
        assertEquals("~/tmp/dir/filename.pages", file.getFullName());
    }

    @Test
    public void getExtension()
    {
        File file1 = new File("filename.pages");
        File file2 = new File("filename.numbers");
        File file3 = new File("filename.docx");

        assertEquals("pages", file1.getExtension());
        assertEquals("numbers", file2.getExtension());
        assertEquals("docx", file3.getExtension());

    }
}