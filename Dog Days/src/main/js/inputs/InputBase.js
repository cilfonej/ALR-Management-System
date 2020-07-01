export default class InputBase {
	constructor(name) {
		if(typeof this.getValue !== 'function') throw new TypeError("Method 'getValue' has not ben defined");
		if(typeof this.setValue !== 'function') throw new TypeError("Method 'setValue' has not ben defined");
		
		if(typeof this.validate !== 'function') throw new TypeError("Method 'validate' has not ben defined");
		if(typeof this.setError !== 'function') throw new TypeError("Method 'setError' has not ben defined");
		if(typeof this.clear !== 'function') throw new TypeError("Method 'clear' has not ben defined");
	
		this.name = name;
	}
	
	setupElement(element) {}
	setupRevertButton(button) {}
	
	getField() {
		return this.name;
	}
	
	onBlur() {
		if(this.validate()) {
			this.fireChangeEvent(this.getValue());
			
		} else {
			this.fireChangeEvent('');
		}
	}
	
	addListener(listener) {
		if(typeof listener !== 'function') return;
		if(!this.listeners) this.listeners = [];
		this.listeners.push(listener);
	}
	
	fireChangeEvent(new_value) {
		if(!this.listeners) return;
		for(var listen of this.listeners) {
			listen(this, new_value);
		}
	}
};