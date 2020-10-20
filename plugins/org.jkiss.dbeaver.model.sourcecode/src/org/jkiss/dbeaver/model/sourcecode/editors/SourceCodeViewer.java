package org.jkiss.dbeaver.model.sourcecode.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.TextViewer;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.model.sourcecode.ui.presentation.PlainTextPresentation;
import org.jkiss.dbeaver.ui.ActionUtils;
import org.jkiss.dbeaver.ui.DBeaverIcons;
import org.jkiss.dbeaver.ui.IHelpContextIds;
import org.jkiss.dbeaver.ui.UIIcon;
import org.jkiss.dbeaver.ui.UIStyles;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.controls.TabFolderReorder;
import org.jkiss.dbeaver.ui.controls.VerticalButton;
import org.jkiss.dbeaver.ui.controls.VerticalFolder;
import org.jkiss.dbeaver.ui.controls.resultset.IResultSetContainer;
import org.jkiss.dbeaver.ui.controls.resultset.IResultSetPanel;
import org.jkiss.dbeaver.ui.controls.resultset.IResultSetPresentation;
import org.jkiss.dbeaver.ui.controls.resultset.handler.ResultSetHandlerMain;
import org.jkiss.dbeaver.ui.controls.resultset.internal.ResultSetMessages;
import org.jkiss.dbeaver.ui.css.CSSUtils;
import org.jkiss.dbeaver.ui.css.DBStyles;
import org.jkiss.utils.CommonUtils;

public class SourceCodeViewer extends Viewer {

	private final Composite mainPanel;
	private final Composite viewerPanel; 
	private SashForm viewerSash;
	
	private  VerticalFolder panelSwitchFolder;
	private  Composite presentationPanel;
	private CTabFolder panelFolder;
	private ToolBarManager panelToolBar;
	  private final Map<String, IResultSetPanel> activePanels = new HashMap<>();
	
	 private final IWorkbenchPartSite site;
//	 private GC sizingGC;
	 private Color defaultBackground, defaultForeground;
	 
	public SourceCodeViewer(@NotNull Composite parent, @NotNull IWorkbenchPartSite site)
    {
        super();
        this.site = site;
//        this.sizingGC = new GC(parent); 
        this.defaultBackground = UIStyles.getDefaultTextBackground();
        this.defaultForeground = UIStyles.getDefaultTextForeground();
        
        this.mainPanel = UIUtils.createPlaceholder(parent,  2);
        
        this.viewerPanel = UIUtils.createPlaceholder(mainPanel, 1);
        this.viewerPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
        CSSUtils.setCSSClass(this.viewerPanel, DBStyles.COLORED_BY_CONNECTION_TYPE);
        UIUtils.setHelp(this.viewerPanel, IHelpContextIds.CTX_RESULT_SET_VIEWER);
//        this.viewerPanel.setRedraw(false);
       
        
        this.panelSwitchFolder = new VerticalFolder(mainPanel, SWT.RIGHT);
        this.panelSwitchFolder.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        CSSUtils.setCSSClass(this.panelSwitchFolder, DBStyles.COLORED_BY_CONNECTION_TYPE);
        
        this.viewerSash = UIUtils.createPartDivider(site.getPart(), this.viewerPanel, SWT.HORIZONTAL | SWT.SMOOTH);//分组控件
        this.viewerSash.setLayoutData(new GridData(GridData.FILL_BOTH));


//        Text textView2=new Text(this.viewerSash, SWT.READ_ONLY);
//        textView2.setText("测试用的2");
//        textView2.setBackground(new Color(50,205,50));
//        textView2.setLayoutData(new GridData(GridData.FILL_BOTH) );
        
        this.presentationPanel = UIUtils.createPlaceholder(this.viewerSash, 1);//主体部分
        this.presentationPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
        JavaEditor javaEditor=new JavaEditor(site);//测试下代码视图放在主体部分效果
        javaEditor.createPartControl(presentationPanel);
         
        settingPanelSwitchFolder();
        settingPannels();
//       this.initView();
    }
	
