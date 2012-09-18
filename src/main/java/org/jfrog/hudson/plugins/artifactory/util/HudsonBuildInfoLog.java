package org.jfrog.hudson.plugins.artifactory.util;

import hudson.model.BuildListener;
import org.jfrog.build.api.util.Log;

/**
 * Wrapper for Hudson build logger, records log messages from BuildInfo
 *
 * @author Shay Yaakov
 */
public class HudsonBuildInfoLog implements Log {
    private BuildListener listener;

    public HudsonBuildInfoLog(BuildListener listener) {
        this.listener = listener;
    }

    public void debug(String message) {
        listener.getLogger().println(message);
    }

    public void info(String message) {
        listener.getLogger().println(message);
    }

    public void warn(String message) {
        listener.getLogger().println(message);
    }

    public void error(String message) {
        listener.getLogger().println(message);
    }

    public void error(String message, Throwable e) {
        listener.getLogger().println(message);
    }
}
