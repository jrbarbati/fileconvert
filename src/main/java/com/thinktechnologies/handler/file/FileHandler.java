package com.thinktechnologies.handler.file;

import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class FileHandler
{
    private final static Map<FileType, FileType> FILE_TYPE_PAIRS = new HashMap<FileType, FileType>() {{
       put(FileType.PAGES, FileType.DOCX);
       put(FileType.DOCX, FileType.PAGES);
       put(FileType.NUMBERS, FileType.XLSX);
       put(FileType.XLSX, FileType.NUMBERS);
       put(FileType.KEYNOTE, FileType.PPTX);
       put(FileType.PPTX, FileType.KEYNOTE);
    }};

    private final static String DOT_REGEX = "\\.";

    public List<File> fetchFilenames(String workingDirectory, String currentExtension, String desiredExtension)
    {
        return fetchAllFilenames(new java.io.File(workingDirectory), currentExtension, desiredExtension);
    }

    protected List<File> fetchAllFilenames(java.io.File workingDirectory, String currentExtension, String desiredExtension)
    {
        List<File> files = new ArrayList<>();
        Map<String, FilePair> filenamePairs = buildFilePairs(workingDirectory);

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

            if (filenamePairs.get(filenameWithoutExtension) != null && filenamePairs.get(filenameWithoutExtension).isCompletePair())
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

    protected Map<String, FilePair> buildFilePairs(java.io.File workingDirectory)
    {
        Map<String, FilePair> pairs = new HashMap<>();
        Stream<java.io.File> fileStream = Arrays.stream(workingDirectory.listFiles());

        Set<String> filenames = fileStream.map(java.io.File::getName).collect(Collectors.toSet());
        Iterator<java.io.File> fileIterator = fileStream.iterator();

        while(fileIterator.hasNext())
        {
            String filename = fileIterator.next().getName();
        }

        return pairs;
    }
}
