
package com.krishagni.catissueplus.core.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.krishagni.catissueplus.core.administrative.domain.DistributionProtocol;
import com.krishagni.catissueplus.core.administrative.domain.factory.DistributionProtocolErrorCode;
import com.krishagni.catissueplus.core.administrative.domain.factory.DistributionProtocolFactory;
import com.krishagni.catissueplus.core.administrative.domain.factory.impl.DistributionProtocolFactoryImpl;
import com.krishagni.catissueplus.core.administrative.events.CreateDistributionProtocolEvent;
import com.krishagni.catissueplus.core.administrative.events.DistributionProtocolCreatedEvent;
import com.krishagni.catissueplus.core.administrative.events.DistributionProtocolDetails;
import com.krishagni.catissueplus.core.administrative.events.DistributionProtocolPatchedEvent;
import com.krishagni.catissueplus.core.administrative.events.DistributionProtocolUpdatedEvent;
import com.krishagni.catissueplus.core.administrative.events.PatchDistributionProtocolEvent;
import com.krishagni.catissueplus.core.administrative.events.UpdateDistributionProtocolEvent;
import com.krishagni.catissueplus.core.administrative.repository.DistributionProtocolDao;
import com.krishagni.catissueplus.core.administrative.repository.UserDao;
import com.krishagni.catissueplus.core.administrative.services.DistributionProtocolService;
import com.krishagni.catissueplus.core.administrative.services.impl.DistributionProtocolServiceImpl;
import com.krishagni.catissueplus.core.biospecimen.repository.DaoFactory;
import com.krishagni.catissueplus.core.common.events.EventStatus;
import com.krishagni.catissueplus.core.services.testdata.DistributionProtocolTestData;

public class DistributionProtocolTest {

	private static final String START_DATE = "start date";

	private static final String PRINCIPAL_INVESTIGATOR = "principle investigator";

	@Mock
	private DaoFactory daoFactory;

	@Mock
	private DistributionProtocolDao distributionProtocolDao;

	@Mock
	private UserDao userDao;

	private DistributionProtocolFactory distributionProtocolFactory;

	private DistributionProtocolService distributionProtocolSvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		when(daoFactory.getDistributionProtocolDao()).thenReturn(distributionProtocolDao);
		when(daoFactory.getUserDao()).thenReturn(userDao);
		when(daoFactory.getUserDao().getUserByLoginNameAndDomainName(anyString(), anyString())).thenReturn(
				DistributionProtocolTestData.getUserDetails());
		when(distributionProtocolDao.isUniqueShortTitle(anyString())).thenReturn(Boolean.TRUE);
		when(distributionProtocolDao.isUniqueTitle(anyString())).thenReturn(Boolean.TRUE);

		DistributionProtocol distributionProtocol = new DistributionProtocol();
		when(distributionProtocolDao.getDistributionProtocol(anyLong())).thenReturn(distributionProtocol);
		distributionProtocolSvc = new DistributionProtocolServiceImpl();
		distributionProtocolFactory = new DistributionProtocolFactoryImpl();
		((DistributionProtocolServiceImpl) distributionProtocolSvc).setDaoFactory(daoFactory);
		((DistributionProtocolServiceImpl) distributionProtocolSvc).setDistributionProtocolFactory(distributionProtocolFactory);
		((DistributionProtocolFactoryImpl) distributionProtocolFactory).setDaoFactory(daoFactory);

	}

	@Test
	public void testForSuccessfulDistributionProtocolCreation() {
		CreateDistributionProtocolEvent event = DistributionProtocolTestData.getCreateDistributionProtocolEvent();
		DistributionProtocolCreatedEvent response = distributionProtocolSvc.createDistributionProtocol(event);

		assertNotNull("Response cannot be null", response);
		assertEquals(EventStatus.OK, response.getStatus());
		DistributionProtocolDetails details = response.getDetails();
		assertEquals(event.getDistributionProtocolDetails().getTitle(), details.getTitle());
	}

	@Test
	public void testForDistributionProtocolCreationWithInvalidInvestigator() {
		when(daoFactory.getUserDao().getUserByLoginNameAndDomainName(anyString(), anyString())).thenReturn(null);
		CreateDistributionProtocolEvent event = DistributionProtocolTestData.getCreateDistributionProtocolEvent();

		DistributionProtocolCreatedEvent response = distributionProtocolSvc.createDistributionProtocol(event);

		assertNotNull("Response cannot be null", response);
		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(DistributionProtocolErrorCode.INVALID_PRINCIPAL_INVESTIGATOR.message(),
				response.getErroneousFields()[0].getErrorMessage());
	}

