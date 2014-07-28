package com.ah.be.cloudauth.processor;

import com.ah.be.event.BeBaseEvent;

public interface IBeProcessor {

    void startTask();
    void addEvent(BeBaseEvent event);
    boolean shutdown();
}
