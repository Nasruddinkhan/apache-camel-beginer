package com.mypractice;

import java.util.function.Consumer;

public class ExceptionHandling {
    private ExceptionHandling(){}
    public static <T, E extends Exception> Consumer<T> handleExceptions(ExceptionConsumer<T> consumer) {
        return item -> {
            try {
                consumer.accept(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
