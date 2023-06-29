package suzaku.util;

import com.pengrad.telegrambot.request.SendMessage;

public class MessageBuilder {

    private final StringBuilder builder = new StringBuilder();

    public MessageBuilder line() {
        builder.append("\n");
        return this;
    }

    public MessageBuilder text(String text) {
        builder.append(text);
        return this;
    }

    public MessageBuilder lined(String text) {
        text(text);
        line();
        return this;
    }

    public String build() {
        return builder.toString();
    }

    public SendMessage build(long chatID) {
        return new SendMessage(chatID, build());
    }

    public static MessageBuilder create() {
        return new MessageBuilder();
    }

}