//	@Test
//	public void testDistributionProtocolCreationWithInvalidActivityStatus() {
//
//		CreateDistributionProtocolEvent reqEvent = DistributionProtocolTestData
//				.getCreateDistributionProtocolEventWithInvalidActivityStatus();
//		DistributionProtocolCreatedEvent response = distributionProtocolSvc.createDistributionProtocol(reqEvent);
//
//		assertNotNull("response cannot be null", response);
//		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
//		assertEquals(DistributionProtocolErrorCode.INVALID_ATTR_VALUE.message(),
//				response.getErroneousFields()[0].getErrorMessage());
//
//	}

	@Test
	public void testCreationforDistributionProtocolWithEmptyInvestigatorName() {
		CreateDistributionProtocolEvent event = DistributionProtocolTestData
				.getCreateDistributionProtocolEventWithEmptyInvestigatorName();
		DistributionProtocolCreatedEvent response = distributionProtocolSvc.createDistributionProtocol(event);

		assertNotNull("Response cannot be null", response);
		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(DistributionProtocolErrorCode.MISSING_ATTR_VALUE.message(),
				response.getErroneousFields()[0].getErrorMessage());

	}

	@Test
	public void testCreationforDistributionProtocolWithDuplicateTitle() {
		when(distributionProtocolDao.isUniqueTitle(anyString())).thenReturn(Boolean.FALSE);

		CreateDistributionProtocolEvent event = DistributionProtocolTestData.getCreateDistributionProtocolEvent();
		DistributionProtocolCreatedEvent response = distributionProtocolSvc.createDistributionProtocol(event);

		assertNotNull("Response cannot be null", response);
		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(DistributionProtocolErrorCode.DUPLICATE_PROTOCOL_TITLE.message(),
				response.getErroneousFields()[0].getErrorMessage());

	}

	@Test
	public void testCreationforDistributionProtocolWithDuplicateShortTitle() {
		when(distributionProtocolDao.isUniqueShortTitle(anyString())).thenReturn(Boolean.FALSE);

		CreateDistributionProtocolEvent event = DistributionProtocolTestData.getCreateDistributionProtocolEvent();
		DistributionProtocolCreatedEvent response = distributionProtocolSvc.createDistributionProtocol(event);

		assertNotNull("Response cannot be null", response);
		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(DistributionProtocolErrorCode.DUPLICATE_PROTOCOL_SHORT_TITLE.message(),
				response.getErroneousFields()[0].getErrorMessage());

	}

	@Test
	public void testCreationforDistributionProtocolWithEmptyTitle() {
		CreateDistributionProtocolEvent event = DistributionProtocolTestData.getCreateDistributionProtocolEventEmptyTitle();
		DistributionProtocolCreatedEvent response = distributionProtocolSvc.createDistributionProtocol(event);

		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(DistributionProtocolErrorCode.MISSING_ATTR_VALUE.message(),
				response.getErroneousFields()[0].getErrorMessage());
	}

	@Test
	public void testCreationforDistributionProtocolWithEmptyShortTitle() {

		CreateDistributionProtocolEvent event = DistributionProtocolTestData
				.getCreateDistributionProtocolEventEmptyShortTitle();
		DistributionProtocolCreatedEvent response = distributionProtocolSvc.createDistributionProtocol(event);

		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(DistributionProtocolErrorCode.MISSING_ATTR_VALUE.message(),
				response.getErroneousFields()[0].getErrorMessage());
	}

	@Test
	public void testDistributionProtocolCreationNullDate() {

		CreateDistributionProtocolEvent reqEvent = DistributionProtocolTestData
				.getCreateDistributionProtocolEventWithNullStartDate();

		DistributionProtocolCreatedEvent response = distributionProtocolSvc.createDistributionProtocol(reqEvent);
		assertNotNull("response cannot be null", response);
		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(START_DATE, response.getErroneousFields()[0].getFieldName());
		assertEquals(DistributionProtocolErrorCode.INVALID_ATTR_VALUE.message(), response.getErroneousFields()[0].getErrorMessage());
	}

	@Test
	public void testDistributionProtocolCreationWithServerErr() {

		doThrow(new RuntimeException()).when(distributionProtocolDao).saveOrUpdate(any(DistributionProtocol.class));

		CreateDistributionProtocolEvent reqEvent = DistributionProtocolTestData.getCreateDistributionProtocolEvent();
		DistributionProtocolCreatedEvent response = distributionProtocolSvc.createDistributionProtocol(reqEvent);

		assertNotNull("response cannot be null", response);
		assertEquals(EventStatus.INTERNAL_SERVER_ERROR, response.getStatus());
	}

	@Test
	public void testCreationforDistributionProtocolWithNegativeAnticipatedSpecimenCountNumber() {
		CreateDistributionProtocolEvent event = DistributionProtocolTestData
				.getCreateDistributionProtocolEventWithNegativeAnticipatedSpecimenCountNumber();
		DistributionProtocolCreatedEvent response = distributionProtocolSvc.createDistributionProtocol(event);

		assertNotNull("Response cannot be null", response);
		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(DistributionProtocolErrorCode.INVALID_ATTR_VALUE.message(),
				response.getErroneousFields()[0].getErrorMessage());

	}
	
	@Test
	public void testForSuccessfulDistributionProtocolUpdate() {
		when(distributionProtocolDao.getDistributionProtocol(anyLong())).thenReturn(
				DistributionProtocolTestData.getDistributionProtocolToReturn());

		UpdateDistributionProtocolEvent reqEvent = DistributionProtocolTestData.getUpdateDistributionProtocolEvent();
		DistributionProtocolUpdatedEvent response = distributionProtocolSvc.updateDistributionProtocol(reqEvent);

		assertEquals(EventStatus.OK, response.getStatus());
		DistributionProtocolDetails createdDistributionProtocol = response.getDetails();
		assertEquals(reqEvent.getDetails().getTitle(), createdDistributionProtocol.getTitle());

	}

	@Test
	public void testForDistributionProtocolUpdationWithInvalidInvestigator() {
		when(daoFactory.getUserDao().getUserByLoginNameAndDomainName(anyString(), anyString())).thenReturn(null);
		when(distributionProtocolDao.getDistributionProtocol(anyLong())).thenReturn(
				DistributionProtocolTestData.getDistributionProtocolToReturn());

		UpdateDistributionProtocolEvent event = DistributionProtocolTestData
				.getUpdateDistributionProtocolEventWithInvalidInvestigator();
		DistributionProtocolUpdatedEvent response = distributionProtocolSvc.updateDistributionProtocol(event);

		assertNotNull("Response cannot be null", response);
		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(DistributionProtocolErrorCode.INVALID_PRINCIPAL_INVESTIGATOR.message(),
				response.getErroneousFields()[0].getErrorMessage());
	}

