import React from "react";
import classNames from "classnames"

function styleClassTransltor(style) {
	switch(style && style.toLowerCase()) {
		case "ghost": return "icon-button_ghost";
		
		default: return "";
	}
}

export class IconButton extends React.Component {
	render() {
		var classes = classNames("icon-button", styleClassTransltor(this.props.style));
		var iconName = classNames(this.props.iconDesign || "fa", this.props.icon);
		
		return (
			<a className={classes} onClick={this.props.onClick}>
				<i className={iconName}></i>
			</a>
		);
	}
}