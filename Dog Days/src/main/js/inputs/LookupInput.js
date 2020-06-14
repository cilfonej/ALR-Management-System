import Input from "./InputBase";

export default class LookupInput extends Input {
	
	constructor(id, jsx) {
		super(id);
		this.jsx = jsx;
		
		jsx.addEventListener("input_change", value => super.fireChangeEvent(value));
	}
	
	getValue() {
		return this.jsx.getValue();
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