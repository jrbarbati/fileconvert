package com.thinktechnologies.handler.file;

import org.apache.commons.io.FileUtils;
import org.apache.tika.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileHandler
{
    public List<File> fetchFilenames(String workingDirectory)
    {
        return fetchAllFilenames(new java.io.File(workingDirectory));
    }

    protected List<File> fetchAllFilenames(java.io.File workingDirectory)
    {
        List<File> files = new ArrayList<>();

        for (java.io.File f : workingDirectory.listFiles())
        {
            if (f.isDirectory())
            {
                files.addAll(fetchAllFilenames(f));
                continue;
            }

            files.add(new File(f.getPath()));
        }

        return files;
    }

    public String download(InputStream exportedFile, File outputFile, File inputFile) throws Exception
    {
        byte[] buffer = new byte[exportedFile.available()];
        exportedFile.read(buffer);

        String outputFilename = inputFile.getName().replace(inputFile.getExtension(), outputFile.getExtension());

        java.io.File file = new java.io.File(outputFilename);
        OutputStream outputStream = new FileOutputStream(file);
        outputStream.write(buffer);

        return outputFilename;
    }
}
