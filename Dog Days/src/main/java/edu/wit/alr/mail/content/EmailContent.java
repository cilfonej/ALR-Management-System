package edu.wit.alr.mail.content;

import java.util.Collection;

import javax.mail.MessagingException;

import org.springframework.mail.javamail.MimeMessageHelper;

import edu.wit.alr.exceptions.ErrorUtils;

public class EmailContent {
	private String subject;
	private String body;
	
	private Collection<EmailAttachment> attachments;

	public EmailContent(String subject, String body, Collection<EmailAttachment> attachments) {
		this.subject = subject;
		this.body = body;
		this.attachments = attachments;
	}
	
	public void addAttachment(EmailAttachment attachment) {
		this.attachments.add(attachment);
	}
	
	public void addAttachments(Collection<EmailAttachment> attachments) {
		if(attachments == null) return;
		this.attachments.addAll(attachments);
	}
	
	public void build(MimeMessageHelper helper) throws MessagingException {
		helper.setSubject(subject);
		helper.setText(body, true);
		
		attachments.forEach(ErrorUtils.sneakyThrow((EmailAttachment a) -> a.attach(helper)));
	}
}
