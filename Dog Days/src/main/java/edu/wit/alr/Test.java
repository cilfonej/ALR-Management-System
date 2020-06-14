package edu.wit.alr;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class Test {
	public static void main(String[] args) throws IOException {
		ApplicationContext context = SpringApplication.run(Test.class, args);
		context.getBean(Demo.class).add();
	}
}
