package org.openstreetmap.josm.plugins;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.tools.Logging;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
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

    /*
     *  Set up logger for UI event instrumentation
     */
    static {
        try {
            pluginLogger = Logger.getLogger(LOGGER_NAME);
            FileHandler fileHandler = new FileHandler(LOGFILE_NAME);
            pluginLogger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);

        } catch (IOException e) {
            Logging.error(e);
        }
    }

    /**
     * Override the default event loop to generate event time logs
     *
     * @param event
     */
    @Override
    protected void dispatchEvent(AWTEvent event) {
        long startNano = System.nanoTime();
        super.dispatchEvent(event);
        long endNano = System.nanoTime();
        actionEventLogStatement(event, startNano, endNano);
    }

    /**
     * Logs the time taken to perform action events
     *
     * @param event
     * @param startNano
     * @param endNano
     *
     * TODO:
     * Support formats easier for aggregating (Don't know what it is?)
     */
    private void actionEventLogStatement(AWTEvent event, long startNano, long endNano) {
        String eventMsg = getEventMsg(event);
        if (eventMsg != null &&
            !eventMsg.isEmpty()) {
            pluginLogger.info(eventMsg + " : " + (endNano - startNano));
        }
    }

    /**
     * Given a parent Component and position, returns the bottom most component at that position
     *
     * @param parent
     * @param posX
     * @param posY
     * @return
     */
    private Component getLeafLevelComponent (Component parent, int posX, int posY) {
        Component child = parent.getComponentAt(posX, posY);
        if (child == parent ||
                child == null) {
            return parent;
        } else {
            return getLeafLevelComponent(child, posX, posY);
        }
    }

    private String getEventMsg (AWTEvent event) {
        if (event instanceof ActionEvent) {
            return ((ActionEvent) event).getActionCommand();
        } else if ((event instanceof MouseEvent) &&
                ((event.getID() == MouseEvent.MOUSE_RELEASED) ||
                        (event.getID() == MouseEvent.MOUSE_CLICKED)) &&
                (event.getSource() instanceof Component)) {

            Component parentComponent = (Component) event.getSource();
            Component leaf = getLeafLevelComponent(parentComponent, ((MouseEvent) event).getX(), ((MouseEvent) event).getY());

            if (leaf instanceof JButton) {
                JosmAction eventAction = (JosmAction) ((JButton) leaf).getAction();
                return eventAction.getShortcut().getLongText();
            }
        }
        return null;
    }
}
