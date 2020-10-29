package org.jkiss.dbeaver.model.sourcecode.core;

public interface ContentListen {
	
	public int TYPE_DATA_CHANGE=1;
	public int SUBTYPE_DATA_CHANGE_ADD=1;
	public int SUBTYPE_DATA_CHANGE_ADD_MUL=2;
	public int SUBTYPE_DATA_CHANGE_DELETE=3;
	public int SUBTYPE_DATA_CHANGE_DELETE_MUL=4;
	public int SUBTYPE_DATA_CHANGE_MODIFY=5;
	
	void contentAction(int type,int subtype,Object data);

}
