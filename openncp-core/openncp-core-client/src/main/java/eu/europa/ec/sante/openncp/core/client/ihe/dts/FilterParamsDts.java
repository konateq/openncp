/*
 * This file is part of epSOS OpenNCP implementation
 * Copyright (C) 2012  SPMS (Serviços Partilhados do Ministério da Saúde - Portugal)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact email: epsos@iuz.pt
 */
package eu.europa.ec.sante.openncp.core.client.ihe.dts;

import eu.europa.ec.sante.openncp.core.common.datamodel.FilterParams;

public class FilterParamsDts {


    public static FilterParams newInstance(final eu.europa.ec.sante.openncp.core.client.FilterParams filterParams) {

        if( filterParams == null) {
            return null;
        }

        final FilterParams result = new FilterParams();

        if(filterParams.getCreatedBefore() != null) {
            result.setCreatedBefore(filterParams.getCreatedBefore().getTime().toInstant());
        }
        if(filterParams.getCreatedAfter() != null) {
            result.setCreatedAfter(filterParams.getCreatedAfter().getTime().toInstant());
        }

        if(filterParams.getMaximumSize() != null) {
            result.setMaximumSize(filterParams.getMaximumSize().longValue());
        }


        return result;
    }

    private FilterParamsDts() {
    }
}
