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

package org.apache.rave.portal.repository.impl;

import org.apache.rave.persistence.jpa.AbstractJpaRepository;
import org.apache.rave.portal.model.*;
import org.apache.rave.portal.repository.PageRepository;
import org.apache.rave.util.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JpaPageRepository extends AbstractJpaRepository<Page> implements PageRepository{

    public JpaPageRepository() {
        super(Page.class);
    }

    @Override
    public List<Page> getAllPages(Long userId, PageType pageType) {
        TypedQuery<Page> query = manager.createNamedQuery(JpaPageUser.GET_BY_USER_ID_AND_PAGE_TYPE, Page.class);
        query.setParameter("userId", userId);
        query.setParameter("pageType", pageType);
        return query.getResultList();
    }

    @Override
    public int deletePages(Long userId, PageType pageType) {
        TypedQuery<Page> query = manager.createNamedQuery(Page.DELETE_BY_USER_ID_AND_PAGE_TYPE, Page.class);
        query.setParameter("userId", userId);
        query.setParameter("pageType", pageType);
        return query.executeUpdate();
    }

    @Override
    public boolean hasPersonPage(long userId){
        TypedQuery<Long> query = manager.createNamedQuery(Page.USER_HAS_PERSON_PAGE, Long.class);
        query.setParameter("userId", userId);
        query.setParameter("pageType", PageType.PERSON_PROFILE);
        return query.getSingleResult() > 0;
    }

    @Override
    public List<PageUser> getPagesForUser(Long userId, PageType pageType) {
        TypedQuery<JpaPageUser> query = manager.createNamedQuery(JpaPageUser.GET_PAGES_FOR_USER, JpaPageUser.class);
        query.setParameter("userId", userId);
        query.setParameter("pageType", pageType);
        return CollectionUtils.<PageUser>toBaseTypedList(query.getResultList());
    }

    @Override
    public PageUser getSingleRecord(Long userId, Long pageId){
        TypedQuery<JpaPageUser> query = manager.createNamedQuery(JpaPageUser.GET_SINGLE_RECORD, JpaPageUser.class);
        query.setParameter("userId", userId);
        query.setParameter("pageId", pageId);
        return query.getSingleResult();
    }

    @Override
    public Page createPageForUser(User user, PageTemplate pt) {
        Page personPageFromTemplate = convert(pt, user);
        return personPageFromTemplate;
    }

    /**
     * convert: PageTemplate, User -> Page
     * Converts the PageTemplate for Person Profiles into a Person Profile Page
     * @param pt PageTemplate
     * @param user User
     * @return Page
     */
    private Page convert(PageTemplate pt, User user) {
        Page p = new Page();
        p.setName(pt.getName());
        p.setPageType(pt.getPageType());
        p.setOwner(user);
        PageUser pageUser = new JpaPageUser(p.getOwner(), p, pt.getRenderSequence());
        pageUser.setPageStatus(PageInvitationStatus.OWNER);
        pageUser.setEditor(true);
        List<PageUser> members = new ArrayList<PageUser>();
        members.add(pageUser);
        p.setMembers(members);

        p.setPageLayout(pt.getPageLayout());
        p.setRegions(convertRegions(pt.getPageTemplateRegions(), p));
        p.setSubPages(convertPages(pt.getSubPageTemplates(), p));
        p = save(p);
        return p;
    }

    /**
     * convertRegions: List of PageTemplateRegion, Page -> List of Regions
     * Converts the JpaRegion Templates of the Page Template to Regions for the page
     * @param pageTemplateRegions List of PageTemplateRegion
     * @param page Page
     * @return list of JpaRegion
     */
    private List<Region> convertRegions(List<PageTemplateRegion> pageTemplateRegions, Page page){
        List<Region> regions = new ArrayList<Region>();
        for (PageTemplateRegion ptr : pageTemplateRegions){
            JpaRegion region = new JpaRegion();
            region.setRenderOrder((int) ptr.getRenderSequence());
            region.setPage(page);
            region.setLocked(ptr.isLocked());
            region.setRegionWidgets(convertWidgets(ptr.getPageTemplateWidgets(), region));
            regions.add(region);
        }
        return regions;
    }

    /**
     * convertWidgets: List of PageTemplateWidget, JpaRegion -> List of RegionWidget
     * Converts the Page Template Widgets to RegionWidgets for the given JpaRegion
     * @param pageTemplateWidgets List of PageTemplateWidget
     * @param region JpaRegion
     * @return List of RegionWidget
     */
    private List<RegionWidget> convertWidgets(List<PageTemplateWidget> pageTemplateWidgets, JpaRegion region){
        List<RegionWidget> widgets = new ArrayList<RegionWidget>();
        for (PageTemplateWidget ptw : pageTemplateWidgets){
            RegionWidget regionWidget = new JpaRegionWidget();
            regionWidget.setRegion(region);
            regionWidget.setCollapsed(false);
            regionWidget.setLocked(ptw.isLocked());
            regionWidget.setHideChrome(ptw.isHideChrome());
            regionWidget.setRenderOrder((int) ptw.getRenderSeq());
            regionWidget.setWidget(ptw.getWidget());
            widgets.add(regionWidget);
        }
        return widgets;
    }

    /**
     * convertPages: List of PageTemplate, Page -> List of Page
     * Converts the template subpages in to a list of Pages for the given page object
     * This is a recursive function. A sub page could have a list of sub pages.
     * @param pageTemplates List of PageTemplate
     * @param page Page
     * @return list of Page
     */
    private List<Page> convertPages(List<PageTemplate> pageTemplates, Page page){
        List<Page> pages = new ArrayList<Page>();
        for(PageTemplate pt : pageTemplates){
            Page lPage = new Page();
            lPage.setName(pt.getName());
            lPage.setPageType(pt.getPageType());
            lPage.setOwner(page.getOwner());
            lPage.setPageLayout(pt.getPageLayout());
            lPage.setParentPage(page);
            lPage.setRegions(convertRegions(pt.getPageTemplateRegions(), lPage));

            // create new pageUser tuple
            PageUser pageUser = new JpaPageUser(lPage.getOwner(), lPage, pt.getRenderSequence());
            pageUser.setPageStatus(PageInvitationStatus.OWNER);
            pageUser.setEditor(true);
            List<PageUser> members = new ArrayList<PageUser>();
            members.add(pageUser);
            lPage.setMembers(members);
            // recursive call
            lPage.setSubPages((pt.getSubPageTemplates() == null || pt.getSubPageTemplates().isEmpty()) ? null : convertPages(pt.getSubPageTemplates(), lPage));
            lPage = save(lPage);
            pages.add(lPage);
        }
        return pages;
    }

}