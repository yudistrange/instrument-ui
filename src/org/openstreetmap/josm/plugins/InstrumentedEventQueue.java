package org.openstreetmap.josm.plugins;

import com.sun.nio.zipfs.ZipPath;
import org.openstreetmap.josm.tools.Logging;

import java.awt.EventQueue;
import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * This class creates an alternative <code>EventQueue</code> which adds logging to every AWTEvent dispatched.
 * The default <code>EventQueue</code> is overridden in the {@link InstrumentationPlugin}.
 *
 * @author udit
 */
public class InstrumentedEventQueue extends EventQueue {

    {
        writer.write();
    }

    @Override
    protected void dispatchEvent (AWTEvent event) {
        long startNano = System.nanoTime();
        super.dispatchEvent(event);
        long endNano = System.nanoTime();

        /**
            TODO:
            - Log to file instead of stdout
            - Read the file location from the plugin configuration
            - Support for different formats? (CSV, JSON)
         */
        if (endNano - startNano > 50000000)
            System.out.println( "Time: " + ((endNano - startNano) / 1000000) + "ms, Event Type: " + event);
    }
}
