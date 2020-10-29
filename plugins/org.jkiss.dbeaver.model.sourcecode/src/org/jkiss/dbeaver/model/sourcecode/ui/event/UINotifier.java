package org.jkiss.dbeaver.model.sourcecode.ui.event;

import org.eclipse.ui.IWorkbenchPartSite;

public class UINotifier {

	private int category;
	private IWorkbenchPartSite site;
	private Object data; 
	private Object composite;
	
	
	public UINotifier() {
		super();
	}
	
	public UINotifier(int category, Object data, Object composite) {
		super();
		this.category = category;
		this.data = data;
		this.composite = composite;
	}
	
	public UINotifier(int category, Object data, Object composite,IWorkbenchPartSite site) {
		super();
		this.category = category;
		this.data = data;
		this.composite = composite;
		this.site=site;
	}
	
 
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}

	public Object getComposite() {
		return composite;
	}

	public void setComposite(Object composite) {
		this.composite = composite;
	}

	public IWorkbenchPartSite getSite() {
		return site;
	}

	public void setSite(IWorkbenchPartSite site) {
		this.site = site;
	}
 
	
	
}
