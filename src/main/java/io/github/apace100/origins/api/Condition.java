package io.github.apace100.origins.api;

@FunctionalInterface
public interface Condition<T> {
    boolean test(T context);
}
