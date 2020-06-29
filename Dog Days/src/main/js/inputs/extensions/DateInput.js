var FORMATS = ["M-D-YYYY", "D-MMM-YYYY", "D-MMMM-YYYY", "YYYY-MM-DD", "MMM-D-YYYY", "MMMM-D-YYYY"];

function sanitizeValue(value) {
	value = value.trim();
	value = value.replace(/[, \.-/]/gi, '-');
	
	// run twice to remove double-spaces from round 1
	value = value.replace(/(--)/gi, '-');
	value = value.replace(/(--)/gi, '-');
	
	return value;
}

export default {
	setupInput: function(input) {
		var name = input.getField() + '_datepicker';
		var cal_jsx = $jsx(name);

		var $cal = $$(".form-input_datepicker").hide();
		var $ele = $(input.ele);

		// grab current-UID
		var uid = UID.peekUID();
		
		// create function to detect clicks outside of form-input
		function attemptClose(event) {
			var $target = $(event.target);
			var ele = $target.closest(".form-input[data-uid='" + uid + "']");

			// if click not on this element, hide-cal and remove listener
			ele.length <= 0 && close_cal();
		}
		
		var close_cal = () => ($cal.hide(), $(window).off("click", attemptClose));
		
		// create popper element for calendar
		var cal_popper = Popper.createPopper(input.ele, $cal[0], {
			placement: 'bottom',
			
			modifiers: [
				{
					name: 'offset',
					options: {
						offset: ['50%', 10]
					}
				}
		]});
		
		// detect calendar value change
		cal_jsx.addEventListener("input", function(e) {
			$ele.val(e.value && e.value.format("MM/DD/YYYY") || "");
			close_cal()
		});
		
		// show calendar on focus
		$ele.on("focusin", e => {
			$cal.show();
			cal_popper.forceUpdate();
			$(window).on("click", attemptClose);
			
		// on enter, close calendar
		}).on("keypress", e => {
			(e.which === 13 || e.which === 27) && close_cal();
			e.which === 13 && input.validate();
			return true;
		});
	},
	
	setupRevertButton: function(input, button) {
		var name = input.getField() + '_datepicker';
		
		// detect calendar value change
		$jsx(name).addEventListener("revert_button", function(e) {
			button.onInputChange();
		});
	},
	
	getValue: function(input) {
		var value = $(input.ele).val();
		value = sanitizeValue(value);
		
		return moment(value, FORMATS, true).toDate();
	},
	
	onInput: function(input) {
		
	},
	
	validate: function(input) {
		var value = $(input.ele).val();
		value = sanitizeValue(value);
		
		var date = moment(value, FORMATS, true);
		
		if(!date.isValid()) {
			input.setError("Unable to parse date");
			return false;
		}
		
		return true;
	}
};