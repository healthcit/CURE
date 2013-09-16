/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.models;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name="entity_tag_permission")
//@IdClass(EntityTagPermissionPk.class)
public class EntityTagPermission {
    public enum TagAccessPermissions {READ, WRITE, SUBMIT, APPROVE}

//    @Transient
    @EmbeddedId
    EntityTagPermissionPk pk;
 
    public EntityTagPermission()
    {
    	pk = new EntityTagPermissionPk();
    }
//    public EntityTagPermission(CoreEntity entity, SharingGroup sharingGroup, Tag tag, TagAccessPermissions permission )
//    {
//    	this.entityId = entityId;
//    	this.tagId = tagId;
//    	this.tagAccessPermission = permission;
//    	pk = new EntityTagPermissionPk(entityId, tagId, permission);
//    	pk = new EntityTagPermissionPk(entity, sharingGroup, tag, permission);
//    }
    
    public EntityTagPermission(String entityId, String tagId, TagAccessPermissions permission )
    {
    	pk = new EntityTagPermissionPk(entityId, tagId, permission);
    }
    
    public String getEntityId()
    {
//    	return entityId;
    	return pk.getEntityId();
    	
    }
    
    public String getTagId()
    {
    	//return tagId;
    	return pk.getTagId();
    }
    
    public TagAccessPermissions getAccessPermission()
    {
    	//return tagAccessPermission;
    	return pk.getTagAccessPermissions();
    }
    
    public EntityTagPermissionPk getPrimaryKey()
    {
    	return pk;
    }
    public void setEntityId(String entityId)
    {
    	pk.setEntityId(entityId);
    }
	
    public void setTagId(String tagId)
    {
    	pk.setTagId(tagId);
    }
     
    public void setEntityId(CoreEntity entity)
    {
    	pk.setEntityId(entity.getId());
    }
	
    public void setTagId(Tag tag)
    {
    	pk.setTagId(tag.getId());
    }
  
    public void setTagAccessPermission(TagAccessPermissions tagAccessPermission)
    {
    	pk.setTagAccessPermission(tagAccessPermission);
    }
}
