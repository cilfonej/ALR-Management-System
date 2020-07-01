export default class RevertButton {
	constructor(element, fields, values) {

		// ensure fields/values are valid
		if(typeof fields === 'undefined') throw "invalid set of fields";
		if(typeof values === 'undefined') throw "invalid set of values";
		
		// covert fields/values to arrays
		fields = !Array.isArray(fields) && [fields] || fields;
		values = !Array.isArray(values) && [values] || values;
		
		// make sure they are of the same size
		if(fields.length != values.length) throw "not enough fields/values provided";
		
		// store data into a map of "field-name": "value"
		this.map = {};
		for(var i = 0; i < fields.length; i ++) {
			this.map[fields[i]] = values[i];
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
		
		for(var field in this.map) {
			var $input = $form.find("[data-form-input='"+field+"'], [data-group-input='"+field+"']");
			if($input.length < 1) {
				console.warn("Could not find field: " + field);
				continue;
			}
			
			var input = $input[0].input
			if(!input) {
				console.warn("Field '" + field  + "' is missing *.input on element");
				continue;
			}
			
			input.setupRevertButton(this);
			input.setValue(this.map[field]);
		}
	}
	
	revertFields() {
		var $ele = $(this.ele);
		var $form = $ele.closest("[data-form]");
		
		// button is disabled, ignore click-event
		if($ele.attr("disabled")) return;
		
		for(var field in this.map) {
			var $input = $form.find("[data-form-input='"+field+"'], [data-group-input='"+field+"']");
			if($input.length < 1) {
				console.warn("Could not find field: " + field);
				continue;
			}
			
			var input = $input[0].input
			if(!input) {
				console.warn("Field '" + field  + "' is missing *.input on element");
				continue;
			}
			
			input.setValue(this.map[field]);
		}
		
		// disable button on revert-to-default
		$ele.attr("disabled", true).addClass("disabled");
	}
	
	onInputChange() {
		var $ele = $(this.ele);
		// enable button once the input has changed
		$ele.attr("disabled", null).removeClass("disabled");
	}
}