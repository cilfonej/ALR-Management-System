import StarndardInput from "./StandardInput";
import LookupInput from "./LookupInput";
import GroupInput from "./GroupInput";
import ButtonGroupInput, {CheckboxInput} from "./ButtonGroupInput";
import AddressInput from "./AddressInput";

import RevertButton from "./revert/RevertButton";

window.Input = (function() {
	
	function setupInput(element, input_obj) {
		var $ele = $(element);
		$ele[0].input = input_obj;
		$ele.attr("data-form-input", input_obj.getField());
		
		input_obj.setupElement(element);
		
		return input_obj;
	}
	
//	======================================== Export ======================================== \\

	return {
		"setupInput": setupInput,
		
		"Std": StarndardInput,
		"Lookup": LookupInput,
		"Checkbox": CheckboxInput,

		"Group": GroupInput,
		"ButtonGroup": ButtonGroupInput,
		
		"AddressInput": AddressInput,
		
		"RevertButton": RevertButton
	};
})();