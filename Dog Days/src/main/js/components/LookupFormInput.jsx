import React from "react";
import PicklistOption from "./LookupOption";
import classNames from "classnames";

function makePicklistGroup(option_grouping, filter, onSelect) {
	var elements = [];
	for(var option of option_grouping.options) {
		elements.push(
			<PicklistOption 
					key={option.key} 
					data={option} 
					filter={filter}
					onSelect={onSelect}/>
				);
	}
	
	return (
		<div key={option_grouping.name} className="filter-input_grouping">
			<h3> {option_grouping.name} </h3>
	        {elements}
		</div>
	);
}

function generateOptionGroups(option_groupings, filter, onSelect) {
	var groups = [];
		
	// if the options haven't loaded
	if(option_groupings === null) {
		return(
			<div key="loading-message" className="form-input_lookup_message"> 
				<i className="fa fa-sync-alt fa-spin"> </i>
				Loading... 
			</div>
		);
	
	// un-queryable message
	} else if(typeof option_groupings === 'string') {
		return(
			<div key="loading-message" className="form-input_lookup_message"> 
				{option_groupings}
			</div>
		);
	
	} else {
		// if there are no matches
		if(!option_groupings.length) {
			return(
				<div key="none-message" className="form-input_lookup_message"> No matching results! </div>
			);
		
		} else {
			for(var group of option_groupings) {
				if(!group.options || !group.options.length)
					continue;
					
				groups.push(makePicklistGroup(group, filter, onSelect));
			}
		}
	}
	
	return groups;
}

