package suzaku.console;

import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import suzaku.Suzaku;
import suzaku.console.command.Reload;
import suzaku.console.command.Stop;

import java.util.Arrays;

public class ConsoleReader extends SimpleTerminalConsole {

    private static final ConsoleCommand[] registry = {
            new Reload(),
            new Stop()
    };

    @Override
    protected boolean isRunning() {
        return !Suzaku.isShutdown();
    }

    @Override
    protected LineReader buildReader(LineReaderBuilder builder) {
        return super.buildReader(
                builder.appName("Core").variable(LineReader.HISTORY_FILE, java.nio.file.Paths.get(".console_history"))
        );
    }

    @Override
    protected void runCommand(String full) {
        Logger logger = Suzaku.getInstance()
                .getLogger();

        String[] split = full.split(" ");
        String label = split[0];

        ConsoleCommand command = null;
        finder:
        {
            for (ConsoleCommand check : registry) {
                for (String name : check.names()) {
                    if (name.equalsIgnoreCase(label)) {
                        command = check;
                        break finder;
                    }
                }
            }
        }

        if (command == null) {
            logger.log(Level.ERROR, "Command not found.");
            return;
        }

        String[] args = split.length > 1 ? Arrays.copyOfRange(split, 1, split.length)
                : new String[0];

        command.execute(logger, full, label, args);
    }

    @Override
    protected void shutdown() {
        Suzaku.getInstance().getLogger().log(Level.INFO, "Bye-Bye!");
        Suzaku.getInstance().shutdown();
    }

}
