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
					input.addListener(value => group.fireChangeEvent(this, { src: input, val: value}));
					
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
				if(group.rev_button && typeof group.setupRevertChildren === 'function')
					group.setupRevertChildren($children, group.rev_button);
			});

			// record the list of input for later use
			group.children = $children;
			group.wrapper = element;
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