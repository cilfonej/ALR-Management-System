package edu.wit.alr.web.preprocessor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Set;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

public class NumberFormatDialect extends AbstractDialect implements IExpressionObjectDialect {
	private static final NumberFormatFactory FACTORY = new NumberFormatFactory();
	
	public NumberFormatDialect() {
		super("customNumberFormat");
	}

	public IExpressionObjectFactory getExpressionObjectFactory() { return FACTORY; }
	
	private static class NumberFormatFactory implements IExpressionObjectFactory {
	    private static final String EVALUATION_NAME = "NumberFormat";
	    private static final Set<String> EXPRESSION_OBJECT_NAMES = Set.of(EVALUATION_NAME);

	    public Set<String> getAllExpressionObjectNames() { 
	    	return EXPRESSION_OBJECT_NAMES;
	    }

	    public Object buildObject(IExpressionContext context, String expressionName) {
	        if(!EVALUATION_NAME.equals(expressionName)) return null;
	        return new PatternDecimalFormat(context.getLocale());
	    }

	    public boolean isCacheable(String expressionName) {
	        return expressionName != null && EVALUATION_NAME.equals(expressionName);
	    }
	}
	
	static class PatternDecimalFormat {
		private Locale locale;
		
		public PatternDecimalFormat(Locale locale) {
			this.locale = locale;
		}
		
		public String format(Double number, String format) {
			return new DecimalFormat(format, DecimalFormatSymbols.getInstance(locale)).format(number);
		}
	}
}
