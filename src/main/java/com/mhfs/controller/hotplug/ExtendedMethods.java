package com.mhfs.controller.hotplug;

import com.mhfs.controller.daemon.ProvidedMethods;
import com.mhfs.ipc.CallFuture;

public interface ExtendedMethods extends ProvidedMethods {

	public CallFuture<Boolean> startControllerSelection();
}
