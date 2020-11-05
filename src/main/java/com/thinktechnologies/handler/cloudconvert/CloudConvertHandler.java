package com.thinktechnologies.handler.cloudconvert;

import com.cloudconvert.client.CloudConvertClient;
import com.cloudconvert.client.setttings.StringSettingsProvider;
import com.cloudconvert.dto.Status;
import com.cloudconvert.dto.request.*;
import com.cloudconvert.dto.response.JobResponse;
import com.cloudconvert.dto.response.TaskResponse;
import com.cloudconvert.dto.result.Result;
import com.google.common.collect.ImmutableMap;
import com.thinktechnologies.handler.cloudconvert.exception.CloudConvertHandlerException;
import com.thinktechnologies.handler.file.File;
import com.thinktechnologies.logger.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CloudConvertHandler
{
    private static final Logger log = new Logger(CloudConvertHandler.class);

    @Value("${cloud-convert.api-key}")
    private String apiKey;

    @Value("${cloud-convert.use-sandbox}")
    private Boolean useSandbox;

    @Value("${sftp-server.host}")
    private String sfptHost;

    @Value("${sftp-server.username}")
    private String sftpUsername;

    @Value("${sftp-server.password}")
    private String sftpPassword;

    private CloudConvertClient cloudConvertClient;

    public List<JobResponse> createConvertJobs(List<File> filenames, String inputFormat, String outputFormat) throws CloudConvertHandlerException
    {
        if (cloudConvertClient == null)
            cloudConvertClient = createClient();

        List<JobResponse> createdJobs = new ArrayList<>();

        for (File file : filenames)
        {
            if (!file.getExtension().equalsIgnoreCase(inputFormat))
                continue;

            String importTaskName = String.format("import-%s", file.getId());
            String convertTaskName = String.format("convert-%s", file.getId());
            String exportTaskName = String.format("export-%s", file.getId());

            try
            {
                InputStream f = getClass().getClassLoader().getResourceAsStream(file.getFullName());

                JobResponse createdJob = cloudConvertClient.jobs().create(
                        ImmutableMap.of(
                                importTaskName,
                                new UploadImportRequest(),
                                convertTaskName,
                                new ConvertFilesTaskRequest()
                                        .setInput(importTaskName)
                                        .setInputFormat(inputFormat)
                                        .setOutputFormat(outputFormat),
                                exportTaskName,
                                new UrlExportRequest()
                                        .setInput(convertTaskName)
                        )).getBody();

                if (createdJob == null)
                {
                    log.errorf("Error creating job for file: %s", file.getFullName());
                    continue;
                }

                TaskResponse importTask = createdJob.getTasks().stream().filter(task -> task.getName().equalsIgnoreCase(importTaskName)).collect(Collectors.toList()).get(0);

                Result<TaskResponse> upload = cloudConvertClient.importUsing().upload(new UploadImportRequest(), f);

                createdJobs.add(createdJob);
            }
            catch (Exception e)
            {
                log.errorf("%s -- %s while creating job for file %s\n", this.getClass().getName(), e.getClass().getSimpleName(), file.getFullName());
            }
        }

        return createdJobs;
    }

    public Status checkJobCompletion(JobResponse job) throws CloudConvertHandlerException
    {
        if (cloudConvertClient == null)
            cloudConvertClient = createClient();

        try
        {
            JobResponse response = cloudConvertClient.jobs().show(job.getId()).getBody();
            return response != null ? response.getStatus() : null;
        }
        catch (Exception e)
        {
            log.errorf("%s -- %s while getting status for job %s\n", this.getClass().getName(), e.getClass().getSimpleName(), job.getId());
            return null;
        }
    }

    protected CloudConvertClient createClient() throws CloudConvertHandlerException
    {
        try
        {
            return new CloudConvertClient(new StringSettingsProvider(apiKey, "", useSandbox));
        }
        catch (Exception e)
        {
            throw new CloudConvertHandlerException("Error encountered while creating Cloud Convert Client. Cannot Proceed.", e);
        }
    }
}
