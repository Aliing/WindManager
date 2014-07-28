package com.ah.be.ts.hiveap;

import java.util.Comparator;
import java.util.List;

public interface DebugNotificationSorting <E extends DebugNotification> extends Comparator<E> {

	List<E> sort(List<E> notifications);

}