package org.msh.pharmadex.mbean.product;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.el.ELException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.FileTemplate;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.ProdCompany;
import org.msh.pharmadex.domain.ProdExcipient;
import org.msh.pharmadex.domain.ProdInn;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.ProdDrugType;
import org.msh.pharmadex.domain.enums.TemplateType;
import org.msh.pharmadex.domain.enums.UseCategory;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.ProductService;
import org.msh.pharmadex.service.TemplService;
import org.msh.pharmadex.service.UserAccessService;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.service.UtilsByReports;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class serves Detail Template as MS Word document
 * @author Alex Kurasoff
 *
 */
@ManagedBean
@ViewScoped
public class TemplMBean {

	@ManagedProperty(value = "#{userAccessService}")
	private UserAccessService userAccessService;

	@ManagedProperty(value = "#{templService}")
	private TemplService templService;

	@ManagedProperty(value= "#{prodApplicationsService}")
	private ProdApplicationsService prodApplicationsService;

	@ManagedProperty(value = "#{userService}")
	private UserService userService;

	@ManagedProperty(value = "#{productService}")
	private ProductService productService;

	@Autowired
	private UtilsByReports utilsByReports;

	@ManagedProperty(value = "#{applicantService}")
	public ApplicantService applicantService;

	@ManagedProperty(value = "#{userSession}")
	public UserSession userSession;

	private ProdApplications prodApplications=null;
	private TemplateType templateType ;
	private FileTemplate fileTemplate = null;
	private String fileName = null;
	private String typeAppEng = "";
	private String typeActiveIngredientEng = "";
	private String prescriptionCategoryEng ="";

	/**
	 * This is a full list of file template regardless of type
	 */
	List<FileTemplate> fullList = new ArrayList<FileTemplate>();

	public UserAccessService getUserAccessService() {
		return userAccessService;
	}

	public void setUserAccessService(UserAccessService userAccessService) {
		this.userAccessService = userAccessService;
	}

