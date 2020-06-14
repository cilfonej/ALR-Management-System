import Input from "./InputBase";

var DEFAULT_PARAMS = {
	"allow-multiple": false,
	"require": true
};

export default class ButtonGroupInput extends Input {
	constructor(name, params) {
		super(name);
		
		// clone DEFAULT_PARAMS and merge with given params
		var base_params = Object.assign({}, DEFAULT_PARAMS);
		// remove all 'null' entries from params
		$.each(params, (key, value) => (value === null && delete params[key], true));
		this.params = Object.assign(base_params, params);
	}
	
	setupElement(element) {
		var $inputs;
		var param = this.params || DEFAULT_PARAMS;
		
		var group = this;
		
		// run after DOM has loaded
		$(function() {
			UID.exe(element, function() {
				$inputs = $$("[data-toggle-init][data-form-input]");
				// remove the input-flag from all children
				$inputs.attr("[data-form-input]", null);
				
				var values = [];
				// ensure only one input per value
				$inputs.each((index, ele) => {
					var val = $(ele).val();
					
					if(values.includes(val)) {
						console.warn("Duplicate value in group [" + 
								$(group.ele).attr("data-form-input") + "]: " + val);
					} else {
						values.push(val);
					}
				});
				
				// setup single-selection on child-inputs
				if(!param["allow-multiple"]) {
					var option_selected = false;
					var onSelect = (input, val) => {
						// un-select all inputs in group
						$inputs.prop("checked", false);
						
						// force the selected element to stay selected
						$(input.ele).prop("checked", true);
					};
					
					$inputs.each((index, ele) => {
						var input = ele.input;
						input.addListener(onSelect);

						// force only one option to be selected
						input.getValue() && (option_selected = true) || $(ele).prop("checked", false);
						
					});
				}
			});
			
			group.wrapper = element;
			// record the list of input for later use
			group.children = $inputs;
		});
	}
	
	getValue() {
		var result = this.children
			.map((index, ele) => ele.input.getValue() && $(ele).val() || undefined)
			.get();
		
		return this.params["allow-multiple"] && result || (result.length > 0 && result[0] || null);
	}

	validate() {
		var param = this.params || DEFAULT_PARAMS;
		
		// if at-least 1 selection is required, check children for selected
		if(param["require"] && this.children.filter(":checked").length < 1) {
			this.setError("Please select " + (param["allow-multiple"] ? "at-least one " : "an ") + "option");
			return false;
		}

		this.setError(null);
		return true;
	}
	
	setError(msg) {
		if(msg) $(this.wrapper).addClass("input-error").attr("data-error", msg);
		else $(this.wrapper).removeClass("input-error").attr("data-error", msg);
	}
	
	clear() {
		this.children.prop("checked", false);
		this.setError(null);
	}
}

export class CheckboxInput extends Input {
	constructor(name) {
		super(name);
	}
	
	setupElement(element) {
		// extract DOM-Element and create jQuery object
		var $ele = $(element);
		if($ele.length < 1) throw "cannot setup empty-input";
		this.ele = $ele[0];
		
		$ele.attr("data-toggle-init", this.getValue());
		$ele.on("change", e => super.fireChangeEvent(this.getValue()));
	}
	
	// for lone check-box value is just its status
	getValue() {
		return $(this.ele).prop("checked");
	}

	// check-boxes cannot be invalid
	validate() { return true; }
	setError(msg) {}
	
	clear() {
		$(this.ele).prop("checked", false);
	}
}