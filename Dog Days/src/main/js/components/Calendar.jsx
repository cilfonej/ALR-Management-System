import React from "react";
import classNames from "classnames";
import { IconButton } from "./Buttons";

class Day extends React.Component {
	render() {
		var classes = classNames({
			"cal-day_today": this.props.date.isSame(this.props.data.today, "day"),
			"cal-day_select":this.props.date.isSame(this.props.data.selected, "day"),
			"cal-day_ghost": !this.props.date.isSame(this.props.data.display, "month"),
		});
		
		return <span className={classes}> {this.props.date.format('D')} </span>;
	}
}

export default class Calendar extends React.Component {
	constructor(prop) {
		super(prop);
		
		this.state = {
			today: moment(),
			
			selected: null,
			display: moment()
		}
	}
	
	setSelectedDate(date) {
		this.setStatus({
			selected: date
		});
	}
	
	setDisplayDate(date) {
		this.setStatus({
			display: date
		});
	}
	
	addMonth(count) {
		this.setState({
			display: this.state.display.add(count, "months")
		});
	}
	
	addYear(count) {
		this.setState({
			display: this.state.display.add(count, "years")
		});
	}
	
	render() {
		var firstDay = this.state.display.clone().startOf('month');
		var day = firstDay.subtract(firstDay.day() + 1, "days");
		
		var days = [];
		for(var i = 0; i < 42; i ++) {
			days.push(<Day key={day.format("MMM-D")} data={this.state} date={day.add(1, "day").clone()} />);
		}
		
		return (
			<div className="calendar">
				<header className="cal-header">
					<div className="cal-month_wrapper">
						<IconButton icon="fa-angle-left" style="ghost" onClick={e => this.addMonth(-1)}/>
						<h2>{this.state.display.format("MMMM")}</h2> 
						<IconButton icon="fa-angle-right" style="ghost" onClick={e => this.addMonth(1)}/>
					</div>
					
					<div className="cal-year_wrapper">
						<IconButton icon="fa-angle-double-left" style="ghost" onClick={e => this.addYear(-1)}/>
						<h2>{this.state.display.year()}</h2> 
						<IconButton icon="fa-angle-double-right" style="ghost" onClick={e => this.addYear(1)}/>
					</div>
				</header>
				
				<nav className="cal-legend">
					<span> Sun </span>
					<span> Mon </span>
					<span> Tue </span>
					<span> Wed </span>
					<span> Thu </span>
					<span> Fri </span>
					<span> Sat </span>
				</nav>
				
				<div className="cal-body_wrapper">
					<div className="cal-body"> {days} </div>
				</div>
			</div>
		);
	}
}