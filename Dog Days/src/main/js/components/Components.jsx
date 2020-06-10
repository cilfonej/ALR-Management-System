import React from "react";
import ReactDOM from "react-dom";

import Calendar from "./Calendar";
import LookupInput from "./LookupInput";
import TrackingInput from "./TrackingInput";

window.ReactComponents = {};

ReactComponents.insertCalendar = function(element, onEvent) {
	return ReactDOM.render(
		<Calendar onEvent={onEvent} />,
		element
	);
};

ReactComponents.insertTrackingInput = function(element, readonly, type, tracking_number, onEvent) {
	return ReactDOM.render(
		<TrackingInput readonly={readonly} type={type} value={tracking_number} onEvent={onEvent} />,
		element
	);
};

ReactComponents.insertLookupInput = function(element, readonly, value, label, icon, type, onEvent) {
	return ReactDOM.render(
		<LookupInput readonly={readonly} label={label} icon={icon} value={value} type={type} onEvent={onEvent} />,
		element
	);
};

ReactComponents.remove = function(component) {
	ReactDOM.unmountComponentAtNode(component);
}