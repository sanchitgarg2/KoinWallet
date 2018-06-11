package domain;

import javax.servlet.http.Cookie;

public class CookieManager {
	
	public static Cookie addCookie(String name, String value){
		Cookie cookie = new Cookie(name, value);
	cookie.setPath("/");
	cookie.setMaxAge(1000);
	cookie.setSecure(Constants.IS_HTTPS_ENABLED);
	cookie.setDomain(Constants.DOMAIN_IP_ADDRESS);
	return cookie;
//	response.addCookie(cookie);
	}

	public static Cookie getBlankCookie(String string) {
		Cookie cookie = new Cookie("sessionID", "");
		cookie.setPath("/");
		cookie.setMaxAge(0);
		cookie.setSecure(false);
		cookie.setDomain(Constants.DOMAIN_IP_ADDRESS);
		return cookie;
	}

}
