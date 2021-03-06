package org.msh.pharmadex.domain;

import org.hibernate.annotations.OrderBy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Cacheable
@Table(name = "administrativeunit", uniqueConstraints =  @UniqueConstraint(columnNames = {"id", "code"}))
public class AdministrativeUnit extends CreationDetail implements Serializable {

    private static final long serialVersionUID = 3571280964748031431L;
    @Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Embedded
	private LocalizedNameComp name = new LocalizedNameComp();

	@ManyToOne
	@JoinColumn(name="PARENT_ID")
	private AdministrativeUnit parent;
	
	@OneToMany(mappedBy="parent",fetch=FetchType.LAZY)
	@OrderBy(clause="NAME1")
	private List<AdministrativeUnit> units = new ArrayList<AdministrativeUnit>();

	@Column(length=50)
	private String legacyId;
	
	// properties to help dealing with trees
	private int unitsCount;
	
	@Column(length=15, nullable=false)
	private String code;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="COUNTRYSTRUCTURE_ID")
	private CountryStructure countryStructure;

	/**
	 * Static method that return the parent code of a given code
	 *
	 * @param code
	 * @return
	 */
	public static String getParentCode(String code) {
		if ((code == null) || (code.length() <= 3))
			return null;

		if (code.length() <= 6)
			return code.substring(0, 3);
		if (code.length() <= 9)
			return code.substring(0, 6);
		if (code.length() <= 12)
			return code.substring(0, 9);
		return code.substring(0, 12);
	}

	/**
	 * Static method to check if a code is equals of a child of the code given by the parentCode param
	 *
	 * @param parentCode
	 * @param code
	 * @return
	 */
	public static boolean isSameOrChildCode(String parentCode, String code) {
		int len = parentCode.length();
		if (len > code.length())
			return false;
		return (parentCode.equals(code.substring(0, parentCode.length())));
	}
	
	/**
	 * Return the parent list including the own object
	 * @return List of {@link org.msh.pharmadex.domain.AdministrativeUnit} instance
	 */
	public List<AdministrativeUnit> getParents() {
		return getParentsTreeList(true);
	}
	
	/**
	 * Return the display name of the administrative unit concatenated with its parent units
	 * @return
	 */
	public String getFullDisplayName() {
		String s = getName().toString();

		for (AdministrativeUnit adm: getParentsTreeList(false)) {
			s += ", " + adm.getName().toString();
		}

		return s;
	}

	/**
	 * Return the parent units display name with the name of this instance only on the end.
	 * @return
	 */
	public String getFullDisplayName2() {
		String s = null;

		for (AdministrativeUnit adm: getParentsTreeList(true)) {
			if(s == null)
				s = adm.getName().toString();
			else
				s += ", " + adm.getName().toString();
		}

		return s;
	}
	
	/**
	 * Return a list with parents administrative unit, where the first is the upper level administrative unit and
	 * the last the lowest level
	 * @return {@link java.util.List} of {@link org.msh.pharmadex.domain.AdministrativeUnit} instances
	 */
	public List<AdministrativeUnit> getParentsTreeList(boolean includeThis) {
		ArrayList<AdministrativeUnit> lst = new ArrayList<AdministrativeUnit>();

		AdministrativeUnit aux;
		if (includeThis)
			 aux = this;
		else aux = getParent();

		while (aux != null) {
			lst.add(0, aux);
			aux = aux.getParent();
		}
		return lst;
	}
	
	/**
	 * Check if an administrative unit code (passed as the code parameter) is a child of the current administrative unit
	 * @param code of the unit
	 * @return true if code is of a child unit, otherwise return false
	 */
	public boolean isSameOrChildCode(String code) {
		return isSameOrChildCode(this.code, code);
/*		int len = this.code.length();
		if (len > code.length())
			return false;
		return (this.code.equals(code.substring(0, this.code.length())));
*/	}

	/**
	 * Return the parent administrative unit based on its level. If level is the same of this unit, it returns itself.
	 * If level is bigger than the level of this unit, it returns null
	 * @param level
	 * @return {@link org.msh.pharmadex.domain.AdministrativeUnit} instance, which is itself or a parent admin unit
	 */
	public AdministrativeUnit getAdminUnitByLevel(int level) {
		if (level == countryStructure.getLevel())
			return this;
		List<AdministrativeUnit> lst = getParents();
		for (AdministrativeUnit adm: lst) {
			if (adm.getLevel()== level)
				return adm;
		}
		return null;
	}

	
	/**
	 * Return parent administrative unit of level 1
	 * @return {@link org.msh.pharmadex.domain.AdministrativeUnit} instance
	 */
	public AdministrativeUnit getParentLevel1() {
		return getAdminUnitByLevel(1);
	}

	
	/**
	 * Return parent administrative unit of level 2
	 * @return {@link org.msh.pharmadex.domain.AdministrativeUnit} instance
	 */
	public AdministrativeUnit getParentLevel2() {
		return getAdminUnitByLevel(2);
	}

	
	/**
	 * Return parent administrative unit of level 3
	 * @return {@link org.msh.pharmadex.domain.AdministrativeUnit} instance
	 */
	public AdministrativeUnit getParentLevel3() {
		return getAdminUnitByLevel(3);
	}

	
	/**
	 * Return parent administrative unit of level 4
	 * @return {@link org.msh.pharmadex.domain.AdministrativeUnit} instance
	 */
	public AdministrativeUnit getParentLevel4() {
		return getAdminUnitByLevel(4);
	}


	/**
	 * Return parent administrative unit of level 5
	 * @return {@link org.msh.pharmadex.domain.AdministrativeUnit} instance
	 */
	public AdministrativeUnit getParentLevel5() {
		return getAdminUnitByLevel(5);
	}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
	 * @return the name
	 */
	public LocalizedNameComp getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(LocalizedNameComp name) {
		this.name = name;
	}

	/**
	 * @return the parent
	 */
	public AdministrativeUnit getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(AdministrativeUnit parent) {
		this.parent = parent;
	}

	/**
	 * @return the units
	 */
	public List<AdministrativeUnit> getUnits() {
		return units;
	}

	/**
	 * @param units the units to set
	 */
	public void setUnits(List<AdministrativeUnit> units) {
		this.units = units;
	}

	/**
	 * @return the legacyId
	 */
	public String getLegacyId() {
		return legacyId;
	}

	/**
	 * @param legacyCode the legacyId to set
	 */
	public void setLegacyId(String legacyCode) {
		this.legacyId = legacyCode;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getFullDisplayName();
	}

	/**
	 * @return the unitsCount
	 */
	public int getUnitsCount() {
		return unitsCount;
	}

	/**
	 * @param unitsCount the unitsCount to set
	 */
	public void setUnitsCount(int unitsCount) {
		this.unitsCount = unitsCount;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (!(obj instanceof AdministrativeUnit))
			return false;
		
		return ((AdministrativeUnit)obj).getId().equals(getId());
	}

	/**
	 * @return the countryStructure
	 */
	public CountryStructure getCountryStructure() {
		return countryStructure;
	}

	/**
	 * @param countryStructure the countryStructure to set
	 */
	public void setCountryStructure(CountryStructure countryStructure) {
		this.countryStructure = countryStructure;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	public int getLevel() {
		String s = getCode();
		if (s == null)
			return 0;

		return s.length()/3;
	}
}
