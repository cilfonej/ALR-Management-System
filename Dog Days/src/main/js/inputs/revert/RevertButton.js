import StandardType from "./StandardRevert";
import AddressType from "./AddressRevert";

var types = {
	"std": StandardType,
	"address": AddressType
};

export default class RevertButton {
	constructor(element, fields, values, types) {

		// ensure fields/values are valid
		if(typeof fields === 'undefined') throw "invalid set of fields";
		if(typeof values === 'undefined') throw "invalid set of values";
		
		// covert fields/values to arrays
		fields = !Array.isArray(fields) && [fields] || fields;
		values = !Array.isArray(values) && [values] || values;
		types && (types = !Array.isArray(types) && [types] || types);
		
		// make sure they are of the same size
		if(fields.length != values.length) throw "not enough fields/values provided";
		
		// store data into a map of "field-name": "value"
		this.map = {};
		for(var i = 0; i < fields.length; i ++) {
			this.map[fields[i]] = {
				value: values[i],
				type: types && types[i] || 'std'
			}
		}
		
		// extract DOM-Element and create jQuery object
		var $ele = $(element);
		if($ele.length < 1) throw "cannot setup empty-input";
		this.ele = $ele[0];
		
		this.ele.button = this;
		$ele.on("click", e => this.revertFields());
		
		// setup change detection on input
		this.bindInputChanges();
		
		// revert to default-value by default
		this.revertFields();
	}
	
	bindInputChanges() {
		var $ele = $(this.ele);
		var $form = $ele.closest("[data-form]");
		
		UID.exe($form, () => {
			for(var field in this.map) {
				var config = this.map[field];
				
				// perform special input-binding based on type
				var type = types[config.type];
				if(!type) {
					console.warn("Unknown RevertButton type: " + config.type);
					continue;
				}
				
				type.bindInputs(this, field, config.value);
			}
		});
	}
	
	revertFields() {
		var $ele = $(this.ele);
		var $form = $ele.closest("[data-form]");
		
		// button is disabled, ignore click-event
		if($ele.attr("disabled")) return;
		
		UID.exe($form, () => {
			for(var field in this.map) {
				var config = this.map[field];
				
				// perform special revert-function based on type
				var type = types[config.type];
				type && type.revertField(this, field, config.value);
			}
		});
		
		// disable button on revert-to-default
		$ele.attr("disabled", true).addClass("disabled");
	}
	
	onInputChange() {
		var $ele = $(this.ele);
		// enable button once the input has changed
		$ele.attr("disabled", null).removeClass("disabled");
	}
}