package com.krishagni.catissueplus.core.biospecimen;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.krishagni.catissueplus.core.administrative.domain.PermissibleValue;
import com.krishagni.catissueplus.core.biospecimen.domain.CollectionProtocol;
import com.krishagni.catissueplus.core.biospecimen.domain.SpecimenTypeUnit;
import com.krishagni.catissueplus.core.biospecimen.events.SpecimenTypeUnitDetail;
import com.krishagni.catissueplus.core.biospecimen.repository.DaoFactory;
import com.krishagni.catissueplus.core.biospecimen.repository.SpecimenTypeUnitsListCriteria;
import com.krishagni.catissueplus.core.biospecimen.services.SpecimenTypeUnitsService;
import com.krishagni.catissueplus.core.common.events.RequestEvent;
import com.krishagni.catissueplus.core.common.events.ResponseEvent;

@Configurable
public class SpecimenUtil {
	private static SpecimenUtil instance = null;

	@Autowired
	private DaoFactory daoFactory;

	@Autowired
	private SpecimenTypeUnitsService unitSvc;

	public static SpecimenUtil getInstance() {
		if (instance == null || instance.daoFactory == null) {
			instance = new SpecimenUtil();
		}

		return instance;
	}

	public List<SpecimenTypeUnit> getUnits(String cpShortTitle) {
		return daoFactory.getSpecimenTypeUnitDao().getUnits(
			new SpecimenTypeUnitsListCriteria()
				.cpShortTitle(cpShortTitle)
				.asc(true)
		);
	}

	public SpecimenTypeUnit getUnit(CollectionProtocol cp, PermissibleValue specimenClass, PermissibleValue type) {
		if (cp == null || specimenClass == null || type == null) {
			return null;
		}

		List<SpecimenTypeUnit> units = daoFactory.getSpecimenTypeUnitDao().getMatchingUnits(
			cp.getId(),
			specimenClass.getId(),
			type.getId()
		);

		SpecimenTypeUnit result = new SpecimenTypeUnit();
		result.setCp(cp);
		result.setSpecimenClass(specimenClass);
		result.setType(type);

		boolean qtyInit = false, concInit = false;
		for (SpecimenTypeUnit unit : units) {
			if (!qtyInit && unit.getQuantityUnit() != null) {
				result.setQuantityUnit(unit.getQuantityUnit());
				qtyInit = true;
			}

			if (!concInit && unit.getConcentrationUnit() != null) {
				result.setConcentrationUnit(unit.getConcentrationUnit());
				concInit = true;
			}

			if (qtyInit && concInit) {
				break;
			}
		}

		return result;
	}

	public SpecimenTypeUnitDetail saveUnit(SpecimenTypeUnitDetail input) {
		return ResponseEvent.unwrap(unitSvc.createUnit(RequestEvent.wrap(input)));
	}
}
