package bg.tuvarna.traveltickets.util;

import javafx.concurrent.Task;

import java.util.function.Supplier;

import static bg.tuvarna.traveltickets.common.Constants.CANNOT_BE_INSTANTIATED_FORMAT;

/**
 * This utility class provides methods for working with {@link Task}s.
 */
public final class TaskUtil {

    public static <T> Task<T> createTask(final Supplier<T> action) {
        return new Task<>() {
            @Override
            protected T call() {
                return action.get();
            }
        };
    }

    private TaskUtil() {
        throw new UnsupportedOperationException(CANNOT_BE_INSTANTIATED_FORMAT.formatted(getClass().toString()));
    }

}
