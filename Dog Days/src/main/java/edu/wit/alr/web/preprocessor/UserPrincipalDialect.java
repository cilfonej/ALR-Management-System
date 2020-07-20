package edu.wit.alr.web.preprocessor;

import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

import edu.wit.alr.web.security.UserPrincipal;

public class UserPrincipalDialect extends AbstractDialect implements IExpressionObjectDialect {
	private static final UserPrincipleAccessFactory FACTORY = new UserPrincipleAccessFactory();
	
	public UserPrincipalDialect() {
		super("customUserPrincipalAccess");
	}

	public IExpressionObjectFactory getExpressionObjectFactory() { return FACTORY; }
	
	private static class UserPrincipleAccessFactory implements IExpressionObjectFactory {
	    private static final String EVALUATION_NAME = "principal";
	    private static final Set<String> EXPRESSION_OBJECT_NAMES = Set.of(EVALUATION_NAME);

	    public Set<String> getAllExpressionObjectNames() { 
	    	return EXPRESSION_OBJECT_NAMES;
	    }

	    public Object buildObject(IExpressionContext context, String expressionName) {
	        if(!EVALUATION_NAME.equals(expressionName)) return null;
	        
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        Object principal = auth.getPrincipal();
	        
	        if(principal instanceof UserPrincipal) return (UserPrincipal) principal;
	        return null;
	    }

	    public boolean isCacheable(String expressionName) {
	        return false;
	    }
	}
}
