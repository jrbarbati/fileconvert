package com.thinktechnologies.handler.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.thinktechnologies.handler.sftp.exception.SftpHandlerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class SftpHandlerTest
{
    @Spy
    @Autowired
    SftpHandler sftpHandler;

    private JSch mockJsch = mock(JSch.class);

    @Before
    public void setup()
    {
        sftpHandler.jsch = mockJsch;
    }

    @Test
    public void download()
    {

    }

    @Test
    public void openSftpConnection() throws Exception
    {
        Session mockSession = mock(Session.class);
        ChannelSftp mockChannel = mock(ChannelSftp.class);

        when(mockJsch.getSession(any(), any(), eq(22))).thenReturn(mockSession);
        when(mockSession.openChannel(any())).thenReturn(mockChannel);

        ChannelSftp channelSftp = sftpHandler.openSftpConnection("SERVER", "USER", "PASS");

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
            sftpHandler.openSftpConnection("SERVER", "USER", "PASS");
        }
        catch (Exception e)
        {
            assertTrue(e instanceof SftpHandlerException);
            assertEquals("JSchException caught while opening SFTP connection to SERVER", e.getMessage());
        }

        verify(mockJsch, times(1)).getSession(any(), any(), eq(22));
        verify(mockSession, times(0)).openChannel("sftp");
    }
}
