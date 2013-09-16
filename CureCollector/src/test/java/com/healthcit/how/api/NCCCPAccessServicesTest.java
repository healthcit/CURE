/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.api;

import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.healthcit.cacure.security.EntityTagPermissions;
import com.healthcit.cacure.security.FormTag;
import com.healthcit.cacure.security.FormTagPermission;
import com.healthcit.cacure.security.FormTagPermissions;
import com.healthcit.cacure.security.ModuleTagPermissions;
import com.healthcit.cacure.security.ObjectFactory;
import com.healthcit.cacure.security.Permissions;
import com.healthcit.cacure.security.Security;
import com.healthcit.how.InvalidDataException;
import com.healthcit.how.businessdelegates.CoreEntityManager;
import com.healthcit.how.businessdelegates.EntityPermissionsManager;
import com.healthcit.how.businessdelegates.FormManager;
import com.healthcit.how.businessdelegates.ModuleManager;
import com.healthcit.how.businessdelegates.SharingGroupManager;
import com.healthcit.how.businessdelegates.TagManager;
import com.healthcit.how.dao.CoreEntityDao;
import com.healthcit.how.dao.FormDao;
import com.healthcit.how.dao.ModuleDao;
import com.healthcit.how.dao.SharingGroupDao;

import com.healthcit.how.models.CoreEntity;
import com.healthcit.how.models.EntityTagPermission;
import com.healthcit.how.models.EntityTagPermission.TagAccessPermissions;
import com.healthcit.how.models.SharingGroupFormInstance;
import com.healthcit.how.models.SharingGroupModule;
import com.healthcit.how.models.Module;
import com.healthcit.how.models.QuestionnaireForm;
import com.healthcit.how.models.SharingGroup;
import com.healthcit.how.models.Tag;
import com.healthcit.how.web.controller.FormAccessServiceController.DataFormat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"file:src/main/webapp/WEB-INF/spring/app-config.xml",
		"file:src/main/webapp/WEB-INF/spring/dao-config.xml"
