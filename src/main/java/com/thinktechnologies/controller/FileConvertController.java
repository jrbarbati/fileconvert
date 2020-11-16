package com.thinktechnologies.controller;

import com.thinktechnologies.handler.cloudconvert.CloudConvertHandler;
import com.thinktechnologies.handler.cloudconvert.Job;
import com.thinktechnologies.handler.file.File;
import com.thinktechnologies.handler.file.FileHandler;
import com.thinktechnologies.logger.Logger;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Api
@RestController
public class FileConvertController
{
    private static final Logger log = new Logger(FileConvertController.class);

    @Autowired
    FileHandler fileHandler;

    @Autowired
    CloudConvertHandler cloudConvertHandler;

    @GetMapping(value = "/api/v1/convert")
    public ResponseEntity convertFiles(@RequestParam("directory") String workingDirectory,
                                       @RequestParam("currentExtension") String currentExtension,
                                       @RequestParam("desiredExtension") String desiredExtension) throws Exception
    {
        log.info("Fetching all filenames under given directory recursively.");
        List<File> filenames = fileHandler.fetchFilenames(workingDirectory, currentExtension, desiredExtension);

        log.infof("Fetched filenames, starting jobs for all files with extension %s that have not already been converted", currentExtension);
        List<Job> jobs = cloudConvertHandler.createConvertJobs(filenames, currentExtension, desiredExtension);

        log.infof("Successfully started %d jobs.\n", jobs.size());

        List<Job> completedJobs = new ArrayList<>();

        while (!jobs.isEmpty())
        {
            Iterator<Job> jobIterator = jobs.iterator();

            while (jobIterator.hasNext())
            {
                Job job = jobIterator.next();

                job = cloudConvertHandler.checkStatus(job);

                log.infof("%s: %s", job.getFile().getName(), job.getStatus());

                if (job.isComplete())
                {
                    if (!job.isError())
                        job.setExportedFile(fileHandler.download(
                                cloudConvertHandler.getExportedFile(job.getExportTask()),
                                new File(cloudConvertHandler.getExportedFileName(job.getExportTask())),
                                job.getFile()
                        ));
                    completedJobs.add(job);
                    jobIterator.remove();
                }
            }
        }

        return new ResponseEntity<>(buildReport(completedJobs), HttpStatus.OK);
    }

    protected String buildReport(List<Job> jobs)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("<h1>Conversion Report:</h1> <br>");

        for (Job job : jobs)
            sb.append(String.format("%s: %s Saved as: %s <br><br>", job.getFile().getName(), job.getStatus(), job.getExportFile() != null ? job.getExportFile() : "ERROR during conversion or saving"));

        return sb.toString();
    }
}
