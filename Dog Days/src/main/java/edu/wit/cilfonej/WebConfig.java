package edu.wit.cilfonej;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import edu.wit.cilfonej.web.preprocessor.IdentifyDialect;
import edu.wit.cilfonej.web.preprocessor.NumberFormatDialect;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry
//			.addResourceHandler("/webjars/**")
//			.addResourceLocations("classpath:/META-INF/resources/webjars/");
//	}
	
	@Bean
	public ConfigurableServletWebServerFactory webServerFactory() throws UnknownHostException {
	    JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
	    factory.setPort(9000);
	    factory.setAddress(Inet4Address.getByName("192.168.1.10")); // 192.168.1.10
	    factory.setContextPath("");
	    factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/notfound.html"));
	    
	    return factory;
	}
	
	@Bean
	public ViewResolver viewResolver() {
	    ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
	    viewResolver.setTemplateEngine(templateEngine());
	    viewResolver.setContentType("text/html");
	    return viewResolver;
	}
	
	@Bean
	public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(htmlTemplateResolver());
        templateEngine.addDialect(new Java8TimeDialect());
        templateEngine.addDialect(new NumberFormatDialect());
        templateEngine.addDialect(new IdentifyDialect());
        return templateEngine;
    }
	 
	@Bean
	public ITemplateResolver htmlTemplateResolver() {
	    ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
	    resolver.setPrefix("templates/");
	    resolver.setSuffix(".html");
	    resolver.setTemplateMode(TemplateMode.HTML);
	    resolver.setCharacterEncoding("UTF-8");
	    resolver.setCacheable(false);
	    return resolver;
	}
}
