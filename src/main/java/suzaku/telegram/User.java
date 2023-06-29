package suzaku.telegram;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.Getter;
import lombok.Setter;
import suzaku.telegram.module.file.FileAccess;
import suzaku.telegram.module.file.SuzakuFile;
import suzaku.util.Loggable;
import suzaku.util.Requester;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author TwentyBytes
 * created in 29.06.2023
 */
@Getter
public class User extends CompletableFuture<User> implements Requester, Loggable {

    private static final LoadingCache<Long, User> USER_CACHE = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(5))
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(200)
            .build(User::new);

    private final long chatId;
    private final Map<String, SuzakuFile> files = new HashMap<>();
    @Setter
    private MessageSession messageSession;

    private boolean loaded;

    public User(long chatId) {
        this.chatId = chatId;

        getDatabase().select("SELECT * FROM `files_registry` WHERE `chat_id` = ?", rs -> {
            while (rs.next()) {
                String identifier = rs.getString("identifier");
                long chat_id = rs.getLong("chat_id");
                String file_id = rs.getString("file_id");
                FileAccess fileAccess = FileAccess.byId(rs.getInt("privacy"));
                String caption = rs.getString("caption");

                files.put(identifier, new SuzakuFile(identifier, chat_id, file_id, fileAccess, caption));
            }

            loaded = true;
            complete(this);
        }, chatId);
    }

    public CompletableFuture<SuzakuFile.AddStatus> addFile(SuzakuFile file) {
        CompletableFuture<SuzakuFile.AddStatus> future = new CompletableFuture<>();

        getDatabase().select("SELECT * FROM `files_registry` WHERE `identifier` = ? LIMIT 1", rs -> {
            if (rs.next()) {
                future.complete(SuzakuFile.AddStatus.REJECTED_IDENTIFIER_BIND);
            } else {
                getDatabase().update("INSERT INTO `files_registry`(`identifier`, `chat_id`, `file_id`, `privacy`, `caption`) VALUES (?, ?, ?, ?, ?)", upds -> {
                    if (upds == 0) {
                        future.complete(SuzakuFile.AddStatus.REJECTED_ERROR);
                    } else {
                        files.put(file.getIdentifier(), file);
                        future.complete(SuzakuFile.AddStatus.SUCCESS);
                    }
                }, file.getIdentifier(), file.getChatId(), file.getFileId(), file.getAccess().getId(), file.getCaption());
            }
        }, file.getIdentifier());

        return future;
    }

    public CompletableFuture<SuzakuFile.RemoveStatus> removeFile(SuzakuFile file) {
        return removeFile(file.getIdentifier());
    }

    public CompletableFuture<SuzakuFile.RemoveStatus> removeFile(String identifier) {
        CompletableFuture<SuzakuFile.RemoveStatus> future = new CompletableFuture<>();

        getDatabase().update("DELETE FROM `files_registry` WHERE `identifier` = ? AND `chat_id` = ?", upds -> {
            if (upds < 1) {
                future.complete(SuzakuFile.RemoveStatus.REJECTED_NOT_EXISTS);
            } else {
                files.remove(identifier);

                future.complete(SuzakuFile.RemoveStatus.SUCCESS);
            }
        }, identifier, chatId);

        return future;
    }

    public MessageSession getMessageSession() {
        MessageSession session = messageSession;

        if (session != null && session.getStatus() == MessageSession.SessionStatus.CONTINUE) {
            return session;
        }

        this.messageSession = null;
        return null;
    }

    public static User getIfPresent(long chatId) {
        return USER_CACHE.getIfPresent(chatId);
    }

    public static User getOrLoad(long chatId) {
        return USER_CACHE.get(chatId);
    }

}
