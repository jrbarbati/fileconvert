package com.thinktechnologies.handler.file;

import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FileHandler
{
    private final static String DOT_REGEX = "\\.";

    public List<File> fetchFilenames(String workingDirectory, String currentExtension, String desiredExtension)
    {
        return fetchAllFilenames(new java.io.File(workingDirectory), currentExtension, desiredExtension);
    }

    protected List<File> fetchAllFilenames(java.io.File workingDirectory, String currentExtension, String desiredExtension)
    {
        List<File> files = new ArrayList<>();
        Map<String, Long> filenameCounts = countFilenames(workingDirectory);

        for (java.io.File f : workingDirectory.listFiles())
        {
            if (f.isDirectory())
            {
                files.addAll(fetchAllFilenames(f, currentExtension, desiredExtension));
                continue;
            }

            String[] splitFilename = f.getName().split(DOT_REGEX);

            String filenameWithoutExtension = splitFilename[0];
            String extension = splitFilename.length > 1 ? splitFilename[1] : null;

            if (filenameCounts.get(filenameWithoutExtension) != null && filenameCounts.get(filenameWithoutExtension) > 1)
                continue;

            if (!currentExtension.equalsIgnoreCase(extension) || desiredExtension.equalsIgnoreCase(extension))
                continue;

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

    protected Map<String, Long> countFilenames(java.io.File workingDirectory)
    {
        return Arrays.stream(workingDirectory.listFiles()).map(file -> file.getName().split(DOT_REGEX)[0]).collect(Collectors.groupingBy(f -> f, Collectors.counting()));
    }
}
