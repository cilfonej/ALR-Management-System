import React from "react";
import classNames from "classnames";

function hightlightMatch(text, search) {
	search = search.replace(/(\r\n|\n|\r)/gm, "|");
	var pattern = new RegExp("^(.*)(" + search + ")(.*)$", "i");
	var result = pattern.exec(text);
	
	if(!result) 
		return <span> {text} </span>;
	
	return <span> {result[1]}<b>{result[2]}</b>{result[3]} </span>;
}

class LookupOption_Drug extends React.Component {
	render() {
		return (
			<div className="input-lookup_option" onClick={this.props.onClick}> 
				<i className='fa fa-pills'></i> 
				{hightlightMatch(this.props.data.name, this.props.search)}
				
				<span className="input-lookup_option-sub">
					{hightlightMatch(this.props.data.min + '-' + this.props.data.max + 'lbs', this.props.search)}
				</span>
			</div>
		);
	}
}

class LookupOption_Address extends React.Component {
	render() {
		var cityLine = '';
		if(this.props.data.city) cityLine += this.props.data.city;
		if(this.props.data.city && this.props.data.state) cityLine += ", ";
		if(this.props.data.state) cityLine += this.props.data.state;
		
		return (
			<div className="input-lookup_option input-lookup_option_address" onClick={this.props.onClick}>
				<i className={this.props.icon}></i>
				    
				<div className="address">
					<span> {hightlightMatch(this.props.data.streetAddress, this.props.search)} </span>
					
					{cityLine && 
						<span> {hightlightMatch(cityLine, this.props.search)} </span>}
						
					{this.props.data.postalCode && 
						<span> {hightlightMatch(this.props.data.postalCode, this.props.search)} </span>}
				</div>
			</div>
		);
	}
}

class LookupOption_User extends React.Component {
	render() {
		return (
			<div className="input-lookup_option" onClick={this.props.onClick}> 
				<i className={this.props.icon}></i> 
				{hightlightMatch(this.props.name, this.props.search)}
			</div>
		);
	}
}

class LookupOption_Dog extends React.Component {
	render() {
		return (
			<div className="input-lookup_option" onClick={this.props.onClick}> 
				<i className="fa fa-dog"></i> 
				{hightlightMatch(this.props.name, this.props.search)}
				
				<span className="input-lookup_option-sub">
					{hightlightMatch('#' + this.props.number, this.props.search)}
				</span>
			</div>
		);
	}
}

/**
	Params:
		- onSelect: function(option)			callback function for when an option is selected
		- filter: string						the filter-text used for highlighting matches
		- data: {								option data, passed back as arg. to 'onSelect'
					- filter: string					the string version of data used when filtering
					- text: string						the text to display when the option is selected
					- type: string						type of data being displayed			
					- key: string						unique value for to identify option
					- ...								any additional data depending on type 	
				}

 */

export default class LookupOption extends React.Component {
	onSelect() {
		this.props.onSelect && this.props.onSelect(this.props.data);
	}
	
	render() {
		var onClick = e => this.onSelect();
	
		switch(this.props.data.type) {
			case 'user': var icon = icon || 'fa alr-user';
			case 'foster':
			case 'adopter':
			case 'caretaker': var icon = icon || 'fa alr-user-foster';
			case 'admin': var icon = icon || 'fa alr-user-distributor';
			case 'coordinator': var icon = icon || 'fa alr-user-distributor';
				return <LookupOption_User 
							icon={icon} 
							name={this.props.data.name} search={this.props.filter} 
							onClick={onClick} />;
				
			case 'dog': 
				return <LookupOption_Dog 
							name={this.props.data.name} number={this.props.data.number} 
							search={this.props.filter} 
							onClick={onClick} />;
				
			case 'mailing_address': var icon = icon || 'fa fa-envelope';
			case 'home_address': var icon = icon || 'fa fa-home';
			case 'address':	var icon = icon || 'fa fa-map-marker';
				return <LookupOption_Address
							icon={icon} 
							data={this.props.data} search={this.props.filter} 
							onClick={onClick} />;
							
			case 'drug':
				return <LookupOption_Drug 
							data={this.props.data} search={this.props.filter} 
							onClick={onClick} />
				
			default:
				return <div className="input-lookup_option" onClick={onClick}> 
					{hightlightMatch(this.props.data.text, this.props.filter)} 
				</div>;
		}
	}
}