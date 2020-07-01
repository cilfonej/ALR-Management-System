import Input from "./InputBase";

export default class GroupInput extends Input {
	constructor(name, params, default_params) {
		super(name);
		
		// clone DEFAULT_PARAMS and merge with given params
		var base_params = Object.assign({}, default_params);
		// remove all 'null' entries from params
		$.each(params, (key, value) => (value === null && delete params[key], true));
		this.params = Object.assign(base_params, params);
	}
	
	setupElement(element) {
		var _this = this;
		
		// run after DOM has loaded
		$(function() {
			var $inputs;
			var param = _this.params;
			
			UID.exe(element, function() {
				$inputs = $$("[data-form-input]");
				// remove the input-flag from all children
				$inputs.attr("data-form-input", null);
				$inputs.attr("data-group-input", $inputs[0].input.getField());
				
				if(typeof _this.setupChildren === 'function')
					_this.setupChildren($inputs);
				
				if(_this.rev_button && typeof _this.setupRevertChildren === 'function')
					_this.setupRevertChildren($inputs, _this.rev_button);
			});

			// record the list of input for later use
			_this.children = $inputs;
			_this.wrapper = element;
		});
	}
	
	setupRevertButton(button) {
		// setup cannot happen yet, must wait till children/DOM is loaded
		this.rev_button = button;
	}
	
	// cannot set an value of a group, by default
	setValue(val) { }
	
	getValue() {
		var data = {};
		$.each(this.children, (index, ele) => {
			var input = ele.input;
			data[input.getField()] = input.getValue();
		});
		
		return data;
	}

	validate() {
		var invalid = false;
		$.each(this.children, (index, ele) => invalid = !ele.input.validate() || invalid);
		
		return !invalid;
	}
	
	// cannot set an error message on a group, by default
	setError(msg) { }
	
	clear() {
		$.each(this.children, (index, ele) => ele.input.clear());
		this.setError(null);
	}
}