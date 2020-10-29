package org.jkiss.dbeaver.model.sourcecode.core;

public interface ContentBroadcas {

	boolean addContentListen(ContentListen listen);
	
	boolean removeContentListen(ContentListen listen);
	
	void send(int type,int supType,Object data);
	
	
}
