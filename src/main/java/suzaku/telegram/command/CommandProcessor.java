package suzaku.telegram.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import suzaku.Suzaku;
import suzaku.telegram.User;
import suzaku.telegram.command.impl.*;
import suzaku.util.Loggable;
import suzaku.util.Util;

import java.util.Arrays;
import java.util.List;

public class CommandProcessor implements Loggable {

    private static final Command[] registry = {
            new Help(),
            new AddFile(),
            new Find(),
            new Start(),
            new Remove(),
            new suzaku.telegram.command.impl.List()
    };

    public static void wrap(List<Update> updates) {
        TelegramBot bot = Suzaku.getInstance().getBot();

        for (Update update : updates) {
            if (update.callbackQuery() != null) {
                wrapCallback(update, update.callbackQuery());
                continue;
            }

            if (update.message() == null) {
                continue;
            }

            try {
                User user = User.getOrLoad(update.message().chat().id()).get();

                if (user.getMessageSession() != null) {
                    user.getMessageSession().execute(update, bot, Suzaku.getInstance().getDatabase(), update.message().chat().id());

                    return;
                }
            } catch (Throwable ignored) {
            }

            String content = update.message().text();

            if (content == null) {
                continue;
            }

            String[] args = content.split(" ");
            String label = args[0];

            main:
            {
                for (Command command : registry) {
                    for (String name : command.names()) {
                        if (name.equalsIgnoreCase(label)) {
                            command.execute(update, bot, Suzaku.getInstance().getDatabase(), update.message().chat().id(),
                                    Arrays.copyOfRange(args, 1, args.length));
                            break main;
                        }
                    }
                }
                bot.execute(new SendMessage(update.message().chat().id(), "Команда не найдена. Список команд: /commands"));
            }
        }
    }

    public static void wrapCallback(Update update, CallbackQuery query) {
        TelegramBot bot = Suzaku.getInstance().getBot();
        String data = query.data();

        if (data != null) {
            String[] args = data.split(":");

            switch (args[0].toLowerCase()) {
                case "qr" -> {
                    String url = "https://t.me/suzaku_file_bot?start=" + args[1];
                    byte[] bytes = Util.qrCode(url);

                    if (bytes != null) {
                        bot.execute(new SendPhoto(query.from().id(), bytes).caption("QR-Код на скачивание"));
                    }
                }
                case "url" -> {
                    String url = "https://t.me/suzaku_file_bot?start=" + args[1];

                    bot.execute(new SendMessage(query.from().id(), "Ссылка на скачивание:\n" + url));
                }
            }
        }
    }

}
