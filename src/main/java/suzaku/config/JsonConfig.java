package suzaku.config;

import suzaku.Suzaku;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.function.Supplier;

/**
 * @author TwentyBytes
 * created in 03.02.2023
 */
public class JsonConfig {

    public static <T> T load(Class<T> clazz, File file, Supplier<T> createDefault) {
        if (!file.exists()) {
            T t = createDefault.get();

            save(t, file);
            return t;
        }

        try (FileReader reader = new FileReader(file)) {
            return Suzaku.getGson().fromJson(reader, clazz);
        } catch (Throwable throwable) {
            T t = createDefault.get();

            save(t, file);
            return t;
        }
    }

    public static void save(Object object, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            Suzaku.getGson().toJson(object, writer);
            writer.flush();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
