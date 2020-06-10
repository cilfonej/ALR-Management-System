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
				{hightlightMatch(this.props.value, this.props.search)}
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

class LookupOption extends React.Component {
	onSelect() {
		this.props.onSelect && this.props.onSelect(this.props.data);
	}
	
	render() {
		var onClick = e => this.onSelect();
	
		switch(this.props.data.type) {
			case 'user': var icon = icon || 'fa alr-user';
			case 'foster': var icon = icon || 'fa alr-user-foster';
			case 'distributor': var icon = icon || 'fa alr-user-distributor';
				return <LookupOption_User 
							icon={icon} 
							value={this.props.data.name} search={this.props.filter} 
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
					{hightlightMatch(this.props.data.text, this.props.search)} 
				</div>;
		}
	}
}

class LookupResultGroup extends React.Component {
	render() {
		var elements = [];
		for(var option of this.props.options) {
			elements.push(<LookupOption key={option.key} 
							data={option} filter={this.props.filter}
							onSelect={this.props.onSelect}/>);
		}
		
		return (
			<div className="input-lookup_sub-header">
				<h3> {this.props.name} </h3>
		        {elements}
			</div>
		);
	}
}

class LookupResults extends React.Component {
	render() {
		var groups = [];
		
		// if results haven't loaded
		if(typeof this.props.results === 'undefined' || this.props.results === null) {
			groups.push(
				<div key="loading-message" className="input-lookup_message"> 
					<i className="fa fa-sync-alt fa-spin"> </i>
					Loading... 
				</div>
			);
		
		// un-queryable message
		} else if(typeof this.props.results === 'string') {
			groups.push(
				<div key="loading-message" className="input-lookup_message"> 
					{this.props.results}
				</div>
			);
		
		} else {
			const { results } = this.props;
			
			// if there are no matches
			if(!results.length) {
				groups.push(<div key="none-message" className="input-lookup_message"> No matching results! </div>);
			
			} else {
				for(var group of results) {
					if(!group.options || !group.options.length)
						continue;
						
					groups.push(<LookupResultGroup key={group.name} 
									name={group.name} options={group.options} 
									filter={this.props.filter} 
									onSelect={this.props.onSelect}/>);
				}
			}
		}
		
		return (
			<div className="input-lookup_results">
				{groups}
			</div>
		);
	}
}

var __focusTrack;

export default class LookupInput extends React.Component {
	constructor(props) {
		super(props);
		
		this.state = {
			"filter": this.props.value.text || '',
			"value": this.props.value.value || '',
			"open": false 
		};
	}

	fireChangeEvent() {
		this.props.onEvent && this.props.onEvent({
			value: this.state.value
		});
	}
	
	setSelection(option) {
		var filter = option && option.text || this.state.filter;
		
		this.setState({
			"value": option && option.value || '',
			"filter": filter,
			"error": !option, // accepts boolean or error message-string
			
			"filtered_results": this.filterResults(filter, this.state.results),
			"open": false
		}, this.fireChangeEvent);
	}
	
	validate() {
		this.setState({
			"error": !this.state.value
		});
		
		return !!this.state.value;
	}	
	
	getValue() {
		return this.state.value;
	}
	
	clear() {
		this.setState({
			"open": false,
			"filter": '',
			"value": '',
			"filtered_results": this.state.results
		}, this.fireChangeEvent);
	}
	
	//	============================== Event Listeners ============================== \\
	
	onKeyDown(e) {
		if(this.props.readonly) ret
		
		// escape
		if(e.keyCode === 27) {
			if(this.state.open) {
				this.setState({
					"filter": '',
					"value": '',
					"filtered_results": this.state.results
				});
				
			} else {
				this.setState({
					"open": false,
				});
			}
		
		// enter
		} else if(e.keyCode === 13) {
			if(this.props.type != "address") {
				this.onFocusLost();
			}
		}
	}
	
	blockFocus(e) {
		if(this.props.readonly) return;
		if($(e.target).closest(".input-lookup_results").length)
			__focusTrack = true;
	}
	
	onFocusLost() {
		if(this.props.readonly || __focusTrack) {
			__focusTrack = false;
			return;
		}
		
		if(!this.state.value) {
			var option = this.checkForOnlyOption();
			
			if(typeof option !== 'string')
				this.setSelection(option);
		}
		
		this.setState({
			"open": false
		});
	}
	
