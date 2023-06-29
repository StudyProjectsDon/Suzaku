package suzaku.console.command;

import org.apache.logging.log4j.Logger;
import suzaku.Suzaku;
import suzaku.console.ConsoleCommand;

/**
 * @author TwentyBytes
 * created in 29.06.2023
 */
public class Reload implements ConsoleCommand {

    @Override
    public String[] names() {
        return new String[] {
                "reload",
                "r",
                "/reload",
                "перезагрузка",
                "перезагруз",
                "п"
        };
    }

    @Override
    public void execute(Logger logger, String full, String label, String... args) {
        logger.info("Trying reload...");

        Suzaku.getInstance().reload();
    }

}
