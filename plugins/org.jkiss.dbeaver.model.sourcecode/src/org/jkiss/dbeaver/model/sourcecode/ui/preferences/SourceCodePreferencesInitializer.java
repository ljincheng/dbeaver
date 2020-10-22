package org.jkiss.dbeaver.model.sourcecode.ui.preferences;


import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.jkiss.dbeaver.core.DBeaverActivator;
import org.jkiss.dbeaver.model.impl.preferences.BundlePreferenceStore;
import org.jkiss.dbeaver.model.preferences.DBPPreferenceStore;

public class SourceCodePreferencesInitializer extends AbstractPreferenceInitializer {

    public SourceCodePreferencesInitializer() {
    }

    @Override
    public void initializeDefaultPreferences() {

        DBPPreferenceStore store = new BundlePreferenceStore(DBeaverActivator.getInstance().getBundle());
        SourceCodePreferences.initializeDefaultPreferences(store);
 
    }

}
