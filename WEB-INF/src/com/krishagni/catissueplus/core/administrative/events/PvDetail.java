
package com.krishagni.catissueplus.core.administrative.events;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.krishagni.catissueplus.core.administrative.domain.PermissibleValue;
import com.krishagni.catissueplus.core.common.util.Utility;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PvDetail {
	private Long id;

	private String value;
	
	private Long parentId;
	
	private String parentValue;
	
	private String conceptCode;

	private Map<String, String> props;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getParentValue() {
		return parentValue;
	}

	public void setParentValue(String parentValue) {
		this.parentValue = parentValue;
	}

	public static PvDetail from(PermissibleValue pv) {
		return from(pv, false);
	}
	
	public String getConceptCode() {
		return conceptCode;
	}

	public void setConceptCode(String conceptCode) {
		this.conceptCode = conceptCode;
	}

	public Map<String, String> getProps() {
		return props;
	}

	public void setProps(Map<String, String> props) {
		this.props = props;
	}

	public static PvDetail from(PermissibleValue pv, boolean includeParent) {
		return from(pv, includeParent, false);
	}

	public static PvDetail from(PermissibleValue pv, boolean includeParent, boolean includeProps) {
		PvDetail result = new PvDetail();
		result.setId(pv.getId());
		result.setValue(pv.getValue());
		result.setConceptCode(pv.getConceptCode());

		if (includeParent && pv.getParent() != null) {
			result.setParentId(pv.getParent().getId());
			result.setParentValue(pv.getParent().getValue());
		}

		if (includeProps && pv.getProps() != null) {
			pv.getProps().size(); // lazy init
			result.setProps(pv.getProps());
		}

		return result;
	}
	
	public static List<PvDetail> from(Collection<PermissibleValue> pvs) {
		return from(pvs, false);
	}
	
	public static List<PvDetail> from(Collection<PermissibleValue> pvs, boolean includeParent) {
		return from(pvs, includeParent, false);
	}

	public static List<PvDetail> from(Collection<PermissibleValue> pvs, boolean includeParent, boolean includeProps) {
		return Utility.nullSafeStream(pvs).map(pv -> from(pv, includeParent, includeProps)).collect(Collectors.toList());
	}
}
