/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.rave.portal.model.conversion;

import org.apache.rave.model.ModelConverter;
import org.apache.rave.portal.model.JpaRegionWidgetPreference;
import org.apache.rave.model.RegionWidgetPreference;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import static org.apache.rave.persistence.jpa.util.JpaUtil.getSingleResult;

@Component
public class JpaRegionWidgetPreferenceConverter implements ModelConverter<RegionWidgetPreference, JpaRegionWidgetPreference> {
    @PersistenceContext
    private EntityManager manager;

    @Override
    public Class<RegionWidgetPreference> getSourceType() {
        return RegionWidgetPreference.class;
    }

    @Override
    public JpaRegionWidgetPreference convert(RegionWidgetPreference source) {
        return source instanceof JpaRegionWidgetPreference ? (JpaRegionWidgetPreference) source : createEntity(source);
    }

    private JpaRegionWidgetPreference createEntity(RegionWidgetPreference source) {
        JpaRegionWidgetPreference converted = null;
        if (source != null) {
            TypedQuery<JpaRegionWidgetPreference> query = manager.createNamedQuery(JpaRegionWidgetPreference.FIND_BY_REGION_WIDGET_AND_NAME, JpaRegionWidgetPreference.class);
            query.setParameter(JpaRegionWidgetPreference.NAME_PARAM, source.getName());
            query.setParameter(JpaRegionWidgetPreference.REGION_WIDGET_ID, Long.parseLong(source.getRegionWidgetId()));
            converted = getSingleResult(query.getResultList());

            if (converted == null) {
                converted = new JpaRegionWidgetPreference();
            }
            updateProperties(source, converted);
        }
        return converted;
    }

    private void updateProperties(RegionWidgetPreference source, JpaRegionWidgetPreference converted) {
        converted.setName(source.getName());
        converted.setRegionWidgetId(source.getRegionWidgetId());
        converted.setValue(source.getValue());
    }
}
