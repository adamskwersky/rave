/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.rave.portal.model.conversion.impl;

import org.apache.rave.portal.model.PageLayout;
import org.apache.rave.portal.model.conversion.HydratingModelConverter;
import org.apache.rave.portal.model.impl.PageLayoutImpl;
import org.springframework.stereotype.Component;

@Component
public class PageLayoutImplConverter implements HydratingModelConverter<PageLayout, PageLayoutImpl> {
    @Override
    public void hydrate(PageLayoutImpl dehydrated) {
        //NOOP
    }

    @Override
    public Class<PageLayout> getSourceType() {
        return PageLayout.class;
    }

    @Override
    public PageLayoutImpl convert(PageLayout source) {
        PageLayoutImpl converted = source instanceof PageLayoutImpl ? ((PageLayoutImpl)source) : new PageLayoutImpl();
        converted.setCode(source.getCode());
        converted.setNumberOfRegions(source.getNumberOfRegions());
        converted.setRenderSequence(source.getRenderSequence());
        converted.setUserSelectable(source.isUserSelectable());
        return converted;
    }
}