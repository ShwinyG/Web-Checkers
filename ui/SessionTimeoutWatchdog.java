/*
Filename: SessionTimeoutWatchdog.java
Java_Version: 8 - current
Apache_Maven_Version: 3.6.2
Spark_Version: 2.8.0
 */

package com.webcheckers.ui;

import java.util.Objects;
import java.util.logging.Logger;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.webcheckers.Model.LobbyModel;
import com.webcheckers.appl.PlayerServices;

/**
 * Whenever an instance of a class that implements {@linkplain HttpSessionBindingListener}
 * gets set as the value for a session attribute, the valueBound() method gets called.
 * Any time that the attribute is removed, set to another value, or the session is
 * invalidated, the valueUnbound() method gets called.
 *
 * @author <a href='mailto:bdbvse@rit.edu'>Bryan Basham</a>
 * @author Ashwin Gowda
 * @author Garrett Rudolfs
 * @author Joseph Becker
 * @author Julie Sojkowski
 */
public class SessionTimeoutWatchdog implements HttpSessionBindingListener {
    private static final Logger LOG = Logger.getLogger(SessionTimeoutWatchdog.class.getName());

    private final PlayerServices playerServices;
    private final String ip;

    public SessionTimeoutWatchdog(final PlayerServices playerServices, final String ip) {
        LOG.fine("Watch dog created.");
        this.playerServices = Objects.requireNonNull(playerServices);
        this.ip = ip;
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        // ignore this event
        LOG.fine("Player session started.");
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        // the session is being terminated do some cleanup
        //playerServices.endSession(this.ip);
        //
        LOG.fine("Player session ended.");
    }
}
