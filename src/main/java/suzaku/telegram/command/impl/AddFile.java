package suzaku.telegram.command.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import me.twentybytes.zuki.api.database.ZukiDatabase;
import suzaku.telegram.User;
import suzaku.telegram.command.Command;
import suzaku.telegram.session.AddFileSession;

/**
 * @author TwentyBytes
 * created in 29.06.2023
 */
public class AddFile implements Command {

    @Override
    public String[] names() {
        return new String[] {
                "addfile",
                "/addfile",
                "/add",
                "add",
                "добавить",
                "/добавить"
        };
    }

    @Override
    public void execute(Update update, TelegramBot bot, ZukiDatabase database, long chatID, String... args) {
        User.getOrLoad(chatID).thenAccept(user -> user.setMessageSession(new AddFileSession(chatID)));
    }

}
