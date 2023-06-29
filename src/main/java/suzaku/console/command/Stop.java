package suzaku.console.command;

import org.apache.logging.log4j.Logger;
import suzaku.Suzaku;
import suzaku.console.ConsoleCommand;

/**
 * @author TwentyBytes
 * created in 29.06.2023
 */
public class Stop implements ConsoleCommand {

    @Override
    public String[] names() {
        return new String[] {
                "stop",
                "shutdown",
                "стоп"
        };
    }

    @Override
    public void execute(Logger logger, String full, String label, String... args) {
        Suzaku.getInstance().shutdown();
    }

}
