package com.healthcit.how.handlers;

import java.util.Iterator;
import java.util.List;

import com.healthcit.how.models.FormMetaDataSummary;

/* Iterator<FormMetaDataSummary> */
public class FormMetaDataSummaryIterator implements Iterator<FormMetaDataSummary>
{
	private FormMetaDataSummaryHandler handler;
	
	private List<FormMetaDataSummary> metaDataElements;
	
	private Iterator<FormMetaDataSummary> iterator;
	
	private enum Status { FORM_UNCHANGED, FORM_CHANGED, FORM_HIERARCHY_CHANGED, COMPLETED };
					
	public FormMetaDataSummaryIterator( FormMetaDataSummaryHandler handler )
	{
		this.handler          = handler;
		this.metaDataElements = this.handler.getMetaDataElements();
		this.iterator		  = this.metaDataElements.iterator();
	}

	@Override
	public boolean hasNext(){
		return this.iterator.hasNext();
	}

	@Override
	public FormMetaDataSummary next(){
		FormMetaDataSummary next  = this.iterator.next();
		
		// Implement any appropriate finalizations before iterating to the next element
		performFinalizations( next );
		
		// Update the iterator so that "next" is now the current element
		this.handler.incrementCurrentIndex();
		this.handler.setCurrentMetaDataElement( next );
		
		if ( this.handler.getCurrentMetaDataElement().hasQuestionAnswerData() )
		{
			handler.addQuestionAndAnswerDataToInstanceType();
		}
		
		return next;
	}

	@Override
	public void remove() {
		// Should NOT be implemented
	}
	
	public void performFinalizations( FormMetaDataSummary metaDataElement )
	{
		this.handler.setCurrentFormId    (       metaDataElement.getFormId() );
		this.handler.setCurrentRootFormId(   metaDataElement.getRootFormId() );
		
		switch ( getCurrentStatus() )
		{
		case FORM_CHANGED:
			this.handler.finalizeFormTypes();
			break;
		case FORM_HIERARCHY_CHANGED:
			this.handler.finalizeFormTypesAndAddToOwner();
			break;
		case COMPLETED:
			this.handler.finalizeFormTypesAndAddToOwner();
			break;
		default:
			break;
		}
	}
	
	public Status getCurrentStatus() {
		if      ( handler.movedToLastElement() ) return Status.COMPLETED;
		else if ( handler.movedToNewFormHierarchy() ) return Status.FORM_HIERARCHY_CHANGED;
		else if ( handler.movedToNewForm() ) return Status.FORM_CHANGED;
		else return Status.FORM_UNCHANGED;
	}
	
	
	
}