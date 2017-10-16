package org.openstreetmap.josm.plugins;

import org.openstreetmap.josm.tools.Logging;
import sun.rmi.runtime.Log;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This class creates an alternative <code>EventQueue</code> which adds logging to every AWTEvent dispatched.
 * The default <code>EventQueue</code> is overridden in the {@link InstrumentationPlugin}.
 *
 * @author udit
 */
public class InstrumentedEventQueue extends EventQueue {
    private static final String LOGGER_NAME = "instrument-ui-logger";
    private static final String LOGFILE_NAME = "/tmp/instrument-ui.log";
    private static Logger pluginLogger = null;

    // Set up logger for UI event instrumentation
    static {
        try {
            // Set up plugin logger
            pluginLogger = Logger.getLogger(LOGGER_NAME);
            FileHandler fileHandler = new FileHandler(LOGFILE_NAME);
            pluginLogger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);

        } catch (IOException e) {
            Logging.error(e);
        }
    }

    @Override
    protected void dispatchEvent (AWTEvent event) {
        long startNano = System.nanoTime();
        super.dispatchEvent(event);
        long endNano = System.nanoTime();

        /**
         TODO:
         - Support for different formats? (CSV, JSON)
         */
        pluginLogger.info("Time: " + (endNano - startNano) + "ns, Event Type: " + event);
    }
}
