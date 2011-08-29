package com.kedzie.vbox.api;

import java.io.IOException;
import com.kedzie.vbox.KSOAP;
import com.kedzie.vbox.api.jaxb.VBoxEventType;

public interface IEventSource extends IRemoteObject {
	
	public IEventListener createListener() ;
	public void registerListener(@KSOAP("listener")IEventListener l, @KSOAP("interesting") VBoxEventType []events, @KSOAP("active") boolean active);
	public void unregisterListener(@KSOAP("listener") IEventListener l);
	public void eventProcessed(@KSOAP("listener")IEventListener l, @KSOAP("event")IEvent event) throws IOException;
	public IEvent getEvent(@KSOAP("listener")IEventListener l, @KSOAP(type="int", value="timeout") int timeout) throws IOException;
	
//	public IEventAggregator createAggregator(String[] subs) throws IOException;
}