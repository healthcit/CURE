/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.healthcit.how.models.EntityTagPermission.TagAccessPermissions;

@Embeddable
public class EntityTagPermissionPk  implements Serializable{
	
	@Column(name="entity_id")
	private String entityId;
	@Column(name="tag_id")
	private String tagId;
	
	@Column(name = "permission", nullable = false)
	@Enumerated(EnumType.STRING)
	private TagAccessPermissions tagAccessPermission;

	public EntityTagPermissionPk()
	{
		
	}
//	public EntityTagPermissionPk(CoreEntity entity, String groupId, Tag tag,TagAccessPermissions permission )
//	{
//		this.entityId = entity.getId();
//		this.tagId = tag.getId();
//		this.groupId = group.getId();
//		this.tagAccessPermission = permission;
//	}
	
	public EntityTagPermissionPk(String entityId, String tagId,TagAccessPermissions permission )
	{
		this.entityId = entityId;
		this.tagId = tagId;
		this.tagAccessPermission = permission;
	}
	public void setEntityId(String entityId)
	{
		this.entityId = entityId;
	}
	
	public void setTagId(String tagId)
	{
		this.tagId = tagId;
	}
	

//	public void setEntity(CoreEntity entity)
//	{
//		this.entity = entity.getId();
//	}
//	
//	public void setTag(Tag tag)
//	{
//		this.tag = tag.getId();
//	}
	
	public void setTagAccessPermission(TagAccessPermissions permission)
	{
		this.tagAccessPermission = permission;
	}
	
	public String getEntityId()
	{
		return entityId;
	}
	
	public String getTagId()
	{
		return this.tagId;
	}


//	public String getEntityId()
//	{
//		return entity;
//	}
//	
//	public String getTagId()
//	{
//		return this.tag;
//	}

	@Override
	public int hashCode()
	{
		int hashCode = (entityId + tagId + tagAccessPermission.toString()).hashCode();
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		boolean areEquals = false;
		if ( obj!= null && obj instanceof EntityTagPermissionPk)
		{
			EntityTagPermissionPk pk2 = (EntityTagPermissionPk)obj;
			if(pk2.entityId.equals(this.entityId)  && pk2.tagId.equals(this.tagId) && pk2.tagAccessPermission.equals(this.tagAccessPermission))
			{
				areEquals = true;
			}
		}		
		return areEquals;
	}
	
	public TagAccessPermissions getTagAccessPermissions()
	{
		return this.tagAccessPermission;
	}
	

}
