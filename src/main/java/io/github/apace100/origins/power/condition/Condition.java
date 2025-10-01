package io.github.apace100.origins.power.condition;

/**
 * Placeholder contract for Origins datapack conditions.
 *
 * @param <T> the context type that will be evaluated against the condition.
 */
@FunctionalInterface
public interface Condition<T> {
    /**
     * Evaluates the condition for the supplied context.
     *
     * @param context the invocation context defined by the condition type
     * @return {@code true} if the condition matches, {@code false} otherwise
     */
    boolean test(T context);
}
