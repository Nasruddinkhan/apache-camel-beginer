package com.mypractice;

import java.util.function.Consumer;

public class ExceptionHandling {
    static <T, E extends Exception> Consumer<T> handleExceptions(ExceptionConsumer<T, E> consumer) {
        return item -> {
            try {
                consumer.accept(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
