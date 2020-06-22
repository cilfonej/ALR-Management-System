package edu.wit.alr.web.response;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class ResponseBuilder {
	private static final Pattern TEMPLATE_PATTERN = Pattern.compile("(.+?)\\s*::\\s*(\\w+)", Pattern.CASE_INSENSITIVE);
	
	
	@Autowired private TemplateEngine engine;
	
	
	public PageResponse redirect(String url, String template, Map<String, Object> variables) {
		return redirect(url, template, Locale.getDefault(), variables);
	}
	
	public PageResponse redirect(String url, String template, Locale locale, Map<String, Object> variables) {
		Matcher match = TEMPLATE_PATTERN.matcher(template);
		if(!match.find()) throw new IllegalArgumentException("Template must be of the format: <file_name> :: <fragment>");
		
		String file = match.group(1);
		String fragment = match.group(2);
		
		String html = engine.process(file, Set.of(fragment), new Context(locale, variables));
		return new PageResponse(url, html);
	}

	public String buildIndependentPage(PageResponse page) {
		return engine.process("index", new Context(Locale.getDefault(), Map.of("content", page.getPageHTML())));
	}
}