	//设置面板选项组标签
	public void settingPanelSwitchFolder() {
		{
        	VerticalButton panelsButton = new VerticalButton(panelSwitchFolder, SWT.RIGHT | SWT.CHECK);
            {
                panelsButton.setText(ResultSetMessages.controls_resultset_config_panels);
                panelsButton.setImage(DBeaverIcons.getImage(UIIcon.PANEL_CUSTOMIZE));
                panelsButton.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
//                    	 viewerSash.setMaximizedControl(viewerSash.getChildren()[0]);
//                    	panelsButton.setChecked(isPanelsVisible()); 
                    		boolean isPanelShow=isPanelsVisible();
//                        	viewerSash.setMaximizedControl(null);
                        	showPanels(!isPanelShow);
                    }
                });
                String toolTip = ActionUtils.findCommandDescription(ResultSetHandlerMain.CMD_TOGGLE_PANELS, site, false);
                if (!CommonUtils.isEmpty(toolTip)) {
                    panelsButton.setToolTipText(toolTip);
                }
                panelsButton.setChecked(isPanelsVisible());
            }
        }
        {
        	VerticalButton panelsButton = new VerticalButton(panelSwitchFolder, SWT.RIGHT | SWT.CHECK);
            {
                panelsButton.setText(ResultSetMessages.controls_resultset_config_panels);
                panelsButton.setImage(DBeaverIcons.getImage(UIIcon.PANEL_CUSTOMIZE));
                panelsButton.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
//                    	boolean isPanelShow=isPanelsVisible();
//                    	viewerSash.setMaximizedControl(null);
                    	showPanels(false);
//                    	panelsButton.setChecked(true);
                    }
                });
                String toolTip = ActionUtils.findCommandDescription(ResultSetHandlerMain.CMD_TOGGLE_PANELS, site, false);
                if (!CommonUtils.isEmpty(toolTip)) {
                    panelsButton.setToolTipText(toolTip);
                }
                panelsButton.setChecked(true);
            }
        }
        
        {
        	//布局方向按键
        	 UIUtils.createEmptyLabel(panelSwitchFolder, 1, 1).setLayoutData(new GridData(GridData.FILL_VERTICAL));
             VerticalButton.create(panelSwitchFolder, SWT.RIGHT | SWT.CHECK, site, ResultSetHandlerMain.CMD_TOGGLE_LAYOUT, false);

        }
	}
	
	public void settingPannels()
	{
		 this.panelFolder = new CTabFolder(this.viewerSash, SWT.FLAT | SWT.TOP);
	        CSSUtils.setCSSClass(panelFolder, DBStyles.COLORED_BY_CONNECTION_TYPE);
	        
	        new TabFolderReorder(panelFolder);
	        this.panelFolder.marginWidth = 0;
	        this.panelFolder.marginHeight = 0;
	        this.panelFolder.setMinimizeVisible(true);
	        this.panelFolder.setMRUVisible(true);
	        this.panelFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
	        //设置双击时属性面板最大显示
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
         
         this.panelFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
             @Override
             public void close(CTabFolderEvent event) {
                 CTabItem item = (CTabItem) event.item;
                 String panelId = (String) item.getData();
                 //removePanel(panelId);
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
       
         panelFolder.setRedraw(false);
         String[] tabList= {"Entity","Setting","Project"};
      for(int i=0,k=tabList.length;i<k;i++)
         { 
    	   String title=tabList[i];
	       CTabItem panelTab = new CTabItem(panelFolder, SWT.NONE);
	       panelTab.setData(null);
	       panelTab.setText(title);
	       panelTab.setImage(DBeaverIcons.getImage(UIIcon.PANEL_CUSTOMIZE));
	       panelTab.setToolTipText(title);
	       panelTab.setShowClose(false);
	       PlainTextPresentation plainText=new PlainTextPresentation();
	       plainText.createPresentation(null, panelFolder);
	       plainText.setText("Hello world! NO."+i);
	       
	       panelTab.setControl(plainText.getControl());
	       panelFolder.setSelection(panelTab);
	      
         }
      panelFolder.setRedraw(true);
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
	
	private void initView()
	{ 
        
        
        this.panelToolBar = new ToolBarManager(SWT.HORIZONTAL | SWT.RIGHT | SWT.FLAT);
        Composite trControl = new Composite(panelFolder, SWT.NONE);
        trControl.setLayout(new FillLayout());
        ToolBar panelToolbarControl = this.panelToolBar.createControl(trControl);
        this.panelFolder.setTopRight(trControl, SWT.RIGHT | SWT.WRAP);
        this.panelFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                CTabItem activeTab = panelFolder.getSelection();
                if (activeTab != null) {
                    setActivePanel((String) activeTab.getData());
                }
            }
        });
        this.panelFolder.addListener(SWT.Resize, event -> {
            if (!viewerSash.isDisposed() ) {
                int[] weights = viewerSash.getWeights();
                if (weights.length == 2) {
                    getPresentationSettings().panelRatio = weights[1];
                }
            }
        });
        this.panelFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
            @Override
            public void close(CTabFolderEvent event) {
                CTabItem item = (CTabItem) event.item;
                String panelId = (String) item.getData();
               // removePanel(panelId);
            }

            @Override
            public void minimize(CTabFolderEvent event) {
               // showPanels(false, true, true);
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
        
        panelToolBar.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS) );
        
	}
	
	 private List<IContributionItem> fillPanelsMenu() {
		 List<IContributionItem> items = new ArrayList<>();
		 items.add(ActionUtils.makeCommandContribution(site, ResultSetHandlerMain.CMD_TOGGLE_MAXIMIZE));
       items.add(ActionUtils.makeCommandContribution(site, ResultSetHandlerMain.CMD_TOGGLE_PANELS));
       items.add(ActionUtils.makeCommandContribution(site, ResultSetHandlerMain.CMD_ACTIVATE_PANELS));
		 return items;
	 }
	 
	 
	 static class PresentationSettings {
	        PresentationSettings() {
	        }

	        final Set<String> enabledPanelIds = new LinkedHashSet<>();
	        String activePanelId;
	        int panelRatio;
	        boolean panelsVisible;
	        boolean verticalLayout;
	    }
	 private PresentationSettings getPresentationSettings() {
	        PresentationSettings settings = null;
	        if (settings == null) {
	            settings = new PresentationSettings();
	            // By default panels are visible for column presentations
	             
	        }
	        return settings;
	 }

	 private void setActivePanel(String panelId) {
	        PresentationSettings settings = getPresentationSettings();
	        settings.activePanelId = panelId;
	        IResultSetPanel panel = activePanels.get(panelId);
	        if (panel != null) {
	            panel.activatePanel();
	           
	        }
	    }
	
	 public void togglePanelsMaximize() {
	        if (this.viewerSash.getMaximizedControl() == null) {
	            this.viewerSash.setMaximizedControl(this.panelFolder);
	        } else {
	            this.viewerSash.setMaximizedControl(null);
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

	
	
}
