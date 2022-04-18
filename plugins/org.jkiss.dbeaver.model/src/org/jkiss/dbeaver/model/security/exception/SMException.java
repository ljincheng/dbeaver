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
package org.jkiss.dbeaver.model.security.exception;

import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.exec.DBCException;
import org.jkiss.dbeaver.model.exec.DBCExecutionContext;

import java.sql.SQLException;

public class SMException extends DBCException {
    public SMException(String message) {
        super(message);
    }

    public SMException(String message, Throwable cause) {
        super(message, cause);
    }

    public SMException(Throwable cause, DBCExecutionContext executionContext) {
        super(cause, executionContext);
    }

    public SMException(String message, Throwable cause, DBCExecutionContext executionContext) {
        super(message, cause, executionContext);
    }

    @Deprecated
    public SMException(SQLException ex, DBPDataSource dataSource) {
        super(ex, dataSource);
    }

    @Deprecated
    public SMException(Throwable cause, DBPDataSource dataSource) {
        super(cause, dataSource);
    }

    @Deprecated
    public SMException(String message, Throwable cause, DBPDataSource dataSource) {
        super(message, cause, dataSource);
    }
}
