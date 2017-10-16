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

    private static Properties pluginProperties = new Properties();
    private static Logger pluginLogger = null;

    // Set up logger for UI event instrumentation
    static {
        try {
            // Load up the properties
            InputStream inputStream = new FileInputStream("instrument-ui.properties");
            pluginProperties.load(inputStream);

            // Set up plugin logger
            pluginLogger = Logger.getLogger(pluginProperties.getProperty("loggername", "instrument-ui-logger"));
            FileHandler fileHandler = new FileHandler(pluginProperties.getProperty("logfile", "/tmp/instrument-ui.log"));
            pluginLogger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);

        } catch (FileNotFoundException e) {
            Logging.error(e);
        } catch (IOException ioEx) {
            Logging.error(ioEx);
        }
    }

    @Override
    protected void dispatchEvent (AWTEvent event) {
        long startNano = System.nanoTime();
        super.dispatchEvent(event);
        long endNano = System.nanoTime();

        /**
            TODO:
           - Read the file location from the plugin configuration
            - Support for different formats? (CSV, JSON)
         */
        pluginLogger.info("Time: " + ((endNano - startNano) / 1000000) + "ms, Event Type: " + event);
        if (endNano - startNano > 50000000)
            System.out.println( "Time: " + ((endNano - startNano) / 1000000) + "ms, Event Type: " + event);
    }
}