// 	checks filtered options for one remaining option
// 	evaluates all groups for one-element with a common key
//	Returns: 
//		element: if only one option is found
//	  	 string: if there's aquery-error message
//		   null: if there are multiple or no canidates
function checkForOnlyOption(option_groups) {
	if(!option_groups) return null;
	// if there is a query-error message
	if(typeof option_groups === 'string')
		return option_groups;
	
	var firstElement;
	for(var group of option_groups) {
		var options = group.options;
		
		// if there is only one option in a group
		if(options && options.length == 1) {
			let option = options[0];
			
			// if the is no canidate yet
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

function filterOptions(filter, all_options) {
	if(all_options == null || typeof all_options === 'string') return;

	// remove new-line and trim whitespace
	filter = (filter || '').replace(/(\r\n|\n|\r)/gm, " ").trim();
	
	// define match filters
	var primary = new RegExp("^" + filter + "(.*)$", "i");
	var secondary = new RegExp("^(.*)" + filter + "(.*)$", "i");
	
	var results = [];
	for(var group of all_options) {
		var options = group.options;
		var matching = [];

		// move all matching results into 'matching' and remove the from 'options'
		options = options.filter(data => !(primary.test(data.filter) && matching.push(data)));	
		options = options.filter(data => !(secondary.test(data.filter) && matching.push(data)));	
		
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

/**
	Params:
		- type: string [input, textarea] 	<default = input>
		- readonly: boolean 				<default = false>
		
		- icon: string
 */

var __focusTrack;

export default class LookupInput extends React.Component {
	constructor(props) {
		super(props);
		
		this.state = {
			"all_options": '<Input has not be initialized>',
			"options": '<Input has not be initialized>',
			
			"filter": this.props.default.filter || '',		// string: search text string
			"value": this.props.default.value || null,	// object: resulting object
			"open": false 	// is the dropdown open
		};
	}
	
// =============================== =============== =============================== \\
// =============================== Value Functions =============================== \\
	
	// fire change event
	fireChangeEvent() {
		this.props.onEvent && this.props.onEvent({
			value: this.state.value
		});
	}
	
	setValue(value) {
		// if the value hasn't actuly changed, return
		if(value == this.state.value) return;
		if(this.state.value && this.state.value.key == value && value.key) return;
		
		// if the option is [null] use the filter text
		var filter = value && value.text || this.state.filter;
		
		// upon setting the value, clear any errors
		this.clearError();
		
		// close the pick-list 
		this.close();
		
		// set the value and filter, refilter options, fire change event
		this.setState({
			"value": value,
			"filter": filter,
			"options": filterOptions(filter, this.state.all_options),
		}, this.fireChangeEvent);
	}
	
// =============================== =============== =============================== \\
// =============================== Clear Functions =============================== \\
	
	// close the results popup
	close() {
		this.setState({
			"open": false,
		});
	}
	
	// clears the search field and the selected value
	clearSearch() {
		// clear the filter
		this.setState({
			"filter": '',
			"options": this.state.all_options
		});
		
		this.setValue(null);
	}
	
	// clears error-message
	clearError() {
		this.setState({
			"error": null
		});
	}
	
// =============================== ================== =============================== \\
// =============================== External Functions =============================== \\
	
	getValue() {
		return this.state.value;
	}
	
	clear() {
		this.close();
		this.clearError();
		this.clearSearch();
		
		/*this.setState({
			"open": false,
			"filter": '',
			"value": '',
			"filtered_results": this.state.results
		}, this.fireChangeEvent);*/
	}
	
	// validate the value of this field
	// Returns: true if completed; false otherwise
	validate() {
		var hasValue = !!this.state.value;
		
		if(!hasValue)
			this.setError('Please select a value');
		else 
			this.clearError();
		
		return !!this.state.value;
	}

	setError(errorMsg) {
		this.setState({
			"error": errorMsg || '<default error>'
		});
	}
	
	// marks search input as unable to load options
	// usually due to a dependency on another (incomplete) field
	setUnqueryable(message) {
		
		// clear the value
		this.setValue(null);
		 
		//set the options to an error-message
		this.setState({
			"all_options": message,
			"options": message
		});
	}
	
	// request a list of options from a provided URL
	requestData(query, data) {
		// remove old options data
		this.setState({
			"all_options": null,
			"options": null
		});
		
		// preform XHR request to provided URL
		$.ajax({
			url: query,
			method: "GET",
			
			data: data
			
		}).done((data, status, xhr) => {
			// on successful data-query
			if(status == "success") {
				
				// filter the results
				var options = filterOptions(this.state.filter, data);
				
				// check if any options match the filter
				if(!options.length) {
					// if no options match the current filter, clear the value
					this.setValue(null);
				
				} else {
					// TODO: check if any of the new options match the current selected value					
				}

				// update the options-data				
				this.setState({
					"all_options": data,
					"options": options
				});
			}
			// TODO: on data-query FAIL
		}).fail((xhr, status, message) => {});
	}
	
// =============================== =============== =============================== \\
// =============================== Event Listeners =============================== \\

	onInputClicked(e) {
		if(this.props.readonly)
			return;
		
		// opens the options pick-list
		this.setState({
			"open": true
		});
		
		// stops the event from triggering the focus-blocker
		e.stopPropagation();
	}

	onKeyDown(e) {
		if(this.props.readonly) return;
		
		// escape
		if(e.keyCode === 27) {
			if(this.state.open) {
				this.clearSearch();
				
			} else {
				this.clearSearch();
				this.close();
			}
		
		// enter
		} else if(e.keyCode === 13) {
			// if this is a 'textarea', then ignore [enter] unless [shift] is pressed
			if(!e.shiftKey && this.props.type == "textarea") return;
			
			
			this.onFocusLost();
		}
	}
	
	// focus event-listener for selecting child elements from the pick-list
	blockFocusLost(e) {
		if(this.props.readonly) return;
		
		// if the target-element is a child of the picklist
		if($(e.target).closest(".form-input_results").length)
			__focusTrack = true;
	}
	
	// when the input loses focus
	onFocusLost() {
		// check if the focus has gone to a picklist element
		if(this.props.readonly || __focusTrack) {
			// if so, flip flag and do nothing
			__focusTrack = false;
			return;
		}
		
		// if there isn't a selected value
		if(!this.state.value) {
			// check to see if there's only one option remaining
			var option = checkForOnlyOption(this.state.options);
			
			// if the a non-error message was returned
			if(typeof option !== 'string')
				this.setValue(option);
		}
		
		// focus has been lost, close the popup
		this.close();
	}
	
	onFilterChange(e) {
		// clear value, 
		this.setValue(null);
		
		// set filter text, update options, show pick-list
		this.setState({
			"filter": e.target.value,
			"options": filterOptions(e.target.value, this.state.all_options),
			"open": true
		})
	}
	
	render() {
		var classess = classNames({
			"form-input": true,
			
			"input-lookup": true,
			"input-lookup_open": this.state.open,
			
			"readonly": this.props.readonly,
			
			"input-error": !!this.state.error,
			"input-has_value": !!this.state.value 
		});
		
		// only if 'this.state.error' is of type 'string'
		var errorMessage = typeof this.state.error === 'string' && this.state.error || undefined;
		
		var input;
		if(this.props.type == "textarea") {
			input = <textarea wrap="off" spellCheck="false"
						readOnly={this.props.readonly} 
						value={this.state.filter} 
						onClick={e => this.onInputClicked(e)}
						onKeyDown={e => this.onKeyDown(e)}
						onChange={e => this.onFilterChange(e)} />
		
		} else {
			input = <input type="text" 
						readOnly={this.props.readonly} 
						value={this.state.filter} 
						onClick={e => this.onInputClicked(e)}
						onKeyDown={e => this.onKeyDown(e)}
						onChange={e => this.onFilterChange(e)} />
		}
		
		return (
			<div className={classess} 
				 data-error={errorMessage}
				 onBlur={e => this.onFocusLost()} 
				 onMouseDown={e => this.blockFocusLost(e)}>
					
				<i className={this.props.icon}> </i>
				{ input }
				<i className="input-status_icon fa fa-check"> </i>
				
				<div className="form-input_results">
					{
						generateOptionGroups(
							this.state.options, 
							this.state.filter, 
							option => this.setValue(option)
						)
					}
				</div>
			</div>
		);
	}
}