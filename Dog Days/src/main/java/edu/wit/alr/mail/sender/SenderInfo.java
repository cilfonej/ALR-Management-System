package edu.wit.alr.mail.sender;

import javax.mail.MessagingException;

import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.MimeMessageHelper;

public abstract class SenderInfo {
	private String from;
	
	public SenderInfo(String from) {
		this.from = from;
	}
	
	public abstract void apply(MimeMessageHelper helper) throws MessagingException;
	
	public String getFrom() {
		return from;
	}
	
	public void setFrom(@NonNull String from) {
		this.from = from;
	}
}
