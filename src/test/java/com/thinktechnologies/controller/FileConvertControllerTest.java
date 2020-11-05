package com.thinktechnologies.controller;


import com.cloudconvert.dto.Status;
import com.cloudconvert.dto.response.JobResponse;
import com.cloudconvert.dto.response.TaskResponse;
import com.thinktechnologies.handler.cloudconvert.CloudConvertHandler;
import com.thinktechnologies.handler.file.File;
import com.thinktechnologies.handler.file.FileHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    public void sftp() throws Exception
    {
        JobResponse finishedJob = mock(JobResponse.class);
        JobResponse errorJob = mock(JobResponse.class);
        TaskResponse errorTask = mock(TaskResponse.class);
        TaskResponse.Result errorResult = mock(TaskResponse.Result.class);
        Map errorFiles = mock(Map.class);

        when(fileHandler.fetchFilenames(any())).thenReturn(Arrays.asList(new File("file1.pages"), new File("file2.pages")));
        when(cloudConvertHandler.createConvertJobs(anyList(), any(), any())).thenReturn(Arrays.asList(finishedJob, errorJob));
        when(cloudConvertHandler.checkJobCompletion(finishedJob)).thenReturn(Status.FINISHED);
        when(cloudConvertHandler.checkJobCompletion(errorJob)).thenReturn(Status.ERROR);

        when(finishedJob.getStatus()).thenReturn(Status.FINISHED);
        when(errorJob.getStatus()).thenReturn(Status.ERROR);
        when(errorJob.getTasks()).thenReturn(Collections.singletonList(errorTask));
        when(errorTask.getResult()).thenReturn(errorResult);
        when(errorResult.getFiles()).thenReturn(Collections.singletonList(errorFiles));
        when(errorFiles.get("filename")).thenReturn("file1.txt");

        fileConvertController.sftp("~/", "pages", "docx");

        verify(fileHandler, times(1)).fetchFilenames(any());
        verify(cloudConvertHandler, times(1)).createConvertJobs(anyList(), any(), any());
    }

    @Test
    public void allComplete_not() throws Exception
    {
        JobResponse job1 = new JobResponse();
        JobResponse job2 = new JobResponse();
        JobResponse job3 = new JobResponse();

        when(cloudConvertHandler.checkJobCompletion(job1)).thenReturn(Status.FINISHED);
        when(cloudConvertHandler.checkJobCompletion(job2)).thenReturn(Status.ERROR);
        when(cloudConvertHandler.checkJobCompletion(job3)).thenReturn(Status.WAITING);

        assertFalse(fileConvertController.allComplete(Arrays.asList(job1, job2, job3)));
    }

    @Test
    public void allComplete() throws Exception
    {
        JobResponse job1 = new JobResponse();
        JobResponse job2 = new JobResponse();
        JobResponse job3 = new JobResponse();

        when(cloudConvertHandler.checkJobCompletion(job1)).thenReturn(Status.FINISHED);
        when(cloudConvertHandler.checkJobCompletion(job2)).thenReturn(Status.ERROR);
        when(cloudConvertHandler.checkJobCompletion(job3)).thenReturn(Status.FINISHED);

        assertTrue(fileConvertController.allComplete(Arrays.asList(job1, job2, job3)));
    }

    @Test
    public void countStatus_error() throws Exception
    {
        JobResponse job1 = new JobResponse();
        JobResponse job2 = new JobResponse();
        JobResponse job3 = new JobResponse();

        when(cloudConvertHandler.checkJobCompletion(job1)).thenReturn(Status.FINISHED);
        when(cloudConvertHandler.checkJobCompletion(job2)).thenReturn(Status.ERROR);
        when(cloudConvertHandler.checkJobCompletion(job3)).thenReturn(Status.FINISHED);

        assertEquals(1, fileConvertController.countStatus(Arrays.asList(job1, job2, job3), Status.ERROR));
    }

    @Test
    public void countStatus_finished() throws Exception
    {
        JobResponse job1 = new JobResponse();
        JobResponse job2 = new JobResponse();
        JobResponse job3 = new JobResponse();

        when(cloudConvertHandler.checkJobCompletion(job1)).thenReturn(Status.FINISHED);
        when(cloudConvertHandler.checkJobCompletion(job2)).thenReturn(Status.ERROR);
        when(cloudConvertHandler.checkJobCompletion(job3)).thenReturn(Status.FINISHED);

        assertEquals(2, fileConvertController.countStatus(Arrays.asList(job1, job2, job3), Status.FINISHED));
    }

    @Test
    public void countStatus_waiting() throws Exception
    {
        JobResponse job1 = new JobResponse();
        JobResponse job2 = new JobResponse();
        JobResponse job3 = new JobResponse();

        when(cloudConvertHandler.checkJobCompletion(job1)).thenReturn(Status.FINISHED);
        when(cloudConvertHandler.checkJobCompletion(job2)).thenReturn(Status.ERROR);
        when(cloudConvertHandler.checkJobCompletion(job3)).thenReturn(Status.FINISHED);

        assertEquals(0, fileConvertController.countStatus(Arrays.asList(job1, job2, job3), Status.WAITING));
    }
}