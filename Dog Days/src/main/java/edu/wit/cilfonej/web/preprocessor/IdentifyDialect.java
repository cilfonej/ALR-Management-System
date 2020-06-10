package edu.wit.cilfonej.web.preprocessor;

import java.util.Set;
import java.util.UUID;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

public class IdentifyDialect extends AbstractDialect implements IExpressionObjectDialect {
	private static final IdentifyFactory FACTORY = new IdentifyFactory();
	
	public IdentifyDialect() {
		super("identifier");
	}

	public IExpressionObjectFactory getExpressionObjectFactory() { return FACTORY; }
	
	private static class IdentifyFactory implements IExpressionObjectFactory {
	    private static final String EVALUATION_NAME = "UID";
	    private static final Set<String> EXPRESSION_OBJECT_NAMES = Set.of(EVALUATION_NAME);

	    public Set<String> getAllExpressionObjectNames() { 
	    	return EXPRESSION_OBJECT_NAMES;
	    }

	    public Object buildObject(IExpressionContext context, String expressionName) {
	        if(!EVALUATION_NAME.equals(expressionName)) return null;
	        return new IdentifyFormatter((String) context.getVariable("u_id"));
	    }

	    public boolean isCacheable(String expressionName) {
	        return false;
	    }
	}
	
	static class IdentifyFormatter {
		private String identifier;
		
		public IdentifyFormatter(String identifier) {
			this.identifier = identifier;
		}
		
		public String $() {
			// TODO: this is a bit much
			return UUID.randomUUID().toString();
		}
		
		public String $(String name) {
			if(identifier == null || identifier.isBlank()) {
				throw new IllegalStateException("Context does not contain a 'u_id' use: #UID.$() to generate a new one");
			}
			
			return identifier + name;
		}
	}
}