//	@Test
//	public void testUpdationWithInvalidActivityStatus() {
//
//		UpdateDistributionProtocolEvent reqEvent = DistributionProtocolTestData
//				.getUpdateDistributionProtocolEventWithInvalidActivityStatus();
//		DistributionProtocolUpdatedEvent response = distributionProtocolSvc.updateDistributionProtocol(reqEvent);
//
//		assertNotNull("response cannot be null", response);
//		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
//		assertEquals(DistributionProtocolErrorCode.INVALID_ATTR_VALUE.message(),
//				response.getErroneousFields()[0].getErrorMessage());
//
//	}

	@Test
	public void testUpdationforDistributionProtocolWithEmptyInvestigatorName() {
		UpdateDistributionProtocolEvent event = DistributionProtocolTestData
				.getUpdateDistributionProtocolEventWithEmptyInvestigatorName();
		DistributionProtocolUpdatedEvent response = distributionProtocolSvc.updateDistributionProtocol(event);

		assertNotNull("Response cannot be null", response);
		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(DistributionProtocolErrorCode.MISSING_ATTR_VALUE.message(),
				response.getErroneousFields()[0].getErrorMessage());

	}

	@Test
	public void testUpdationforDistributionProtocolWithDuplicateTitle() {
		when(distributionProtocolDao.getDistributionProtocol(anyLong())).thenReturn(
				DistributionProtocolTestData.getDistributionProtocolToReturn());
		when(distributionProtocolDao.isUniqueTitle(anyString())).thenReturn(Boolean.FALSE);

		UpdateDistributionProtocolEvent event = DistributionProtocolTestData.getUpdateDistributionProtocolEvent();

		DistributionProtocolUpdatedEvent response = distributionProtocolSvc.updateDistributionProtocol(event);

		assertNotNull("Response cannot be null", response);
		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(DistributionProtocolErrorCode.DUPLICATE_PROTOCOL_TITLE.message(),
				response.getErroneousFields()[0].getErrorMessage());

	}

	@Test
	public void testUpdationforDistributionProtocolWithDuplicateShortTitle() {
		when(distributionProtocolDao.getDistributionProtocol(anyLong())).thenReturn(
				DistributionProtocolTestData.getDistributionProtocolToReturn());
		when(distributionProtocolDao.isUniqueShortTitle(anyString())).thenReturn(Boolean.FALSE);

		UpdateDistributionProtocolEvent event = DistributionProtocolTestData.getUpdateDistributionProtocolEvent();

		DistributionProtocolUpdatedEvent response = distributionProtocolSvc.updateDistributionProtocol(event);

		assertNotNull("Response cannot be null", response);
		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(DistributionProtocolErrorCode.DUPLICATE_PROTOCOL_SHORT_TITLE.message(),
				response.getErroneousFields()[0].getErrorMessage());

	}

	@Test
	public void testUpdationforDistributionProtocolWithEmptyTitle() {
		UpdateDistributionProtocolEvent event = DistributionProtocolTestData.getUpdateDistributionProtocolEventEmptyTitle();
		DistributionProtocolUpdatedEvent response = distributionProtocolSvc.updateDistributionProtocol(event);

		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(DistributionProtocolErrorCode.MISSING_ATTR_VALUE.message(),
				response.getErroneousFields()[0].getErrorMessage());
	}

	@Test
	public void testUpdationforDistributionProtocolWithEmptyShortTitle() {

		UpdateDistributionProtocolEvent event = DistributionProtocolTestData
				.getUpdateDistributionProtocolEventEmptyShortTitle();
		DistributionProtocolUpdatedEvent response = distributionProtocolSvc.updateDistributionProtocol(event);

		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(DistributionProtocolErrorCode.MISSING_ATTR_VALUE.message(),
				response.getErroneousFields()[0].getErrorMessage());
	}

	@Test
	public void testDistributionProtocolUpdationNullDate() {

		UpdateDistributionProtocolEvent event = DistributionProtocolTestData.getUpdateDistributionProtocolEventNullDate();
		DistributionProtocolUpdatedEvent response = distributionProtocolSvc.updateDistributionProtocol(event);
		assertNotNull("response cannot be null", response);
		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(START_DATE, response.getErroneousFields()[0].getFieldName());
		assertEquals(DistributionProtocolErrorCode.INVALID_ATTR_VALUE.message(), response.getErroneousFields()[0].getErrorMessage());
	}

	@Test
	public void testDistributionProtocolUpdateNullOldDistributionProtocol() {
		when(daoFactory.getDistributionProtocolDao().getDistributionProtocol(anyLong())).thenReturn(null);
		UpdateDistributionProtocolEvent event = DistributionProtocolTestData.getUpdateDistributionProtocolEvent();
		DistributionProtocolUpdatedEvent response = distributionProtocolSvc.updateDistributionProtocol(event);
		assertNotNull("response cannot be null", response);
		assertEquals(EventStatus.NOT_FOUND, response.getStatus());
	}

	@Test
	public void testDistributionProtocolUpdationWithServerErr() {

		when(distributionProtocolDao.getDistributionProtocol(anyLong())).thenReturn(
				DistributionProtocolTestData.getDistributionProtocolToReturn());
		doThrow(new RuntimeException()).when(distributionProtocolDao).saveOrUpdate(any(DistributionProtocol.class));

		UpdateDistributionProtocolEvent event = DistributionProtocolTestData.getUpdateDistributionProtocolEvent();
		DistributionProtocolUpdatedEvent response = distributionProtocolSvc.updateDistributionProtocol(event);

		assertNotNull("response cannot be null", response);
		assertEquals(EventStatus.INTERNAL_SERVER_ERROR, response.getStatus());
	}

	@Test
	public void testUpdationforDistributionProtocolWithNegativeAnticipatedSpecimenCountNumber() {
		UpdateDistributionProtocolEvent event = DistributionProtocolTestData
				.getUpdateDistributionProtocolEventWithNegativeAnticipatedSpecimenCountNumber();
		DistributionProtocolUpdatedEvent response = distributionProtocolSvc.updateDistributionProtocol(event);

		assertNotNull("Response cannot be null", response);
		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(DistributionProtocolErrorCode.INVALID_ATTR_VALUE.message(),
				response.getErroneousFields()[0].getErrorMessage());

	}
	@Test
	public void testForSuccessfulDistributionProtocolPatch() {
		when(distributionProtocolDao.getDistributionProtocol(anyLong())).thenReturn(
				DistributionProtocolTestData.getDistributionProtocolToReturn());

		PatchDistributionProtocolEvent reqEvent = DistributionProtocolTestData.getPatchDistributionProtocolEvent();
		DistributionProtocolPatchedEvent response = distributionProtocolSvc.patchDistributionProtocol(reqEvent);

		assertEquals(EventStatus.OK, response.getStatus());
		DistributionProtocolDetails createdDistributionProtocol = response.getDetails();
		assertEquals(reqEvent.getDetails().getTitle(), createdDistributionProtocol.getTitle());

	}

	@Test
	public void testForDistributionProtocolPatchWithInvalidInvestigator() {
		when(daoFactory.getUserDao().getUserByLoginNameAndDomainName(anyString(), anyString())).thenReturn(null);
		when(distributionProtocolDao.getDistributionProtocol(anyLong())).thenReturn(
				DistributionProtocolTestData.getDistributionProtocolToReturn());

		PatchDistributionProtocolEvent reqEvent = DistributionProtocolTestData.getPatchDistributionProtocolEvent();
		DistributionProtocolPatchedEvent response = distributionProtocolSvc.patchDistributionProtocol(reqEvent);

		assertNotNull("Response cannot be null", response);
		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(DistributionProtocolErrorCode.INVALID_PRINCIPAL_INVESTIGATOR.message(),
				response.getErroneousFields()[0].getErrorMessage());
	}
	