//		"file:src/main/webapp/WEB-INF/spring/mailTemplates-config.xml" 
//		"file:src/main/webapp/WEB-INF/spring/mvc-config.xml"
		                           })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class NCCCPAccessServicesTest {
	@Autowired
	EntityManagerFactory emf;

   
	EntityManager em;
	
	@Autowired
	SharingGroupDao sharingGroupDao;
	
	@Autowired
	CoreEntityDao coreEntityDao;
	
	@Autowired
	CoreEntityManager coreEntityManager;
	
	@Autowired
	FormDao formDao;
	
	@Autowired
	ModuleDao moduleDao;
	
	@Autowired
	ModuleManager moduleManager;
	
	@Autowired
	TagManager tagManager;
	
//	protected Session session;
	@Autowired
	AccessServices accessServices;
	
	@Autowired
	SharingGroupManager sharingGroupManager;

	@Autowired
	FormManager formManager;
	
	@Autowired
	EntityPermissionsManager entityPermissionsManager;
	
	private String ctx[] = {"ncccp"};
	
//	@BeforeClass
//	public static  void createSharingGroupName()
//	{
//		sharingGroupName = new Date().toString();
//	}
	@Before
    public void setUp() {
		em = emf.createEntityManager();
		TransactionSynchronizationManager.bindResource(emf , new EntityManagerHolder(em));
    }

    @After
    public void tearDown() throws Exception {
    	TransactionSynchronizationManager.unbindResourceIfPossible(emf);
    }
    
	@Test
	public void testGetAllModules()throws Exception {
		String sharingGroupName = new Date().toString();
		String entityId = accessServices.registerNewEntityInNewGroup(sharingGroupName);
		String xml = accessServices.getAllModules(entityId, ctx);
		assertNotNull("XML is null", xml);
	}

	@Test
	public void testAvailableModules() throws Exception {
		String sharingGroupName = new Date().toString();
		String entityId = accessServices.registerNewEntityInNewGroup(sharingGroupName);
		String xml = accessServices.availableModules(entityId, ctx);
		assertNotNull("XML is null", xml);
	}
	
	@Test
	public void testTagAssignmentToForm() throws Exception
	{
		String sharingGroupName = new Date().toString();
		String moduleName = "module-" + new Date().toString();
		String formName = "form-" + new Date().toString();
		
		saveModule(moduleName, formName, sharingGroupName);
		cleanupModule(moduleName, formName, sharingGroupName);


	}
	
	@Test
	public void testGetTags() throws Exception
	{
		String sharingGroupName = new Date().toString();
		String moduleName = "module-" + new Date().toString();
		String formName = "form-" + new Date().toString();
		
		saveModule(moduleName, formName, sharingGroupName);

		tearDown();
		setUp();
		QuestionnaireForm form = formManager.getForm(formName);
		Tag tag = form.getTag();
		List<SharingGroup> sGroups = sharingGroupManager.getSharingGroupsByName(sharingGroupName);
		SharingGroup sGroup = sGroups.get(0);
		Set<CoreEntity> entities = sGroup.getEntities();
		CoreEntity[] coreEntities = entities.toArray(new CoreEntity[1]);
		CoreEntity entity = coreEntities[0];
		EnumSet<TagAccessPermissions> permissions = entity.getTagPermissionsForTag(tag);
		EnumSet<TagAccessPermissions> expectedPermissions = EnumSet.of(TagAccessPermissions.READ);
		assertEquals("Sets are not equal", expectedPermissions, permissions);
		cleanupModule(moduleName, formName, sharingGroupName);
	}
	
	@Test
	public void testAssignEntityPermissions() throws Exception
	{
		String sharingGroupName = new Date().toString();
		String moduleName = "module-" + new Date().toString();
		String formName = "form-" + new Date().toString();
		
		String entityId = saveModule(moduleName, formName, sharingGroupName);
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + 
		"<security xmlns=\"http://www.healthcit.com/CollectorSecurity\"><permissions><entityTagPermissions entityId=\"{entityId}\">" +
		"<formTagPermissions><tag id=\"{tagId}\"><formTagPermission>write</formTagPermission><formTagPermission>submit</formTagPermission></tag></formTagPermissions>" +
		"<moduleTagPermissions/></entityTagPermissions><entityTagPermissions entityId=\"dba09ea4-a7d7-4e43-9370-cc80012ea5ff\"><formTagPermissions>" + 
		"<tag id=\"form-Thu Nov 17 14:50:12 EST 2011\"><formTagPermission>read</formTagPermission></tag></formTagPermissions><moduleTagPermissions/></entityTagPermissions></permissions></security>";
		
		xml = xml.replaceFirst("\\{entityId\\}", entityId);
		xml = xml.replaceFirst("\\{tagId\\}", formName);
		
		entityPermissionsManager.saveEntityPermissions(entityId, xml);
//		ObjectFactory of = new ObjectFactory();
//		Security security = of.createSecurity();
		
//		Permissions permissionsJaxb = of.createPermissions();
//		security.setPermissions(permissionsJaxb);
//		List<EntityTagPermissions> entityTagPermissionsListJaxb = permissionsJaxb.getEntityTagPermissions();
//		EntityTagPermissions entityTagPermissions = of.createEntityTagPermissions();
//		entityTagPermissions.setEntityId(entity.getId());
//		FormTagPermissions formTagPermissionsJaxb = of.createFormTagPermissions();
//		List<FormTag> formTagsJaxb = formTagPermissionsJaxb.getTag();
//		//entityTagPermissions.setFormTagPermissions(value);
//		for(EntityTagPermissions entityTagPermissionsJaxb: entityTagPermissionsListJaxb)
//		{
//			String coreEntityId = entityTagPermissionsJaxb.getEntityId();
//			if(coreEntityId.equals(entity.getId()))
//			{
//				///CoreEntity entity = entityManager.getCoreEntity(coreEntityId);
//				
//				//FormTagPermissions formTagPermissionsJaxb = entityTagPermissionsJaxb.getFormTagPermissions();
//				List<EntityTagPermission> entityTagPermissions = new ArrayList<EntityTagPermission>();
//				List<FormTag> formTagsJaxb = formTagPermissionsJaxb.getTag();
//				for(FormTag formTag: formTagsJaxb)
//				{
//					String tagId = formTag.getId();
//					List<FormTagPermission> formTagPermissionJaxb = formTag.getFormTagPermission();
//					for(FormTagPermission permissionJaxb : formTagPermissionJaxb)
//					{
//						EntityTagPermission entityTagPermission = new EntityTagPermission(coreEntityId, tagId, TagAccessPermissions.valueOf(permissionJaxb.toString()));
//						entityTagPermissions.add(entityTagPermission);
//					}
//				}
//				entity.setFormTagAccessPermissions(entityTagPermissions);
//				ModuleTagPermissions moduleTagPermissionsJaxb = entityTagPermissionsJaxb.getModuleTagPermissions();
//				entity.setModuleTagAccessPermissions();
//				entityManager.updateCoreEntity(entity);
//			}
//		}
		
		
	}
	
	private void cleanupModule(String moduleName, String formName, String sharingGroupName) throws Exception
	{
		tearDown();
		setUp();
		SharingGroup sGroup = (sharingGroupManager.getSharingGroupsByName(sharingGroupName)).get(0);
		String sGroupId = sGroup.getId();
		Module module = moduleManager.getModule(moduleName);
		Set<CoreEntity> entities = sGroup.getEntities();

		for(QuestionnaireForm form: module.getForms())
		{
			Tag tag = form.getTag();
			tag.removeForm(form);
			tagManager.deleteTag(tag);
		}
		sharingGroupManager.deleteSharingGroup(sGroup);

//		for(CoreEntity entity: entities)
//		{
////			entity.setSharingGroup(null);
////			coreEntityManager.updateCoreEntity(entity);
////			sGroup.removeCoreEntity(entity);
//			entity.removeAllPermissions();
////			coreEntityManager.updateCoreEntity(entity);
////			coreEntityManager.deleteCoreEntity(entity);
//		}

		moduleManager.deleteModule(module);
//		CoreEntity entity = coreEntityManager.getCoreEntity("c9c38124-8039-4c6c-86a8-c93f274ca8ce");
//		entity.removeAllPermissions();
//		coreEntityManager.deleteCoreEntity(entity);
//		coreEntityManager.deleteCoreEntity("c9c38124-8039-4c6c-86a8-c93f274ca8ce");
//		tearDown();
//		setUp();
//		em.clear();
		
//		for(CoreEntity entity: entities)
//		{
////			CoreEntity entity1 = coreEntityManager.getCoreEntity(entity.getId());
////			coreEntityManager.deleteCoreEntity(entity1.getId());
//			coreEntityDao.deleteEntityAndPermissions(entity);
//		}
		coreEntityDao.deleteEntitiesForGroup(sGroupId);
		
	}
	private String saveModule(String moduleName, String formName, String sharingGroupName)
	{
		Module module = new Module();
		module.setId(moduleName);
		module.setContext("DEFAULT");
		module.setName(moduleName);
		
		QuestionnaireForm form = new QuestionnaireForm();
		form.setId(formName);
		form.setName(formName);
		form.setQuestionCount(new BigInteger("1"));
		form.setOrder(1l);
		form.setAuthor("me");
		form.setXformLocation("ss");
		module.addForm(form);
				
		

		moduleManager.addNewModule(module);
		Tag tag = new Tag();
		tag.setId(form.getName());
		tag.addForm(form);
		tagManager.addNewTag(tag);

//		em.flush();
//		em.clear();
//		tearDown();
//		setUp();

//		Module newModule = moduleManager.getModule(moduleName);
//		QuestionnaireForm newForm = module.getForms().get(0);
		
		SharingGroup sharingGroup = new SharingGroup();
		sharingGroup.setName(sharingGroupName);
		CoreEntity coreEntity = new CoreEntity();
		coreEntity.addFormAccessPermission(TagAccessPermissions.READ, tag);
		sharingGroup.addCoreEntity(coreEntity);

//		EntityTagPermission tagPermission = new EntityTagPermission(coreEntity.getId(), tag.getId(), TagAccessPermissions.READ);

		
		
		SharingGroupModule sharingGroupModule = new SharingGroupModule();
		sharingGroupModule.setModule(module);
		
		SharingGroupFormInstance sForm = new SharingGroupFormInstance();
		sForm.setForm(form);
		sharingGroup.addFormInstance(sForm);
		sharingGroup.addEntityModule(sharingGroupModule);
		
		sharingGroupManager.addNewSharingGroup(sharingGroup);
		return coreEntity.getId();
	}
	
	@Test 
	public void testModuleEquals()
	{
		Module module1 = new Module();
		Module module2 = new Module();
		String moduleName= new Date().toString();
		module1.setId(moduleName);
		module2.setId(moduleName);
		
		assertEquals("should be equal", module1, module2);
		assertEquals("should be equals", module1.hashCode(), module2.hashCode());
		
		
		SharingGroup sg1 = new SharingGroup();
		SharingGroupModule sgm1 = new SharingGroupModule();
		SharingGroupModule sgm2 = new SharingGroupModule();
		sgm1.setModule(module1);
		sgm1.setCoreEntity(sg1);
		
		sgm2.setModule(module1);
		sgm2.setCoreEntity(sg1);
		assertEquals("SGModules should be equals", sgm1.hashCode(), sgm2.hashCode());
		
		assertTrue(" should be true", sgm1.equals(sgm2));
		
		
		
//		module1.addEntityModule(sgm1);
//		sg1.addEntityModule(sgm1);
	}

	@Test
	public void testRegisterNewEntity()throws Exception 
	{
		String sharingGroupName = new Date().toString();
		String entityId = accessServices.registerNewEntityInNewGroup(sharingGroupName);
		
		
	}
	
	@Test
	public void testReopenModule() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetEntityModule() {
		fail("Not yet implemented"); // TODO
	}

//	@Test
//	public void testSubmitModule() {
//		fail("Not yet implemented"); // TODO
//	}

	@Test
	public void testGetStaleForms() throws Exception {
		String xml = accessServices.getStaleFormInstances();
		assertNotNull("XML is null", xml);
	}

	@Test
	public void testNextFormId() throws Exception{
		String formId = "5baa010b-05d0-4694-a253-657fb5d0fbb9";
		String nextFormIdExpected = "278bda46-420a-4614-a040-5a7a6e0dca5a";
		String sharingGroupName = new Date().toString();
		String entityId = accessServices.registerNewEntityInNewGroup(sharingGroupName);
		String nextFormId = accessServices.nextFormId(entityId, formId);
		assertEquals("FormId is not what was expected", nextFormIdExpected, nextFormId);
	}

	@Test
	public void testPreviousFormId() throws Exception{
		String formId = "278bda46-420a-4614-a040-5a7a6e0dca5a";
		String prevFormIdExpected = "5baa010b-05d0-4694-a253-657fb5d0fbb9";
		String sharingGroupName = new Date().toString();
		String entityId = accessServices.registerNewEntityInNewGroup(sharingGroupName);
		String prevFormId = accessServices.previousFormId(entityId, formId);
		assertEquals("FormId is not what was expected", prevFormIdExpected, prevFormId);
	}

	@Test
	public void testGetFormData() throws Exception{
		String formId = "0205083d-1cc2-45a2-bc56-8841ac36c53c";
		String sharingGroupName = new Date().toString();
		String entityId = accessServices.registerNewEntityInNewGroup(sharingGroupName);
		String xml = accessServices.getFormData(entityId, formId, DataFormat.XML.name());
		assertNotNull("XML is null", xml);
	}

	@Test
	public void testCreateNewSharingGroup() 
	{
		String sharingGroupName = new Date().toString();
		try
		{
			String groupId = accessServices.createNewSharingGroup(sharingGroupName);
			tearDown();
			setUp();
			SharingGroup sharingGroup = sharingGroupDao.getById(groupId);
			assertNotNull("SharingGroup is null", sharingGroup);
			//Check that all modules and Forms got assigned to the group
			List<SharingGroupFormInstance> eForms = sharingGroup.getFormInstances();
			List<SharingGroupModule> eModules = sharingGroup.getEntityModules();
			List<Module> modules = moduleDao.list();
			List<QuestionnaireForm> forms = formDao.list();
			assertEquals("Number of SharingGroupForms is not equal to the number of forms", forms.size(), eForms.size());
			assertEquals("Number of SharingGroupModules is not equal to the number of modules", modules.size(), eModules.size());
		}
		catch (Exception e)
		{
			fail("EXCEPTION: " + e.getMessage());
		}
	}

	@Test
	public void testRegisterNewEntityInNewGroup() {
		String sharingGroupName = new Date().toString();
		try
		{
			String entityId = accessServices.registerNewEntityInNewGroup(sharingGroupName);
			tearDown();
			setUp();
			CoreEntity coreEntity = coreEntityDao.getById(entityId);
			SharingGroup sharingGroup = sharingGroupDao.getById(coreEntity.getSharingGroup().getId());
			assertNotNull("SharingGroup is null", sharingGroup);
			assertNotNull("CoreEntity is null", coreEntity);
			//Check that all modules and Forms got assigned to the group
			List<SharingGroupFormInstance> eForms = sharingGroup.getFormInstances();
			List<SharingGroupModule> eModules = sharingGroup.getEntityModules();
			List<Module> modules = moduleDao.list();
			List<QuestionnaireForm> forms = formDao.list();
			assertEquals("Number of SharingGroupForms is not equal to the number of forms", forms.size(), eForms.size());
			assertEquals("Number of SharingGroupModules is not equal to the number of modules", modules.size(), eModules.size());
		}
		catch (Exception e)
		{
			fail("EXCEPTION: " + e.getMessage());
		}
	}

	@Test
	public void testRegisterNewEntityInGroup()
	{
		String sharingGroupName = new Date().toString();
		try
		{
			String groupId = accessServices.createNewSharingGroup(sharingGroupName);
		
			String entityId = accessServices.registerNewEntityInGroup(groupId);
			tearDown();
			setUp();
			CoreEntity coreEntity = coreEntityDao.getById(entityId);
			assertNotNull("CoreEntity is null", coreEntity);
			String entityGroupId = coreEntity.getSharingGroup().getId();
			assertEquals("Group id does not match the original group", entityGroupId, groupId);
		}
		catch (Exception e)
		{
			fail("EXCEPTION: " + e.getMessage());
		}
	}
	
	@Test
	@ExpectedException(value=InvalidDataException.class)
	public void testDuplicateSharingGroupName() throws Exception
	{
		String sharingGroupName = new Date().toString();
		String groupId = accessServices.createNewSharingGroup(sharingGroupName);
		tearDown();
		setUp();
		String groupId1 = accessServices.createNewSharingGroup(sharingGroupName);
	}

	
	@Test
	public void testDeleteEntity() throws Exception{
		String sharingGroupName = new Date().toString();
		String entityId = accessServices.registerNewEntityInNewGroup(sharingGroupName);
		tearDown();
		setUp();
		accessServices.deleteEntity(entityId);
		tearDown();
		setUp();
		CoreEntity coreEntity = coreEntityManager.getCoreEntity(entityId);
		assertNull("Core Entity should be null", coreEntity);
	}

	@Test
	public void testGetForm() throws Exception {
		String formId = "0205083d-1cc2-45a2-bc56-8841ac36c53c";
		String sharingGroupName = new Date().toString();
		String entityId = accessServices.registerNewEntityInNewGroup(sharingGroupName);
		
		accessServices.getForm(entityId, formId, new PrintWriter(System.out));
	}

	@Test
	public void testSaveForm() {
		fail("Not yet implemented"); // TODO
	}

}
