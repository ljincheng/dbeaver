package org.jkiss.dbeaver.model.sourcecode.ui.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.model.sourcecode.editors.JavaEditor;
import org.jkiss.dbeaver.model.sourcecode.ui.context.EntityContext;
import org.jkiss.dbeaver.model.sourcecode.ui.context.TabItemContext;
import org.jkiss.dbeaver.model.sourcecode.ui.event.ViewDataRunnableEvent;
import org.jkiss.dbeaver.model.sourcecode.ui.handler.EntityCodeViewHandler;
import org.jkiss.dbeaver.ui.ActionUtils;
import org.jkiss.dbeaver.ui.DBeaverIcons;
import org.jkiss.dbeaver.ui.IHelpContextIds;
import org.jkiss.dbeaver.ui.UIIcon;
import org.jkiss.dbeaver.ui.UIStyles;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.controls.VerticalButton;
import org.jkiss.dbeaver.ui.controls.VerticalFolder;
import org.jkiss.dbeaver.ui.controls.resultset.handler.ResultSetHandlerTogglePanel;
import org.jkiss.dbeaver.ui.css.CSSUtils;
import org.jkiss.dbeaver.ui.css.DBStyles;
import org.jkiss.utils.CommonUtils;

/**
 * 单个Entity的视图
 * @author ljc
 *
 */
public class EntityCodeView extends Viewer implements SelectionListener,ViewDataRunnableEvent{
	
	public static final String CONTROL_ID = EntityCodeView.class.getName();
	private EntityContext context;
	private final IWorkbenchPartSite site;
	private Color defaultBackground, defaultForeground;
	
	private final Composite mainPanel;
	private final Composite viewerPanel; 
	private SashForm viewerSash;
	
	private  VerticalFolder panelSwitchFolder;
	private  Composite presentationPanel;
	private CTabFolder panelFolder;
	private ToolBarManager panelToolBar;
	private JavaEditor mJavaEditor;
	
	 private Composite statusBar;
	  
	
	public EntityCodeView(@NotNull Composite parent, @NotNull IWorkbenchPartSite site,EntityContext context)
    {
		super();
		this.site=site;
		this.context=context;
		this.context.setViewDataRunnableEvent(this);
		
		this.defaultBackground = UIStyles.getDefaultTextBackground();
		this.defaultForeground = UIStyles.getDefaultTextForeground();
		this.mainPanel = UIUtils.createPlaceholder(parent, 2);

		this.viewerPanel = UIUtils.createPlaceholder(mainPanel, 1);
		this.viewerPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.viewerPanel.setData(CONTROL_ID, this);
		CSSUtils.setCSSClass(this.viewerPanel, DBStyles.COLORED_BY_CONNECTION_TYPE);
		UIUtils.setHelp(this.viewerPanel, IHelpContextIds.CTX_RESULT_SET_VIEWER);
	    //this.viewerPanel.setRedraw(false);

		//右边板面的选择卡
		this.panelSwitchFolder = new VerticalFolder(mainPanel, SWT.RIGHT);
		this.panelSwitchFolder.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		CSSUtils.setCSSClass(this.panelSwitchFolder, DBStyles.COLORED_BY_CONNECTION_TYPE);

		// 分组控件
		this.viewerSash = UIUtils.createPartDivider(site.getPart(), this.viewerPanel, SWT.HORIZONTAL | SWT.SMOOTH);
		this.viewerSash.setLayoutData(new GridData(GridData.FILL_BOTH));

		// 主体部分
		this.presentationPanel = UIUtils.createPlaceholder(this.viewerSash, 1);
		this.presentationPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		 
		init_panelSwitchFolder();
		init_pannels();
		init_codePannel();
		init_statusBar();
		refreshActions();
    }
	
