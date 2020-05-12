package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.enums.ProdCategory;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.ProdApplicationsService;

import java.io.Serializable;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 * Created by utkarsh on 4/6/15.
 */

public class ProdTable implements Serializable {
	private static final long serialVersionUID = -5984034223079547657L;

    private Long id;
    private String regNo;
    private String prodName;
    private String genName;
    private ProdCategory prodCategory;
    private String appName;
    private Long appId;
    private Date regDate;
    private Date regExpiryDate;
    private String manufName;
    private String prodDesc;
    private Long prodAppID;
    private String fnm;
    private RegState regState;
    private Integer daysToExpire;
    private Integer daysFromRegistration;

	private boolean accesible;
    
    
//    /**
//     * Does the current user has access to full product data
//     * AK 2019-08-14
//     * @return
//     */
//    public boolean getHasAccess() {
//    	//RULE 1. Anonymous users never have access to full product data
//    	if(userSession.isPublicDomain()) {
//    		return false;
//    	}
//    	if(userSession.isCompany()) {
//    		ProdApplications pa = prodApplicationsService.findProdApplications(getProdAppID());
//    		if(pa==null) {
//    			return false; //no prodapplications!
//    		}else {
//    			//RULE 3. Only responsible applicant user can
//    			return userSession.getLoggedINUserID() == pa.getApplicantUser().getUserId();
//    		}
//    	}else {
//    		return true; //RULE 2. Department users always have access to the full product data
//    	}
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getGenName() {
        return genName;
    }

    public void setGenName(String genName) {
        this.genName = genName;
    }

    public ProdCategory getProdCategory() {
        return prodCategory;
    }

    public void setProdCategory(ProdCategory prodCategory) {
        this.prodCategory = prodCategory;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getRegExpiryDate() {
        return regExpiryDate;
    }

    public void setRegExpiryDate(Date regExpiryDate) {
        this.regExpiryDate = regExpiryDate;
    }

    public String getManufName() {
        return manufName;
    }

    public void setManufName(String manufName) {
        this.manufName = manufName;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getProdDesc() {
        return prodDesc;
    }

    public void setProdDesc(String prodDesc) {
        this.prodDesc = prodDesc;
    }

    public Long getProdAppID() {
        return prodAppID;
    }

    public void setProdAppID(Long prodAppID) {
        this.prodAppID = prodAppID;
    }

	public String getFnm() {
		return fnm;
	}

	public void setFnm(String fnm) {
		this.fnm = fnm;
	}

	public RegState getRegState() {
		return regState;
	}

	public void setRegState(RegState regState) {
		this.regState = regState;
	}

	public Integer getDaysToExpire() {
		return daysToExpire;
	}

	public void setDaysToExpire(Integer daysToExpire) {
		this.daysToExpire = daysToExpire;
	}

	public Integer getDaysFromRegistration() {
		return daysFromRegistration;
	}

	public void setDaysFromRegistration(Integer daysFromRegistration) {
		this.daysFromRegistration = daysFromRegistration;
	}
	/**
	 * Can the current user access the full product data 
	 * @param access
	 */
	public void setAccesible(boolean access) {
		this.accesible=access;	
	}
	/**
	 * Can the current user access the full product data 
	 */
	public boolean isAccesible() {
		return accesible;
	}
	
	

   	
}
