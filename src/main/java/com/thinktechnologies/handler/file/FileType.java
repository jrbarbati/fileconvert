package com.thinktechnologies.handler.file;

import com.google.common.collect.Sets;

import java.util.HashSet;

public enum FileType
{
    PAGES("pages"), NUMBERS("numbers"), KEYNOTE("key", "keynote"),
    DOCX("docx"), XLSX("xslx"), PPTX("pptx");

    HashSet<String> extensions;

    FileType(String... extensions)
    {
        this.extensions = Sets.newHashSet(extensions);
    }

    public HashSet<String> getExtensions()
    {
        return this.extensions;
    }
}
