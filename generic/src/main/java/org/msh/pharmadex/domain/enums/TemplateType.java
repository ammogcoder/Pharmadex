package org.msh.pharmadex.domain.enums;

/**
 * Types of file templates. Each template has own type
 * @author Alex Kurasoff
 *
 */
public enum TemplateType {	
	CERTIFICATE,
	BIEF;
		
	public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }
}
