package io.github.apace100.origins.api;

@FunctionalInterface
public interface Action<T> {
    void run(T context);
}
