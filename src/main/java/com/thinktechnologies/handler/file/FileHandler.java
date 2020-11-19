package com.thinktechnologies.handler.file;

import org.jetbrains.annotations.NotNull;
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
    private final static String DOT_REGEX = "\\.";

    public List<File> fetchFilenames(String workingDirectory, String currentExtension, String desiredExtension)
    {
        return fetchFilenames(new java.io.File(workingDirectory), currentExtension, desiredExtension);
    }

    protected List<File> fetchFilenames(java.io.File workingDirectory, String currentExtension, String desiredExtension)
    {
        List<File> files = new ArrayList<>();
        Map<String, FilePair> filenamePairs = buildFilePairs(workingDirectory, currentExtension, desiredExtension);

        for (java.io.File f : workingDirectory.listFiles())
        {
            if (f.isDirectory())
            {
                files.addAll(fetchFilenames(f, currentExtension, desiredExtension));
                continue;
            }

            String[] splitFilename = f.getName().split(DOT_REGEX);
            String extension = splitFilename.length > 1 ? splitFilename[1] : null;

            if (filenamePairs.get(f.getName()) != null && filenamePairs.get(f.getName()).isCompletePair())
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

    protected Map<String, FilePair> buildFilePairs(java.io.File workingDirectory, String currentExtension, String desiredExtension)
    {
        Map<String, FilePair> pairs = new HashMap<>();

        FileType macFileType = FileType.findTypeForExtension(currentExtension);
        FileType windowsFileType = FileType.findTypeForExtension(desiredExtension);

        List<String> macFiles = getFilenamesAsStream(workingDirectory, macFileType).collect(Collectors.toList());
        Set<String> windowsFiles = getFilenamesAsStream(workingDirectory, windowsFileType).collect(Collectors.toSet());

        for (String macFilename : macFiles)
            if (windowsFiles.contains(macFilename))
                pairs.put(
                        macFilename + "." + macFileType.getExtensions().stream().findFirst().orElse(""),
                        new FilePair(
                                macFilename + "." + macFileType.getExtensions().stream().findFirst().orElse(""),
                                macFilename + "." + windowsFileType.getExtensions().stream().findFirst().orElse("")
                        )
                );
            else
                pairs.put(
                        macFilename + "." + macFileType.getExtensions().stream().findFirst().orElse(""),
                        new FilePair(
                                macFilename + "." + macFileType.getExtensions().stream().findFirst().orElse(""),
                                null
                        )
                );

        return pairs;
    }

    @NotNull
    private Stream<String> getFilenamesAsStream(java.io.File workingDirectory, FileType fileType)
    {
        java.io.File[] files = workingDirectory.listFiles();
        return Arrays.stream(files).map(java.io.File::getName).filter(file -> {
            String[] split = file.split(DOT_REGEX);
            if (split.length <= 1)
                return false;

            return fileType.getExtensions().contains(split[1]);
        }).map(filename -> filename.split(DOT_REGEX)[0]);
    }
}
