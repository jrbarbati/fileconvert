package com.thinktechnologies.handler.cloudconvert;

import com.cloudconvert.client.CloudConvertClient;
import com.cloudconvert.client.setttings.StringSettingsProvider;
import com.cloudconvert.dto.request.ConvertFilesTaskRequest;
import com.cloudconvert.dto.request.UploadImportRequest;
import com.cloudconvert.dto.request.UrlExportRequest;
import com.cloudconvert.dto.response.TaskResponse;
import com.thinktechnologies.handler.cloudconvert.exception.CloudConvertHandlerException;
import com.thinktechnologies.handler.file.File;
import com.thinktechnologies.logger.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class CloudConvertHandler
{
    private static final Logger log = new Logger(CloudConvertHandler.class);

    @Value("${cloud-convert.api-key}")
    private String apiKey;

    @Value("${cloud-convert.use-sandbox}")
    private Boolean useSandbox;

    private CloudConvertClient cloudConvertClient;

    public List<Job> createConvertJobs(List<File> filenames, String inputFormat, String outputFormat) throws CloudConvertHandlerException
    {
        if (cloudConvertClient == null)
            cloudConvertClient = createClient();

        List<Job> createdJobs = new ArrayList<>();

        for (File file : filenames)
        {
            if (!file.getExtension().equalsIgnoreCase(inputFormat))
                continue;

            try
            {
                TaskResponse importTask = cloudConvertClient.importUsing().upload(new UploadImportRequest(), new java.io.File(file.getName())).getBody();

                if (importTask == null)
                {
                    log.errorf("Failed to create import task for file: %s", file.getName());
                    continue;
                }

                TaskResponse convertTask = cloudConvertClient.tasks().convert(
                        new ConvertFilesTaskRequest()
                                .setInput(importTask.getId())
                                .setInputFormat(inputFormat)
                                .setOutputFormat(outputFormat)
                ).getBody();

                if (convertTask == null)
                {
                    log.errorf("Failed to create convert task for file: %s", file.getName());
                    continue;
                }

                TaskResponse exportTask = cloudConvertClient.exportUsing().url(new UrlExportRequest().setInput(convertTask.getId())).getBody();

                if (exportTask == null)
                {
                    log.errorf("Failed to create export task for file: %s", file.getName());
                }

                createdJobs.add(new Job(file, importTask, convertTask, exportTask));
            }
            catch (Exception e)
            {
                log.errorf("%s -- %s while creating job for file %s\n", this.getClass().getName(), e.getClass().getSimpleName(), file.getName());
            }
        }

        return createdJobs;
    }

    public Job checkStatus(Job job) throws Exception
    {
        if (cloudConvertClient == null)
            cloudConvertClient = createClient();

        job.setImportStatus(Objects.requireNonNull(cloudConvertClient.tasks().show(job.getImportTask().getId()).getBody()).getStatus());
        job.setConvertStatus(Objects.requireNonNull(cloudConvertClient.tasks().show(job.getConvertTask().getId()).getBody()).getStatus());
        job.setExportStatus(Objects.requireNonNull(cloudConvertClient.tasks().show(job.getExportTask().getId()).getBody()).getStatus());

        return job;
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

    public InputStream getExportedFile(TaskResponse exportTask) throws Exception
    {
        exportTask = cloudConvertClient.tasks().show(exportTask.getId()).getBody();
        return cloudConvertClient.files().download(exportTask.getResult().getFiles().get(0).get("url")).getBody();
    }

    public String getExportedFileName(TaskResponse exportTask) throws Exception
    {
        exportTask = cloudConvertClient.tasks().show(exportTask.getId()).getBody();
        return exportTask.getResult().getFiles().get(0).get("filename");
    }
}
