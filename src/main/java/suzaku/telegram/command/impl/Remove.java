package suzaku.telegram.command.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import me.twentybytes.zuki.api.database.ZukiDatabase;
import suzaku.telegram.User;
import suzaku.telegram.command.Command;

/**
 * @author TwentyBytes
 * created in 29.06.2023
 */
public class Remove implements Command {

    @Override
    public String[] names() {
        return new String[] {
                "remove",
                "/remove",
                "удалить",
                "/удалить",
                "delete",
                "/delete",
                "/rem",
                "rem",
                "/del",
                "del"
        };
    }

    @Override
    public void execute(Update update, TelegramBot bot, ZukiDatabase database, long chatID, String... args) {
        if (args.length < 1) {
            execute(new SendMessage(chatID, "удалить <id>"));
            return;
        }

        User.getOrLoad(chatID).thenAccept(user -> user.removeFile(args[0]).thenAccept(removeStatus -> {
            switch (removeStatus) {
                case SUCCESS -> execute(new SendMessage(chatID, "Файл успешно удален."));
                case REJECTED_NOT_EXISTS -> execute(new SendMessage(chatID, "У вас нет файла с таким id."));
            }
        }));
    }

}
