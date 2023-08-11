package com.mypractice;

import java.io.IOException;

@FunctionalInterface
interface ExceptionConsumer<T, E extends Exception> {
    void accept(T t) throws IOException;
}