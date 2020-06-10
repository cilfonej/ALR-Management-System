
var Shipment = (function() {
	
//	======================================== Add Items ======================================== \\
	
	function addItemRow() {
		if($jsx('ship-add').validate()) {
			var value = $jsx('ship-add').getValue();
			var count = $$("#ship-add_count").val();
			
			$jsx('ship-add').clear();
			$$("#ship-add_count").val(1);
			
			$.ajax({
				url: "/render/shipment/item",
				method: "GET",
				
				data: {
					"drug_id": value,
					"count": count
				}
			}).done(UID.bind(function(data, status, xhr) {
				if(status === "success") {
					// find shipment-items container
					var container = $$("#shipment-items");

					// add new row to start of list
					container.prepend(data);
					// update container's count attribute 
					container.attr("data-count", container.find(".shipment-item").length);
				}
			})).fail(UID.bind(function(xhr, status, error) {
			}));
		}
	}
	
	function toggleShipmetItemShow() {
		// find shipment-item section					toggle open/close
		$$(".shipment-items_section").toggleClass("shipment-show_more");
	}

	function removeShipmentRow(e) {
		// find shipment-item container
		var container = $$(".shipment-items_container");
		
		// remove row for list
		$(e.target).closest(".shipment-item").remove();
		// update container's count attribute 
		container.attr("data-count", container.find(".shipment-item").length);
	}
	
//	======================================== Submit ======================================== \\
	
	function clearInputs() {
		$jsx('ship-to') && $jsx('ship-to').clear();
		$jsx('ship-for') && $jsx('ship-for').clear();
		$jsx('ship-add') && $jsx('ship-add').clear();
		$jsx('ship-address') && $jsx('ship-address').clear();
		
		$$("#shipment-track-input input[type='text']").val("");
		$$("#shipment-items .shipment-item").remove();
	}
	
	function submitShipment() {
		var items = [];
		$$("#shipment-items .shipment-item").each(function(index, ele) {
			var e = $(ele);
			items.push({
				"item_id": e.attr("data-id"),
				"count": e.attr("data-count")
			});
		});
		
		$.ajax({
			url: "/api/shipment/create",
			method: "POST",
			
		    contentType: 'application/json; charset=utf-8',
			data: JSON.stringify({
				"addressDetails": {
					"address_id": $jsx('ship-address').getValue(),
				},
		
				"for_dog_id": $jsx('ship-for').getValue(),
				
				"courier": $$("#shipment-track-input input#courier").val(),
				"tracking_number": $$("#shipment-track-input input#tracking_number").val(),
				
				"itemDetails": {
					"items": items
				}
			})
		}).done(UID.bind(function(data, status, xhr) {
			if(status === 'success') {
				Sidebar.removeTab("Shipment");
				Sidebar.loadAndReplace("/render/shipment/tab/receipt?id=" + data);
				
				$.ajax({
					url: "render/shipment/history?id=" + data,
					method: "GET"
				}).done(function(data, status, xhr) {
					if(status === 'success') {
						$("#drug-history_list").prepend($(data));
					}
				}).fail(function(xhr, status, error) {
				});
			}
		})).fail(UID.bind(function(xhr, status, error) {
		}));
	}
	
//	======================================== Export ======================================== \\
	
	return  {
		// Add Items
		"addItemRow": UID.expose(addItemRow),
		"removeShipmentRow": UID.expose(removeShipmentRow),
		"toggleShipmetItemShow": UID.expose(toggleShipmetItemShow),
		
		// Submit
		"clearInputs": UID.expose(clearInputs),
		"submitShipment": UID.expose(submitShipment),
	}
})();