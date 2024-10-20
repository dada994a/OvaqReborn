package net.shoreline.client.api.event;

// ジェネリックなイベントリスナーインターフェース
@FunctionalInterface
public interface EventListener<T> {
    void onEvent(T event);
}
