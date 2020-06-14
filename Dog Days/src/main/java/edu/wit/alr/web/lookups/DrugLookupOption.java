package edu.wit.alr.web.lookups;

import java.text.DecimalFormat;

import edu.wit.alr.database.model.Drug;

public class DrugLookupOption extends LookupOption {
	private static final DecimalFormat FORMAT = new DecimalFormat("#.##");
	
	protected String name;
	
	protected String min;
	protected String max;
	
	public DrugLookupOption(Drug drug) {
		super("drug", 
				drug.getType().getDisplayName() + " " + 
					drug.getName() + " " + 
					FORMAT.format(drug.getWeightRange().getMin()) + "-" + 
					FORMAT.format(drug.getWeightRange().getMax()) + "lbs", 
					
				drug.getName(),
				drug.getID());
		
		this.name = drug.getName();
		this.min = FORMAT.format(drug.getWeightRange().getMin());
		this.max = FORMAT.format(drug.getWeightRange().getMax());
	}
}
