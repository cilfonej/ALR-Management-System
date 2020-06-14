import Input from "./InputBase";

var DEFAULT_PARAMS = {
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
		
		// bind input event-listeners
		$ele.on("input", e => this.onInput(this.ele))
			.on("blur", e => this.onBlur(this.ele)); // <-- super definition 

		// perform initial update
		this.onInput(this.ele);
	}
	
	getValue() { 
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
		
		if(!param["allow-blank"] && /^\s*$/.test(value)) {
			this.setError('Please complete this field');
			return false;
		}
		
		if(value.length < param["min-length"]) {
			this.setError('Please enter at-least ' + param["min-length"] + ' characters');
			return false;
		}
		
		// clear error message
		this.setError(null);
		return true;
	}
}