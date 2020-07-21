var Form = (function() {
	function getInputs(form_element) {
		var inputs;
		
		UID.exe(form_element, () => {
			inputs = $$("[data-form-input]")
				.map((index, ele) => ele.input || undefined)
				.get();
		});
		
		return inputs;
	}
	
//	======================================== Submit ======================================== \\
	function submit(form_element) {
		var $form = $(form_element);
		if($form.length < 1) throw "Missing parameter: <element> form";
		form_element = $form[0];

		var url = $form.attr("data-form");
		if(!url) throw "Invalid Form: missing [data-form=<url>] attribute";
		
		// validate form data
		if(validate(form_element)) {
			
			var form_data = {};
			// extract form data into JSON object
			for(var input of getInputs(form_element)) {
				if(!input.isVisible()) continue;
				form_data[input.getField()] = input.getValue();
			}
			
			// TODO: Add form-object, allow for extra methods (onClose, beforeSubmit, ect.) and remove this line \/
			try { MicroModal.close(); } catch(ex) {}
			
			var config = {};
			if($form.attr("data-form-plain")) {
				config = {
					contentType: 'application/x-www-form-urlencoded',
					raw_data: true
				};
			}
			
			Request.send(url, form_data, config);
			
		} else {
			// TODO: ???
			alert("invalid");
		}
	}
	
//	======================================== Validate ======================================== \\
	function validate(form_element) {
		var inputs = getInputs(form_element);
		var invalid = false;

		for(var input of inputs) {
			if(!input.isVisible()) continue;
			// check if input is NOT valid
			invalid = !input.validate() || invalid;
		}
		
		return !invalid;
	}
	
//	======================================== Export ======================================== \\
	
	return {
		"submit": submit,
	};
})();