/**
 * The FormMetaDataSummaryHandler is responsible for iterating through a set of ordered FormMetaDataSummary elements
 * (acquired based on a results of a database query),
 * using the data to construct a JAXB tree which corresponds to the Gateway Form Schema.
 * 
 * NOTE: This handler currently uses an iterative approach to construct the tree, 
 * as opposed to a recursive approach.
 * @author oawofolu
 */

package com.healthcit.how.handlers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.healthcit.cacure.metadata.gateway.AnswerType;
import com.healthcit.cacure.metadata.gateway.FieldType;
import com.healthcit.cacure.metadata.gateway.FormType;
import com.healthcit.cacure.metadata.gateway.InstanceType;
import com.healthcit.cacure.metadata.gateway.ObjectFactory;
import com.healthcit.cacure.metadata.gateway.OwnerType;
import com.healthcit.cacure.metadata.gateway.RecordType;
import com.healthcit.how.models.FormMetaDataSummary;

public class FormMetaDataSummaryHandler implements Iterable<FormMetaDataSummary>{
	
	private ObjectFactory objectFactory;		
	private JSONObject questionsAndAnswers;
	private OwnerType ownerType;
	private List<FormMetaDataSummary> metaDataElements;

	
	private String formId                              = null;
	private String rootFormId                          = null;
	private LinkedList<FormType> formTypes             = new LinkedList<FormType>();
	private LinkedList<InstanceType> instanceTypes     = new LinkedList<InstanceType>();
	
	private String currentRootFormId			       = null;
	private int currentIndex					       = 0;
	private String currentFormId             	       = null;
	private FormMetaDataSummary currentMetaDataElement = null;
		
	public FormMetaDataSummaryHandler( 	List<FormMetaDataSummary> metaDataElements, ObjectFactory objectFactory, JSONObject questionsAndAnswers, OwnerType ownerType )
	{
		setMetaDataElements   ( metaDataElements );
		
		setObjectFactory      ( objectFactory == null ? 
							    new ObjectFactory() : 
							    objectFactory );
		
		setQuestionsAndAnswers( questionsAndAnswers == null ? 
						        new JSONObject() : 
						        questionsAndAnswers );
		
		setOwnerType          ( ownerType == null ? 
								new OwnerType() : 
								ownerType );
	}
	
	
	/* Boolean methods */
	public boolean movedToNewForm()
	{
		return ! ( StringUtils.equals( currentFormId, formId ) );
	}
	
	public boolean movedToNewFormHierarchy()
	{
		return ! ( StringUtils.equals( currentRootFormId, rootFormId ) );
	}
	
	public boolean movedToLastElement()
	{
		return ( currentIndex == metaDataElements.size() - 1 );
	}
	
	private boolean anyNewFormTypesExist()
	{
		return  ! formTypes.isEmpty() && ! instanceTypes.isEmpty() ;
	}
	
	private boolean doesQuestionHaveMultipleTableRows( String shortName )
	{
		// For now, always returns false
		return false;
	}
	
	private boolean doesQuestionHaveMultipleAnswers( String shortName )
	{
		// For now, always return false
		return false;
	}
	
	
	/* Business Logic Methods */
	
	/**
	 * Used to complete the construction of the most recently generated FormType
	 * by adding completed instanceType elements to the FormType.
	 */
	private void finalizeLastFormType() 
	{
		if ( anyNewFormTypesExist() ) 
		{
			FormType lastFormType          = formTypes.getLast();
			InstanceType lastInstanceType  = instanceTypes.getLast();
			lastFormType.getInstance().add( lastInstanceType );
		}
	}
	
	
	/**
	 * Used to attach a completed FormType element to the main tree 
	 * by adding it to the OwnerType element.
	 */
	private void addFormTypesToOwner()
	{
		if ( anyNewFormTypesExist() )
		{
			for ( int index = formTypes.size() - 1; index >= 1 ; --index )
			{
				FormType     lastFormType            = formTypes.get( index );
				InstanceType beforeLastInstanceType  = instanceTypes.get( index - 1 );
				beforeLastInstanceType.getForm().add( lastFormType );
			}
			
			ownerType.getForm().add( formTypes.getFirst() );
			
			formTypes.clear();
			instanceTypes.clear();
		}
	}
	
	/**
	 * Generates a new formType element for the current formId
	 */
	private void constructNewFormType()
	{
		FormType formType         = objectFactory.createFormType();
		formType.setId( currentFormId );
		formTypes.add( formType );
	}
	
	/**
	 * Generates a new instanceType which will be added to a formType element
	 */
	private void constructNewInstanceType()
	{
		InstanceType instanceType = objectFactory.createInstanceType();
		instanceType.setIndex( getInstanceIndex( questionsAndAnswers ) );
		instanceTypes.add( instanceType );
	}
	
