package edu.wit.alr.mail.sender;

import java.util.Set;

import javax.mail.MessagingException;
import javax.validation.constraints.NotEmpty;

import org.springframework.mail.javamail.MimeMessageHelper;

public class IndividualSenderInfo extends SenderInfo {
	private Set<String> to;

	public IndividualSenderInfo(String from, @NotEmpty Set<String> to) {
		super(from);
		this.to = to;
	}

	public void apply(MimeMessageHelper helper) throws MessagingException {
		helper.setFrom(super.getFrom());
		helper.setTo(to.toArray(size -> new String[size]));
	}
}
