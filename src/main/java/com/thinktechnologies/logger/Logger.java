package com.thinktechnologies.logger;

public class Logger
{
    private Class<?> clazz;

    public Logger(Class<?> clazz)
    {
        this.clazz = clazz;
    }

    public void info(String message)
    {
        log(message);
    }

    public void infof(String format, Object... args)
    {
        logf(format, args);
    }

    public void error(String message)
    {
        log(message);
    }

    public void errorf(String format, Object... args)
    {
        logf(format, args);
    }

    private void log(String message)
    {
        System.out.printf("%s -- %s\n", this.clazz.getName(), message);
    }

    private void logf(String format, Object... args)
    {
        System.out.printf("%s -- %s\n", this.clazz.getName(), String.format(format, args));
    }
}
