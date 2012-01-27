function showCSVImportOptions(response) {
	var csvPreviewTable = $("#CSVPreviewTable");
	var csvImportDiv = $("#CSVImportDiv");
	// TODO Reset the CSV import options
	$("tr", csvPreviewTable).remove();
	csvPreviewTable.append($("<tr>").append($("<td>").addClass("rowIndexCell").text("File Row Number")));
	
	var responseJSON = $.parseJSON(response);
	var headers = responseJSON["elements"][0]["headers"];
	
	//Change the source name
	$("#CSVSourceName", csvImportDiv).text(responseJSON["elements"][0]["fileName"]);
	
	// Populate the headers
	if(headers != null)  {
		var trTag = $("<tr>");
		$.each(headers, function(index, val) {
			if(index == 0){
				trTag.append($("<td>").addClass("rowIndexCell").text(val));
			} else {
				trTag.append($("<th>").text(val));
			}
		});
		csvPreviewTable.append(trTag);
	} else {
		// Put empty column names
		var trTag = $("<tr>");
		$.each(responseJSON["elements"][0]["rows"][0], function(index, val) {
			if(index == 0){
				trTag.append($("<td>").addClass("rowIndexCell").text("-"));
			} else {
				trTag.append($("<th>").text("Column_" + index).addClass("ItalicColumnNames"));
			}
			
		});
		csvPreviewTable.append(trTag);
	}
	
	// Populate the data
	var rows = responseJSON["elements"][0]["rows"];
	$.each(rows, function(index, row) {
		var trTag = $("<tr>");
		$.each(row, function(index2, val) {
			var displayVal = val;
			if(displayVal.length > 20) {
				displayVal = displayVal.substring(0,20) + "...";
			}
			if(index2 == 0) {
				trTag.append($("<td>").addClass("rowIndexCell").text(displayVal));
			} else {
				trTag.append($("<td>").text(displayVal));
			}
		});
		csvPreviewTable.append(trTag);
	});
	
	// Attach the command ID
	csvImportDiv.data("commandId", responseJSON["elements"][0]["commandId"]);
	
	// Open the dialog
	csvImportDiv.dialog({ modal: true , width: 820, title: 'Import CSV File Options',
		buttons: { "Cancel": function() { $(this).dialog("close"); }, "Import":CSVImportOptionsChanged}});
}

function CSVImportOptionsChanged(flag) {
	var csvImportDiv = $("#CSVImportDiv");
	var options = new Object();
	options["command"] = "ImportCSVFileCommand";
	options["commandId"] = csvImportDiv.data("commandId");
	options["delimiter"] = $("#delimiterSelector").val();
	options["CSVHeaderLineIndex"] = $("#CSVHeaderLineIndex").val();
	options["startRowIndex"] = $("#startRowIndex").val();
	options["textQualifier"] = $("#textQualifier").val();
	options["workspaceId"] = $.workspaceGlobalInformation.id;
	options["interactionType"] = "generatePreview";
	
	// Import the CSV if Import button invoked this function
	if(typeof(flag) == "object") {
		options["execute"] = true;
		options["interactionType"] = "importTable";
	}
		

	var returned = $.ajax({
	   	url: "/RequestController", 
	   	type: "POST",
	   	data : options,
	   	dataType : "json",
	   	complete : 
	   		function (xhr, textStatus) {
	   			if(!options["execute"])
	    			showCSVImportOptions(xhr.responseText);
	    		else{
	    			$("#CSVImportDiv").dialog("close");
	    			var json = $.parseJSON(xhr.responseText);
	    			parse(json);
	    		}		
		   	}
		});	
}

function resetCSVDialogOptions() {
	var csvImportDiv = $("div#CSVImportDiv");
	$("#delimiterSelector :nth-child(1)", csvImportDiv).attr('selected', 'selected');
	$("#CSVHeaderLineIndex", csvImportDiv).val("1");
	$("#startRowIndex", csvImportDiv).val("2");
	$("#textQualifier", csvImportDiv).val("\"");
}