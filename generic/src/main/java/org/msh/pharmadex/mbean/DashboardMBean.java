package org.msh.pharmadex.mbean;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.service.DashboardService;
import org.msh.pharmadex.service.SubmittedAppDTO;
import org.msh.pharmadex.service.UsersPerformanceDTO;


/**
 * Dashboard for all indicators
 * @author Alex Kurasoff
 *
 */
@ManagedBean
@ViewScoped
public class DashboardMBean {
	@ManagedProperty("#{dashboardService}")
	DashboardService dashboardService;
	SubmittedAppDTO currentYQ = null;
	UsersPerformanceDTO currentReviewer=null;
	boolean primaryNeed=true;


	public UsersPerformanceDTO getCurrentReviewer() {
		return currentReviewer;
	}

	public void setCurrentReviewer(UsersPerformanceDTO currentReviewer) {
		this.currentReviewer = currentReviewer;
	}

	public boolean isPrimaryNeed() {
		return primaryNeed;
	}

	public void setPrimaryNeed(boolean primaryNeed) {
		this.primaryNeed = primaryNeed;
	}

	public SubmittedAppDTO getCurrentYQ() {
		return currentYQ;
	}

	public void setCurrentYQ(SubmittedAppDTO currentYQ) {
		this.currentYQ = currentYQ;
	}

	public DashboardService getDashboardService() {
		return dashboardService;
	}

	public void setDashboardService(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}

	public List<SubmittedAppDTO> getSubmitted(){
		return getDashboardService().fetchAllSubmitted();
	}

	public void setSubmitted(List<SubmittedAppDTO> list){
		// only for specifications
	}
	/**
	 * Set currently selected submitted DTO 
	 * @param dto
	 */
	public void changeYearQuart(SubmittedAppDTO dto){
		setCurrentYQ(dto);
	}
	/**
	 * Get dialog box to drill down quarter - month header
	 * @return
	 */
	public String getQuartHeader(){
		if (getCurrentYQ() != null){
			ResourceBundle res = FacesContext.getCurrentInstance().getApplication().getResourceBundle(FacesContext.getCurrentInstance(), "msgs");
			return res.getString("year")
					+ " " + getCurrentYQ().getYear()
					+ " " + res.getString("quarter")
					+ " " + getCurrentYQ().getQuarter();
		}else{
			return "";
		}

	}
	public void setQuartHeader(){
		//nothing to do, bean spec only
	}

	public List<SubmittedAppDTO> getSubmittedByMonths(){
		List<SubmittedAppDTO> ret = new ArrayList<SubmittedAppDTO>();
		if(getCurrentYQ() != null){
			ret = getDashboardService().drillDownSubmitted(getCurrentYQ());
		}
		return ret;
	}

	public void setSubmittedByMonths(List<SubmittedAppDTO> l){
		// only for specifications
	}

	public List<SubmittedAppDTO> getSubmittedByState(){
		List<SubmittedAppDTO> ret = new ArrayList<SubmittedAppDTO>();
		if(getCurrentYQ() != null){
			ret = getDashboardService().drillDownSubmittedStates(getCurrentYQ());
			if(ret!=null){ //rewrite states to have nice look
				FacesContext context = FacesContext.getCurrentInstance();
				ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");
				for(SubmittedAppDTO dto : ret){
					dto.setRegState(bundle.getString("RegState."+dto.getRegState()));
				}

			}
		}
		return ret;
	}

	/**
	 * Get first level of reviewers performance report 
	 * @return
	 */
	public List<UsersPerformanceDTO> getReviewers(){
		return getDashboardService().fetchReviewers();
	}

	public void setReviewers(List<UsersPerformanceDTO> dummy){
		//TODO to complete specifications
	}

	/**
	 * set a current reviewer to expand reviewer's workload details
	 * @param item current reviewer
	 * @param primary primary reviewer's details should be expanded
	 */
	public void changeCurrentReviewer(UsersPerformanceDTO item, boolean primary){
		setCurrentReviewer(item);
		setPrimaryNeed(primary);
	}

	/**
	 * Get details about currently active reviewer
	 * @return details or empty list
	 */
	public List<UsersPerformanceDTO> getReviewerDetails(){
		if(getCurrentReviewer() != null){
			List<UsersPerformanceDTO> ret = getDashboardService().fetchReviewerDetails(getCurrentReviewer().getUserId(),isPrimaryNeed()); 
			if(ret!=null){ //decrypt states
				FacesContext context = FacesContext.getCurrentInstance();
				ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");
				for(UsersPerformanceDTO dto : ret){
					dto.setCtdModule(bundle.getString("CTDModule."+dto.getCtdModule()));
					dto.setReviewStatus(bundle.getString("ReviewStatus."+dto.getReviewStatus()));
				}
			}
			return ret;
		}else{
			return new ArrayList<UsersPerformanceDTO>();
		}
	}
	
	public String getReviwerDetailHeader(){
		String ret="";
		if(getCurrentReviewer()!=null){
			FacesContext context = FacesContext.getCurrentInstance();
			ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");
			ret = bundle.getString("isprimaryprosessorfor");
			if(!isPrimaryNeed()){
				ret=bundle.getString("issecondprosessorfor");
			}
			ret =  getCurrentReviewer().getUserName()+ " " + ret;
		}
		return ret;
	}


}
