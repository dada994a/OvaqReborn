package net.shoreline.client.api.event;

import net.shoreline.client.impl.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDispatcher {

    // イベントクラスごとのリスナーを管理するマップ
    private static final Map<Class<?>, List<EventListener<?>>> listenersMap = new HashMap<>();

    // イベントリスナーを登録するメソッド
    public static <T> void register(Class<T> eventClass, EventListener<T> listener) {
        listenersMap.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(listener);
    }

    // イベントを発火するメソッド
    @SuppressWarnings("unchecked")
    public static <T> void post(T event) {
        List<EventListener<?>> listeners = listenersMap.get(event.getClass());
        if (listeners != null) {
            for (EventListener<?> listener : listeners) {
                ((EventListener<T>) listener).onEvent(event);
            }
        }
    }

    // EntityDeathEventを発火させるメソッド
    public static void dispatch(EntityDeathEvent deathEvent) {
        post(deathEvent); // postメソッドを使ってリスナーに通知
    }
}
