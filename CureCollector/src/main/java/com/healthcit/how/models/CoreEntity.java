/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.models;


import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.healthcit.how.models.EntityTagPermission.TagAccessPermissions;

@Entity
@Table(name="core_entity")
public class CoreEntity implements StateTracker{

    
	@Id
	private String id; //UUID

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="core_entity_sharing_group", joinColumns = {
			@JoinColumn(name="entity_id") 
	},
	inverseJoinColumns = {
	@JoinColumn(name="group_id")
	})
	List<SharingGroup> sharingGroups = new ArrayList<SharingGroup>();
	
	@OneToMany(cascade={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.LAZY, orphanRemoval=true)
	@JoinColumn(name = "entity_id")
	@MapKey
	Map<EntityTagPermissionPk, EntityTagPermission> tagAccessPermissions = new HashMap<EntityTagPermissionPk, EntityTagPermission>();
	
	public boolean isNew() {
		return (id == null);
	}
	
	public CoreEntity()
	{
		id = UUID.randomUUID().toString();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setSharingGroups(List<SharingGroup> sharingGroups)
	{
		this.sharingGroups = sharingGroups;
		
	}
	
	public void addSharingGroup(SharingGroup sharingGroup)
	{
		sharingGroups.add(sharingGroup);
	}
	
	public List<SharingGroup> getSharingGroups()
	{
		return this.sharingGroups;
	}
	
	public boolean canRead(String tagId)
	{
		EntityTagPermissionPk pk = new EntityTagPermissionPk();
		pk.setEntityId(id);
		pk.setTagId(tagId);
		pk.setTagAccessPermission(TagAccessPermissions.READ);
		
		
		if( tagAccessPermissions != null && tagAccessPermissions.containsKey(pk))
		{
			return true;
	}
		return false;
	}
	
	public boolean canRead(Tag tag)
	{
		EntityTagPermissionPk pk = new EntityTagPermissionPk();
		pk.setEntityId(id);
		pk.setTagId(tag.getId());
		pk.setTagAccessPermission(TagAccessPermissions.READ);
	
		
		if( tagAccessPermissions != null && tagAccessPermissions.containsKey(pk))
		{
			return true;
		}
		return false;
	}
	public boolean canWrite(String tagId)
	{
		EntityTagPermissionPk pk = new EntityTagPermissionPk();
		pk.setEntityId(id);
		pk.setTagId(tagId);
		pk.setTagAccessPermission(TagAccessPermissions.WRITE);
		if( tagAccessPermissions != null && tagAccessPermissions.containsKey(pk))
		{
			return true;
		}
		return false;
	}
	public boolean canSubmit(String tagId)
	{
		EntityTagPermissionPk pk = new EntityTagPermissionPk();
		pk.setEntityId(id);
		pk.setTagId(tagId);
		pk.setTagAccessPermission(TagAccessPermissions.SUBMIT);
		if( tagAccessPermissions != null && tagAccessPermissions.containsKey(pk))
		{
			return true;
		}
		return false;
	}
	public boolean canApprove(String tagId)
	{
		EntityTagPermissionPk pk = new EntityTagPermissionPk();
		pk.setEntityId(id);
		pk.setTagId(tagId);
		pk.setTagAccessPermission(TagAccessPermissions.SUBMIT);
		if( tagAccessPermissions != null && tagAccessPermissions.containsKey(pk))
		{
			return true;
		}
		return false;
	}
	
	public void setFormTagAccessPermissions(List<EntityTagPermission> permissions)
	{
//		Map<EntityTagPermissionPk, EntityTagPermission> tagAccessPermissionsLocal = new HashMap<EntityTagPermissionPk, EntityTagPermission>();
//		for(EntityTagPermissionPk pk: tagAccessPermissions.keySet())
//		{
//			tagAccessPermissionsLocal.put(pk, tagAccessPermissions.get(pk));
//		}
//		tagAccessPermissionsLocal.putAll(tagAccessPermissions);
		for(EntityTagPermission permission: permissions)
		{
			tagAccessPermissions.put(permission.getPrimaryKey(), permission);
//			tagAccessPermissionsLocal.put(permission.getPrimaryKey(), permission);
		}
//		tagAccessPermissions = tagAccessPermissionsLocal;
	}
	
	public void setModuleTagAccessPermissions()
	{
		
		
	}
	public void addFormAccessPermission(TagAccessPermissions permission, String tagId)
	{
		EntityTagPermission permissions = new EntityTagPermission(this.getId(), tagId, permission);

		tagAccessPermissions.put(permissions.getPrimaryKey(), permissions);
	}

	public Collection<EntityTagPermission> getTagPermissions()
	{
		Collection<EntityTagPermission> permissions = tagAccessPermissions.values();
		return permissions;
	}
	
	public EnumSet<TagAccessPermissions> getTagPermissionsForTag(Tag tag)
	{
		EnumSet<TagAccessPermissions> tagPermissions = EnumSet.noneOf(TagAccessPermissions.class);
		TagAccessPermissions[] permissions = TagAccessPermissions.values();
		EntityTagPermissionPk pk = new EntityTagPermissionPk();
		pk.setEntityId(id);
		pk.setTagId(tag.getId());

		for(TagAccessPermissions permission: permissions)
		{
			pk.setTagAccessPermission(permission);
			if(tagAccessPermissions.containsKey(pk))
			{
				tagPermissions.add(permission);
			}
		}
		return tagPermissions;
	}
	public EnumSet<TagAccessPermissions> getTagPermissionsForTag(String tagId)
	{
		EnumSet<TagAccessPermissions> tagPermissions = EnumSet.noneOf(TagAccessPermissions.class);
		TagAccessPermissions[] permissions = TagAccessPermissions.values();
		EntityTagPermissionPk pk = new EntityTagPermissionPk();
		pk.setEntityId(id);
		pk.setTagId(tagId);

		for(TagAccessPermissions permission: permissions)
		{
			pk.setTagAccessPermission(permission);
			if(tagAccessPermissions.containsKey(pk))
			{
				tagPermissions.add(permission);
			}
		}
		return tagPermissions;
	}
	
	public void removeAllPermissions()
	{
		tagAccessPermissions.clear();
	}
}
