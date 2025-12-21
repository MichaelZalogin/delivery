package libs.common;

import io.micrometer.common.lang.Nullable;

import java.util.function.Function;

public class CommonUtils {

    private CommonUtils() {
    }

    @Nullable
    public static <T, R> R mapIfNotNull(@Nullable T obj, Function<T, R> mapper) {
        return (R) (obj == null ? null : mapper.apply(obj));
    }

}
