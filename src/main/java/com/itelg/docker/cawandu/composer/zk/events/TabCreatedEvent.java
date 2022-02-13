package com.itelg.docker.cawandu.composer.zk.events;

import lombok.Getter;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Window;

public class TabCreatedEvent extends Event {
    private static final long serialVersionUID = -4042638927767041356L;
    @Getter
    private final String tabId;

    public TabCreatedEvent(String tabId, Window window) {
        super(TabCreatedEvent.class.getName(), null, window);
        this.tabId = tabId;
    }

    public Window getWindow() {
        return (Window) getData();
    }
}