package com.krishagni.catissueplus.core.biospecimen.domain.factory.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.krishagni.catissueplus.core.administrative.domain.PermissibleValue;
import com.krishagni.catissueplus.core.administrative.domain.Site;
import com.krishagni.catissueplus.core.administrative.domain.factory.SiteErrorCode;
import com.krishagni.catissueplus.core.biospecimen.domain.CollectionProtocol;
import com.krishagni.catissueplus.core.biospecimen.domain.Specimen;
import com.krishagni.catissueplus.core.biospecimen.domain.SpecimenLabelPrintRule;
import com.krishagni.catissueplus.core.biospecimen.domain.factory.CpErrorCode;
import com.krishagni.catissueplus.core.biospecimen.domain.factory.SpecimenErrorCode;
import com.krishagni.catissueplus.core.common.Pair;
import com.krishagni.catissueplus.core.common.domain.LabelPrintRule;
import com.krishagni.catissueplus.core.common.domain.factory.impl.AbstractLabelPrintRuleFactory;
import com.krishagni.catissueplus.core.common.errors.OpenSpecimenException;
import com.krishagni.catissueplus.core.common.service.PvValidator;

public class SpecimenLabelPrintRuleFactoryImpl extends AbstractLabelPrintRuleFactory {

	@Override
	public String getItemType() {
		return Specimen.getEntityName();
	}

	@Override
	public LabelPrintRule fromRuleDef(Map<String, Object> ruleDef, boolean failOnError, OpenSpecimenException ose) {
		SpecimenLabelPrintRule rule = new SpecimenLabelPrintRule();

		setCps(ruleDef, failOnError, rule, ose);
		setVisitSite(ruleDef, failOnError, rule, ose);
		setLineage(ruleDef, failOnError, rule, ose);
		setSpecimenClasses(ruleDef, failOnError, rule, ose);
		setSpecimenTypes(ruleDef, failOnError, rule, ose);
		return rule;
	}

	private void setCps(Map<String, Object> inputMap, boolean failOnError, SpecimenLabelPrintRule rule, OpenSpecimenException ose) {
		List<String> inputList = objToList(inputMap.get("cps"));
		if (inputList.isEmpty()) {
			inputList = objToList(inputMap.get("cpShortTitle")); // backward compatibility
			if (inputList.isEmpty()) {
				return;
			}
		}

		List<CollectionProtocol> result = new ArrayList<>();
		Pair<List<Long>, List<String>> idsAndTitles = getIdsAndNames(inputList);
		if (!idsAndTitles.first().isEmpty()) {
			List<CollectionProtocol> cps = getList(
				(ids) -> daoFactory.getCollectionProtocolDao().getByIds(ids),
				idsAndTitles.first(), (cp) -> cp.getId(),
				failOnError ? ose : null, failOnError ? CpErrorCode.DOES_NOT_EXIST : null);
			result.addAll(cps);
		}

		if (!idsAndTitles.second().isEmpty()) {
			List<CollectionProtocol> cps = getList(
				(titles) -> daoFactory.getCollectionProtocolDao().getCpsByShortTitle(titles),
				idsAndTitles.second(), (cp) -> cp.getShortTitle(),
				failOnError ? ose : null, failOnError ? CpErrorCode.DOES_NOT_EXIST : null);
			result.addAll(cps);
		}

		rule.setCps(result);
	}

	private void setVisitSite(Map<String, Object> input, boolean failOnError, SpecimenLabelPrintRule rule, OpenSpecimenException ose) {
		if (isEmptyString(input.get("visitSite"))) {
			return;
		}

		String visitSite = input.get("visitSite").toString();
		Site site = null;
		try {
			site = daoFactory.getSiteDao().getById(Long.parseLong(visitSite));
		} catch (NumberFormatException nfe) {
			site = daoFactory.getSiteDao().getSiteByName(visitSite);
		}

		if (failOnError && site == null) {
			ose.addError(SiteErrorCode.NOT_FOUND, visitSite);
		}

		rule.setVisitSite(site);
	}

	private void setLineage(Map<String, Object> input, boolean failOnError, SpecimenLabelPrintRule rule, OpenSpecimenException ose) {
		if (isEmptyString(input.get("lineage"))) {
			return;
		}

		String lineage = input.get("lineage").toString();
		if (!Specimen.isValidLineage(lineage)) {
			ose.addError(SpecimenErrorCode.INVALID_LINEAGE, lineage);
		}

		rule.setLineage(lineage);
	}

	private void setSpecimenClasses(Map<String, Object> inputMap, boolean failOnError, SpecimenLabelPrintRule rule, OpenSpecimenException ose) {
		List<String> inputList = objToList(inputMap.get("specimenClasses"));

		List<PermissibleValue> result = new ArrayList<>();
		Pair<List<Long>, List<String>> idsAndValues = getIdsAndNames(inputList);
		if (!idsAndValues.first().isEmpty()) {
			List<PermissibleValue> classPvs = getList(
				(ids) -> daoFactory.getPermissibleValueDao().getByIds(ids),
				idsAndValues.first(), (pv) -> pv.getId(),
				failOnError ? ose : null, failOnError ? SpecimenErrorCode.INVALID_SPECIMEN_CLASS : null);
			result.addAll(classPvs);
		}

		if (!idsAndValues.second().isEmpty()) {
			List<PermissibleValue> classPvs = getList(
				(values) -> daoFactory.getPermissibleValueDao().getPvs("specimen_type", idsAndValues.second()),
				idsAndValues.second(), (pv) -> pv.getValue(),
				failOnError ? ose : null, failOnError ? SpecimenErrorCode.INVALID_SPECIMEN_CLASS : null);
			result.addAll(classPvs);
		}

		rule.setSpecimenClasses(result);
	}

	private void setSpecimenTypes(Map<String, Object> input, boolean failOnError, SpecimenLabelPrintRule rule, OpenSpecimenException ose) {
		List<String> inputList = objToList(input.get("specimenTypes"));

		List<PermissibleValue> result = new ArrayList<>();
		Pair<List<Long>, List<String>> idsAndValues = getIdsAndNames(inputList);
		if (!idsAndValues.first().isEmpty()) {
			List<PermissibleValue> typePvs = getList(
				(ids) -> daoFactory.getPermissibleValueDao().getByIds(ids),
				idsAndValues.first(), (pv) -> pv.getId(),
				failOnError ? ose : null, failOnError ? SpecimenErrorCode.INVALID_SPECIMEN_TYPE : null);
			result.addAll(typePvs);
		}

		if (!idsAndValues.second().isEmpty()) {
			List<PermissibleValue> typePvs = getList(
				(values) -> daoFactory.getPermissibleValueDao().getPvs("specimen_type", idsAndValues.second()),
				idsAndValues.second(), (pv) -> pv.getValue(),
				failOnError ? ose : null, failOnError ? SpecimenErrorCode.INVALID_SPECIMEN_TYPE : null);
			result.addAll(typePvs);
		}

		rule.setSpecimenTypes(result);
	}
}
