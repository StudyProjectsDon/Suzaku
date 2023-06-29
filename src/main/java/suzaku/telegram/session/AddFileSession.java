package suzaku.telegram.session;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import io.nayuki.qrcodegen.QrCode;
import me.twentybytes.zuki.api.database.ZukiDatabase;
import suzaku.telegram.MessageSession;
import suzaku.telegram.User;
import suzaku.telegram.module.file.FileAccess;
import suzaku.telegram.module.file.SuzakuFile;
import suzaku.util.MessageBuilder;
import suzaku.util.Util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Objects;

/**
 * @author TwentyBytes
 * created in 29.06.2023
 */
public class AddFileSession implements MessageSession {

    public static final long EXPIRE_TIME = 120000;

    AddFileStatus addFileStatus = AddFileStatus.WAIT_FILE;
    SessionStatus sessionStatus = SessionStatus.CONTINUE;

    long chat_id;
    String file_id;
    String identifier;
    FileAccess fileAccess;
    String caption = null;

    long lastCall;

    public AddFileSession(long chat_id) {
        execute(new SendMessage(chat_id, "Отправьте файл, который будем сохранять."));

        this.lastCall = System.currentTimeMillis();
        this.chat_id = chat_id;
    }

    @Override
    public void execute(Update update, TelegramBot bot, ZukiDatabase database, long chatID) {
        lastCall = System.currentTimeMillis();
        chat_id = chatID;

        if (update == null || update.message() == null) {
            sessionStatus = SessionStatus.COMPLETED;

            execute(new SendMessage(chatID, "Произошла ошибка при сохранении файла."));
            return;
        }

        Message message = update.message();

        switch (addFileStatus) {
            case WAIT_FILE -> {
                if (message.document() == null) {
                    sessionStatus = SessionStatus.COMPLETED;

                    execute(new SendMessage(chatID, "Вы не отправили файл. Операция отменена."));
                    return;
                }

                addFileStatus = AddFileStatus.WAIT_IDENTIFIER;
                file_id = message.document().fileId();
                execute(new SendMessage(chatID, "Введите идентификатор, по которому будет происходить поиск файла."));
            }
            case WAIT_IDENTIFIER -> {
                if (message.text() == null) {
                    execute(new SendMessage(chatID, "Вы не отправили идентификатор. Операция отменена."));

                    sessionStatus = SessionStatus.COMPLETED;
                    return;
                }

                identifier = message.text();
                sessionStatus = SessionStatus.WAIT;

                getDatabase().select("SELECT * FROM `files_registry` WHERE `identifier` = ?", rs -> {
                    if (rs.next()) {
                        execute(new SendMessage(chatID, "Этот идентификатор уже занят. Придумайте новый."));
                    } else {
                        addFileStatus = AddFileStatus.WAIT_PRIVACY;

                        MessageBuilder builder = new MessageBuilder()
                                .lined("*Введите уровень приватности:*")
                                .line()
                                .lined(" *0* - Файл доступен только вам;")
                                .lined(" *1* - Файл доступен всем пользователям");

                        execute(new SendMessage(chatID, builder.build()).parseMode(ParseMode.Markdown));
                    }

                    sessionStatus = SessionStatus.CONTINUE;
                }, identifier);
            }
            case WAIT_PRIVACY -> {
                if (message.text() == null) {
                    execute(new SendMessage(chatID, "Вы не отправили уровень приватности. Операция отменена."));
                    sessionStatus = SessionStatus.COMPLETED;

                    return;
                }

                try {
                    int val = Integer.parseInt(message.text());

                    if (val > 1 || val < 0) {
                        execute(new SendMessage(chatID, "Отправьте число от 0 до 1."));
                        return;
                    }

                    fileAccess = FileAccess.byId(val);

                    execute(new SendMessage(chatID, "Введите описание файла"));
                    addFileStatus = AddFileStatus.WAIT_CAPTION;
                } catch (Throwable throwable) {
                    execute(new SendMessage(chatID, "Отправьте число от 0 до 1."));
                }
            }
            case WAIT_CAPTION -> {
                if (message.text() != null) {
                    caption = message.text();
                }

                sessionStatus = SessionStatus.COMPLETED;

                User.getOrLoad(chatID).thenAccept(user -> {
                    user.addFile(new SuzakuFile(identifier, chatID, file_id, fileAccess, caption)).thenAccept(status -> {
                        switch (status) {
                            case SUCCESS -> {
                                String url = "https://t.me/suzaku_file_bot?start=" + identifier;
                                byte[] data = Util.qrCode(url);

                                if (data != null) {
                                    SendPhoto document = new SendPhoto(chatID, data);
                                    document.caption("Файл добавлен! \n\nСсылка на скачивание: " + url);

                                    execute(document);
                                } else {
                                    execute(new SendMessage(chatID, "Файл добавлен! \n\nСсылка на скачивание: " + url));
                                }
                            }
                            case REJECTED_ERROR -> execute(new SendMessage(chatID, "При добавлении файла произошла ошибка!"));
                            case REJECTED_IDENTIFIER_BIND -> execute(new SendMessage(chatID, "Этот идентификатор уже занят!"));
                        }
                    });
                });
            }
        }
    }

    @Override
    public SessionStatus getStatus() {
        if (lastCall + EXPIRE_TIME < System.currentTimeMillis()) {
            return SessionStatus.EXPIRED;
        }

        return sessionStatus;
    }

    public enum AddFileStatus {

        WAIT_FILE,
        WAIT_IDENTIFIER,
        WAIT_PRIVACY,
        WAIT_CAPTION

    }

}
