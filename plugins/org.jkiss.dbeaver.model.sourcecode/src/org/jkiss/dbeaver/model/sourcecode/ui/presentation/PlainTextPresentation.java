package org.jkiss.dbeaver.model.sourcecode.ui.presentation;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ScrollBar;
import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.model.data.DBDAttributeBinding;
import org.jkiss.dbeaver.ui.UIStyles;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.controls.StyledTextFindReplaceTarget;
import org.jkiss.dbeaver.ui.controls.resultset.AbstractPresentation;
import org.jkiss.dbeaver.ui.controls.resultset.IResultSetController;
import org.jkiss.dbeaver.ui.controls.resultset.ResultSetCopySettings;
import org.jkiss.dbeaver.ui.controls.resultset.ResultSetPreferences;
import org.jkiss.dbeaver.ui.editors.TextEditorUtils;

public class PlainTextPresentation extends AbstractPresentation {

	private StyledText text;
	private DBDAttributeBinding curAttribute;
    private StyledTextFindReplaceTarget findReplaceTarget;
    public boolean activated;
    private Color curLineColor;
	private String curSelection;
	@Override
	 public void createPresentation(@NotNull final IResultSetController controller, @NotNull Composite parent) {
	        super.createPresentation(controller, parent);

	        UIUtils.createHorizontalLine(parent);
	        text = new StyledText(parent, SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
	        text.setBlockSelection(true);
	        text.setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_IBEAM));
	        text.setMargins(4, 4, 4, 4);
	        text.setForeground(UIStyles.getDefaultTextForeground());
	        text.setBackground(UIStyles.getDefaultTextBackground());
	       // text.setTabs(controller.getPreferenceStore().getInt(ResultSetPreferences.RESULT_TEXT_TAB_SIZE));
	        text.setTabStops(null);
	        text.setFont(UIUtils.getMonospaceFont());
	        text.setLayoutData(new GridData(GridData.FILL_BOTH));
	        text.setText("这是测试内容，HELLO WORLD！");

	        final ScrollBar verticalBar = text.getVerticalBar();
	        verticalBar.addSelectionListener(new SelectionAdapter() {
	            @Override
	            public void widgetSelected(SelectionEvent e) {
	                if (verticalBar.getSelection() + verticalBar.getPageIncrement() >= verticalBar.getMaximum()) {
//	                    if (controller.getPreferenceStore().getBoolean(ResultSetPreferences.RESULT_SET_AUTO_FETCH_NEXT_SEGMENT) &&
//	                        !controller.isRecordMode() &&
//	                        controller.isHasMoreData()) {
//	                        controller.readNextSegment();
//	                    }
	                }
	            }
	        });
	        findReplaceTarget = new StyledTextFindReplaceTarget(text);
//	        TextEditorUtils.enableHostEditorKeyBindingsSupport(controller.getSite(), text);

	        applyCurrentThemeSettings();

//	        registerContextMenu();
//	        activateTextKeyBindings(controller, text);
//	        trackPresentationControl();
	    }
	
	public void setText(String text)
	{
		this.text.setText(text);
	}
	 
	@Override
	public Control getControl() {
		 return text;
	}

	@Override
	public void refreshData(boolean refreshMetadata, boolean append, boolean keepState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void formatData(boolean refreshData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearMetaData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateValueView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changeMode(boolean recordMode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DBDAttributeBinding getCurrentAttribute() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String copySelectionToString(ResultSetCopySettings settings) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	

}
