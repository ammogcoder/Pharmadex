package org.msh.pharmadex.service;

import java.math.BigInteger;
import java.util.Date;

import org.msh.pharmadex.domain.enums.CTDModule;

/**
 * This DTO represent any record of users performance report
 * It is a mix of primary and detail data
 * @author Alex Kurasoff
 *
 */
public class UsersPerformanceDTO {
	//primary data
	private String userName;
	private BigInteger userId;
	private Integer primary;
	private Integer secondary;
	//detail data
	private BigInteger reviewId;
	private Date reviewDate;
	private String ctdModule;
	private String reviewStatus;
	private String applicant;
	private String product;
	private String moderator;
	private Integer days;
	
	
	/**
	 * Constructor for the first report
	 * @param userName
	 * @param userId
	 * @param primary
	 * @param secondary
	 */
	public UsersPerformanceDTO(String userName, BigInteger userId, Integer primary, Integer secondary) {
		super();
		this.userName = userName;
		this.userId = userId;
		this.primary = primary;
		this.secondary = secondary;
	}
	
	/**
	 * Constructor for the detail report
	 * @param reviewId
	 * @param reviewDate
	 * @param ctdModule
	 * @param reviewStatus
	 * @param applicant
	 * @param product
	 * @param moderator
	 */
	public UsersPerformanceDTO(BigInteger reviewId, Date reviewDate, String ctdModule, String reviewStatus, String applicant,
			String product, String moderator){
		this.reviewId= reviewId;
		this.reviewDate=reviewDate;
		this.ctdModule = ctdModule;
		this.reviewStatus=reviewStatus;
		this.applicant=applicant;
		this.product=product;
		this.moderator=moderator;
	}
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public BigInteger getUserId() {
		return userId;
	}
	public void setUserId(BigInteger userId) {
		this.userId = userId;
	}
	public Integer getPrimary() {
		return primary;
	}
	public void setPrimary(Integer primary) {
		this.primary = primary;
	}
	public Integer getSecondary() {
		return secondary;
	}
	public void setSecondary(Integer secondary) {
		this.secondary = secondary;
	}

	public BigInteger getReviewId() {
		return reviewId;
	}

	public void setReviewId(BigInteger reviewId) {
		this.reviewId = reviewId;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}

	public String getCtdModule() {
		return ctdModule;
	}

	public void setCtdModule(String ctdModule) {
		this.ctdModule = ctdModule;
	}

	public String getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
	}

	public String getApplicant() {
		return applicant;
	}

	public void setApplicant(String applicant) {
		this.applicant = applicant;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getModerator() {
		return moderator;
	}

	public void setModerator(String moderator) {
		this.moderator = moderator;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}
	
}
