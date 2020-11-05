package com.thinktechnologies.handler.cloudconvert;

import com.cloudconvert.client.CloudConvertClient;
import com.cloudconvert.dto.Status;
import com.cloudconvert.dto.response.JobResponse;
import com.cloudconvert.dto.result.Result;
import com.cloudconvert.resource.sync.JobsResource;
import com.thinktechnologies.handler.cloudconvert.exception.CloudConvertHandlerException;
import com.thinktechnologies.handler.file.File;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class CloudConvertHandlerTest {

    @Autowired
    CloudConvertHandler cloudConvertHandler;

    @Before
    public void setup()
    {
        cloudConvertHandler = spy(cloudConvertHandler);
    }

    @Test
    public void createConvertJobs_createClientFailure() throws Exception
    {
        doThrow(new CloudConvertHandlerException("BAD", null)).when(cloudConvertHandler).createClient();

        try
        {
            cloudConvertHandler.createConvertJobs(new ArrayList<>(), "pages", "docx");
        }
        catch (Exception e)
        {
            assertTrue(e instanceof CloudConvertHandlerException);
            assertEquals("BAD", e.getMessage());
        }
    }

    @Test
    public void createConvertJobs() throws Exception
    {
        CloudConvertClient mockCloudConvertClient = mock(CloudConvertClient.class);
        JobsResource mockJobsResource = mock(JobsResource.class);
        Result mockResult = mock(Result.class);
        JobResponse mockJobResponse = mock(JobResponse.class);

        doReturn(mockCloudConvertClient).when(cloudConvertHandler).createClient();
        when(mockCloudConvertClient.jobs()).thenReturn(mockJobsResource);
        when(mockJobsResource.create(any())).thenReturn(mockResult);
        when(mockResult.getBody()).thenReturn(mockJobResponse);

        when(mockJobResponse.getId()).thenReturn("1").thenReturn("2");

        List<JobResponse> jobs = cloudConvertHandler.createConvertJobs(Arrays.asList(new File("file1.pages"), new File("file2.pages")), "pages", "docx");

        assertEquals("1", jobs.get(0).getId());
        assertEquals("2", jobs.get(1).getId());

        verify(mockCloudConvertClient, times(2)).jobs();
        verify(mockJobsResource, times(2)).create(any());
    }

    @Test
    public void createConvertJobs_failedToCreate() throws Exception
    {
        CloudConvertClient mockCloudConvertClient = mock(CloudConvertClient.class);
        JobsResource mockJobsResource = mock(JobsResource.class);

        doReturn(mockCloudConvertClient).when(cloudConvertHandler).createClient();
        when(mockCloudConvertClient.jobs()).thenReturn(mockJobsResource);
        when(mockJobsResource.create(any())).thenThrow(new IOException());

        List<JobResponse> jobs = cloudConvertHandler.createConvertJobs(Arrays.asList(new File("file1.pages"), new File("file2.pages")), "pages", "docx");

        assertTrue(jobs.isEmpty());

        verify(mockCloudConvertClient, times(2)).jobs();
        verify(mockJobsResource, times(2)).create(any());
    }

    @Test
    public void checkJobCompletion() throws Exception
    {
        CloudConvertClient mockCloudConvertClient = mock(CloudConvertClient.class);
        JobsResource mockJobsResource = mock(JobsResource.class);
        Result mockResult = mock(Result.class);
        JobResponse mockJobResponse = mock(JobResponse.class);

        doReturn(mockCloudConvertClient).when(cloudConvertHandler).createClient();
        when(mockCloudConvertClient.jobs()).thenReturn(mockJobsResource);
        when(mockJobsResource.show(any())).thenReturn(mockResult);
        when(mockResult.getBody()).thenReturn(mockJobResponse);
        when(mockJobResponse.getId()).thenReturn("1");
        when(mockJobResponse.getStatus()).thenReturn(Status.FINISHED);

        Status status = cloudConvertHandler.checkJobCompletion(mockJobResponse);

        assertNotNull(status);
        assertEquals(Status.FINISHED, status);
    }

    @Test
    public void checkJobCompletion_error() throws Exception
    {
        CloudConvertClient mockCloudConvertClient = mock(CloudConvertClient.class);
        JobsResource mockJobsResource = mock(JobsResource.class);
        JobResponse mockJobResponse = mock(JobResponse.class);

        doReturn(mockCloudConvertClient).when(cloudConvertHandler).createClient();
        when(mockCloudConvertClient.jobs()).thenReturn(mockJobsResource);
        when(mockJobsResource.show(any())).thenThrow(new IOException());
        when(mockJobResponse.getId()).thenReturn("1");

        Status status = cloudConvertHandler.checkJobCompletion(mockJobResponse);

        assertNull(status);
    }
}