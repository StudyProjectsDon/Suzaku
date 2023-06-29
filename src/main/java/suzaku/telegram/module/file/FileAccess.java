package suzaku.telegram.module.file;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author TwentyBytes
 * created in 29.06.2023
 */
@Getter @AllArgsConstructor
public enum FileAccess {

    PRIVATE(0),
    PUBLIC(1);

    private final int id;

    public static FileAccess byId(int id) {
        for (FileAccess value : VALUES) {
            if (value.id != id) {
                continue;
            }

            return value;
        }

        return null;
    }

    public static final FileAccess[] VALUES = values();

}
