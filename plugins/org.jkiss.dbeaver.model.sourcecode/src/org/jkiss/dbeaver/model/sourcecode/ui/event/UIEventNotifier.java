package org.jkiss.dbeaver.model.sourcecode.ui.event;

public interface UIEventNotifier {
	
	public int TYPE_VIEW_CREATE=1;
	public int TYPE_VIEW_LOADDATA=2;
	public int TYPE_VIEW_REFRESH=3;
	public int TYPE_VIEW_ACTION=4;
	
	
	public int TYPE_VIEW_ACTION_CATEGORY_CODE=1;//刷设置
	public int TYPE_VIEW_ACTION_CATEGORY_CODE_SETTING=2;//刷设置
	public int TYPE_VIEW_ACTION_CATEGORY_CODE_TEMPLATE=3;//刷设置
	public int TYPE_VIEW_ACTION_CATEGORY_CODE_COPY=4;//刷设置
	public int TYPE_VIEW_ACTION_CATEGORY_CODE_EXPORT=5;//刷设置
	
	public int TYPE_VIEW_REFRESH_CATEGORY_SETTING=1;//刷设置
	public int TYPE_VIEW_REFRESH_CATEGORY_TEMPLATE=2;//刷设置
	
	void action(int type,UINotifier notifier, Object context);

}
