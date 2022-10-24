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
package org.jkiss.dbeaver.ext.mysql.data;

import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.model.DBValueFormatting;
import org.jkiss.dbeaver.model.data.DBDDisplayFormat;
import org.jkiss.dbeaver.model.impl.jdbc.data.handlers.JDBCUUIDValueHandler;
import org.jkiss.dbeaver.model.struct.DBSTypedObject;

public class MySQLUUIDValueHandlerProvider extends JDBCUUIDValueHandler {

    static final MySQLUUIDValueHandlerProvider INSTANCE = new MySQLUUIDValueHandlerProvider();

    @NotNull
    @Override
    public String getValueDisplayString(@NotNull DBSTypedObject column, Object value, @NotNull DBDDisplayFormat format) {
        return DBValueFormatting.getDefaultValueDisplayString(value, format);
    }
}
