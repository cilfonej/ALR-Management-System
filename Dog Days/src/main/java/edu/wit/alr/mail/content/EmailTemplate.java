package edu.wit.alr.mail.content;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

import edu.wit.alr.web.preprocessor.SubContext;

public class EmailTemplate {
	private static final Pattern TEMPLATE_PATTERN = Pattern.compile("^(.+?)(?:\\s*::\\s*(\\w+))?$", Pattern.CASE_INSENSITIVE);
	
	private String file;
	private String fragment;
	
	private String subject;
	
	private Context context;
	private Collection<EmailAttachment> attachments;

	public EmailTemplate(String subject, String template, Locale locale) {
		this(subject, template, locale, null, null);
	}
	
	public EmailTemplate(String subject, 
							String template, Locale locale, Map<String, Object> vars, 
							Collection<EmailAttachment> attachments) {
		
		Matcher match = TEMPLATE_PATTERN.matcher(template);
		if(!match.find()) throw new IllegalArgumentException("Template must be of the format: <file_name> or <file_name> :: <fragment>");
		
		this.file = match.group(1);
		this.fragment = match.group(2);
		
		this.subject = subject;
		
		this.context = new Context(locale, vars);
		this.attachments = attachments == null ? new HashSet<>() : attachments;
	}
	
	public EmailContent build(TemplateEngine engine, Map<String, Object> vars) {
		IContext context = new SubContext(this.context, vars);
		
		String subject = engine.process(this.subject, context);
		String content = engine.process(file, fragment == null ? null : Set.of(fragment), context);
		
		return new EmailContent(subject, content, attachments);
	}
}
