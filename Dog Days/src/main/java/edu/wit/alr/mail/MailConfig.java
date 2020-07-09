package edu.wit.alr.mail;

import java.util.Properties;

import javax.validation.constraints.Email;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import edu.wit.alr.web.preprocessor.AddressHelperDialect;
import edu.wit.alr.web.preprocessor.NumberFormatDialect;

@Configuration
@ConfigurationProperties("alr.email")
public class MailConfig {
	
	/** Host-name and Port-number of SMTP mail-server */
	private String host;
	private int port;
	
	/** Email address used as the default "from" when sending emails */
	private String from = "ALR No-reply <no-reply@alr.com>";

	/** Sending account's mail-server login @See #host */
	private String username;
	private String password;

	@Bean
	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(host);
		mailSender.setPort(port);
		
		mailSender.setUsername(username);
		mailSender.setPassword(password);
		
		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "true");
		    
		return mailSender;
	}
	
	@Bean
	@Qualifier("mail")
	public SpringTemplateEngine mailTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(mailTemplateResolver());
        templateEngine.addDialect(new Java8TimeDialect());
        templateEngine.addDialect(new NumberFormatDialect());
        templateEngine.addDialect(new AddressHelperDialect());
        return templateEngine;
    }
	 
	@Bean
	public ITemplateResolver mailTemplateResolver() {
	    ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
	    resolver.setPrefix("mail/");
	    resolver.setSuffix(".html");
	    resolver.setTemplateMode(TemplateMode.HTML);
	    resolver.setCharacterEncoding("UTF-8");
	    resolver.setCacheable(false);
	    return resolver;
	}

// ***** AUTO-CONFIG SETTERS *****

	void setHost(String host) { this.host = host; }
	void setPort(int port) { this.port = port; }
	
	void setFrom(String from) { this.from = from; }
	
	void setUsername(String username) { this.username = username; }
	void setPassword(String password) { this.password = password; }
}
