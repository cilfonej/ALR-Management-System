import Input from "./InputBase";
import GroupInput from "./GroupInput";

var DEFAULT_PARAMS = {
	"allow-multiple": false,
	"require": true
};

export default class ButtonGroupInput extends GroupInput {
	constructor(name, params) {
		super(name, params, DEFAULT_PARAMS);
	}
	
	setupChildren($inputs) {
		var param = this.params;
		
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
			$inputs.each((index, ele) => {
				var input = ele.input;
				// force only one option to be selected
				input.getValue() && (option_selected = true) || $(ele).prop("checked", false);
				
			});
		}
	}
	
	setupRevertChildren($inputs, button) {
		var onSelect = () => button.onInputChange();
		
		// on child value change, trigger revert-button state change
		$inputs.each((index, ele) => {
			var input = ele.input;
			input.addListener(onSelect);
		});
	}
	
	setValue(values) {
		// convert value to array
		values = !Array.isArray(values) && [values] || values;
		
		// if attempt to set before init is done
		if(!this.children) {
			var input = this;
			// queue the event up for later
			$(() => input.setValue(values));
			return;
		}
		
		this.children.each((index, ele) => {
			var $ele = $(ele);
			var val = $ele.val();
			
			// if values contains this options, then select it, otherwise un-check
			$ele.prop("checked", values.includes(val));
		});
	}
	
	fireChildEvent(input, value) {
		// if only one selection is allowed, update all options
		if(!this.params["allow-multiple"]) {
			// un-select all inputs in group
			this.children.prop("checked", false);
			
			// force the selected element to stay selected
			$(input.ele).prop("checked", true);
		}
		
		// chain-fire event
		super.fireChildEvent(input, value);
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
	
	setupRevertButton(button) {
		$ele.on("change", e => button.onInputChange());
	}
	
	setValue(val) {
		$(this.ele).prop("checked", String(val) == "true");
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