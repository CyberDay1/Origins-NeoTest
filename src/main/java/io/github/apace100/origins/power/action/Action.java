package io.github.apace100.origins.power.action;

/**
 * Placeholder contract for Origins datapack actions.
 * <p>
 * Actions will be hydrated from datapack JSON and executed later in the
 * runtime once the supporting systems are implemented. Until then this
 * interface provides a simple scaffold for the action stubs generated as
 * part of the Fabric parity effort.
 *
 * @param <T> the context type supplied when running the action.
 */
@FunctionalInterface
public interface Action<T> {
    /**
     * Executes the action against the provided context object.
     *
     * @param context the invocation context defined by the action type
     */
    void execute(T context);
}
