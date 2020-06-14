package edu.wit.alr.database.util;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

public class IdGenerator implements IdentifierGenerator, Configurable {
	private static final Random RAND = new Random();
	private static final int MAX_SEARCH_ITERATIONS = 256;
	
	private static final int MIN_ID_SIZE = 3;
	private static final int MAX_ID_SIZE = (int) Math.floor(Math.log10(Integer.MAX_VALUE));
	private static final int DEFAULT_ID_SIZE = 6;
	
	private int id_size;
	private int id_min, id_limit;
	
	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
		id_size = Integer.parseInt(params.getProperty("id_size", String.valueOf(DEFAULT_ID_SIZE)));
		
		if(MIN_ID_SIZE > id_size || id_size > MAX_ID_SIZE) 
			throw new IllegalArgumentException("Dog-ID size excedes bounds! [" + MIN_ID_SIZE + ", " + MAX_ID_SIZE + "]");
		
		id_min = (int) Math.pow(10, id_size - 1);
		id_limit = (int) Math.pow(10, id_size) - id_min;
	}
	
	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		Connection connection = session.connection();
		
		try {
			int id;
			PreparedStatement query = connection.prepareStatement("SELECT id FROM Dog WHERE id = ? LIMIT 1");
			
			// limit the nuber of times we attempt to generate a unique ID
			for(int i = 0; i < MAX_SEARCH_ITERATIONS; i ++) {
				
				// pick a unique ID
				id = RAND.nextInt(id_limit) + id_min;
				
				// query for other record with same ID
				query.setInt(1, id);
				ResultSet results = query.executeQuery();

				// if no other records were found (w/ ID) return in 
				if(!results.next()) return id;
			}
		} catch(SQLException e) {
			throw new HibernateException(e);
		}
		
		throw new HibernateException("Exceeded max-search iteractions when generating Dog-ID");
	}
}