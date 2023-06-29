package suzaku.telegram.module.file;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author TwentyBytes
 * created in 29.06.2023
 */
@Getter @AllArgsConstructor
public class SuzakuFile {

    String identifier;

    @SerializedName("chat-id")
    long chatId;

    @SerializedName("message-id")
    String fileId;

    FileAccess access;

    String caption;

    public enum AddStatus {

        REJECTED_IDENTIFIER_BIND,
        REJECTED_ERROR,
        SUCCESS

    }

    public enum RemoveStatus {

        REJECTED_NOT_EXISTS,
        SUCCESS

    }

}
