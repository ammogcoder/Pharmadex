package org.msh.pharmadex.service;

import java.math.BigInteger;
import java.text.DateFormatSymbols;
import java.util.List;

import javax.faces.context.FacesContext;

import org.msh.pharmadex.dao.DashboardDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * All Dashboard services
 * @author Alex Kurasoff
 *
 */
@Service
public class DashboardService {
	@Autowired
	DashboardDAO dashboardDAO;
	/**
	 * Fetch all submitted applications
	 * @return empty list or list of submitted ones
	 */
	public List<SubmittedAppDTO> fetchAllSubmitted() {
		return dashboardDAO.fetchSubmittedQuart();
	}
	/**
	 * Drill down quarter submitted data to months in quarter
	 * @param currentYQ
	 * @return empty list or list of results
	 */
	public List<SubmittedAppDTO> drillDownSubmitted(SubmittedAppDTO currentYQ) {
		List<SubmittedAppDTO> ret = dashboardDAO.fetchSubmittedMonth(currentYQ.getYear(), currentYQ.getQuarter(), currentYQ.getAppId());
		//set month names
		 DateFormatSymbols dfs = new DateFormatSymbols(FacesContext.getCurrentInstance().getViewRoot().getLocale());
		 String[] mNames = dfs.getMonths();
		for(SubmittedAppDTO dto :ret){
			dto.setMonthName(mNames[dto.getMonth()-1]);
		}
		return ret;
	}
	public List<SubmittedAppDTO> drillDownSubmittedStates(SubmittedAppDTO currentYQ) {
		List<SubmittedAppDTO> ret = dashboardDAO.fetchSubmittedStates(currentYQ.getYear(), currentYQ.getQuarter(), currentYQ.getAppId());
		return ret;
	}
	/**
	 * Fetch data for first level reviewers performance report
	 * @return
	 */
	public List<UsersPerformanceDTO> fetchReviewers() {
		return dashboardDAO.fetchReviewers();
	}
	/**
	 * Fetch details about reviewer's workload
	 * @param userId reviewer
	 * @param primaryNeed only primary or only secondary
	 * @return
	 */
	public List<UsersPerformanceDTO> fetchReviewerDetails(BigInteger userId, boolean primaryNeed) {
		return dashboardDAO.loadReviewerDetails(userId, primaryNeed);
	}

}
