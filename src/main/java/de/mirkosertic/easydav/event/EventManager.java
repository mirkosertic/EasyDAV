package de.mirkosertic.easydav.event;

import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private List<EventListener> listener;

    public EventManager() {
        listener = new ArrayList<>();
    }

    public void register(EventListener aListener) {
        listener.add(aListener);
    }

    public void fire(Event aEvent) {
        for (EventListener theListener : listener) {
            theListener.handle(aEvent);
        }
    }
}
