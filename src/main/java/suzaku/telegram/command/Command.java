package suzaku.telegram.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import me.twentybytes.zuki.api.database.ZukiDatabase;
import suzaku.util.Loggable;
import suzaku.util.Requester;

public interface Command extends Requester, Loggable {

    String[] names();

    void execute(Update update, TelegramBot bot, ZukiDatabase database, long chatID, String... args);

}
