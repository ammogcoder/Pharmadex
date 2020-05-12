package org.msh.pharmadex.service;

import java.io.Serializable;

/**
 * It is DTO for dashboard's list of submitted applications
 * @author Alex Kurasoff
 *
 */
public class SubmittedAppDTO implements Serializable {
	private static final long serialVersionUID = 152668440295986900L;
	private Integer year;
	private Integer quarter;
	private Integer month;
	private String monthName;
	private String name;
	private Integer appId;
	private String regState;
	private Integer quantity; //of applications
	
	/**
	 * 
	 * @param year
	 * @param quarter
	 * @param month number - 1-12
	 * @param name applicant name
 	 * @param quantity of submitted applications
	 */
	public SubmittedAppDTO(Integer year, Integer quarter, Integer month, String name, Integer appId, Integer quantity) {
		super();
		this.year = year;
		this.quarter = quarter;
		this.month = month;
		this.name = name;
		this.appId=appId;
		this.quantity = quantity;
		
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
	public Integer getQuarter() {
		return quarter;
	}
	public void setQuarter(Integer quarter) {
		this.quarter = quarter;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}

	public String getMonthName() {
		return monthName;
	}

	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}

	public String getRegState() {
		return regState;
	}

	public void setRegState(String regState) {
		this.regState = regState;
	}
	
}
