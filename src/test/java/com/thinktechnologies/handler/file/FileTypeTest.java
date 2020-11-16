package com.thinktechnologies.handler.file;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class FileTypeTest
{
    @Test
    public void allExtensions()
    {
        List<String> extensions = FileType.allExtensions();

        assertEquals(7, extensions.size());
    }

    @Test
    public void findTypeForExtension()
    {
        assertEquals(FileType.PAGES, FileType.findTypeForExtension("pages"));
        assertEquals(FileType.KEYNOTE, FileType.findTypeForExtension("key"));
        assertEquals(FileType.PPTX, FileType.findTypeForExtension("pptx"));
        assertEquals(FileType.XLSX, FileType.findTypeForExtension("xlsx"));
    }
}