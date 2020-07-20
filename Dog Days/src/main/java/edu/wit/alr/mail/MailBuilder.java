package edu.wit.alr.mail;

import java.util.Collection;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import edu.wit.alr.mail.content.EmailAttachment;
import edu.wit.alr.mail.content.EmailContent;
import edu.wit.alr.mail.content.EmailTemplate;
import edu.wit.alr.mail.sender.SenderInfo;

@Service
public class MailBuilder {
	
	@Autowired
	private MailConfig config;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	@Qualifier("mail")
	private TemplateEngine emailBuilder;
	
	public EmailContent buildTemplate(EmailTemplate template, Map<String, Object> vars, 
											Collection<EmailAttachment> attachments) throws MessagingException {
		
		EmailContent content = template.build(emailBuilder, vars);
		content.addAttachments(attachments);
		
		return content;
	}
	
	public void sendEmail(SenderInfo addressTo, EmailContent content) throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
		
		// if the sender does not have a from address, assign the default 
		if(addressTo.getFrom() == null)
			addressTo.setFrom(config.getDefaultFrom());
		
		addressTo.apply(helper);
		content.build(helper);
		
		try {
			mailSender.send(mimeMessage);
		} catch(MailException e) {
			// TODO: handle failed to send email
			throw e;
		}
	}
}
