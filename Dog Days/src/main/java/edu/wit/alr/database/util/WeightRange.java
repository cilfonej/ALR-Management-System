package edu.wit.alr.database.util;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class WeightRange {

	@Column(name = "min_weight", scale = 4, precision = 2, nullable = false)
	private double min;
	
	@Column(name = "max_weight", scale = 4, precision = 2, nullable = false)
	private double max;
	
	// no-args constructor
	WeightRange() { }
	
	public WeightRange(double min, double max) {
		this.min = min;
		this.max = max;
	}
	
	public boolean containWeight(double weight) {
		return min <= weight && weight <= max;
	}

	public double getMin() { return min; }
	public double getMax() { return max; }

	public void setMin(double min) {
		this.min = min;
	}

	public void setMax(double max) {
		this.max = max;
	}
}
