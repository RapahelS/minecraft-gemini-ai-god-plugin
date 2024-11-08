package net.bigyous.gptgodmc.interfaces;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface AsyncFunction<T, V> {
    public CompletableFuture<V> run(T object);
}