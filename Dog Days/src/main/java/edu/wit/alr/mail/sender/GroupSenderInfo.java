package edu.wit.alr.mail.sender;

import java.util.HashSet;
import java.util.Set;

import javax.mail.MessagingException;
import javax.validation.constraints.NotEmpty;

import org.springframework.mail.javamail.MimeMessageHelper;

public class GroupSenderInfo extends SenderInfo {
	private Set<String> to;
	
	private Set<String> cc;
	private Set<String> bcc;
	
	public GroupSenderInfo(String from, @NotEmpty Set<String> to) {
		super(from);
		this.to = to;
		
		this.cc = new HashSet<>();
		this.bcc = new HashSet<>();
	}
	
	public void apply(MimeMessageHelper helper) throws MessagingException {
		helper.setFrom(super.getFrom());
		
		helper.setTo(to.toArray(size -> new String[size]));
		helper.setCc(cc.toArray(size -> new String[size]));
		helper.setBcc(bcc.toArray(size -> new String[size]));
	}
	
	public void addCC(String cc) {
		this.cc.add(cc);
	}
	
	public void addCC(Set<String> cc) {
		this.cc.addAll(cc);
	}
	
	public void addBCC(String bcc) {
		this.bcc.add(bcc);
	}
	
	public void add(Set<String> bcc) {
		this.bcc.addAll(bcc);
	}
}
