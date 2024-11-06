package net.bigyous.gptgodmc.interfaces;

@FunctionalInterface
public interface SimpFunction<T> {
    public void run(T object);
}