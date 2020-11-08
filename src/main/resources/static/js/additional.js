var selectedTickets = [];

function toggleTicketSelection(ticketNum) {
	var ticketElement = document.getElementById("ticket" + ticketNum);

	if (!ticketElement.classList.contains("bought")) {

		if (ticketElement.classList.contains("selected")) {
			selectedTickets.pop(ticketNum);

			ticketElement.classList.remove("selected");
			setNewTooltip(ticketElement, ticketNum + " - Available");

			if (!document.getElementById("saveButton").classList.contains("disabled")) {
				document.getElementById("saveButton").classList.add("disabled");
			}

		} else {
			if (selectedTickets.length < MAX_TICKETS) {
				selectedTickets.push(ticketNum);

				ticketElement.classList.add("selected");
				setNewTooltip(ticketElement, ticketNum + " - Selected");

				if (selectedTickets.length == MAX_TICKETS) {
					document.getElementById("saveButton").classList.remove("disabled");
				}
			}
		}

		document.getElementById("selectedTickets").innerHTML = selectedTickets.length;


	}
}

function setNewTooltip(element, tooltip) {
	$(element).attr('data-original-title', tooltip)
		.tooltip('hide')
		.tooltip('show');
}

function saveSelection() {
	if (!document.getElementById("saveButton").classList.contains("disabled")) {
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function() {
				if (this.readyState == 4 && this.status == 200) {
					if (xhttp.responseText == 1) {
						console.log(window.location.href)
						window.location.href = "/?info="+selectedTickets.length+" ticket selection" + (selectedTickets.length != 1 ? "s" : "" ) + " saved!";
					} else {
						window.location.href = "/select-tickets?code="+code + "&error=Ticket selection failed! Please try again - the tickets you selected may have already been selected by another person.";
					}
				}
		};
		xhttp.open("POST", "/select-tickets-action");
		xhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		xhttp.send(JSON.stringify({ "code": code, "tickets": selectedTickets }));
	}
}
