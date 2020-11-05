package com.thinktechnologies.handler.cloudconvert;

import com.cloudconvert.client.CloudConvertClient;
import com.cloudconvert.dto.request.UploadImportRequest;
import com.cloudconvert.dto.response.TaskResponse;
import com.cloudconvert.dto.result.Result;
import com.cloudconvert.resource.sync.ExportFilesResource;
import com.cloudconvert.resource.sync.ImportFilesResource;
import com.cloudconvert.resource.sync.TasksResource;
import com.thinktechnologies.handler.cloudconvert.exception.CloudConvertHandlerException;
import com.thinktechnologies.handler.file.File;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
        ImportFilesResource mockImportFilesResource = mock(ImportFilesResource.class);
        TasksResource mockTasksResource = mock(TasksResource.class);
        ExportFilesResource mockExportFilesResource = mock(ExportFilesResource.class);

        doReturn(mockCloudConvertClient).when(cloudConvertHandler).createClient();

        when(mockCloudConvertClient.importUsing()).thenReturn(mockImportFilesResource);
        when(mockImportFilesResource.upload(any(UploadImportRequest.class), any(java.io.File.class))).thenReturn(Result.<TaskResponse>builder().body(new TaskResponse()).build());
        when(mockCloudConvertClient.tasks()).thenReturn(mockTasksResource);
        when(mockTasksResource.convert(any())).thenReturn(Result.<TaskResponse>builder().body(new TaskResponse()).build());
        when(mockCloudConvertClient.exportUsing()).thenReturn(mockExportFilesResource);
        when(mockExportFilesResource.url(any())).thenReturn(Result.<TaskResponse>builder().body(new TaskResponse()).build());

        List<Job> jobs = cloudConvertHandler.createConvertJobs(Arrays.asList(new File("file1.pages"), new File("file2.pages")), "pages", "docx");

        assertEquals("file1.pages", jobs.get(0).getFile().getName());
        assertEquals("file2.pages", jobs.get(1).getFile().getName());
    }

    @Test
    public void createConvertJobs_failedToCreate() throws Exception
    {
        List<Job> jobs = cloudConvertHandler.createConvertJobs(Arrays.asList(new File("file1.pages"), new File("file2.pages")), "pages", "docx");

        assertTrue(jobs.isEmpty());
    }
}