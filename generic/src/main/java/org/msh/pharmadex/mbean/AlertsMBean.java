package org.msh.pharmadex.mbean;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.mbean.product.ProdTable;
import org.msh.pharmadex.mbean.product.ProductMbean;
import org.msh.pharmadex.mbean.product.ReviewInfoTable;
import org.msh.pharmadex.service.ProductService;
import org.msh.pharmadex.service.ReviewService;

/**
 * Common MBean for all alerts
 * @author Alex Kurasoff
 *
 */
@ManagedBean
@RequestScoped
public class AlertsMBean {
	@ManagedProperty(value = "#{productService}")
	private ProductService productService;
	@ManagedProperty(value="#{userSession}")
	UserSession userSession;
	@ManagedProperty(value="#{reviewService}")
	ReviewService reviewService;
	@ManagedProperty(value="#{productMbean}")
	ProductMbean productMbean;
	
	
	public ProductMbean getProductMbean() {
		return productMbean;
	}



	public void setProductMbean(ProductMbean productMbean) {
		this.productMbean = productMbean;
	}



	public ProductService getProductService() {
		return productService;
	}



	public void setProductService(ProductService productService) {
		this.productService = productService;
	}



	public UserSession getUserSession() {
		return userSession;
	}



	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}



	public ReviewService getReviewService() {
		return reviewService;
	}



	public void setReviewService(ReviewService reviewService) {
		this.reviewService = reviewService;
	}



	/**
	 * List of all submitted products for screener
	 * @return
	 */
	public List<ProdTable> getAllSubmitted(){
		return getProductService().findSubmittedProds(getUserSession().isCompany(),getUserSession().getLoggedINUserID());	
	}
	
	/**
	 * List of review with FIR submitted for reviewers and moderator
	 * @return
	 */
	public List<ReviewInfoTable> getReviewFIRTables(){
		return getReviewService().findRevFIRByUser(getUserSession().getLoggedINUserID());
	}
	/**
	 * Get applications ready for executive summary. Only for Moderator
	 * @return
	 */
	public List<ProdTable> getReadyForExecSummary(){
		return getProductService().findReadyForModerator(getUserSession().getLoggedINUserID());
	}
	
	/**
	 * Get applications ready to assign reviewers. Only for Moderator
	 * @return
	 */
	public List<ProdTable> getReadyToAssignReviwers(){
		return getProductService().findReadyToAssignReviwers(getUserSession().getLoggedINUserID());
	}
	
	/**
	 * get all applications ready for final solution
	 * @return
	 */
	public List<ProdTable> getReadyForHead(){
		return getProductService().findReadyForHead();
	}
	
	/**
	 * Get registered applications that about to expire. For company user only for this Company
	 * For department users - all, for anonymous - all
	 * @return
	 */
	public List<ProdTable> getAboutToExpire(){
		List<ProdTable> ret = getProductService().findAboutToExpire(getUserSession().isCompany(), getUserSession().getLoggedINUserID());
		if(ret!=null) {
			for(ProdTable prod : ret) {
				prod.setAccesible(getProductMbean().calcAccess(prod, userSession.getLoggedINUserID()));
			}
		}
		return ret;
	}
	
	/**
	 * Get just registered products
	 * @return
	 */
	public List<ProdTable> getJustRegistered(){
		List<ProdTable> ret = getProductService().findJustRegistered();
		if(ret!=null) {
			for(ProdTable prod : ret) {
				prod.setAccesible(getProductMbean().calcAccess(prod, userSession.getLoggedINUserID()));
			}
		}
		return ret;
	}
	
	/**
	 * Get products that just lost registration
	 * @return
	 */
	public List<ProdTable> getJustLost(){
		List<ProdTable> ret =getProductService().findJustLost();
		if(ret!=null) {
			for(ProdTable prod : ret) {
				prod.setAccesible(getProductMbean().calcAccess(prod, userSession.getLoggedINUserID()));
			}
		}
		return ret;
	}
	
}