	public UserSession getUserSession() {
		return userSession;
	}

	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}

	public TemplService getTemplService() {
		return templService;
	}

	public void setTemplService(TemplService templService) {
		this.templService = templService;
	}

	/**
	 * Upload a template
	 * @param event
	 */
	public void handleFileUpload(FileUploadEvent event) {
		//templateType = (TemplateType) getFlash().get("templateType");
		FileTemplate fileTemplate = new FileTemplate ();//getUserAccessService().getWorkspace();
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle resourceBundle = context.getApplication().getResourceBundle(context, "msgs");
		try {
			byte[] fileStream = IOUtils.toByteArray(event.getFile().getInputstream());
			setProdApplications(null); //test only!
			fileTemplate.setTemplateType(templateType);
			fileTemplate.setFile(fileStream);
			fileTemplate.setFileName(event.getFile().getFileName());
			fileTemplate.setContentType(event.getFile().getContentType());				
			getTemplService().save(fileTemplate);						
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,resourceBundle.getString("global.success"), event.getFile().getFileName() + resourceBundle.getString("upload_success"));
			context.addMessage(null, msg);
			setTemplateType(null); //clean up for next choice
			if (!checkTemplate(new ByteArrayInputStream(fileStream))){
				uploadWarning(event, context, resourceBundle);
			}
		} catch (IOException e) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL,resourceBundle.getString("global.error"), event.getFile().getFileName() + resourceBundle.getString("upload_error"));
			context.addMessage(null, msg);
		}
	}
	/**
	 * Upload a template
	 * @param event
	 */
	public void handleFileChangeUpload(FileUploadEvent event) {
		fileTemplate = getFileTemplate();
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle resourceBundle = context.getApplication().getResourceBundle(context, "msgs");
		try {
			byte[] fileStream = IOUtils.toByteArray(event.getFile().getInputstream());
			setProdApplications(null); //test only!
			fileTemplate.setFile(fileStream);
			fileTemplate.setFileName(event.getFile().getFileName());
			fileTemplate.setContentType(event.getFile().getContentType());				
			getTemplService().save(fileTemplate);						
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,resourceBundle.getString("global.success"), event.getFile().getFileName() + " " +resourceBundle.getString("upload_success"));
			context.addMessage(null, msg);
			if (!checkTemplate(new ByteArrayInputStream(fileStream))){			
				uploadWarning(event, context, resourceBundle);
			}
		} catch (IOException e) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL,resourceBundle.getString("global.error"), event.getFile().getFileName() + resourceBundle.getString("upload_error"));
			context.addMessage(null, msg);
		}
	}

	/**
	 * Download a template
	 * @return
	 */
	public StreamedContent getFile() {	
		fileTemplate = getFileTemplate();
		InputStream ist = new ByteArrayInputStream(fileTemplate.getFile());		
		StreamedContent download = new DefaultStreamedContent(ist, "docx", fileTemplate.getFileName());
		return download;
	}
	/**
	 * Does the template exist
	 * @return
	 */
	public boolean isUploaded(FileTemplate fileTemplate){		
		return fileTemplate.getFileName() != null && fileTemplate.getFileName().length()>5;
	}
	/**
	 * Display upload warning
	 * @param event
	 * @param context
	 * @param resourceBundle
	 */
	public void uploadWarning(FileUploadEvent event, FacesContext context, ResourceBundle resourceBundle) {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,resourceBundle.getString("warning"), event.getFile().getFileName() + resourceBundle.getString("upload_success"));
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

			if(found>0){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,resourceBundle.getString("global.success"),
						found + " "+ resourceBundle.getString("elfound"));
				context.addMessage(null, msg);
				return true;
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL,resourceBundle.getString("global_fail"), resourceBundle.getString("templateVoid"));
				context.addMessage(null, msg);
				return false;
			}
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

	public List<FileTemplate> getFullList() {
		List<FileTemplate> listFileTemplate = getTemplService().findAll();
		setFullList(listFileTemplate);
		return listFileTemplate;
	}
	/**
	 * @return All TemplateType
	 */
	public List<TemplateType> getAllTemplateType(){
		List<TemplateType> templateType = new ArrayList<TemplateType>();		
		templateType.add(TemplateType.CERTIFICATE);	
		templateType.add(TemplateType.BIEF);
		return templateType;
	}
	/**
	 * @return not select TemplateType in FileTemplate
	 */
	public List<TemplateType> completeTemplateTypeList(){		
		List<TemplateType> templateType = getAllTemplateType();		
		List<FileTemplate> listFileTemplate = getTemplService().findAll();
		if(listFileTemplate!=null && listFileTemplate.size()>0){
			for(FileTemplate ft : listFileTemplate){				
				if(ft.getTemplateType()!=null){
					templateType.remove(ft.getTemplateType());
				}
			}
		}		
		return templateType;		
	}



	public void onItemSelect(SelectEvent event) {	
		event.getObject();
	}

	public void setFullList(List<FileTemplate> fullList) {
		this.fullList = fullList;
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

	public TemplateType getTemplateType() {
		return templateType;
	}

	public void setTemplateType(TemplateType templateType) {
		this.templateType = templateType;
	}

	/**
	 * @param appId - id ProdApplications
	 * @param templateType - type TemplateType
	 * @return StreamedContent which contain file 
	 */
	public StreamedContent getReport(Long appId, TemplateType templateType ){
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle resourceBundle = context.getApplication().getResourceBundle(context, "msgs");		
		setProdApplications(getProdApplicationsService().findProdApplications(appId));		
		FileTemplate fileTemplate = getTemplService().findByTemplateType(templateType);
		InputStream ist = null;
		if(fileTemplate!=null){
			ist = new ByteArrayInputStream(fileTemplate.getFile());
		}		
		try {
			InputStream downloadStream = getTemplService().generateReport(ist);
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,resourceBundle.getString("global.success"),  resourceBundle.getString("global.success"));
			context.addMessage(null, msg);
			StreamedContent download = new DefaultStreamedContent(downloadStream, "docx",getFileName());
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

	/*------------*/
	/**
	 * create Registration Certificate
	 * @param appId - id ProdApplications
	 * @param templateType - CERTIFICATE
	 * @param name - file name
	 * @return StreamedContent where contains file
	 */
	public StreamedContent getReport(String fileName,Long appId, TemplateType templateType){
		setFileName(fileName);
		return getReport(appId, templateType);
	}
	/*------------*/

	/**
	 * @return type of report
	 */
	public FileTemplate getFileTemplate() {
		return fileTemplate;
	}
	/**
	 * @return type of report
	 */
	public void setFileTemplate(FileTemplate fileTemplate) {
		this.fileTemplate = fileTemplate;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public ProductService getProductService() {
		return productService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public UtilsByReports getUtilsByReports() {
		return utilsByReports;
	}

	public void setUtilsByReports(UtilsByReports utilsByReports) {
		this.utilsByReports = utilsByReports;
	}

	public ApplicantService getApplicantService() {
		return applicantService;
	}

	public void setApplicantService(ApplicantService applicantService) {
		this.applicantService = applicantService;
	}

	/**
	 * We can upload a template only if template type is defined
	 */
	public boolean isUploadTemplate(){
		return getTemplateType() != null;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTypeAppEng() {
		return typeAppEng;
	}

	public void setTypeAppEng(String typeAppEng) {
		this.typeAppEng = typeAppEng;
	}

	public String getTypeActiveIngredientEng() {
		return typeActiveIngredientEng;
	}

	public void setTypeActiveIngredientEng(String typeActiveIngredientEng) {
		this.typeActiveIngredientEng = typeActiveIngredientEng;
	}

	public String getPrescriptionCategoryEng() {
		return prescriptionCategoryEng;
	}

	public void setPrescriptionCategoryEng(String prescriptionCategoryEng) {
		this.prescriptionCategoryEng = this.prescriptionCategoryEng +" " +prescriptionCategoryEng;
	}


	/***************************  EL VALUES *******************************************************/


	/**
	 * @return Applicant
	 */
	public Applicant getApplicant(){
		if(getProdApplications()!=null && getProdApplications().getApplicant()!=null){
			return getProdApplications().getApplicant();
		}
		return null;
	}


	/**
	 * @return Applicant Name
	 */
	public String getApplicantName(){
		if(getProdApplications() != null &&  getApplicant()!=null){
			return getApplicant().getAppName();

		}else{
			return "test applicant";
		}
	}

	/**
	 * @return Applicant Addr1
	 */
	public String getApplicantAddr1(){
		if(getProdApplications() != null &&  getApplicant()!=null &&  getApplicant().getAddress()!=null){
			String s = getApplicant().getAddress().getAddress1();
			if (s!= null){
				return s;
			}else{
				return "";
			}
		}else{
			return "";
		}
	}
	/**
	 * @return Applicant Addr2
	 */
	public String getApplicantAddr2(){
		if(getProdApplications() != null &&  getApplicant()!=null &&  getApplicant().getAddress()!=null){
			String s = getApplicant().getAddress().getAddress2();
			if (s!= null){
				return s;
			}else{
				return "";
			}
		}else{
			return "";
		}
	}
	/**
	 * @return Applicant Country
	 */
	public String getApplicantCountry(){
		if(getProdApplications() != null && getApplicant()!=null && getApplicant().getAddress()!=null
				&& getApplicant().getAddress().getCountry() != null){			
			return getApplicant().getAddress().getCountry().getCountryName();
		}else{
			return "";
		}			
	}


	/**
	 * @return Applicant Country, ZipCode
	 */
	public String getApplicantCountryZipCode(){
		String s = getApplicantCountry();
		if(getProdApplications() != null && getApplicant()!=null && getApplicant().getAddress()!=null){
			if(getApplicant().getAddress().getZipcode()!=null && !"".equals(getApplicant().getAddress().getZipcode())){
				s += ", "+getApplicant().getAddress().getZipcode();
				return s;
			}
		}				
		return "";
	}


	/**
	 * @return Product applications number 
	 */
	public String getProdAppNo(){
		if(getProdApplications() != null){
			return getProdApplications().getProdAppNo()!=null?getProdApplications().getProdAppNo():"";
		}else{
			return "test ProdApplications ProdAppNo";
		}
	}


	/**
	 * @return Product
	 */
	public Product getProduct(){
		if( getProdApplications()!=null && getProdApplications().getProduct()!=null ){
			return getProductService().findProduct(getProdApplications().getProduct().getId());
		}else{
			return null;
		}		
	}

	/**
	 * @return Product name
	 */
	public String getProdName(){
		Product prod =  getProduct();
		if(prod !=null){
			return prod.getProdName()!=null?prod.getProdName():"";
		}else{
			return "";
		}
	}

	/**
	 * @return Date of registration
	 */
	public String getRegistrationDate(){
		if(getProdApplications()!=null){
			if(getProdApplications().getRegistrationDate() != null)
				return  utilsByReports.getDateformat().format(getProdApplications().getRegistrationDate());	
		}
		return "test Date of registration";
	}	

	/**
	 * @return Name of manufacturer
	 */
	public String getManufName(){		
		Product product = getProduct();
		if(product!=null){			
			List<ProdCompany> companyList = product.getProdCompanies();
			if (companyList != null){
				for(ProdCompany company:companyList){					
					if (company.getCompanyType().equals(CompanyType.FIN_PROD_MANUF)){
						return company.getCompany().getCompanyName();						
					}
				}
			}			
		}
		return "No Name of manufacturer";
	}

	/**
	 * @return Dosage form
	 */
	public String getDosageForm(){
		Product prod = getProduct();
		if(prod!=null){
			return (prod.getDosForm() != null && prod.getDosForm().getDosForm() != null) ? prod.getDosForm().getDosForm():"";
		}
		return "test Dosage form";
	}

	/**
	 * @return Strength(s) per dosage unit
	 */
	public String getDosageStrength() {
		Product prod = getProduct();
		if(prod!=null){
			String res = "";
			if(prod.getDosStrength() != null){
				res = prod.getDosStrength();
			}
			if(prod.getDosUnit() != null ){
				res += " " + prod.getDosUnit().getUom();
			}
			return res;
		}
		return "test Strength(s) per dosage unit";
	}

	/**
	 * @return Pack size 
	 */
	public String getPackSize(){
		Product prod = getProduct();
		if(prod!=null){
			return prod.getPackSize() != null ? prod.getPackSize():"";
		}
		return "test Pack size";
	}

	/**
	 * @return Storage condition
	 */
	public String getStorageCondition(){
		Product prod = getProduct();
		if(prod!=null){
			return prod.getStorageCndtn() != null ? prod.getStorageCndtn():"";
		}
		return "test Storage condition";
	}
	/**
	 * @return Excipients
	 */
	public String getExcipients(){
		Product prod = getProduct();
		if(prod!=null){
			String str = "";
			List<ProdExcipient> expsl = prod.getExcipients();
			if(expsl != null) {
				Set<ProdExcipient> exps = new java.util.HashSet<ProdExcipient>();
				exps.addAll(expsl);
				if(exps != null && exps.size() > 0){
					for(ProdExcipient pe : exps){
						if(pe.getExcipient() != null){
							str += pe.getExcipient().getName() + ", "; 
						}
					}				
				}
			}
			if(str.length()>3) {
				str = str.substring(0, str.length()-2);
			}
			return str;			
		}
		return "test Excipients";
	}

	/**
	 * @return Active ingredient
	 */
	public String getActiveIngredient (){	
		String str = "";
		if( getProduct()!=null && getProduct().getInns()!=null){
			List<ProdInn> inns =  getProduct().getInns();
			if(inns != null && inns.size() > 0){
				for(int i = 0; i < inns.size(); i++){
					if(inns.get(i).getInn() != null){
						if(i == (inns.size() - 1))
							str += inns.get(i).getInn().getName();
						else
							str += inns.get(i).getInn().getName() + " + ";
					}
				}

			}			
			return str;		
		}		
		return "test Active ingredient ";
	}

	/**
	 * @return X- if Fnm is null, else "" 
	 */
	public String getFnm(){
		Product prod = getProduct();
		if(prod!=null){
			if("".equals(prod.getFnm())){
				return "Não/No";
			}else{
				return "Sim /yes";
			}
		}
		return "";
	}

	/**
	 * @return Type of application
	 */
	public String getTypeApp(){		
		if(getProdApplications() != null){
			ProdAppType type = getProdApplications().getProdAppType();
			if(type != null){
				if(type.toString().equals("NEW_CHEMICAL_ENTITY")){
					setTypeAppEng("Complete");
					return "Completo";
				}else if(type.toString().equals("GENERIC")){
					setTypeAppEng("Abreviated");
					return "Abreviado";
				}else if(type.toString().equals("RECOGNIZED")){
					setTypeAppEng("Recognised");
					return "Reconhecimento";
				}else if(type.toString().equals("RENEW")){
					setTypeAppEng("Collaborative");
					return "Colaborativo";
				}
			}
		}
		return "test Type of application";
	}
	/**
	 * @return Type of Active ingredient
	 */
	public String getTypeActiveIngredient(){
		Product prod = getProduct();
		if(prod!=null){
			ProdDrugType type = prod.getDrugType();
			if(type != null){
				if(type.equals(ProdDrugType.PHARMACEUTICAL)){
					setTypeActiveIngredientEng("Chemical");
					return "Química";
				}else if(type.equals(ProdDrugType.MEDICAL_DEVICE)){
					setTypeActiveIngredientEng("");
					return "";
				}else if(type.equals(ProdDrugType.RADIO_PHARMA)){
					setTypeActiveIngredientEng("Ratiopharmaceutical");
					return "Radiofármaco";
				}else if(type.equals(ProdDrugType.VACCINE)){
					setTypeActiveIngredientEng("");
					return "";
				}else if(type.equals(ProdDrugType.BIOLOGICAL)){
					setTypeActiveIngredientEng("Other Biologicals");
					return "Outros  produtos Biólogos/";
				}else if(type.equals(ProdDrugType.COMPLIMENTARY_MEDS)){
					setTypeActiveIngredientEng("");
					return "";
				}
			}else{
				return "";
			}
		}		
		return "test Type of Active ingredient";
	}
	/**
	 * @return Prescription category
	 */
	public String getPrescriptionCategory(){
		Product prod = getProduct();
		String str = "", res = "";
		if(prod!=null){
			List<UseCategory> cats = prod.getUseCategories();
			if(cats != null && cats.size() > 0){
				for(UseCategory c:cats){
					str += c.ordinal();
				}
			}
			if(str.length()>0){
				if(str.contains("0")){
					setPrescriptionCategoryEng("Narcotics, Stupefacients");
					res += " "+"Narcótico, Estupefaciente";
				}else if(str.contains("1")){
					setPrescriptionCategoryEng("POM");
					res += " "+"MSRM";
				}else if(str.contains("2")){
					setPrescriptionCategoryEng("Hospital Only");
					res += " "+"Hospitalar/";
				}else if(str.contains("3")){
					setPrescriptionCategoryEng("OTC");
					res += " "+"MNSRM";
				}
				if(res.length()>0){
					return res.replaceFirst(" ", "");
				}
			}			
		}
		return "test Prescription category";
	} 
	/**
	 * @return Expiry Date
	 */
	public String getExpiryDate(){
		if(getProdApplications()!=null){
			if(getProdApplications().getRegExpiryDate() != null){
				return utilsByReports.getDateformat().format(getProdApplications().getRegExpiryDate());
			}
		}
		return "test Expiry date";
	}

	/**
	 * @return Submition date
	 */
	public String getSubmitDate(){
		if(getProdApplications()!=null){
			if(getProdApplications().getSubmitDate()!=null){
				return utilsByReports.getDateformat().format(getProdApplications().getSubmitDate());
			}
		}
		return "test Submition date";
	}

	/**
	 * @return Shelf Life
	 */
	public String getShelfLife(){
		Product prod = getProduct();
		if(prod!=null){
			return prod.getShelfLife() != null ? prod.getShelfLife():"";
		}
		return "test Shelf Life";
	}

	/**
	 * @return Registration number
	 */
	public String getRegistrationNumber(){
		if(getProdApplications()!=null && getProdApplications().getProdRegNo() != null){
			return getProdApplications().getProdRegNo() ;
		}
		return "test Registration number";
	}

	/**
	 * @return Headers by Table
	 */
	public String getHeaders(){
		return "";
	}

	/**
	 * @return Rows by Table
	 */
	public String getRows(){
		return "";
	}
}
