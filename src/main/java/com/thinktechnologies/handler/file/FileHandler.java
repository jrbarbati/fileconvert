package com.thinktechnologies.handler.file;

import com.jcraft.jsch.*;
import com.thinktechnologies.handler.file.exception.SftpHandlerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class FileHandler
{
    protected JSch jsch = new JSch();
    private Session session;
    private Channel channel;

    @Value("${sftp-server.host}")
    private String sftpHost;

    @Value("${sftp-server.username}")
    private String sftpUsername;

    @Value("${sftp-server.password}")
    private String sftpPassword;

    public List<File> fetchFilenames(String workingDirectory) throws SftpHandlerException
    {
        java.io.File folder = new java.io.File(workingDirectory);
        return Arrays.stream(folder.listFiles()).map(file -> new File(file.getName(), file.getPath())).collect(Collectors.toList());
//        ChannelSftp sftpChannel = openSftpConnection(sftpHost, sftpUsername, sftpPassword);
//
//        List<File> filenames = fetchAllFilenames(sftpChannel, workingDirectory);
//
//        if (session != null) session.disconnect();
//        if (channel != null) channel.disconnect();
//
//        return filenames;
    }

    protected ChannelSftp openSftpConnection(String server, String username, String password) throws SftpHandlerException
    {
        try
        {
            session = jsch.getSession(username, server, 22);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);
            session.setPassword(password);
            session.connect();

            channel = session.openChannel("sftp");
            channel.connect();

            return (ChannelSftp) channel;
        }
        catch (Exception e)
        {
            throw new SftpHandlerException(String.format("%s caught while opening SFTP connection to %s", e.getClass().getSimpleName(), server), e);
        }
    }

    protected List<File> fetchAllFilenames(ChannelSftp sftpChannel, String workingDirectory) throws SftpHandlerException
    {
        try
        {
            List<File> filenames = new ArrayList<>();
            Vector files = sftpChannel.ls(workingDirectory);

            for (Object file : files)
            {
                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) file;
                // TODO: If needed, here is how to find out if the entry is a directory -- entry.getAttrs().isDir();
                filenames.add(new File(entry.getFilename(), workingDirectory));
            }

            return filenames;
        }
        catch (Exception e)
        {
            throw new SftpHandlerException(String.format("%s encountered while retrieving filenames from sftp server", e.getClass().getSimpleName()), e);
        }
    }
}
