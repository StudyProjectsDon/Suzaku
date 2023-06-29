package suzaku.telegram.command.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import me.twentybytes.zuki.api.database.ZukiDatabase;
import suzaku.telegram.User;
import suzaku.telegram.command.Command;
import suzaku.telegram.module.file.SuzakuFile;
import suzaku.util.MessageBuilder;

/**
 * @author TwentyBytes
 * created in 29.06.2023
 */
public class List implements Command {

    @Override
    public String[] names() {
        return new String[] {
                "list",
                "/list",
                "список",
                "/список"
        };
    }

    @Override
    public void execute(Update update, TelegramBot bot, ZukiDatabase database, long chatID, String... args) {
        User.getOrLoad(chatID).thenAccept(user -> {
            MessageBuilder builder = new MessageBuilder();

            if (!user.getFiles().isEmpty()) {
                int i = 0;
                for (SuzakuFile value : user.getFiles().values()) {
                    builder.lined("#" + ++i + ". `" + value.getIdentifier() + "` - " + value.getCaption());
                }
            } else {
                builder.lined("Пусто");
            }

            builder.line();
            builder.lined("Для скачивания: `скачать id`");
            builder.lined("Для удаления: `удалить id`");

            execute(new SendMessage(chatID, builder.build()).parseMode(ParseMode.Markdown));
        });
    }

}
