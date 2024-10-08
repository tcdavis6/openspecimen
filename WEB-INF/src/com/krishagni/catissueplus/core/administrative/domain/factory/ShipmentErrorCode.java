package com.krishagni.catissueplus.core.administrative.domain.factory;

import com.krishagni.catissueplus.core.common.errors.ErrorCode;

public enum ShipmentErrorCode implements ErrorCode {
	REQ_CHG_NA,

	NOT_FOUND,
	
	NAME_REQUIRED,
	
	DUP_NAME,

	INVALID_TYPE,

	CANNOT_CHG_TYPE,
	
	SEND_SITE_REQUIRED,
	
	REC_SITE_REQUIRED,
	
	DUPLICATE_SPECIMENS,

	RECV_QUALITY_REQ,

	INV_RECEIVED_QUALITY,
	
	NO_SPECIMENS_TO_SHIP,

	NO_CONTAINERS_TO_SHIP,
	
	STATUS_REQUIRED,
	
	INVALID_STATUS,
	
	NOT_SHIPPED_TO_RECV,

	SHIP_DT_BEFORE_REQ_DT,

	RECV_DT_BEFORE_SHIP_DT,
	
	ALREADY_SHIPPED,
	
	SPECIMEN_ALREADY_SHIPPED,
	
	CLOSED_SPECIMENS,
	
	UNAVAILABLE_SPECIMENS,
	
	ALREADY_RECEIVED,
	
	INVALID_SPECIMENS,
	
	INVALID_SHIPPED_SPECIMENS,

	INVALID_SHIPPED_CONTAINERS,

	CANNOT_SHIP,

	CANNOT_RECEIVE,
	
	STATUS_CHANGE_NOT_ALLOWED,

	SPMN_NOT_STORED_AT_SEND_SITE,

	CANNOT_STORE_SPMN_AT_RECV_SITE,
	
	NOTIFY_USER_NOT_BELONG_TO_INST,

	RPT_TMPL_NOT_CONF,

	CONTS_NOT_AT_SEND_SITE,

	CANNOT_STORE_CONT_AT_RECV_SITE,

	CONTAINER_ALREADY_SHIPPED,

	CONT_NAMES_REQ,

	REQ_INV_CREATE_STATUS,

	INV_REQ_STATUS,

	NOT_REQUEST
	
	;
	
	@Override
	public String code() {
		return "SHIPMENT_" + this.name();
	}
}
