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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

        doReturn(new ArrayList<>()).when(fileHandler).fetchAllFilenames(workingDirectory);

        List<File> files = fileHandler.fetchFilenames("path/to/directory");

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
        when(f2.getName()).thenReturn("f2.pages");
        when(f2.getPath()).thenReturn("~/f2.pages");
        when(f2.isDirectory()).thenReturn(false);

        java.io.File f3 = mock(java.io.File.class);
        when(f3.getName()).thenReturn("f3.numbers");
        when(f3.getPath()).thenReturn("~/f3.numbers");
        when(f3.isDirectory()).thenReturn(false);

        java.io.File f4 = mock(java.io.File.class);
        when(f4.getName()).thenReturn("f4.pages");
        when(f4.getPath()).thenReturn("~/d1/f4.pages");
        when(f4.isDirectory()).thenReturn(false);

        java.io.File d1 = mock(java.io.File.class);
        when(d1.getName()).thenReturn("~/d1");
        when(d1.getPath()).thenReturn("~/");
        when(d1.isDirectory()).thenReturn(true);
        when(d1.listFiles()).thenReturn(new java.io.File[] {f4});

        java.io.File home = mock(java.io.File.class);
        when(home.listFiles()).thenReturn(new java.io.File[] {f1, f2, f3, d1});

        List<File> files = fileHandler.fetchAllFilenames(home);

        assertEquals(4, files.size());
        assertEquals("~/f1.numbers", files.get(0).getName());
        assertEquals("~/f2.pages", files.get(1).getName());
        assertEquals("~/f3.numbers", files.get(2).getName());
        assertEquals("~/d1/f4.pages", files.get(3).getName());
    }
}