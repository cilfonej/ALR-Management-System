package edu.wit.alr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
	private static Config INSTANCE;

	@Bean
	public Config configDetails() {
		INSTANCE = new Config();
		return INSTANCE;
	}

	@Value("${config.country_code:US}")
	private String defaultCountryCode;

	public static String getDefaulCountryCode() { return INSTANCE.defaultCountryCode; }
}
