import Input from "./InputBase";

import DateType from "./extensions/DateInput";
import NumberType from "./extensions/NumberInput";

var types = {
	"date": DateType,
	"number": NumberType,
	"decimal": NumberType,
};

var DEFAULT_PARAMS = {
	"type": 'text',
	
	"min-length": 0,
	"allow-blank": true
};

export default class StandardInput extends Input {
	
	constructor(name, params) {
		super(name);
		
		// clone DEFAULT_PARAMS and merge with given params
		var base_params = Object.assign({}, DEFAULT_PARAMS);
		// remove all 'null' entries from params
		$.each(params, (key, value) => (value === null && delete params[key], true));
		this.params = Object.assign(base_params, params);
	}
	
	setupElement(element) {
		// extract DOM-Element and create jQuery object
		var $ele = $(element);
		if($ele.length < 1) throw "cannot setup empty-input";
		this.ele = $ele[0];
		
		// locate counter element (if it exists)
		this.counter = $ele.siblings(".form-input_counter");
		var input = this;
		
		// perform special setup based on input-type
		UID.exe(input.ele, () => {
			var type = types[this.params['type']]
			type && type.setupInput && type.setupInput(this);
		});
		
		// bind input event-listeners
		$ele.on("input", e => this.onInput(this.ele))
			.on("blur", e => this.onBlur(this.ele)); // <-- super definition 

		// perform initial update
		this.onInput(this.ele);
	}
	
	setupRevertButton(button) {
		var $ele = $(this.ele);
		$ele.on("input", e => button.onInputChange());
		
		// perform special setup based on input-type
		UID.exe(this.ele, () => {
			var type = types[this.params['type']]
			type && type.setupRevertButton && type.setupRevertButton(this, button);
		});
	}
	
	setValue(val) {
		// perform special value-assignment based on input-type
		var type = types[this.params['type']]
		if(type && type.setValue) type.setValue(this, val);
		// default-value
		else $(this.ele).val(val); 
		
		// after assigning-value, update and validate the input
		this.onInput();
		this.validate();
	}
	
	getValue() { 
		// perform special value-generation based on input-type
		var type = types[this.params['type']]
		if(type && type.getValue) return type.getValue(this);
		
		// default-value
		return $(this.ele).val(); 
	}

	setError(msg) {
		if(msg) $(this.ele).closest(".form-input").addClass("input-error").attr("data-error", msg);
		else $(this.ele).closest(".form-input").removeClass("input-error").attr("data-error", msg);
	}
	
	clear() {
		$(this.ele).val('');
		this.setError(null);
	}
	
	onInput() {
		var $ele = $(this.ele);
		var param = this.params;
		
		// if there's a max-length
		if(param["max-length"]) {
			var max = param["max-length"];
			var val = $ele.val();
			
			// if value is longer then max-length
			if(val.length > max) {
				// reduce to only [max-length]
				$ele.val(val.substring(0, max));
			}
		}

		// perform special input-filtering based on input-type
		var type = types[this.params['type']]
		type && type.validate && type.onInput(this);
		
		// --- ALL VISUAL UPDATE AFTER HERE ---
		
		var counter = this.counter;
		// if there is an element counter
		if(counter && counter.length > 0) {
			var len = $ele.val().length;
			var max = param["max-length"];
			counter.html(len + "&thinsp;/&thinsp;" + max);
		}
	}
	
	validate() {
		var $ele = $(this.ele);
		var param = this.params || DEFAULT_PARAMS;
		
		var value = $ele.val() || '';
		
		var isBlank = /^\s*$/.test(value);
		var allowBlank = param["allow-blank"];
		
		// short-cut validity, if blank and allowing blank, done
		if(isBlank && allowBlank) {
			// clear error message
			this.setError(null);
			return true;
		}
		
		if(!allowBlank && isBlank) {
			this.setError('Please complete this field');
			return false;
		}
		
		if(value.length < param["min-length"]) {
			this.setError('Please enter at-least ' + param["min-length"] + ' characters');
			return false;
		}
		
		// perform special validation based on input-type
		var type = types[this.params['type']]
		if(type && type.validate && !type.validate(this)) {
			return false;
		}
		
		// clear error message
		this.setError(null);
		return true;
	}
}
