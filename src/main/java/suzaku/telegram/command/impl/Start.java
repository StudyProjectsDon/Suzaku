package suzaku.telegram.command.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import me.twentybytes.zuki.api.database.ZukiDatabase;
import org.apache.logging.log4j.Logger;
import suzaku.console.ConsoleCommand;
import suzaku.telegram.command.Command;

/**
 * @author TwentyBytes
 * created in 29.06.2023
 */
public class Start implements Command {

    @Override
    public String[] names() {
        return new String[] {
                "start",
                "/start"
        };
    }

    @Override
    public void execute(Update update, TelegramBot bot, ZukiDatabase database, long chatID, String... args) {
        if (args.length > 0) {
            getDatabase().select("SELECT * FROM `files_registry` WHERE `identifier` = ?", rs -> {
                if (rs.next()) {
                    execute(new SendDocument(chatID, rs.getString("file_id")));
                } else {
                    execute(new SendMessage(chatID, "Файла с таким id не существует или у вас нет доступа к нему."));
                }
            }, args[0]);
        }
    }

}
