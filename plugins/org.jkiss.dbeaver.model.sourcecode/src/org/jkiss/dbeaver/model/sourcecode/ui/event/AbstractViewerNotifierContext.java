package org.jkiss.dbeaver.model.sourcecode.ui.event;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;

public abstract class AbstractViewerNotifierContext extends Viewer implements UINotifierContext{
	
	
	private LinkedHashMap<Integer,  List<UIEventNotifier>> mEventTypeMap;

	@Override
	public void send(int type, UINotifier notifier, Object context) {
		if(mEventTypeMap!=null)
		{
			List<UIEventNotifier>  notifierList=mEventTypeMap.get(type);
			if(notifierList!=null )
			{
				for(int i=0,k=notifierList.size();i<k;i++)
				{
					UIEventNotifier eventNotifier=notifierList.get(i);
					eventNotifier.action(type, notifier, context);
				}
			}
		}
		if(notifier!=null) {
			notifier=null;
		}
		
	}

	@Override
	public void add(int type, UIEventNotifier notifier) {
		List<UIEventNotifier> mEventNotifierList=null;
		 if(mEventTypeMap==null) {
			 mEventTypeMap=new LinkedHashMap<Integer, List<UIEventNotifier>>();
			 mEventNotifierList=new ArrayList<UIEventNotifier>();
			 mEventTypeMap.put(type, mEventNotifierList); 
		 }else {
			 mEventNotifierList=mEventTypeMap.get(type);
			 if(mEventNotifierList==null){
				 mEventNotifierList=new ArrayList<UIEventNotifier>();
				 mEventTypeMap.put(type, mEventNotifierList); 
			 }
		 }
		 mEventNotifierList.add(notifier);
		
	}
	


}
