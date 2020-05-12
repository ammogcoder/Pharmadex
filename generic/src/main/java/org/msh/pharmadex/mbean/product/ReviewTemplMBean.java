package org.msh.pharmadex.mbean.product;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.el.ELException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.ProdCompany;
import org.msh.pharmadex.domain.ProdExcipient;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.Workspace;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.msh.pharmadex.domain.enums.UseCategory;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.TemplService;
import org.msh.pharmadex.service.UserAccessService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.lowagie.text.pdf.codec.Base64.OutputStream;

/**
 * This class serves Review Detail Template as MS Word document
 * @author Alex Kurasoff
 *
 */
@ManagedBean
@RequestScoped
public class ReviewTemplMBean {

	@ManagedProperty(value = "#{userAccessService}")
	private UserAccessService userAccessService;

	@ManagedProperty(value = "#{templService}")
	private TemplService templService;

	@ManagedProperty(value= "#{prodApplicationsService}")
	private ProdApplicationsService prodApplicationsService;

	private ProdApplications prodApplications=null;

	public UserAccessService getUserAccessService() {
		return userAccessService;
	}

	public void setUserAccessService(UserAccessService userAccessService) {
		this.userAccessService = userAccessService;
	}

	public ProdApplicationsService getProdApplicationsService() {
		return prodApplicationsService;
	}

