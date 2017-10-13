package org.openstreetmap.josm.plugins;

import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

import java.awt.*;

/**
 * The instrumentation plugin replaces the default AWT EventQueue with the custom queue defined in
 * {@link In}
 *
 * @author udit
 */
public class InstrumentationPlugin extends Plugin {
    public InstrumentationPlugin(PluginInformation info) {
        super(info);
        EventQueue customEventQueue = new InstrumentedEventQueue();
        EventQueue applicationQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        applicationQueue.push(customEventQueue);
    }
}
