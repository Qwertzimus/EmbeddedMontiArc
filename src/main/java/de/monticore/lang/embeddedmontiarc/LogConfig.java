package de.monticore.lang.embeddedmontiarc;

import de.se_rwth.commons.logging.Log;

/**
 * @author Sascha Schneiders
 */
public class LogConfig extends Log {
    static Log log;
    boolean disableOutput = true;

    static {
        log = new LogConfig();
        Log.setLog(log);
    }

    @Override
    protected void doInfo(String msg, Throwable t, String logName) {
        if (!disableOutput) {
            super.doInfo(msg, t, logName);
        }
    }

    @Override
    protected void doInfo(String msg, String logName) {
        if (!disableOutput) {
            super.doInfo(msg, logName);
        }
    }


    @Override
    protected void doDebug(String msg, Throwable t, String logName) {
        if (!disableOutput) {
            super.doDebug(msg, t, logName);
        }
    }

    @Override
    protected void doDebug(String msg, String logName) {
        if (!disableOutput) {
            super.doDebug(msg, logName);
        }
    }

}