	public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
		this.prodApplicationsService = prodApplicationsService;
	}

	public ProdApplications getProdApplications() {
		return prodApplications;
	}

	public void setProdApplications(ProdApplications prodApplications) {
		this.prodApplications = prodApplications;
	}

	/**
	 * Upload a template
	 * @param event
	 */
	public void handleFileUpload(FileUploadEvent event) {
		Workspace workspace = getUserAccessService().getWorkspace();
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle resourceBundle = context.getApplication().getResourceBundle(context, "msgs");
		try {
			byte[] fileStream = IOUtils.toByteArray(event.getFile().getInputstream());
			setProdApplications(null); //test only!
			if (checkTemplate(new ByteArrayInputStream(fileStream))){
				workspace.setFile(fileStream);
				workspace.setFileName(event.getFile().getFileName());
				workspace.setContentType(event.getFile().getContentType());
				getUserAccessService().saveWorkspace(workspace);
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,resourceBundle.getString("global.success"), event.getFile().getFileName() + resourceBundle.getString("upload_success"));
				context.addMessage(null, msg);
			}else{
				uploadError(event, context, resourceBundle);
			}
		} catch (IOException e) {
			uploadError(event, context, resourceBundle);
		}
	}

	/**
	 * Display upload error
	 * @param event
	 * @param context
	 * @param resourceBundle
	 */
	public void uploadError(FileUploadEvent event, FacesContext context, ResourceBundle resourceBundle) {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL,resourceBundle.getString("global_fail"), event.getFile().getFileName() + resourceBundle.getString("upload_fail"));
		context.addMessage(null, msg);
	}
	/**
	 * Validate the template
	 * @param template
	 * @return true when check is OK
	 */
	private boolean checkTemplate(InputStream template) {
		FacesContext context = FacesContext.getCurrentInstance();
		int found = 0;
		ResourceBundle resourceBundle = context.getApplication().getResourceBundle(context, "msgs");
		try {
			found = getTemplService().testIt(template);
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,resourceBundle.getString("global.success"),  found + " "+ resourceBundle.getString("elfound"));
			context.addMessage(null, msg);
			return true;
		} catch (ELException e) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL,resourceBundle.getString("global_fail"), e.getLocalizedMessage());
			context.addMessage(null, msg);
			return false;
		} catch (IOException e) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL,resourceBundle.getString("global_fail"), e.getLocalizedMessage());
			context.addMessage(null, msg);
			return false;
		}
	}

	/**
	 * Download a template
	 * @return
	 */
	public StreamedContent getFile() {
		Workspace workspace = getUserAccessService().getWorkspace();
		InputStream ist = new ByteArrayInputStream(workspace.getFile());
		Calendar c = Calendar.getInstance();
		StreamedContent download = new DefaultStreamedContent(ist, "docx", workspace.getFileName());
		return download;
	}

	/**
	 * Create and download a report for application appId
	 * @param appId
	 * @return
	 */
	public StreamedContent getReport(Long appId){
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle resourceBundle = context.getApplication().getResourceBundle(context, "msgs");
		Workspace workspace = getUserAccessService().getWorkspace();
		setProdApplications(getProdApplicationsService().findProdApplications(appId));
		InputStream ist = new ByteArrayInputStream(workspace.getFile());
		try {
			InputStream downloadStream = getTemplService().generateReport(ist);
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,resourceBundle.getString("global.success"),  resourceBundle.getString("global.success"));
			context.addMessage(null, msg);
			StreamedContent download = new DefaultStreamedContent(downloadStream, "docx", getProdApplications().getProdAppNo()+".docx");
			return download;
		} catch (ELException e) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL,resourceBundle.getString("global_fail")+ " " + e.getLocalizedMessage(),
					resourceBundle.getString("global_fail")+ " " + e.getLocalizedMessage());
			context.addMessage(null, msg);
			return null;
		} catch (IOException e) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL,resourceBundle.getString("global_fail")+ " " + e.getLocalizedMessage(),
					resourceBundle.getString("global_fail")+ " " + e.getLocalizedMessage());
			context.addMessage(null, msg);
			return null;
		} finally{
			try {
				ist.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Does the template exist
	 * @return
	 */
	public boolean isUploaded(){
		Workspace workspace = getUserAccessService().getWorkspace();
		return workspace.getFileName() != null && workspace.getFileName().length()>5;
	}

	/**
	 * Commercial name of the product
	 * @return
	 */
	public String getProdName(){
		if(getProdApplications() != null){
			return getProdApplications().getProduct().getProdName();
		}else{
			return "test product";
		}
	}

	public String getApplicantName(){
		if(getProdApplications() != null){
			return getProdApplications().getApplicant().getAppName();
		}else{
			return "test applicant";
		}
	}

	public String getApplicantAddr1(){
		if(getProdApplications() != null){
			String s = getProdApplications().getApplicant().getAddress().getAddress1();
			if (s!= null){
				return s;
			}else{
				return "";
			}
		}else{
			return "test applicant addr 1";
		}
	}

	public String getApplicantAddr2(){
		if(getProdApplications() != null){
			String s = getProdApplications().getApplicant().getAddress().getAddress2();
			if (s!= null){
				return s;
			}else{
				return "";
			}
		}else{
			return "test applicant addr 2";
		}
	}

	public String getApplicantCountry(){
		if(getProdApplications() != null && getProdApplications().getApplicant().getAddress().getCountry() != null){
			String s = getProdApplications().getApplicant().getAddress().getCountry().getCountryName();
			if (s!= null){
				return s;
			}else{
				return "";
			}
		}else{
			return "Neverland";
		}
	}

	public String getGenericName(){
		if(getProdApplications() != null){
			return getProdApplications().getProduct().getGenName();
		}else{
			return "test applicant";
		}
	}

	public String getDosage(){
		if(getProdApplications() != null){
			String s = getProdApplications().getProduct().getDosStrength() + " " + getProdApplications().getProduct().getDosUnit().getUom();
			return s!=null?s:"";
		}else{
			return "0 Mg";
		}
	}

	public String getExcipients(){
		if(getProdApplications() != null){
			List<ProdExcipient> exList = getProdApplications().getProduct().getExcipients();
			String s="";
			if(exList!=null){
				for(ProdExcipient pe : exList){
					s=s+pe.getExcipient().getName() +", ";
				}
			}
			if(s.endsWith(", ")){
				s = s.substring(0, s.length()-2);
			}
			return s.trim();
		}else{
			return "no excipients";
		}
	}

	public String getDosageForm(){
		if(getProdApplications() != null && getProdApplications().getProduct().getDosForm() != null){
			String s = getProdApplications().getProduct().getDosForm().getDosForm();
			return s!=null?s:"";
		}else{
			return "0 Mg";
		}
	}

	public String getProdDesc(){
		if(getProdApplications() != null){
			String s = getProdApplications().getProduct().getProdDesc();
			return s!=null?s:"";
		}else{
			return "big black box is playing";
		}
	}

	public String getUseCategory(){
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle resourceBundle = context.getApplication().getResourceBundle(context, "msgs");
		if(getProdApplications() != null){
			List<UseCategory> catList = getProdApplications().getProduct().getUseCategories();
			String s = "";
			for(UseCategory cat :catList){
				s=s+resourceBundle.getString(cat.getKey())+", ";
			}
			if(s.endsWith(", ")){
				s = s.substring(0, s.length()-2);
			}
			return s.trim();
		}else{
			return "test use";
		}
	}

	public String getShelfLife(){
		if(getProdApplications() != null){
			String s = getProdApplications().getProduct().getShelfLife();
			return s!=null?s:"";
		}else{
			return "0";
		}
	}

	public String getStorageCondition(){
		if(getProdApplications() != null){
			String s = getProdApplications().getProduct().getStorageCndtn();
			return s!=null?s:"";
		}else{
			return "not defined";
		}
	}

	public String getAdminRoute(){
		if(getProdApplications() != null && getProdApplications().getProduct().getAdminRoute() != null){
			String s = getProdApplications().getProduct().getAdminRoute().getName();
			return s!=null?s:"";
		}else{
			return "No Route!";
		}
	}

	public String getApplType(){
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle resourceBundle = context.getApplication().getResourceBundle(context, "msgs");
		if(getProdApplications() != null && getProdApplications().getProdAppType() != null){
			String s = resourceBundle.getString(getProdApplications().getProdAppType().getKey());
			return s!=null?s:"";
		}else{
			return "Type is unknown";
		}
	}

	public String getManufacturerName(){
		if(getProdApplications() != null && getProdApplications().getProduct().getProdCompanies() != null){
			Company manuf = fetchFinishManufacturer(getProdApplications().getProduct());
			if(manuf!=null){
				String s = manuf.getCompanyName();
				return s!=null?s:"";
			}else{
				return "Unknown";
			}
		}else{
			return "Unknown";
		}
	}
	/**
	 * Fetch finished product manufacturer
	 * @param product
	 * @return
	 */
	private Company fetchFinishManufacturer(Product product) {
		List<ProdCompany> pcList = product.getProdCompanies();
		if(pcList != null){
			for(ProdCompany pc :pcList){
				if(pc.getCompanyType().equals(CompanyType.FIN_PROD_MANUF)){
					return pc.getCompany();
				}
			}
		}
		return null;
	}

	public String getManufacturerAddress(){
		if(getProdApplications() != null && getProdApplications().getProduct().getProdCompanies() != null){
			Company manuf = fetchFinishManufacturer(getProdApplications().getProduct());
			if(manuf!=null && manuf.getAddress()!=null){
				String s = manuf.getAddress().getAddress1() + " " + manuf.getAddress().getAddress2();
				return s!=null?s.trim():"";
			}else{
				return "Unknown";
			}
		}else{
			return "Unknown";
		}
	}

	public String getManufacturerCountry(){
		if(getProdApplications() != null && getProdApplications().getProduct().getProdCompanies() != null){
			Company manuf = fetchFinishManufacturer(getProdApplications().getProduct());
			if(manuf!=null  && manuf.getAddress()!=null && manuf.getAddress().getCountry()!=null){
				String s = manuf.getAddress().getCountry().getCountryName();
				return s!=null?s.trim():"";
			}else{
				return "Unknown";
			}
		}else{
			return "Unknown";
		}
	}

	public String getReviewers(){
		if(getProdApplications() != null){
			List<ReviewInfo> revList = getProdApplications().getReviewInfos();
			Set<String> buf = new HashSet<String>();
			if(revList!=null){
				String s="";
				for(ReviewInfo ri : revList){
					User rev1 = ri.getReviewer();
					User rev2 = ri.getSecReviewer();
					if(rev1!=null){
						buf.add(rev1.getName());
					}
					if(rev2!=null){
						buf.add(rev2.getName());
					}
				}
				for(String st : buf){
					s=s+st+", ";
				}
				if(s.endsWith(", ")){
					s=s.substring(0, s.length()-2);
				}
				return s;
			}else{
				return "Unknown";
			}
		}else{
			return "Unknown";
		}
	}

	public TemplService getTemplService() {
		return templService;
	}

	public void setTemplService(TemplService templService) {
		this.templService = templService;
	}
}
