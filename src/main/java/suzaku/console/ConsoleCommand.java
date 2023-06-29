package suzaku.console;

import org.apache.logging.log4j.Logger;
import suzaku.util.Loggable;
import suzaku.util.Requester;

public interface ConsoleCommand extends Requester, Loggable {

    String[] names();

    void execute(Logger logger, String full, String label, String... args);

}
