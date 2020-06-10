import React from "react";
import Tippy from "@tippyjs/react";
import classNames from "classnames";

var couriers = [
	{ name: "UPSP", 	icon: "fab fa-usps", 	pattern: "" },
	{ name: "USP", 		icon: "fab fa-ups", 	pattern: "" },
	{ name: "FedEx",	icon: "fab fa-fedex", 	pattern: "#### #### #### ###" },
	{ name: "DHL", 		icon: "fab fa-dhl", 	pattern: "" },
	{ name: "Other",	icon: "fa fa-question", pattern: "" }
];

class TrackingSelectList extends React.Component {
	pickType(typeName) {
		this.props.onTypeSelect && this.props.onTypeSelect(typeName);
	}
	
	render() {
		var items = [];
		for(let data of couriers) {
			let classes = classNames({
				"tracking-select_item": true,
				"selected": data.name == this.props.type
			});
			
			items.push(
				<div key={data.name} className={classes} onClick={e => this.pickType(data.name)}> 
					<span className="tracking-select_item_icon">
						<i className={data.icon}></i>
					</span>
					<span className="tracking-select_item_label">
						{data.name}
					</span>
				</div>
			);
		}
		
		let classes = classNames({
			"tracking-select_list": true,
			"closed": !this.props.open
		});
		
		return (
			<div className={classes}>
				{items}
			</div>
		);
	}
}

class TrackingInputBox extends React.Component {
	formatInput(e) {
		//e.srcElement.value;
	}
	
	render() {
		return (
			<div className="tracking-input_wrapper">
				<input type="text" 
					id="tracking_number"
					value={this.props.value} 
					readOnly={this.props.readonly} 
					onKeyUp={e => this.formatInput(e)} 
					onChange={e => this.formatInput(e)} />
				
				<Tippy content="Copy">
					<a>
						<i className="fa fa-copy"></i>
					</a>
				</Tippy>
			</div>
		);
	}
}

export default class TrackingInput extends React.Component {
	constructor(props) {
		super(props);
		
		this.state = {
			"type": this.lookupType(props.type),
			"value": this.props.value || ''
		};
		
		$(window).on("click", e => {
			if(!this.state.showTypes)
				return;
				
			this.setState({
				"showTypes": false 
			});
		});
	}
	
	lookupType(typeName) {
		for(let data of couriers) {
			if(data.name == typeName) {
				return data;
			}
		}
		
		return couriers[couriers.length - 1];
	}
	
	setType(typeName) {
		this.setState({
			"type": this.lookupType(typeName)
		});
	}
	
	openTypeMenu(e) {
		if(this.props.readonly) 
			return;
		
		this.setState({
			"showTypes": true 
		});
		
		e.stopPropagation(); 
	}
	
	render() {
		return (
			<div className="tracking-input">
				<div className="tracking-input_type">
					<input type="hidden" value={this.state.type.name} id="courier" />
				
					<a onClick={e => this.openTypeMenu(e)}>
						<i className={this.state.type.icon}></i>
					</a>
					
					<TrackingSelectList 
						type={this.state.type.name} 
						open={this.state.showTypes}
						onTypeSelect={type => this.setType(type)} />
				</div>
				
        		<TrackingInputBox 
					readonly={this.props.readonly}
					format={this.state.type.pattern} 
					value={this.state.value} />
			</div>
		);
	}
}