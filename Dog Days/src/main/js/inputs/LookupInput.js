import Input from "./InputBase";

export default class LookupInput extends Input {
	
	constructor(id, jsx) {
		super(id);
		this.jsx = jsx;
		
		jsx.addEventListener("input_change", value => super.fireChangeEvent(value));
	}
	
	setupRevertButton(button) {
		var skip = true; // flag used to skip the first event from JSX (due to setting to default)
		this.jsx.addEventListener("revert_button", value => skip && !(skip = false) || button.onInputChange());
	}
	
	addListener(listener) {
		var id = Math.floor(Math.random() * 10000);
		this.jsx.addEventListener("listener_" + id, value => this.fireChangeEvent(this, value));
	}
	
	setValue(val) {
		this.jsx.setSelectedValue(val);
	}
	
	getValue() {
		return {
			"option": this.jsx.getValue()
		};
	}

	validate() {
		return this.jsx.validate();
	}
	
	setError(msg) {
		this.jsx.setError(msg);
	}
	
	clear() {
		this.jsx.clear();
	}
}