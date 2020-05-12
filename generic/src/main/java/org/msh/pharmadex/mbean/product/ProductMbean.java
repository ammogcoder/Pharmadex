package org.msh.pharmadex.mbean.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.ProductService;

/**
 * Author: usrivastava
 */
@ManagedBean
@RequestScoped
public class ProductMbean implements Serializable {
    private static final long serialVersionUID = -7982763544138941526L;

    @ManagedProperty(value = "#{productService}")
    private ProductService productService;
    @ManagedProperty(value = "#{userSession}")
	private UserSession userSession;
    @ManagedProperty(value = "#{applicantService}")
	private ApplicantService applicantService;
    
    private List<ProdTable> products;
    private List<ProdTable> filteredProducts;
    private List<ProdTable> suspendedProds;
    private List<ProdTable> revokedProds;

    public String sentToDetail(Long id) {
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        flash.put("prodAppID", id);
        return "/internal/processreg?faces-redirect=true";
    }

    public UserSession getUserSession() {
		return userSession;
	}

	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}

	public ApplicantService getApplicantService() {
		return applicantService;
	}

	public void setApplicantService(ApplicantService applicantService) {
		this.applicantService = applicantService;
	}

	public List<ProdTable> getProducts() {
        if (products == null)
            products = productService.findAllRegisteredProduct();
            Long currentUserId = userSession.getLoggedINUserID();
        	if(products!=null) {
        		for(ProdTable prod : products) {
        			prod.setAccesible(calcAccess(prod, currentUserId));
        		}
        	}
        return products;
    }
    /**
     * Calculate access to the full product data
     * @param prod product table data
     * @param currentUserId id of the current user
     * @return
     */
    public boolean calcAccess(ProdTable prod, Long currentUserId) {
    	//RULE 1. Restrict access to not authentified user
		if(userSession.isPublicDomain()) {
			return false;
		}
		//RULE 2. Allow access to any department user
		if(!userSession.isCompany()) {
			return true;
		}
		//RULE 3. Allow access only to Applicants responsible
		Applicant appl = applicantService.findApplicant(prod.getAppId());
		for (User user :appl.getUsers()) {
			if(user.getUserId()==currentUserId) {
				return true;
			}
		}
		//RULE 4. Restrict access to any other
		return false;
	}

	public void setProducts(List<ProdTable> products) {
        this.products = products;
    }

    public List<ProdTable> getFilteredProducts() {
        return filteredProducts;
    }

    public void setFilteredProducts(List<ProdTable> filteredProducts) {
        this.filteredProducts = filteredProducts;
    }

    public String goToDetails(Long id) {
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        flash.put("prodAppID", id);
        flash.put("backTo","/public/suspendedproducts.xhtml");
        return "/internal/processreg?faces-redirect=true";
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public List<ProdTable> getSuspendedProds() {
        if (suspendedProds == null) {
            suspendedProds = productService.findSuspendedProducts();
        }
        return suspendedProds;
    }

    public void setSuspendedProds(List<ProdTable> suspendedProds) {
        this.suspendedProds = suspendedProds;
    }

    public List<ProdTable> getRevokedProds() {
        if (revokedProds == null) {
            revokedProds = productService.findRevokedProds();
        }
        return revokedProds;
    }

    public void setRevokedProds(List<ProdTable> revokedProds) {
        this.revokedProds = revokedProds;
    }
    
    /**
     * Get all submitted products for civil society
     * @return
     */
    public List<ProdTable> getSubmittedProds(){
    	return getProductService().findProcessingProds();
    }
    
}
