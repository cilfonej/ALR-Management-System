package edu.wit.alr.web.preprocessor;

import java.util.Set;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

import edu.wit.alr.database.model.Address;

public class AddressHelperDialect extends AbstractDialect implements IExpressionObjectDialect {
	private static final NumberFormatFactory FACTORY = new NumberFormatFactory();
	
	public AddressHelperDialect() {
		super("customAddressHelper");
	}

	public IExpressionObjectFactory getExpressionObjectFactory() { return FACTORY; }
	
	private static class NumberFormatFactory implements IExpressionObjectFactory {
	    private static final String EVALUATION_NAME = "Addresses";
	    private static final Set<String> EXPRESSION_OBJECT_NAMES = Set.of(EVALUATION_NAME);

	    public Set<String> getAllExpressionObjectNames() { 
	    	return EXPRESSION_OBJECT_NAMES;
	    }

	    public Object buildObject(IExpressionContext context, String expressionName) {
	        if(!EVALUATION_NAME.equals(expressionName)) return null;
	        return new AddressHelper();
	    }

	    public boolean isCacheable(String expressionName) {
	        return expressionName != null && EVALUATION_NAME.equals(expressionName);
	    }
	}
	
	static class AddressHelper {
		public String stateName(Address address) {
			return stateName(address.getState());
		}
		
		public String stateName(String state) {
			if(state == null) return "";
			
			switch(state.toUpperCase()) {
				case "AL": return "Alabama";
				case "AK": return "Alaska";
				case "AZ": return "Arizona";
				case "AR": return "Arkansas";
				case "CA": return "California";
				case "CO": return "Colorado";
				case "CT": return "Connecticut";
				case "DE": return "Delaware";
				case "FL": return "Florida";
				case "GA": return "Georgia";
				case "HI": return "Hawaii";
				case "ID": return "Idaho";
				case "IL": return "Illinois";
				case "IN": return "Indiana";
				case "IA": return "Iowa";
				case "KS": return "Kansas";
				case "KY": return "Kentucky";
				case "LA": return "Louisiana";
				case "ME": return "Maine";
				case "MD": return "Maryland";
				case "MA": return "Massachusetts";
				case "MI": return "Michigan";
				case "MN": return "Minnesota";
				case "MS": return "Mississippi";
				case "MO": return "Missouri";
				case "MT": return "Montana";
				case "NE": return "Nebraska";
				case "NV": return "Nevada";
				case "NH": return "New Hampshire";
				case "NJ": return "New Jersey";
				case "NM": return "New Mexico";
				case "NY": return "New York";
				case "NC": return "North Carolina";
				case "ND": return "North Dakota";
				case "OH": return "Ohio";
				case "OK": return "Oklahoma";
				case "OR": return "Oregon";
				case "PA": return "Pennsylvania";
				case "RI": return "Rhode Island";
				case "SC": return "South Carolina";
				case "SD": return "South Dakota";
				case "TN": return "Tennessee";
				case "TX": return "Texas";
				case "UT": return "Utah";
				case "VT": return "Vermont";
				case "VA": return "Virginia";
				case "WA": return "Washington";
				case "WV": return "West Virginia";
				case "WI": return "Wisconsin";
				case "WY": return "Wyoming";
				
				default: return "";
			}
		}
		
		public String formatStateLine(Address address) {
			String city = address.getCity();
			String state = address.getState();
			
			return  (city != null ? city : "") + 
					(city != null && state != null ? ", " : "") + 
					(state != null ? state : "");
		}
		
		public String formatPostalLine(Address address) {
			String postal = address.getPostalCode();
			String country = address.getCountry();
			
			return  (postal != null ? postal : "") + 
					(postal != null && country != null ? ", " : "") + 
					(country != null ? country : "");
		}
	}
}
