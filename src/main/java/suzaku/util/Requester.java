package suzaku.util;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.BaseRequest;
import me.twentybytes.zuki.api.database.ZukiDatabase;
import suzaku.Suzaku;

public interface Requester {

    default Suzaku getMainInstance() {
        return Suzaku.getInstance();
    }

    default TelegramBot getTelegramBot() {
        return getMainInstance().getBot();
    }

    default ZukiDatabase getDatabase() {
        return getMainInstance().getDatabase();
    }

    default void respond(BaseRequest<?, ?> request) {
        getTelegramBot().execute(request);
    }

    default void execute(BaseRequest<?, ?> request) {
        getTelegramBot().execute(request);
    }

}