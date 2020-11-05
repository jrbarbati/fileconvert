package com.thinktechnologies.handler.cloudconvert;

import com.cloudconvert.dto.Status;
import com.cloudconvert.dto.response.TaskResponse;
import com.thinktechnologies.handler.file.File;

public class Job
{
    private final File file;
    private final TaskResponse importTask;
    private final TaskResponse convertTask;
    private final TaskResponse exportTask;
    private Status importStatus;
    private Status convertStatus;
    private Status exportStatus;
    private String exportedFile;

    public Job(File file, TaskResponse importTask, TaskResponse convertTask, TaskResponse exportTask)
    {
        this.file = file;
        this.importTask = importTask;
        this.convertTask = convertTask;
        this.exportTask = exportTask;
    }

    public File getFile()
    {
        return file;
    }

    public TaskResponse getImportTask()
    {
        return importTask;
    }

    public TaskResponse getConvertTask()
    {
        return convertTask;
    }

    public TaskResponse getExportTask()
    {
        return exportTask;
    }

    public String getStatus()
    {
        return String.format("Import: %s\tConvert: %s\tExport: %s", this.importStatus, this.convertStatus, this.exportStatus);
    }

    public boolean isComplete()
    {
        return this.exportStatus == Status.FINISHED || this.exportStatus == Status.ERROR;
    }

    public boolean isError()
    {
        return this.exportStatus == Status.ERROR;
    }

    public boolean isSuccess()
    {
        return this.exportStatus == Status.FINISHED;
    }

    public void setImportStatus(Status status)
    {
        this.importStatus = status;
    }

    public void setConvertStatus(Status status)
    {
        this.convertStatus = status;
    }

    public void setExportStatus(Status status)
    {
        this.exportStatus = status;
    }

    public void setExportedFile(String filename)
    {
        this.exportedFile = filename;
    }

    public String getExportFile()
    {
        return this.exportedFile;
    }
}
