package net.shoreline.client.api.event;


@FunctionalInterface
public interface EventListener<T> {
    void onEvent(T event);
}
