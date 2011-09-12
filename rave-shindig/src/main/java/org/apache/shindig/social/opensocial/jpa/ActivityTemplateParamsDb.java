/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.shindig.social.opensocial.jpa;

import org.apache.shindig.social.opensocial.jpa.api.DbObject;
import org.apache.shindig.social.opensocial.model.Activity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import static javax.persistence.CascadeType.ALL;

/**
 *
 */
@Entity
@Table(name = "template_params")
public class ActivityTemplateParamsDb implements DbObject {
    
  /**
   * The internal object ID used for references to this object.
   */
  @Id
  @Column(name = "oid")
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "activityTemplateParamsIdGenerator")
  @TableGenerator(name = "activityTemplateParamsIdGenerator", table = "RAVE_SHINDIG_SEQUENCES", pkColumnName = "SEQ_NAME",
          valueColumnName = "SEQ_COUNT", pkColumnValue = "template_params", allocationSize = 1, initialValue = 1)
  protected long objectId;

  /**
   * An optimistic locking field.
   */
  @Version
  @Column(name = "version")
  protected long version;

  /**
   * Create a link to the activities joining activity_id here to oid in
   * activities.
   */

  @ManyToOne(targetEntity = ActivityDb.class, cascade = ALL)
  @JoinColumn(name = "activity_id", referencedColumnName = "oid")
  protected Activity activity;
  // TODO RAVE-25: <openjpa-1.2.0-r422266:683325 fatal user error>
  // org.apache.openjpa.persistence.ArgumentException: The type of field
  // "org.apache.shindig.social.opensocial.jpa.ActivityTemplateParamsDb.activities"
  // isn't supported
  // by declared persistence strategy "ManyToOne". Please choose a different
  // strategy.

  /**
   * The name of the value in the template parameters.
   */
  @Basic
  @Column(name = "template_name")
  protected String name;

  /**
   * The value of the parameter.
   */
  @Basic
  @Column(name = "template_value")
  protected String value;

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.jpa.api.DbObject#getObjectId()
   */
  public long getObjectId() {
    return objectId;
  }

}
