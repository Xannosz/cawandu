package com.itelg.docker.cawandu.composer.zk.events;

import org.zkoss.zk.ui.event.Event;

public class LogRefreshEvent extends Event { //TODO drop
    public LogRefreshEvent() {
        super(LogRefreshEvent.class.getName());
    }
}
