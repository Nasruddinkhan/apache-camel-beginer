package com.mypractice;

import java.io.IOException;

@FunctionalInterface
interface ExceptionConsumer<T> {
    void accept(T t) throws IOException;
}