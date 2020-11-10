package com.thinktechnologies.handler.sftp;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.thinktechnologies.handler.file.File;
import com.thinktechnologies.handler.sftp.exception.SftpHandlerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Properties;

@Component
public class SftpHandler
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

    public String download(InputStream exportedFile, String targetDirectory, File outputFile, File inputFile) throws Exception
    {
        try {
            ChannelSftp sftp = openSftpConnection(sftpHost, sftpUsername, sftpPassword);

            String[] split = inputFile.getName().split("[/|\\\\]");

            String outputFilename = targetDirectory + "/" + split[split.length - 1].replace(inputFile.getExtension(), outputFile.getExtension());
            sftp.put(exportedFile, outputFilename);

            return outputFilename;
        }
        catch (Exception e)
        {
            throw new SftpHandlerException(String.format("%s encountered while putting file: %s to sftp server", e.getClass().getSimpleName(), outputFile.getName()), e);
        }
    }

    // TODO: Finish out this if we decide to SFTP drop into windows server.
    public boolean doesFileExist(String filename, String targetDirectory) throws Exception
    {
        ChannelSftp sftp = openSftpConnection(sftpHost, sftpUsername, sftpPassword);
        String[] split = filename.split("[/|\\\\]");

        filename = targetDirectory + "/" + split[split.length - 1];

        return false;
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
}