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

package org.apache.rave.portal.model;

import org.apache.rave.portal.model.impl.PageUserImpl;
import org.apache.rave.portal.repository.UserRepository;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;


@XmlAccessorType(value = XmlAccessType.FIELD)
public class MongoDbPageUser extends PageUserImpl {
    @XmlTransient @JsonIgnore
    private UserRepository userRepository;

    private Long userId;

    public MongoDbPageUser() {}

    public MongoDbPageUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MongoDbPageUser(User owner, Page lPage, long renderSequence) {
        super(owner, lPage, renderSequence);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUser() {
        User user = super.getUser();
        if(user == null) {
            user = userRepository.get(userId);
            super.setUser(user);
        }
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageUser)) return false;
        PageUser that = (PageUser) o;
        return !(this.getId() != null ? !this.getId().equals(that.getId()) : that.getId() != null);

    }

    @Override
    public int hashCode() {
        return this.getId() != null ? this.getId().hashCode() : 0;
    }
}