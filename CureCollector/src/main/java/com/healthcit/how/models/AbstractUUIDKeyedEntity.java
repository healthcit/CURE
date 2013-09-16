/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.models;

import java.util.UUID;

import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

/**
 * Abstract class which allows subclasses to implement a UUID primary key generator.
 * @author Oawofolu
 *
 */
@MappedSuperclass
@EntityListeners( { AbstractUUIDKeyedEntity.UUIDGenerator.class } )
public abstract class AbstractUUIDKeyedEntity {
	@Id
	protected UUID id;
	
	@Transient
	public String getId(){
		return ( this.id == null ? null : this.id.toString() );
	}
	
	public void setId( String id ) {
		this.id = UUID.fromString( id );
	}
	
	/** 
	 * Generates the UUID for this entity
	 * @return
	 */
	public UUID generateUUID(){
		this.id = UUID.randomUUID();
		return this.id;
	}
	
	/**
	 * Overrides the equals method
	 */
	@Override
	public boolean equals( Object obj ) 
	{
		return ( obj == this || 
					( obj instanceof AbstractUUIDKeyedEntity  
					&& StringUtils.equals(((AbstractUUIDKeyedEntity)obj).getId(),this.getId())) );
	}
	
	/**
	 * Overrides the hashCode method
	 */
	@Override
	public int hashCode() {
		return ( getId() == null ? 0 : getId().hashCode() );
	}
	
	public static class UUIDGenerator {
		@PrePersist
		public void onPrePersist( AbstractUUIDKeyedEntity entity ) {
			if ( entity.getId() == null )
				entity.generateUUID();
		}
	}
}
