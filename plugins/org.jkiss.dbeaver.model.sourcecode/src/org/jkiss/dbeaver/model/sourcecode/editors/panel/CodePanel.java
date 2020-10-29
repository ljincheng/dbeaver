package org.jkiss.dbeaver.model.sourcecode.editors.panel;

import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Composite;
import org.jkiss.dbeaver.model.sourcecode.editors.JavaEditor;
import org.jkiss.dbeaver.model.sourcecode.editors.SourceCodeViewContext;
import org.jkiss.dbeaver.model.sourcecode.ui.event.UIEventNotifier;
import org.jkiss.dbeaver.model.sourcecode.ui.event.UINotifier;
import org.jkiss.dbeaver.model.sourcecode.ui.event.UINotifierContext;
import org.jkiss.dbeaver.ui.UIUtils;

public class CodePanel implements UIEventNotifier{

	private JavaEditor mJavaEditor;
	private UINotifierContext mUINotifierContext;
	
	public CodePanel(UINotifierContext context) {
		super();
		mUINotifierContext=context;
		mUINotifierContext.add(TYPE_VIEW_CREATE, this);
		mUINotifierContext.add(TYPE_VIEW_ACTION, this);
		
	}
	
	@Override
	public void action(int type, UINotifier notifier, Object context) {
		if(TYPE_VIEW_CREATE==type) {
			if(notifier.getCategory()==3 && notifier.getComposite() instanceof Composite)
			{
				mJavaEditor=new JavaEditor(notifier.getSite());//测试下代码视图放在主体部分效果
			 	mJavaEditor.createPartControl((Composite)notifier.getComposite());
	//		 	mJavaEditor.setCode(this.context.generateCode());
			}
		}else if(TYPE_VIEW_ACTION == type)
		{
			if(context!=null && context instanceof SourceCodeViewContext)
			{
				switch (notifier.getCategory()) {
					case TYPE_VIEW_ACTION_CATEGORY_CODE_TEMPLATE: {
						SourceCodeViewContext sourceCodeViewContext = (SourceCodeViewContext) context;
						mJavaEditor.setCode(sourceCodeViewContext.getSourceCode());
					}
					break;
					case TYPE_VIEW_ACTION_CATEGORY_CODE_COPY:{
						 UIUtils.setClipboardContents(notifier.getSite().getShell().getDisplay(), TextTransfer.getInstance(),mJavaEditor.getCode());
						break;
					}

				default:
					break;
				}
				 
			}else {
				switch (notifier.getCategory()) {
				case TYPE_VIEW_ACTION_CATEGORY_CODE_COPY:{
					 UIUtils.setClipboardContents(notifier.getSite().getShell().getDisplay(), TextTransfer.getInstance(),mJavaEditor.getCode());
					break;
				}

			default:
				break;
			}
			}
		}
	}
	public JavaEditor getCodeViewer() {
		return mJavaEditor;
	}
	

}
