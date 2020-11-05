package com.thinktechnologies.handler.file;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.thinktechnologies.handler.file.exception.SftpHandlerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class FileHandlerTest
{
    @Spy
    @Autowired
    FileHandler fileHandler;

    private JSch mockJsch = mock(JSch.class);

    @Before
    public void setup()
    {
        fileHandler.jsch = mockJsch;
    }

    @Test
    public void handle() {}

    @Test
    public void openSftpConnection() throws Exception
    {
        Session mockSession = mock(Session.class);
        ChannelSftp mockChannel = mock(ChannelSftp.class);

        when(mockJsch.getSession(any(), any(), eq(22))).thenReturn(mockSession);
        when(mockSession.openChannel(any())).thenReturn(mockChannel);

        ChannelSftp channelSftp = fileHandler.openSftpConnection("SERVER", "USER", "PASS");

        assertEquals(mockChannel, channelSftp);

        verify(mockJsch, times(1)).getSession(any(), any(), eq(22));
        verify(mockSession, times(1)).openChannel("sftp");
    }

    @Test
    public void openSftpConnection_exception() throws Exception
    {
        Session mockSession = mock(Session.class);

        when(mockJsch.getSession(any(), any(), eq(22))).thenThrow(new JSchException("ERROR"));

        try
        {
            fileHandler.openSftpConnection("SERVER", "USER", "PASS");
        }
        catch (Exception e)
        {
            assertTrue(e instanceof SftpHandlerException);
            assertEquals("JSchException caught while opening SFTP connection to SERVER", e.getMessage());
        }

        verify(mockJsch, times(1)).getSession(any(), any(), eq(22));
        verify(mockSession, times(0)).openChannel("sftp");
    }

    @Test
    public void fetchAllFilenames_emptyList() throws Exception
    {
        ChannelSftp mockChannel = mock(ChannelSftp.class);

        when(mockChannel.ls(any())).thenReturn(new Vector());

        List<File> filenames = fileHandler.fetchAllFilenames(mockChannel, "~/");

        assertNotNull(filenames);
        assertTrue(filenames.isEmpty());
    }

    @Test
    public void fetchAllFilenames() throws Exception
    {
        ChannelSftp mockChannel = mock(ChannelSftp.class);
        ChannelSftp.LsEntry mockEntry = mock(ChannelSftp.LsEntry.class);

        when(mockChannel.ls(any())).thenReturn(new Vector(Collections.singletonList(mockEntry)));
        when(mockEntry.getFilename()).thenReturn("File1.txt");

        List<File> filenames = fileHandler.fetchAllFilenames(mockChannel, "~/");

        assertNotNull(filenames);
        assertFalse(filenames.isEmpty());
        assertEquals(1, filenames.size());
        assertEquals("File1.txt", filenames.get(0).getFilename());
    }
}