// Source From: https://github.com/adamwdraper/Numeral-js/issues/170#issuecomment-337332434
numeral.register('format', 'phone', {
	regexps: {
		format: /\+?N?[-. ]?\(?NNN\)?[-. ]?NNN[-. ]?NNNN/
	},
	
	format: function(value, formatString) {
		function normalize(phoneNumber) {
			if(phoneNumber == null) return "";
			return phoneNumber.toString().replace(
					/^[\+\d{1,3}\-\s]*\(?([0-9]{3})\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})$/,
					"$1$2$3");
		}

		function format(phoneNumber, formatString) {
			phoneNumber = normalize(phoneNumber);

			for(var i = 0; i < phoneNumber.length; i ++) {
				formatString = formatString.replace("N", phoneNumber[i]);
			}
			
			return formatString.replace(/N/g, ' ');
		}

		return format(value, formatString);
	}
});

// https://stackoverflow.com/questions/499126/jquery-set-cursor-position-in-text-area
function setSelectionRange(input, selectionStart, selectionEnd) {
	if(input.setSelectionRange) {
		input.focus();
		input.setSelectionRange(selectionStart, selectionEnd);
	
	} else if(input.createTextRange) {
		var range = input.createTextRange();
		
		range.collapse(true);
		range.moveEnd('character', selectionEnd);
		range.moveStart('character', selectionStart);
		range.select();
	}
}


export default {
	setupInput: function(input) {
		
	},
	
	onInput: function(input) {
		var value = $(input.ele).val();
		var param = input.params;
		
		// if the input is blank, return
		if(/^\s*$/.test(value)) return;
		
		// user Numeral.js to parse and clean input
		var formatted = numeral(value).format('(NNN) NNN-NNNN');
		
		// find the index of the last number
		var index = 0, matcher = /\d/g;
		while((matcher.test(formatted), matcher.lastIndex > 0))
			index = matcher.lastIndex;
		
		// Update display value
		$(input.ele).val(formatted);
		// ensure cursor is set to a removable character
		index >= 0 && setSelectionRange(input.ele, index, index);
	},
	
	validate: function(input) {
		var value = $(input.ele).val();
		var parsed = numeral(value).value();
		
		if(isNaN(parsed)) {
			input.setError("Unable to parse phone-number");
			return false;
		}
		
		return true;
	}
};