//	@Test
//	public void testPatchWithInvalidActivityStatus() {
//
//		PatchDistributionProtocolEvent reqEvent = DistributionProtocolTestData
//				.getPatchDistributionProtocolEventWithInvalidActivityStatus();
//		DistributionProtocolPatchedEvent response = distributionProtocolSvc.patchDistributionProtocol(reqEvent);
//
//		assertNotNull("response cannot be null", response);
//		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
//		assertEquals(DistributionProtocolErrorCode.INVALID_ATTR_VALUE.message(),
//				response.getErroneousFields()[0].getErrorMessage());
//
//	}

	@Test
	public void testPatchforDistributionProtocolWithEmptyInvestigatorName() {
		when(daoFactory.getUserDao().getUserByLoginNameAndDomainName(anyString(), anyString())).thenReturn(null);
		PatchDistributionProtocolEvent event = DistributionProtocolTestData
				.getPatchDistributionProtocolEventWithEmptyInvestigatorName();
		DistributionProtocolPatchedEvent response = distributionProtocolSvc.patchDistributionProtocol(event);
		assertNotNull("Response cannot be null", response);
		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(DistributionProtocolErrorCode.MISSING_ATTR_VALUE.message(),
				response.getErroneousFields()[0].getErrorMessage());

	}

	@Test
	public void testPatchforDistributionProtocolWithDuplicateTitle() {
		when(distributionProtocolDao.getDistributionProtocol(anyLong())).thenReturn(
				DistributionProtocolTestData.getDistributionProtocolToReturn());
		when(distributionProtocolDao.isUniqueTitle(anyString())).thenReturn(Boolean.FALSE);

		PatchDistributionProtocolEvent event = DistributionProtocolTestData.getPatchDistributionProtocolEvent();
		DistributionProtocolPatchedEvent response = distributionProtocolSvc.patchDistributionProtocol(event);

		assertNotNull("Response cannot be null", response);
		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(DistributionProtocolErrorCode.DUPLICATE_PROTOCOL_TITLE.message(),
				response.getErroneousFields()[0].getErrorMessage());

	}

	@Test
	public void testPatchforDistributionProtocolWithDuplicateShortTitle() {
		when(daoFactory.getDistributionProtocolDao().getDistributionProtocol(anyLong())).thenReturn(
				DistributionProtocolTestData.getDistributionProtocolToReturn());
		when(distributionProtocolDao.isUniqueShortTitle(anyString())).thenReturn(Boolean.FALSE);

		PatchDistributionProtocolEvent event = DistributionProtocolTestData.getPatchDistributionProtocolEventWithDiffTitles();
		DistributionProtocolPatchedEvent response = distributionProtocolSvc.patchDistributionProtocol(event);

		assertNotNull("Response cannot be null", response);
		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(DistributionProtocolErrorCode.DUPLICATE_PROTOCOL_SHORT_TITLE.message(),
				response.getErroneousFields()[0].getErrorMessage());

	}

	@Test
	public void testPatchforDistributionProtocolWithEmptyTitle() {
		PatchDistributionProtocolEvent event = DistributionProtocolTestData
				.getPatchDistributionProtocolEventWithEmptyTitle();
		DistributionProtocolPatchedEvent response = distributionProtocolSvc.patchDistributionProtocol(event);
		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(DistributionProtocolErrorCode.MISSING_ATTR_VALUE.message(),
				response.getErroneousFields()[0].getErrorMessage());
	}

	@Test
	public void testPatchforDistributionProtocolWithEmptyShortTitle() {

		PatchDistributionProtocolEvent event = DistributionProtocolTestData
				.getPatchDistributionProtocolEventWithEmptyShortTitle();
		DistributionProtocolPatchedEvent response = distributionProtocolSvc.patchDistributionProtocol(event);
		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(DistributionProtocolErrorCode.MISSING_ATTR_VALUE.message(),
				response.getErroneousFields()[0].getErrorMessage());
	}

	@Test
	public void testDistributionProtocolPatchNullDate() {
		PatchDistributionProtocolEvent event = DistributionProtocolTestData.getPatchDistributionProtocolEventNullDate();
		DistributionProtocolPatchedEvent response = distributionProtocolSvc.patchDistributionProtocol(event);
		assertNotNull("response cannot be null", response);
		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(START_DATE, response.getErroneousFields()[0].getFieldName());
		assertEquals(DistributionProtocolErrorCode.INVALID_ATTR_VALUE.message(), response.getErroneousFields()[0].getErrorMessage());
	}

	@Test
	public void testDistributionProtocolPatchWithServerErr() {

		when(distributionProtocolDao.getDistributionProtocol(anyLong())).thenReturn(
				DistributionProtocolTestData.getDistributionProtocolToReturn());
		doThrow(new RuntimeException()).when(distributionProtocolDao).saveOrUpdate(any(DistributionProtocol.class));

		PatchDistributionProtocolEvent event = DistributionProtocolTestData.getPatchData();
		DistributionProtocolPatchedEvent response = distributionProtocolSvc.patchDistributionProtocol(event);

		assertNotNull("response cannot be null", response);
		assertEquals(EventStatus.INTERNAL_SERVER_ERROR, response.getStatus());
	}

	@Test
	public void testDistributionProtocolPatchNullOldDistributionProtocol() {
		when(daoFactory.getDistributionProtocolDao().getDistributionProtocol(anyLong())).thenReturn(null);
		PatchDistributionProtocolEvent event = DistributionProtocolTestData.getPatchDistributionProtocolEvent();
		DistributionProtocolPatchedEvent response = distributionProtocolSvc.patchDistributionProtocol(event);
		assertNotNull("response cannot be null", response);
		assertEquals(EventStatus.NOT_FOUND, response.getStatus());
	}

	@Test
	public void testPatchforDistributionProtocolWithNegativeAnticipatedSpecimenCountNumber() {
		PatchDistributionProtocolEvent event = DistributionProtocolTestData
				.getPatchDistributionProtocolEventWithNegativeAnticipatedSpecimenCountNumber();
		DistributionProtocolPatchedEvent response = distributionProtocolSvc.patchDistributionProtocol(event);

		assertNotNull("Response cannot be null", response);
		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(1, response.getErroneousFields().length);
		assertEquals(DistributionProtocolErrorCode.INVALID_ATTR_VALUE.message(),
				response.getErroneousFields()[0].getErrorMessage());
	}
	
	@Test
	public void testDistributionProtocolUpdationNullDateAndInvestigator() {

		when(daoFactory.getUserDao().getUserByLoginNameAndDomainName(anyString(), anyString())).thenReturn(null);
		UpdateDistributionProtocolEvent event = DistributionProtocolTestData.getUpdateDistributionProtocolEventNullDate();
		DistributionProtocolUpdatedEvent response = distributionProtocolSvc.updateDistributionProtocol(event);
		assertNotNull("response cannot be null", response);
		assertEquals(EventStatus.BAD_REQUEST, response.getStatus());
		assertEquals(2, response.getErroneousFields().length);
		assertEquals(PRINCIPAL_INVESTIGATOR, response.getErroneousFields()[0].getFieldName());
		assertEquals(START_DATE, response.getErroneousFields()[1].getFieldName());
		assertEquals(DistributionProtocolErrorCode.INVALID_PRINCIPAL_INVESTIGATOR.message(),
				response.getErroneousFields()[0].getErrorMessage());
		assertEquals(DistributionProtocolErrorCode.INVALID_ATTR_VALUE.message(),
				response.getErroneousFields()[1].getErrorMessage());
	}

}
