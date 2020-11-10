package com.thinktechnologies.controller;

import com.thinktechnologies.handler.cloudconvert.CloudConvertHandler;
import com.thinktechnologies.handler.cloudconvert.Job;
import com.thinktechnologies.handler.file.File;
import com.thinktechnologies.handler.file.FileHandler;
import com.thinktechnologies.handler.sftp.SftpHandler;
import com.thinktechnologies.logger.Logger;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api
@RestController
public class FileConvertController
{
    private static final Logger log = new Logger(FileConvertController.class);

    @Autowired
    SftpHandler sftpHandler;

    @Autowired
    FileHandler fileHandler;

    @Autowired
    CloudConvertHandler cloudConvertHandler;

    @GetMapping(value = "/api/v1/convert")
    public ResponseEntity convertFiles(@RequestParam("sourceDirectory") String sourceDirectory,
                                       @RequestParam("targetDirectory") String targetDirectory,
                                       @RequestParam("currentExtension") String currentExtension,
                                       @RequestParam("desiredExtension") String desiredExtension) throws Exception
    {
        List<File> filenames = fileHandler.fetchFilenames(sourceDirectory);
        List<Job> jobs = cloudConvertHandler.createConvertJobs(filenames, currentExtension, desiredExtension, targetDirectory);

        log.infof("Successfully started %d jobs.\n", jobs.size());

        int completedCount = 0;

        while (completedCount < jobs.size())
        {
            for (Job job : jobs)
            {
                job = cloudConvertHandler.checkStatus(job);

                log.infof("%s: %s", job.getFile().getName(), job.getStatus());

                if (job.isComplete())
                {
                    if (!job.isError())
                        job.setExportedFile(sftpHandler.download(
                                cloudConvertHandler.getExportedFile(job.getExportTask()),
                                targetDirectory,
                                new File(cloudConvertHandler.getExportedFileName(job.getExportTask())),
                                job.getFile()
                        ));
                    completedCount++;
                }
            }
        }

        return new ResponseEntity<>(buildReport(jobs), HttpStatus.OK);
    }

    protected String buildReport(List<Job> jobs)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("<h1>Conversion Report:</h1> <br>");

        for (Job job : jobs)
            sb.append(String.format("%s: %s Saved as: %s <br>", job.getFile().getName(), job.getStatus(), job.getExportFile()));

        return sb.toString();
    }
}
