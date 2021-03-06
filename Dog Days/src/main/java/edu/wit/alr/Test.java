package edu.wit.alr;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

import edu.wit.alr.config.YamlPropertyLoaderFactory;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
@EnableConfigurationProperties
@PropertySource(value = "classpath:/secret.yaml", factory = YamlPropertyLoaderFactory.class)
public class Test {
	public static void main(String[] args) throws IOException {
		SpringApplication.run(Test.class, args);
	}
}
