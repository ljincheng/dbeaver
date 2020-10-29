package org.jkiss.dbeaver.model.sourcecode.ui.event;

import org.eclipse.core.commands.ExecutionEvent;

public interface UIActionEvent {
	
	public String COMMAND_OBJECT_ID="COMMAND_OBJECT";
	
	public Object executeAction(ExecutionEvent event);

}
