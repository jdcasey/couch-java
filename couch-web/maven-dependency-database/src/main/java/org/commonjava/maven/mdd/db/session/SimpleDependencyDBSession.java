/*******************************************************************************
 * Copyright (C) 2011  John Casey
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.commonjava.maven.mdd.db.session;

public class SimpleDependencyDBSession
    implements DependencyDBSession
{

    private final String baseUrl;

    private final String logicApplication;

    public SimpleDependencyDBSession( final String baseUrl )
    {
        this.baseUrl = baseUrl;
        this.logicApplication = DEFAULT_LOGIC_APPLICATION;
    }

    public SimpleDependencyDBSession( final String baseUrl, final String logicApplication )
    {
        this.baseUrl = baseUrl;
        this.logicApplication = logicApplication;
    }

    @Override
    public String getBaseUrl()
    {
        return baseUrl;
    }

    @Override
    public String getLogicApplication()
    {
        return logicApplication;
    }

}
