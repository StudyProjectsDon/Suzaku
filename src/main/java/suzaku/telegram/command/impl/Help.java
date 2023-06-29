package suzaku.telegram.command.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.ForwardMessage;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import me.twentybytes.zuki.api.database.ZukiDatabase;
import suzaku.telegram.command.Command;
import suzaku.util.MessageBuilder;

/**
 * @author TwentyBytes
 * created in 29.06.2023
 */
public class Help implements Command {

    @Override
    public String[] names() {
        return new String[] {
                "помощь",
                "help",
                "/help",
                "/помощь"
        };
    }

    @Override
    public void execute(Update update, TelegramBot bot, ZukiDatabase database, long chatID, String... args) {
        MessageBuilder builder = MessageBuilder.create()
                .lined("*Что умеет этот бот?*")
                .line()
                .lined("Он позволяет Вам загружать ваши")
                .lined("файлы на бессрочное бесплатное хранение")
                .lined("а также делится ими с помощью ссылок")
                .lined("при этом у файлов есть уровни доступа.")
                .line()
                .lined("Сервис абсолютно бесплатный и позволяет")
                .lined("хранить бесконечное количество файлов.")
                .line()
                .lined("С уважением, разработчик Suzaku @twentybytes");

        SendMessage request = new SendMessage(chatID, builder.build()).parseMode(ParseMode.Markdown);
        bot.execute(request);
    }

}
