package suzaku.config;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import me.twentybytes.zuki.impl.config.SimpleZukiConfig;

import java.util.UUID;

/**
 * @author TwentyBytes
 * created in 29.06.2023
 */
@Data @FieldDefaults(level = AccessLevel.PRIVATE)
public class Config {

    @SerializedName("telegram")
    TelegramConfig telegramConfig = new TelegramConfig();

    @SerializedName("database")
    SimpleZukiConfig databaseConfig = new SimpleZukiConfig("127.0.0.1", 3306, "database", "root", "1234");

    @Data @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TelegramConfig {

        @SerializedName("auth-token")
        String authToken = UUID.randomUUID().toString();

    }

}
