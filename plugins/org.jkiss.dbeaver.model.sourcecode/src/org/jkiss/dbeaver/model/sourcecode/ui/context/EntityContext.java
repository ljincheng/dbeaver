package org.jkiss.dbeaver.model.sourcecode.ui.context;

import java.util.List;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.jkiss.dbeaver.model.runtime.DBRRunnableWithProgress;
import org.jkiss.dbeaver.model.sourcecode.ui.event.ViewDataRunnableEvent; 

public interface EntityContext extends  DBRRunnableWithProgress{
	
	String generateCode();
	
	/**
	 * 属性面板卡片
	 * @return
	 */
	List<TabItemContext> panelTabs();
	
	
	void createPanelTabs(IWorkbenchPart part,CTabFolder panelFolder);
	
	
	
	void setViewDataRunnableEvent(ViewDataRunnableEvent event);

}
