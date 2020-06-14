package edu.wit.alr.web.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.wit.alr.database.model.Drug;
import edu.wit.alr.database.repository.DrugRepository;
import edu.wit.alr.web.response.ItemListDetails.LineItemDetails;
import edu.wit.alr.web.response.ItemListInflator.ItemListInfo.LineItem;

@Component
public class ItemListInflator {
	@Autowired private DrugRepository drugRepository;
	
	
	// this is a VO (value-object) or maybe a DTO (data-transfer-object)
	public static class ItemListInfo {
		private List<LineItem> items;
		
		public ItemListInfo() { this(new ArrayList<>()); }
		public ItemListInfo(List<LineItem> items) {
			this.items = items;
		}

		public static class LineItem {
			private Drug item;
			private int count;
			
			public LineItem(Drug item, int count) {
				this.item = item;
				this.count = count;
			}
			
			public Drug getItem() { return item; }
			public int getCount() { return count; }
		}
		
		public void addItem(LineItem item) { this.items.add(item); }
		public List<LineItem> getItems() { return items; }
	}
	
	public ItemListInfo inflate(ItemListDetails listDetails) {
		ItemListInfo info = new ItemListInfo();
		
		for(LineItemDetails item : listDetails.items) {
			Optional<Drug> drug_opt = drugRepository.findById(item.item_id);
			if(drug_opt.isEmpty()) {
				// TODO: find way to parse ALL items
				throw new InflationException("No such Drug with ID: " + item.item_id);
			}
			
			info.addItem(new LineItem(drug_opt.get(), item.count));
		}
		
		return info;
	}
}