	/**
	 * Adds questions/answers to the latest instanceType as appropriate
	 */
	public void addQuestionAndAnswerDataToInstanceType()
	{
		InstanceType instanceType     = instanceTypes.getLast();
		String tableShortName         = currentMetaDataElement.getTableShortName();
		String[] shortNames			  = currentMetaDataElement.getQuestionShortNames();
		
		if ( currentMetaDataElement.hasTableQuestion() )
		{

			boolean multipleRows      = doesQuestionHaveMultipleTableRows( tableShortName );
			
			com.healthcit.cacure.metadata.gateway.QuestionType questionType = 
					constructTableQuestionType( tableShortName, shortNames, multipleRows );
					
			instanceType.getQuestion().add( questionType );
			 
		}
		
		else
		{
			for ( String shortName : shortNames )
			{
				boolean multipleAnswers   = doesQuestionHaveMultipleAnswers( shortName );
				
				com.healthcit.cacure.metadata.gateway.QuestionType questionType = 
						constructRegularQuestionType( shortName, multipleAnswers ) ;
				
				instanceType.getQuestion().add( questionType );
				
			}
			
		}
	}
	
	/**
	 * Constructs a JAXB Question Element for a Table Question.
	 */
	private com.healthcit.cacure.metadata.gateway.QuestionType constructTableQuestionType( 
			String tableShortName, 
			String[] shortNames, 
			boolean multipleRows )
	{
		com.healthcit.cacure.metadata.gateway.QuestionType questionType = objectFactory.createQuestionType();
		
		questionType.setName( tableShortName );
		
		if ( multipleRows )
		{		
			//TODO: Create a recordType for each row of the table
		}
		
		else
		{
			RecordType recordType = objectFactory.createRecordType();
			
			recordType.setOrder( getTableRow(questionsAndAnswers) );
			
			for ( int order = 1; order <= shortNames.length; ++order )
			{
				String shortName = shortNames[ order - 1 ];
				
				FieldType fieldType = objectFactory.createFieldType();
				
				fieldType.setOrder( order );
				
				fieldType.setValue( questionsAndAnswers.getString( shortName ) );
				
				recordType.getField().add( fieldType );
			}
		
			questionType.getRecord().add( recordType );
		}
		
		return questionType;
	}
	
	/**
	 * Constructs a JAXB QuestionType element for a regular question.
	 * @param shortName
	 * @param multipleAnswers
	 * @return
	 */
	private com.healthcit.cacure.metadata.gateway.QuestionType constructRegularQuestionType( 
			String shortName,
			boolean multipleAnswers )
	{
		com.healthcit.cacure.metadata.gateway.QuestionType questionType = objectFactory.createQuestionType();
		
		questionType.setName( shortName );
		
		AnswerType answerType = objectFactory.createAnswerType();		
		
		
		if ( multipleAnswers )
		{
			// TODO: Construct an answerType with value fields for each answer
		}
		
		else
		{		
			String answerValue = questionsAndAnswers.getString( shortName );
			
			answerType.getValue().add( answerValue );
		}
		
		questionType.setAnswer( answerType );
		
		return questionType;
	}
	
	/**
	 * Finalizes the generation of a FormType JAXB element based on the data provided thus far.
	 */
	public void finalizeFormTypes(){
		finalizeLastFormType(); // completes the construction of the most recently generated FormType
		constructNewFormType(); // generates a new formType for the current formId
		constructNewInstanceType(); // generates a new instanceType for the new formType
		resetFormIds(); //resets formId, rootFormId
	}
	
	/**
	 * Finalizes the generation of a FormTyoe JAXB element, 
	 * then attaches it to the tree by adding it to the OwnerType element.
	 */
	public void finalizeFormTypesAndAddToOwner(){
		finalizeLastFormType();
		addFormTypesToOwner(); 
		constructNewFormType(); 
		constructNewInstanceType();
		resetFormIds();
	}
	
	/*Getters and Setters */
	
	public void setObjectFactory(ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}
	public void setQuestionsAndAnswers(JSONObject questionsAndAnswers) {
		this.questionsAndAnswers = questionsAndAnswers;
	}
	public void setOwnerType(OwnerType ownerType) {
		this.ownerType = ownerType;
	}	
	public List<FormMetaDataSummary> getMetaDataElements() {
		return metaDataElements;
	}
	public void setMetaDataElements(List<FormMetaDataSummary> metaDataElements) {
		this.metaDataElements = metaDataElements;
	}
	public void incrementCurrentIndex() {
		++this.currentIndex;
	}
	public void resetFormIds(){
		this.formId = this.currentFormId;
		this.rootFormId = this.currentRootFormId;
	}
	public FormMetaDataSummary getCurrentMetaDataElement() {
		return currentMetaDataElement;
	}
	public void setCurrentMetaDataElement(FormMetaDataSummary currentMetaDataElement) {
		this.currentMetaDataElement = currentMetaDataElement;
	}
	public void setCurrentRootFormId(String currentRootFormId) {
		this.currentRootFormId = currentRootFormId;
	}
	public void setCurrentFormId(String currentFormId) {
		this.currentFormId = currentFormId;
	}


	@Override
	public Iterator<FormMetaDataSummary> iterator() {
		return new FormMetaDataSummaryIterator(this);
	}
	
	/* Helper Methods */
	public void iterateThroughMetaDataElements()
	{
		Iterator<FormMetaDataSummary> iterator = this.iterator();
		while ( iterator.hasNext() ) iterator.next();
	}
	
	private int getInstanceIndex( JSONObject questionsAndAnswers )
	{
		// For now, always return 1
		return 1;
	}
	
	private int getTableRow( JSONObject questionsAndAnswers )
	{
		// For now, always return 1
		return 1;
	}	
}
