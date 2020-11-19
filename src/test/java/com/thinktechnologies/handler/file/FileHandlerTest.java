package com.thinktechnologies.handler.file;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class FileHandlerTest
{
    @Autowired
    FileHandler fileHandler;

    @Before
    public void setup()
    {
        fileHandler = spy(fileHandler);
    }

    @Test
    public void fetchFileNames()
    {
        java.io.File workingDirectory = new java.io.File("path/to/directory");

        doReturn(new ArrayList<>()).when(fileHandler).fetchFilenames(workingDirectory, "pages", "docx");

        List<File> files = fileHandler.fetchFilenames("path/to/directory", "pages", "docx");

        assertTrue(files.isEmpty());
    }

    @Test
    public void fetchAllFilenames()
    {
        java.io.File f1 = mock(java.io.File.class);
        when(f1.getName()).thenReturn("f1.numbers");
        when(f1.getPath()).thenReturn("~/f1.numbers");
        when(f1.isDirectory()).thenReturn(false);

        java.io.File f2 = mock(java.io.File.class);
        when(f2.getName()).thenReturn("f2.numbers");
        when(f2.getPath()).thenReturn("~/f2.numbers");
        when(f2.isDirectory()).thenReturn(false);

        java.io.File f3 = mock(java.io.File.class);
        when(f3.getName()).thenReturn("f3.numbers");
        when(f3.getPath()).thenReturn("~/f3.numbers");
        when(f3.isDirectory()).thenReturn(false);

        java.io.File f5numbers = mock(java.io.File.class);
        when(f5numbers.getName()).thenReturn("f5.numbers");
        when(f5numbers.getPath()).thenReturn("~/f5.numbers");
        when(f5numbers.isDirectory()).thenReturn(false);

        java.io.File f5excel = mock(java.io.File.class);
        when(f5excel.getName()).thenReturn("f5.xlsx");
        when(f5excel.getPath()).thenReturn("~/f5.xlsx");
        when(f5excel.isDirectory()).thenReturn(false);

        java.io.File f4 = mock(java.io.File.class);
        when(f4.getName()).thenReturn("f4.numbers");
        when(f4.getPath()).thenReturn("~/d1/f4.numbers");
        when(f4.isDirectory()).thenReturn(false);

        java.io.File d1 = mock(java.io.File.class);
        when(d1.getName()).thenReturn("~/d1");
        when(d1.getPath()).thenReturn("~/");
        when(d1.isDirectory()).thenReturn(true);
        when(d1.listFiles()).thenReturn(new java.io.File[] {f4});

        java.io.File home = mock(java.io.File.class);
        when(home.listFiles()).thenReturn(new java.io.File[] {f1, f2, f3, f5excel, f5numbers, d1});

        List<File> files = fileHandler.fetchFilenames(home, "numbers", "xlsx");

        assertEquals(4, files.size());
        assertEquals("~/f1.numbers", files.get(0).getName());
        assertEquals("~/f2.numbers", files.get(1).getName());
        assertEquals("~/f3.numbers", files.get(2).getName());
        assertEquals("~/d1/f4.numbers", files.get(3).getName());
    }

    @Test
    public void buildFilePairs()
    {
        java.io.File f1 = mock(java.io.File.class);
        when(f1.getName()).thenReturn("f1.numbers");
        when(f1.getPath()).thenReturn("~/f1.numbers");
        when(f1.isDirectory()).thenReturn(false);

        java.io.File f2 = mock(java.io.File.class);
        when(f2.getName()).thenReturn("f2.numbers");
        when(f2.getPath()).thenReturn("~/f2.numbers");
        when(f2.isDirectory()).thenReturn(false);

        java.io.File f3 = mock(java.io.File.class);
        when(f3.getName()).thenReturn("f3.numbers");
        when(f3.getPath()).thenReturn("~/f3.numbers");
        when(f3.isDirectory()).thenReturn(false);

        java.io.File f3pages = mock(java.io.File.class);
        when(f3pages.getName()).thenReturn("f3.pages");
        when(f3pages.getPath()).thenReturn("~/f3.pages");
        when(f3pages.isDirectory()).thenReturn(false);

        java.io.File f5numbers = mock(java.io.File.class);
        when(f5numbers.getName()).thenReturn("f5.numbers");
        when(f5numbers.getPath()).thenReturn("~/f5.numbers");
        when(f5numbers.isDirectory()).thenReturn(false);

        java.io.File f5excel = mock(java.io.File.class);
        when(f5excel.getName()).thenReturn("f5.xlsx");
        when(f5excel.getPath()).thenReturn("~/f5.xlsx");
        when(f5excel.isDirectory()).thenReturn(false);

        java.io.File f4 = mock(java.io.File.class);
        when(f4.getName()).thenReturn("f4.numbers");
        when(f4.getPath()).thenReturn("~/d1/f4.numbers");
        when(f4.isDirectory()).thenReturn(false);

        java.io.File d1 = mock(java.io.File.class);
        when(d1.getName()).thenReturn("~/d1");
        when(d1.getPath()).thenReturn("~/");
        when(d1.isDirectory()).thenReturn(true);
        when(d1.listFiles()).thenReturn(new java.io.File[] {f4});

        java.io.File home = mock(java.io.File.class);
        when(home.listFiles()).thenReturn(new java.io.File[] {f1, f2, f3, f3pages, f5excel, f5numbers, d1});

        Map<String, FilePair> pairs = fileHandler.buildFilePairs(home, "numbers", "xlsx");

        assertEquals(4, pairs.size());
        assertFalse(pairs.get("f1.numbers").isCompletePair());
        assertFalse(pairs.get("f2.numbers").isCompletePair());
        assertFalse(pairs.get("f3.numbers").isCompletePair());
        assertTrue(pairs.get("f5.numbers").isCompletePair());
    }
}