	//设置面板选项组标签
	public void init_panelSwitchFolder() {
		if(context!=null && context.panelTabs()!=null)
		{
			List<TabItemContext> tabItems=context.panelTabs();
			for(int i=0,k=tabItems.size();i<k;i++)
			{
				TabItemContext tabItem=tabItems.get(i); 
				VerticalButton panelsButton = new VerticalButton(panelSwitchFolder, SWT.RIGHT | SWT.CHECK);
				panelsButton.setText(tabItem.getName());
				panelsButton.setData(tabItem.getId());
				panelsButton.setImage(DBeaverIcons.getImage(UIIcon.PANEL_CUSTOMIZE));
				panelsButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						boolean isPanelShow = isPanelsVisible();
						showPanels(!isPanelShow);
					}
				});
				String toolTip =tabItem.getTip();
				if (!CommonUtils.isEmpty(tabItem.getTip())) {
					panelsButton.setToolTipText(toolTip);
				}
				panelsButton.setChecked(isPanelsVisible());
			}
		}
		 
	 
	}
	
	 
	
	public void init_pannels()
	{
		 this.panelFolder = new CTabFolder(this.viewerSash, SWT.FLAT | SWT.TOP);
         CSSUtils.setCSSClass(panelFolder, DBStyles.COLORED_BY_CONNECTION_TYPE);
//			new TabFolderReorder(panelFolder);
			this.panelFolder.marginWidth = 0;
			this.panelFolder.marginHeight = 0;
			this.panelFolder.setMinimizeVisible(true);
			this.panelFolder.setMRUVisible(true);
			this.panelFolder.setLayoutData(new GridData(GridData.FILL_BOTH)); 
			// 设置双击时属性面板最大显示
			this.panelFolder.addListener(SWT.MouseDoubleClick, event -> {
				if (event.button != 1) {
					return;
				}
				CTabItem selectedItem = panelFolder.getItem(new Point(event.getBounds().x, event.getBounds().y));
				if (selectedItem != null && selectedItem == panelFolder.getSelection()) {
					togglePanelsMaximize();
				}
			});
			
		 this.panelToolBar = new ToolBarManager(SWT.HORIZONTAL | SWT.RIGHT | SWT.FLAT);//属性面板中的工具栏
		 Composite trControl = new Composite(panelFolder, SWT.NONE);
         trControl.setLayout(new FillLayout());
         ToolBar panelToolbarControl = this.panelToolBar.createControl(trControl);
         this.panelFolder.setTopRight(trControl, SWT.RIGHT | SWT.WRAP);
         this.panelFolder.addSelectionListener(this);
         this.panelFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
             @Override
             public void close(CTabFolderEvent event) {
                 
             }

             @Override
             public void minimize(CTabFolderEvent event) {
                 showPanels(false);
             }

             @Override
             public void maximize(CTabFolderEvent event) {

             }
         });
         MenuManager panelsMenuManager = new MenuManager();
         panelsMenuManager.setRemoveAllWhenShown(true);
         panelsMenuManager.addMenuListener(manager -> {
             for (IContributionItem menuItem : fillPanelsMenu()) {
                 panelsMenuManager.add(menuItem);
             }
         });
         Menu panelsMenu = panelsMenuManager.createContextMenu(this.panelFolder);
         this.panelFolder.setMenu(panelsMenu);
         this.panelFolder.addDisposeListener(e -> panelsMenuManager.dispose()); 
	       if(this.context!=null)
	       {
	         this.context.createPanelTabs(this.site.getPart(),this.panelFolder);
	       }
	}
	
	 
		 
	
	public void init_codePannel() {
		if(this.context!=null)
		{
			 
			 	mJavaEditor=new JavaEditor(site);//测试下代码视图放在主体部分效果
			 	mJavaEditor.createPartControl(presentationPanel);
			 	mJavaEditor.setCode(this.context.generateCode());
		}
	}
	
	 class NewFragmentAction extends Action {
		 
	        public NewFragmentAction() {
	           // super("test",UIIcon.ACCEPT);
	          //  setImageDescriptor(PDEPluginImages.DESC_NEWFRAGPRJ_TOOL);
	        }
	 
	        @Override
	        public void run() {
	           // handleNewFragment();
	        }
	    }
	 
	 private void init_statusBar()
	    {
	        Composite statusComposite = UIUtils.createPlaceholder(viewerPanel, 3);
	        statusComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	        statusBar = new Composite(statusComposite, SWT.NONE);
	        statusBar.setBackgroundMode(SWT.INHERIT_FORCE);
	        statusBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	        CSSUtils.setCSSClass(statusBar, DBStyles.COLORED_BY_CONNECTION_TYPE);
	        RowLayout toolbarsLayout = new RowLayout(SWT.HORIZONTAL);
	        toolbarsLayout.marginTop = 0;
	        toolbarsLayout.marginBottom = 0;
	        toolbarsLayout.center = true;
	        toolbarsLayout.wrap = true;
	        toolbarsLayout.pack = true;
	        //toolbarsLayout.fill = true;
	        statusBar.setLayout(toolbarsLayout);
//	        statusBar.setData(CONTROL_ID, this);
	        {
	        	 ToolBarManager editToolBarManager = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL | SWT.RIGHT);
	        	 editToolBarManager.add(new Separator());
	        	 editToolBarManager.add(ActionUtils.makeCommandContribution(site, EntityCodeViewHandler.CODE_REFRESH,"Refresh",null,"Refresh",false));
	        	 editToolBarManager.add(ActionUtils.makeCommandContribution(site, EntityCodeViewHandler.CODE_COPY,"Copy",null,"Copy source code",false));
	        	 editToolBarManager.add(ActionUtils.makeCommandContribution(site, EntityCodeViewHandler.CODE_SAVE,"Save",null,"Save source code",false));
	        	 
	    			
	    			 ToolBar editorToolBar = editToolBarManager.createControl(statusBar);
	 	            CSSUtils.setCSSClass(editorToolBar, DBStyles.COLORED_BY_CONNECTION_TYPE);


	        }
	         
	         
	         
	    }

	
