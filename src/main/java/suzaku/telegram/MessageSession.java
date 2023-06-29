package suzaku.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import me.twentybytes.zuki.api.database.ZukiDatabase;
import suzaku.util.Loggable;
import suzaku.util.Requester;

/**
 * @author TwentyBytes
 * created in 29.06.2023
 */
public interface MessageSession extends Loggable, Requester {

    void execute(Update update, TelegramBot bot, ZukiDatabase database, long chatID);

    SessionStatus getStatus();

    enum SessionStatus {

        EXPIRED,
        CONTINUE,
        WAIT,
        COMPLETED

    }

}
