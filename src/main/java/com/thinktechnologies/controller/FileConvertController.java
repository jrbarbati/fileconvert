package com.thinktechnologies.controller;

import com.cloudconvert.dto.Status;
import com.cloudconvert.dto.response.JobResponse;
import com.thinktechnologies.handler.cloudconvert.CloudConvertHandler;
import com.thinktechnologies.handler.cloudconvert.exception.CloudConvertHandlerException;
import com.thinktechnologies.handler.file.File;
import com.thinktechnologies.handler.file.FileHandler;
import com.thinktechnologies.logger.Logger;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
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
    FileHandler fileHandler;

    @Autowired
    CloudConvertHandler cloudConvertHandler;

    @GetMapping(value = "/api/v1/convert/sftp")
    public void sftp(@RequestParam("directory") String workingDirectory,
                     @RequestParam("currentExtension") String currentExtension,
                     @RequestParam("desiredExtension") String desiredExtension) throws Exception
    {
        List<File> filenames = fileHandler.fetchFilenames(workingDirectory);
        List<JobResponse> jobs = cloudConvertHandler.createConvertJobs(filenames, currentExtension, desiredExtension);

        log.infof("Successfully started %d jobs.\n", jobs.size());

        while (!allComplete(jobs))
        {
            log.info("Checking jobs statuses.");

            long waiting = countStatus(jobs, Status.WAITING);
            long processing = countStatus(jobs, Status.PROCESSING);
            long finished = countStatus(jobs, Status.FINISHED);
            long error = countStatus(jobs, Status.ERROR);

            log.infof("Waiting - %d/%d", waiting, jobs.size());
            log.infof("Processing - %d/%d", processing, jobs.size());
            log.infof("Finished - %d/%d", finished, jobs.size());
            log.infof("Error - %d/%d", error, jobs.size());
            log.infof("Unknown - %d/%d", jobs.size() - (waiting + processing + finished + error), jobs.size());
        }

        log.infof("Finished all %d jobs.\n", jobs.size());
        log.info("List of Errored Files: ");

        for (JobResponse job : jobs)
            if (job.getStatus().equals(Status.ERROR))
                log.info(job.getTasks().get(0).getResult().getFiles().get(0).get("filename"));
    }

    protected boolean allComplete(List<JobResponse> jobs)
    {
        return jobs.stream().allMatch(job -> {
            try
            {
                Status status = cloudConvertHandler.checkJobCompletion(job);
                return Status.FINISHED.equals(status) || Status.ERROR.equals(status);
            }
            catch (CloudConvertHandlerException e)
            {
                return false;
            }
        });
    }

    protected long countStatus(List<JobResponse> jobs, Status status)
    {
        return jobs.stream().map(job -> {
            try
            {
                Status s = cloudConvertHandler.checkJobCompletion(job);

                return s != null ? s.toString() : "UKNOWN";
            }
            catch (CloudConvertHandlerException e)
            {
                return "UNKNOWN";
            }
        }).filter(s -> s.equals(status.toString())).count();
    }
}
