package edu.wit.alr.web.util;

import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieHelper {
	public static Optional<Cookie> findCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();

		// check if request had cookies
		if(cookies != null) {
			for(Cookie cookie : cookies) {
				// search for the cookie with the provided name
				if(cookie.getName().equals(name)) {
					return Optional.of(cookie);
				}
			}
		}

		// return <empty> if no cookie was found
		return Optional.empty();
	}

	public static void addCookie(HttpServletResponse response, String name, String value, int lifetime) {
		Cookie cookie = new Cookie(name, value);
		
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(lifetime);
		
		response.addCookie(cookie);
	}

	public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String name) {
		Cookie[] cookies = request.getCookies();

		// check if request had cookies
		if(cookies != null) {
			for(Cookie cookie : cookies) {
				// checks if the cookie exists, before attempting to remove it
				if(cookie.getName().equals(name)) {
					
					// if the cookie was found, "remove" it
					cookie.setValue("");
					cookie.setPath("/");
					cookie.setMaxAge(0);
					
					response.addCookie(cookie);
				}
			}
		}
	}
}
