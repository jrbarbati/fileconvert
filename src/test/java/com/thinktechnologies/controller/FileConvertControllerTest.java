package com.thinktechnologies.controller;


import com.thinktechnologies.handler.cloudconvert.CloudConvertHandler;
import com.thinktechnologies.handler.file.FileHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class FileConvertControllerTest
{
    @Spy
    FileConvertController fileConvertController;

    @Mock
    FileHandler fileHandler;

    @Mock
    CloudConvertHandler cloudConvertHandler;

    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);

        fileConvertController.fileHandler = fileHandler;
        fileConvertController.cloudConvertHandler = cloudConvertHandler;
    }

    @Test
    public void convertFiles() throws Exception
    {

    }
}