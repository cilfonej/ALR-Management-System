package edu.wit.cilfonej.database;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mysql.cj.jdbc.MysqlDataSource;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:/edu/wit/cilfonej/database/database.properties")
public class DatabaseConfig {

	@Value("${database.user_access.username}")
	private String database_username;
	
	@Value("${database.user_access.password}")
	private String database_password;
	
	@Value("${spring.datasource.url}")
	private String database_url;

	@Bean("entityManagerFactory")
    public LocalSessionFactoryBean databaseSessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(new String[] { "edu.wit.cilfonej.database.model" });
        sessionFactory.setHibernateProperties(hibernateProperties());
 
        return sessionFactory;
    }

	@Bean
	@ConfigurationProperties("spring.datasource")
	public DataSource dataSource() {
		MysqlDataSource dataSource = new MysqlDataSource();
//		dataSource.setUrl(database_url);
		dataSource.setUser("root");
		dataSource.setPassword("admin");
		
		return dataSource;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		transactionManager.setSessionFactory(databaseSessionFactory().getObject());
		return transactionManager;
	}

	private final Properties hibernateProperties() {
		Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "update"); // ddl-auto validate 
		hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL55Dialect");
		
		System.setProperty("hibernate.dialect.storage_engine", "innodb");
		return hibernateProperties;
	}

}