//	public void runCode() {
//		if(this.context!=null && mJavaEditor!=null)
//		{
//			UIUtils.runInUI(context);
//			mJavaEditor.setCode(context.generateCode());
//		}
//	}
	
	 private List<IContributionItem> fillPanelsMenu() {
		 List<IContributionItem> items = new ArrayList<>();
		List<TabItemContext> tabItems=this.context.panelTabs();
		for(int i=0,k=tabItems.size();i<k;i++) {
			TabItemContext tabItem = tabItems.get(i);
			CommandContributionItemParameter params = new CommandContributionItemParameter(site, tabItem.getId(),
					ResultSetHandlerTogglePanel.CMD_TOGGLE_PANEL, CommandContributionItem.STYLE_CHECK);
			Map<String, String> parameters = new HashMap<>();
			parameters.put(ResultSetHandlerTogglePanel.PARAM_PANEL_ID, tabItem.getId());
			params.parameters = parameters;
			items.add(new CommandContributionItem(params));
		 }
//		 items.add(ActionUtils.makeCommandContribution(site, ResultSetHandlerMain.CMD_TOGGLE_MAXIMIZE));
//       items.add(ActionUtils.makeCommandContribution(site, ResultSetHandlerMain.CMD_TOGGLE_PANELS));
//       items.add(ActionUtils.makeCommandContribution(site, ResultSetHandlerMain.CMD_ACTIVATE_PANELS));
		 return items;
	 }

		//设置属性面板是否可见
		public void showPanels(boolean show) {
			 if (!show) {
		            viewerSash.setMaximizedControl(viewerSash.getChildren()[0]);
			  } else {
				  viewerSash.setMaximizedControl(null);
			  }
		}
	   
		//判断属性面板是否可见
	    public boolean isPanelsVisible() {
	        return viewerSash != null && viewerSash.getMaximizedControl() == null;
	    }
		
	    public void togglePanelsMaximize() {
	        if (this.viewerSash.getMaximizedControl() == null) {
	            this.viewerSash.setMaximizedControl(this.panelFolder);
	        } else {
	            this.viewerSash.setMaximizedControl(null);
	        }
	    }
	    
	    private void setActivePanel(String panelId) {
			if (!CommonUtils.isEmpty(panelId)) {
				for (CTabItem panelItem : panelFolder.getItems()) {
					if (panelItem.getData() != null && panelId.equals(panelItem.getData())) {
						panelFolder.setSelection(panelItem);
					}
				}
			}
		}
	@Override
	public Control getControl() {
		return mainPanel;
	}

	@Override
	public Object getInput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISelection getSelection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInput(Object input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSelection(ISelection selection, boolean reveal) {
		// TODO Auto-generated method stub
		
	}

	
	//############### Panel Selected Event  ######################
	@Override
	public void widgetSelected(SelectionEvent e) {
		 CTabItem activeTab = panelFolder.getSelection();
         if (activeTab != null) {
             setActivePanel((String) activeTab.getData());
         }
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
		
	}

	@Override
	public boolean refreshActions() {
		if(this.context!=null && mJavaEditor!=null)
		{
			UIUtils.runInUI(context);
			mJavaEditor.setCode(context.generateCode());
			return true;
		}
		return false;
	}
	
	public String getSourceCode() {
		return mJavaEditor.getCode();
	}
	

}
