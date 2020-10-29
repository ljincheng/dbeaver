package org.jkiss.dbeaver.model.sourcecode.ui.event;

public interface UINotifierContext {
	
	
	
	void send(int type,UINotifier notifier,Object context); 
	
	void add(int type,UIEventNotifier notifier);

}
