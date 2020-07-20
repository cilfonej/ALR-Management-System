package edu.wit.alr.mail.content;

import java.io.File;
import java.io.InputStream;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.MimeMessageHelper;

public class EmailAttachment {
	private String filename;
	private String cid;
	
	private DataSource dataSource;
	private InputStreamSource streamSource;

	private String contentType;
	
	private EmailAttachment(boolean inline, String name, InputStreamSource src, String contentType) {
		if(inline)
			this.cid = name;
		else
			this.filename = name;
		
		this.streamSource = src;
		this.contentType = contentType;
	}
	
	private EmailAttachment(boolean inline, String name, DataSource src) {
		if(inline)
			this.cid = name;
		else
			this.filename = name;
		
		this.dataSource = src;
	}
	
	// ****************************************** Builders ****************************************** \\
	
	// ============================== Attachments ============================== \\
	
	// ---------------------- Files ---------------------- \\
	public static EmailAttachment of(File file) {
		return of(file.getName(), file);
	}
	
	public static EmailAttachment of(String name, File file) {
		return of(name, new FileDataSource(file));
	}
	
	public static EmailAttachment of(String name, DataSource dataSource) {
		return new EmailAttachment(false, name, dataSource);
	}

	// ---------------------- Stream ---------------------- \\
	public static EmailAttachment of(String name, byte[] data, String contentType) {
		return of(name, new ByteArrayResource(data), contentType);
	}
	
	public static EmailAttachment of(String name, InputStream stream, String contentType) {
		return of(name, new InputStreamResource(stream), contentType);
	}
	
	public static EmailAttachment of(String name, InputStreamSource stream, String contentType) {
		return new EmailAttachment(false, name, stream, contentType);
	}

	// ============================== Inlines ============================== \\
	
	// ---------------------- Files ---------------------- \\
	public static EmailAttachment inline(File file) {
		return inline(file.getName(), file);
	}
	
	public static EmailAttachment inline(String name, File file) {
		return inline(name, new FileDataSource(file));
	}
	
	public static EmailAttachment inline(String name, DataSource dataSource) {
		return new EmailAttachment(true, name, dataSource);
	}

	// ---------------------- Stream ---------------------- \\
	public static EmailAttachment inline(String name, byte[] data, String contentType) {
		return inline(name, new ByteArrayResource(data), contentType);
	}
	
	public static EmailAttachment inline(String name, InputStream stream, String contentType) {
		return inline(name, new InputStreamResource(stream), contentType);
	}
	
	public static EmailAttachment inline(String name, InputStreamSource stream, String contentType) {
		return new EmailAttachment(true, name, stream, contentType);
	}

	// ****************************************** Actions ****************************************** \\
	
	public boolean isInline() { return cid != null; }
	
	public void attach(MimeMessageHelper builder) throws MessagingException {
		if(dataSource != null) {
			if(cid != null) builder.addInline(cid, dataSource);
			else builder.addAttachment(filename, dataSource);
		} else {
			if(cid != null) builder.addInline(cid, streamSource, contentType);
			else builder.addAttachment(filename, streamSource, contentType);
		}
	}
}
