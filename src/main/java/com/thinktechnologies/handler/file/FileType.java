package com.thinktechnologies.handler.file;

import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public enum FileType
{
    PAGES("pages"), NUMBERS("numbers"), KEYNOTE("key", "keynote"),
    DOCX("docx"), XLSX("xlsx"), PPTX("pptx");

    HashSet<String> extensions;

    FileType(String... extensions)
    {
        this.extensions = Sets.newHashSet(extensions);
    }

    public HashSet<String> getExtensions()
    {
        return this.extensions;
    }

    public static FileType findTypeForExtension(String extension)
    {
        if (PAGES.getExtensions().contains(extension))
            return PAGES;

        if (NUMBERS.getExtensions().contains(extension))
            return NUMBERS;

        if (KEYNOTE.getExtensions().contains(extension))
            return KEYNOTE;

        if (DOCX.getExtensions().contains(extension))
            return DOCX;

        if (XLSX.getExtensions().contains(extension))
            return XLSX;

        if (PPTX.getExtensions().contains(extension))
            return PPTX;

        return null;
    }

    public static List<String> allExtensions() {
        return Arrays.stream(FileType.values()).flatMap(fileType -> fileType.getExtensions().stream()).collect(Collectors.toList());
    }
}
