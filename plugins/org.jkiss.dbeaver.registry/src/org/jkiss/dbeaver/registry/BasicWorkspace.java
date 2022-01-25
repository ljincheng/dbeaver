/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2022 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.registry;

import org.eclipse.core.resources.IWorkspace;
import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.model.app.DBPPlatform;

/**
 * DBeaver workspace.
 *
 * Basically just a wrapper around Eclipse workspace.
 * Additionally holds information about remote workspace.
 * Identified by unique ID (random UUID).
 */
public class BasicWorkspace extends BaseWorkspaceImpl {

    private String workspaceId;

    public BasicWorkspace(DBPPlatform platform, IWorkspace eclipseWorkspace) {
        super(platform, eclipseWorkspace);

        workspaceId = readWorkspaceId();
    }

    @NotNull
    @Override
    public String getWorkspaceId() {
        return workspaceId;
    }

}