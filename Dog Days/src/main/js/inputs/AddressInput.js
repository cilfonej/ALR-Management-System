import GroupInput from "./GroupInput";

var DEFAULT_PARAMS = {
	"require": true
};

export default class AddressGroupInput extends GroupInput {
	constructor(name, params) {
		super(name, params, DEFAULT_PARAMS);
	}
	
	setupChildren($children) {
		var param = this.params;
		
		this.fields = {
			"street": $children.filter("[data-group-input='street']")[0].input,
			
			"city": $children.filter("[data-group-input='city']")[0].input,
			"state": $children.filter("[data-group-input='state']")[0].input,
			
			"zipcode": $children.filter("[data-group-input='zipcode']")[0].input,
			"country": $children.filter("[data-group-input='country']")[0].input,	
		};
	}
	
	setValue(value) {
		if(!this.fields) return;
		
		this.fields["street"].setValue(value && value.streetAddress || '');
		
		this.fields["city"].setValue(value && value.city || '');        
		this.fields["state"].setValue(value && value.state || '');       
		
		this.fields["zipcode"].setValue(value && value.postalCode || '');  
		this.fields["country"].setValue(value && value.country || '');     
	}
	
	getValue() {
		// if the input is blank, return null
		var hasStreet = !/^\s*$/.test(this.fields["street"].getValue());
		if(!hasStreet) return null;
		
		return super.getValue();
	}
	
	validate() {
		var param = this.params || DEFAULT_PARAMS;
		
		// propagate "required" property to fields
		// NOTE: we only care about [street, state, city] the reset are up to the implementer
		this.fields["street"].params["allow-blank"] = !param["require"];
		this.fields["city"].params["allow-blank"] = !param["require"];
		this.fields["state"].params["allow-blank"] = !param["require"];
		
		// validate all fields, for valid input
		var valid = super.validate();
		// if invalid stop here
		if(!valid) return false;
		
		// only if the input is optional
		if(!param["require"]) {
			// check which fields have a value
			var hasStreet = !/^\s*$/.test(this.fields["street"].getValue());
			var hasCity = !/^\s*$/.test(this.fields["city"].getValue());
			var hasState = !/^\s*$/.test(this.fields["state"].getValue());
			
			// if they are not all blank/populated, add error
			if(hasStreet != hasCity || hasCity != hasState) {
				if(!hasStreet) this.fields["street"].setError("Incomplete Address; enter a value");
				if(!hasCity) this.fields["city"].setError("Incomplete Address; enter a value");
				if(!hasState) this.fields["state"].setError("Incomplete Address; enter a value");
				
				return false;
			}
		}

		return true;
	}
}