	// 	checks filtered options for one remaining option
	// 	evaluates all groups for one-element with a common key
	//	Returns: 
	//		element: if one common option found
	//		   null: if multiple or no canidates;
	checkForOnlyOption() {
		var results = this.state.filtered_results;
		
		if(!results) return null;
		if(typeof results === 'string')
			return results;
		
		var firstElement;
		for(var group of results) {
			var options = group.options;
			
			// if there is only one option in a group
			if(options && options.length == 1) {
				let option = options[0];
				
				if(!firstElement) {
					firstElement = option;
				
				// if we already have a canidate, check if same
				} else if(firstElement.key != option.key) {
					return null;
				}
			// more then 1 remaining option
			} else {
				return null;
			}
		}
		
		return firstElement;
	}
	
	//	============================== Filter & Results ============================== \\
	
	onFilterChange(e) {
		this.setState({
			"value": '',
			"filter": e.target.value,
			"filtered_results": this.filterResults(e.target.value, this.state.results),
			"open": true
		})
	}
	
	openResults(e) {
		if(this.props.readonly)
			return;
		
		this.setState({
			"open": true
		});
		
		e.stopPropagation();
	}
	
	filterResults(filter, allResults) {
		if(allResults == null || typeof allResults === 'string') return;

		// remove new-line and trim whitespace
		filter = (filter || '').replace(/(\r\n|\n|\r)/gm, " ").trim();
		
		var primary = new RegExp("^" + filter + "(.*)$", "i");
		var secondary = new RegExp("^(.*)" + filter + "(.*)$", "i");
		
		var results = [];
		for(var group of allResults) {
			var options = group.options;
			var matching = [];

			// move all matching results into 'matching' and remove the from 'options'
			options = options.filter(data => !(primary.test(data.filter_text) && matching.push(data)));	
			options = options.filter(data => !(secondary.test(data.filter_text) && matching.push(data)));	
			
			// if there are matching elements in the group
			if(matching.length) {
				// assign results to group
				results.push({
					name: group.name,
					options: matching
				});
			}
		}
		
		return results;
	}
	
	setUnqueryable(message) {
		this.setState({
			"value": '',
			"results": message,
			"filtered_results": message
		}, this.fireChangeEvent);
	}
	
	requestData(query, data) {
		this.setState({
			"results": null,
			"filtered_results": null
		});
		
		$.ajax({
			url: query,
			method: "GET",
			
			data: data
			
		}).done((data, status, xhr) => {
			if(status == "success") {
				var results = this.filterResults(this.state.filter, data);
				
				// if no results match the filter
				if(!results.length) {
					// clear value
					this.setState({
						"value": ''
					}, this.fireChangeEvent);
				}
				
				this.setState({
					"results": data,
					"filtered_results": results
				});
			}
		}).fail((xhr, status, message) => {});
	}
	
	//	============================== Render ============================== \\
	
	render() {
		var classess = classNames({
			"input": true,
			"input-lookup": true,
			"readonly": this.props.readonly,
			"input-lookup_open": this.state.open,
			
			"input-error": !!this.state.error,
			"input-has_value": !!this.state.value 
		});
		
		// only if 'this.state.error' is a 'string'
		var errorMessage = typeof this.state.error === 'string' && this.state.error || undefined;
		
		var input;
		if(this.props.type == "address") {
			input = <textarea className="input-address" wrap="off" spellCheck="false"
						readOnly={this.props.readonly} 
						value={this.state.filter} 
						onClick={e => this.openResults(e)}
						onKeyDown={e => this.onKeyDown(e)}
						onChange={e => this.onFilterChange(e)} />
		
		} else {
			input = <input type="text" 
						readOnly={this.props.readonly} 
						value={this.state.filter} 
						onClick={e => this.openResults(e)}
						onKeyDown={e => this.onKeyDown(e)}
						onChange={e => this.onFilterChange(e)} />
		}
		
		return (
			<div className={classess} onBlur={e => this.onFocusLost()} onMouseDown={e => this.blockFocus(e)}>
				<span className="input-label"> {this.props.label} </span>
	    
				<div className="input-wrapper" data-error={errorMessage}> 
					<i className={this.props.icon}> </i>
					
					{input}
						
					<input type="hidden" value={this.state.value} onChange={() => {}}/> 
					<i className="input-status_icon fa fa-check"> </i>
				</div>
				
				<LookupResults 
					filter={this.state.filter} results={this.state.filtered_results}
					onSelect={option => this.setSelection(option)} />
			</div>
		);
	}
}