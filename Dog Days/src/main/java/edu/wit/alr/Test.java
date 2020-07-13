package edu.wit.alr;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
@PropertySource("classpath:secret.yaml")
public class Test {
	public static void main(String[] args) throws IOException {
		SpringApplication.run(Test.class, args);
	}
}
