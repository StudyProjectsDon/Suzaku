package suzaku.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import suzaku.Suzaku;

/**
 * @author TwentyBytes
 * created in 14.05.2023
 */
public interface Loggable {

    default Logger getLogger() {
        return Suzaku.getInstance().getLogger();
    }

    default void log(Level level, Object... messages) {
        for (Object message : messages) {
            getLogger().log(level, message.toString());
        }
    }

    default void error(Object... messages) {
        log(Level.ERROR, messages);
    }

    default void info(Object... messages) {
        log(Level.INFO, messages);
    }

    default void warn(Object... messages) {
        log(Level.WARN, messages);
    }

}
