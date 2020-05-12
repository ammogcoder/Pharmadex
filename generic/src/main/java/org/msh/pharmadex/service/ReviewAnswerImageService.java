package org.msh.pharmadex.service;

import org.springframework.stereotype.Service;

/**
 * Service that manage image that belongs to particular review answer
 * @author Alex Kurasoff
 *
 */
@Service
public class ReviewAnswerImageService {
	/**
	 * It is very first test to fine tune CKEditor
	 * Return in JSON format success and link to a MOZ emblem
	 * @return
	 */
	public String testImage() {
		String ret="{'uploaded': '1', 'fileName': 'logo_mz.png', 'url': '/resources/images/logo_mz.png'}";
		return ret;
	}

}
