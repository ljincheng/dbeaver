package org.jkiss.dbeaver.model.sourcecode.ui.context;

/**
 * 卡片项
 * @author ljc
 *
 */
public class TabItemContext {
	
	private String id;
	private String name;
	private String tip;
	private Object data;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTip() {
		return tip;
	}
	public void setTip(String tip) {
		this.tip = tip;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	

	
}
