package suzaku;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.Getter;
import me.twentybytes.zuki.api.database.ZukiDatabase;
import me.twentybytes.zuki.impl.database.SimpleZukiDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import suzaku.config.Config;
import suzaku.config.JsonConfig;
import suzaku.console.ConsoleReader;
import suzaku.console.DefaultUncaughtExceptionHandler;
import suzaku.telegram.command.CommandProcessor;

import java.io.File;

/**
 * @author TwentyBytes
 * created in 29.06.2023
 */
@Getter
public class Suzaku {

    @Getter
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Getter
    private static boolean shutdown = false;

    @Getter
    private static Suzaku instance;

    private final Logger logger = LogManager.getLogger(Suzaku.class);
    private final File dataFolder;

    private Config config;
    private ZukiDatabase database;
    private TelegramBot bot;

    public Suzaku() {
        this.dataFolder = new File("data");
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            System.exit(1);
        }

        this.reload();

        Thread thread = new Thread(() -> new ConsoleReader().start(), "Console Handler Thread");
        thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(logger));
        thread.start();
    }

    public void reload() {
        if (this.bot != null) {
            this.bot.removeGetUpdatesListener();
            this.bot.shutdown();

            this.bot = null;
        }

        if (this.database != null) {
            this.database.close();

            this.database = null;
        }

        this.config = JsonConfig.load(
                Config.class,
                new File(dataFolder, "config.json"),
                Config::new
        );

        this.database = new SimpleZukiDatabase(config.getDatabaseConfig());
        this.database.start();

        this.registerTables();

        this.bot = new TelegramBot(config.getTelegramConfig().getAuthToken());
        this.bot.setUpdatesListener(updates -> {
            try {
                CommandProcessor.wrap(updates);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

        this.logger.info("Service reloaded.");
    }

    public void shutdown() {
        if (shutdown) {
            return;
        }

        shutdown = true;

        if (this.bot != null) {
            this.bot.shutdown();

            logger.info("Successful shutdown telegram bot.");
        }

        if (this.database != null) {
            this.database.close();

            logger.info("Successful shutdown database.");
        }

        logger.info("Successful shutdown.");
        System.exit(1);
    }

    private void registerTables() {
        database.update("""
                CREATE TABLE IF NOT EXISTS `files_registry` (
                `identifier` varchar(32) NOT NULL,
                `chat_id` BIGINT NOT NULL,
                `file_id` LONGTEXT NOT NULL,
                `privacy` int(32) NOT NULL,
                `caption` LONGTEXT DEFAULT NULL,
                PRIMARY KEY (`identifier`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
                """);
    }

    public static void main(String... args) {
        instance = new Suzaku();
    }

}
