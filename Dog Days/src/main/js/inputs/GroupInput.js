import 'regenerator-runtime/runtime'; // resolves "regeneratorRuntime is not defined"
import Input from "./InputBase";

var groupsQueue = [];
// rough way to alter the order that groups load
async function setupGroups() {
	while(groupsQueue.length) {
		var group = groupsQueue.shift();
		
		// check if the group has finished loading
		if(await !group.next().done) {
			// if not, add it to the back of the queue
			groupsQueue.push(group);
		}
	}
}

// after DOM loads, evaluate the groups
function scheduleGroupLoad(generator) {
	// if the queue is empty, schedule a group-load after DOM loads
	!groupsQueue.length && $(function() { setupGroups(); });
	
	groupsQueue.push(generator());
}

export default class GroupInput extends Input {
	constructor(name, params, default_params) {
		super(name);
		
		// clone DEFAULT_PARAMS and merge with given params
		var base_params = Object.assign({}, default_params);
		// remove all 'null' entries from params
		$.each(params, (key, value) => (value === null && delete params[key], true));
		this.params = Object.assign(base_params, params);
	}

	// do not override, use "setupChildren($children)"
	setupElement(element) {
		var group = this;
		
		// delay group loading
		scheduleGroupLoad(function* () {
			yield; // wait for load cycle to start
			
			var $children = $();
			var param = group.params;
			
			// load children
			search: while(true) {
				var $inputs;
				
				// query remaining form-inputs inside of the group
				UID.exe(element, () => $inputs = $$("[data-form-input]"));
				
				// for each input
				for(var ele of $inputs) {
					var $ele = $(ele);
					var input = ele.input;
					
					// if the input is a group, and it has not yet claimed it's children
					if(input instanceof GroupInput && typeof input.children === 'undefined') {
						yield; 			 // pause execution
						continue search; // restart search
					}

					// chain events from children
					input.addListener((child, value) => group.fireChildEvent(child, value));
					
					// remove the input-flag from all children
					$ele.attr("data-form-input", null);
					// replace the input-flag with a group-input flag
					$ele.attr("data-group-input", input.getField());
					
					// add element to list of children
					$children = $children.add($ele);
				}
				
				// if we exit the for-loop, then all inputs processed
				break;
			}
			
			UID.exe(element, function() {
				// attempt to setup children
				if(typeof group.setupChildren === 'function')
					group.setupChildren($children);
				
				// if a revert button was added, attempt to perform setup
				if(group.rev_button) 
					group.setupRevertChildren($children, group.rev_button);
			});

			// record the list of input for later use
			group.children = $children;
			group.wrapper = element;
		});
	}
	
	// do not override, use "setupRevertChildren($children, button)"
	setupRevertButton(button) {
		// setup cannot happen yet, must wait till children/DOM is loaded
		this.rev_button = button;
	}
	
	setupRevertChildren($inputs, button) {
		// DEFAULT *** implementation 
		
		var onSelect = () => button.onInputChange();
		// on child value change, trigger revert-button state change
		$inputs.each((index, ele) => ele.input.addListener(onSelect));
		
		// enable the revert-button
		button.onInputChange();
		// now that the group is initialized, we can "reset" the value
		button.revertFields();
	}
	
	fireChildEvent(input, value) {
		// DEFAULT *** implementation
		this.fireChangeEvent({ src: input, val: value})
	}
	
	// cannot set an value of a group, by default
	setValue(val) { }

	getValue() {
		// DEFAULT *** implementation
		
		var data = {};
		$.each(this.children, (index, ele) => {
			var input = ele.input;
			data[input.getField()] = input.getValue();
		});
		
		return data;
	}

	validate() {
		// DEFAULT *** implementation
		
		var invalid = false;
		$.each(this.children, function(index, ele) { invalid = !ele.input.validate() || invalid });
		
		return !invalid;
	}
	
	// cannot set an error message on a group, by default
	setError(msg) { }
	
	clear() {
		// DEFAULT *** implementation
		
		$.each(this.children, (index, ele) => ele.input.clear());
		this.setError(null);
	}
}