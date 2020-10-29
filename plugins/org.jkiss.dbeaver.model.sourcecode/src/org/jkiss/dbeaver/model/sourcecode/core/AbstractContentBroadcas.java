package org.jkiss.dbeaver.model.sourcecode.core;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractContentBroadcas implements ContentBroadcas{
	
	private List<ContentListen> mContextListenList;

	@Override
	public boolean addContentListen(ContentListen listen) {
		if(mContextListenList==null)
		{
			mContextListenList=new ArrayList<ContentListen>();
		}
		return this.mContextListenList.add(listen);
	}

	@Override
	public boolean removeContentListen(ContentListen listen) {
		if(mContextListenList==null)
		{
			mContextListenList=new ArrayList<ContentListen>();
		}
		return this.mContextListenList.remove(listen);
	}

	@Override
	public void send(int type, int supType, Object data) {
		if(mContextListenList!=null)
		{
			for(int i=0,k=mContextListenList.size();i<k;i++)
			{
				mContextListenList.get(i).contentAction(type, supType, data);
			}
		}
	}
	
	

}
