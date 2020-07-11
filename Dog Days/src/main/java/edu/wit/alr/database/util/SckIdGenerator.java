package edu.wit.alr.database.util;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.core.annotation.AnnotationUtils;

public class SckIdGenerator implements IdentifierGenerator {
	private static final Random RAND = new Random();
	
	private static final String DEFAULT_SEQUENCE_NAME = "sck_id_sequence";
	private static final int MAX_SEARCH_ITERATIONS = 256;
	
	private static final int BASE_ID_SIZE = 6; 
	private static final int TYPE_MTPL = (int) Math.pow(10, BASE_ID_SIZE); 
	private static final int MIN_ID = (int) Math.pow(10, BASE_ID_SIZE - 1); 
	private static final int MAX_ID = TYPE_MTPL - 1; 
	
	private static final SckidConfig DEFAULT_CONFIG = AnnotationUtils.synthesizeAnnotation(SckidConfig.class);
	private static final Map<Class<?>, SckidConfig> CONFIG_CACHE = new HashMap<>();
	
	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		Connection connection = session.connection();
		
		SckidConfig config = CONFIG_CACHE.computeIfAbsent(object.getClass(), 
				clazz -> Optional.ofNullable(clazz.getAnnotation(SckidConfig.class)).orElse(DEFAULT_CONFIG));
		
		int prefix = config.prefix();
		// if prefix == -1, pick number [1, 9]
		if(prefix < 0) prefix = RAND.nextInt(8) + 1;
		prefix *= TYPE_MTPL;
		
		try {
			int id;
			PreparedStatement query = connection.prepareStatement("SELECT id FROM " + DEFAULT_SEQUENCE_NAME + " WHERE id = ? LIMIT 1");
			Statement statement = connection.createStatement();
			boolean failedOnce = false;
			
			// limit the number of times we attempt to generate a unique ID
			for(int i = 0; i < MAX_SEARCH_ITERATIONS; i ++) {
				
				// pick a unique ID
				id = RAND.nextInt(MAX_ID - MIN_ID) + MIN_ID + prefix;
				
				try { 
					// query for other record with same ID
					query.setInt(1, id);
					ResultSet results = query.executeQuery();

					// if no other records were found (w/ ID) return in 
					if(!results.next()) {
						statement.executeUpdate("INSERT INTO " + DEFAULT_SEQUENCE_NAME + " VALUES (" + id + ")");
						return id;
					}
					
				} catch(SQLException e) {
					if(failedOnce) throw e;
					// if this is the first time we try the query, the table might be missing, so try creating it
					failedOnce = true;
					statement.execute("CREATE table " + DEFAULT_SEQUENCE_NAME + " (id INT NOT NULL, UNIQUE INDEX id_UNIQUE (id ASC))");
				}
			}
		} catch(SQLException e) {
			throw new HibernateException(e);
		}
		
		throw new HibernateException("Exceeded max-search iteractions when generating ID");
	}
}