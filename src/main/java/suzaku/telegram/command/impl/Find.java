package suzaku.telegram.command.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.ForwardMessage;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import me.twentybytes.zuki.api.database.ZukiDatabase;
import suzaku.telegram.command.Command;
import suzaku.telegram.module.file.FileAccess;

/**
 * @author TwentyBytes
 * created in 29.06.2023
 */
public class Find implements Command {

    @Override
    public String[] names() {
        return new String[] {
                "find",
                "/find",
                "поиск",
                "/поиск",
                "скачать",
                "/скачать",
                "/download",
                "download"
        };
    }

    @Override
    public void execute(Update update, TelegramBot bot, ZukiDatabase database, long chatID, String... args) {
        if (args.length < 1) {
            execute(new SendMessage(chatID, "/поиск <id>"));
            return;
        }

        getDatabase().select("SELECT * FROM `files_registry` WHERE `identifier` = ?", rs -> {
            if (rs.next()) {
                long chatId = rs.getLong("chat_id");
                FileAccess fileAccess = FileAccess.byId(rs.getInt("privacy"));

                if (fileAccess == FileAccess.PRIVATE && chatID != chatId) {
                    execute(new SendMessage(chatID, "У вас нет доступа к этому документу :("));
                    return;
                }

                SendDocument document = new SendDocument(chatID, rs.getString("file_id"));

                String caption = rs.getString("caption");
                if (caption != null) {
                    document.caption(caption);
                }

                InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(
                        new InlineKeyboardButton[] {
                                new InlineKeyboardButton("Ссылка на файл")
                                        .callbackData("url:" + args[0])
                        },
                        new InlineKeyboardButton[] {
                                new InlineKeyboardButton("QR-код")
                                        .callbackData("qr:" + args[0])
                        }
                );

                document.replyMarkup(keyboardMarkup);

                execute(document);
            } else {
                execute(new SendMessage(chatID, "Файла с таким id не существует."));
            }
        }, args[0]);
    }

}
