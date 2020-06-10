package edu.wit.cilfonej.web.response;

import java.util.List;

public class ItemListDetails {
	protected List<LineItemDetails> items;
	
	protected static class LineItemDetails {
		protected int item_id;
		protected int count;
		
		public void setCount(int count) {
			this.count = count;
		}
		
		public void setItem_id(int item_id) {
			this.item_id = item_id;
		}
	}
	
	public void setItems(List<LineItemDetails> items) {
		this.items = items;
	}
}
