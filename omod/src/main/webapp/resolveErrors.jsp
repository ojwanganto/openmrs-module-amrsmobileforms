<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="View Mobile Form Errors" otherwise="/login.htm" redirect="/module/amrsmobileforms/resolveErrors.list"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp"%>

<openmrs:htmlInclude file="/moduleResources/amrsmobileforms/js/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRAMRSMobileFormsService.js"/>
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude file="/moduleResources/amrsmobileforms/css/smoothness/jquery-ui-1.8.16.custom.css" />
<openmrs:htmlInclude file="/moduleResources/amrsmobileforms/css/dataTables_jui.css" />

<h2><spring:message code="amrsmobileforms.resolveErrors.title"/></h2>

<style type="text/css">
	/* comment and resolve buttons */
	button.action { border: 1px solid gray; font-size: .75em; color: black; width: 52px; margin: 2px; padding: 1px; cursor: pointer; }
	button.resolve { background-color: #E0E0F0; }
	button.comment { background-color: lightpink; }

	/* error table */
	#errors { margin: 1em; }
	#errors table { width: 100%; }
	#tools { margin: 1em; }
	.centered { text-align: center; }

	/* datatable stuff */
	.dataTables_info { font-weight: normal; }
	.dataTables_wrapper { padding-bottom: 0; }
	.ui-widget-header { font-weight: inherit; }
	.css_right { float: right; }
	.css_left { float: left; }
	.dataTables_length { width: auto; }
</style>

<script>
	var eTable = null;

	$j(document).ready(function(){

		// set up the error datatable
		eTable = $j("#errorTable").dataTable({
			bAutoWidth: false,
			bDeferRender: true,
			bJQueryUI: true,
			bPaginate: true,
			sPaginationType: "full_numbers",
			aoColumnDefs: [
				{ aTargets: ["_all"], bSortable: false },
				{
					aTargets: [0],
					sClass: "centered",
					mData: null,
				    mRender: function(data, type, full) {
							var id = full.id;
							return '<input name="errorIds" type="checkbox" value="' + id + '"/>';
						}
				},
                {
                    aTargets: [1],
                    sClass: "centered",
                    mData: null,
                    mRender: function(data, type, full) {
                        var id = full.id;
                        var out = "";
                        if (full.comment) {
                            out += '<button class="action resolve" errorId="' + id + '">Resolve</button>';
                        } else {
                            out += '<button class="action comment" errorId="' + id + '">Comment</button>';
                        }
                        return out;
                    }
                },
                { aTargets: [2], mData: "id" },
				{ aTargets: [3], mData: "error" },
				{ aTargets: [4], mData: "errorDetails" },
				{ aTargets: [5], mData: "formName" },
				{ aTargets: [6], mData: "comment", sClass: "centered" }
			],
			bProcessing: true,
			bServerSide: true,
			bStateSave: false,
			fnDrawCallback: function(oSettings){
				if ($j("span.numSelected").html() == oSettings.fnRecordsDisplay()) {
					$j("input[name=errorIds]").attr("checked", "checked");
				} else {
					$j("span.numDisplayed").html(oSettings.fnRecordsDisplay());
					$j("#selectAll").removeAttr("checked");
					$j("input[name=errorIds]").removeAttr("checked");
					updateNumSelected(0);
				}
			},
			sAjaxSource: "<openmrs:contextPath/>/module/amrsmobileforms/errorList.json"
		});

		// click events for comment and resolve buttons
		
		
		$j("button.comment").live("click", function(){
			var errorId = $j(this).attr("errorId");
           // document.location = "resolveErrorComment.form?errorId=" + $j(this).attr("errorId");
            DWRAMRSMobileFormsService.populateCommentForm(errorId ,function(data){
                  errObj= data[0];
                  generate_table(errObj);



            });


            
            $j( "#dialog-form" ).dialog({
                height: 600,
                width: 1000,
                modal: false,
                buttons:{
                    "OK":function(){
                    	 //alert(errObj.getDateCreated());
                    	


                    },
                    Cancel: function() {
                        $j(this).dialog( "close" );
                    }
                }

            });

            return false;

        });

        $j("button.resolve").live("click", function(){ document.location = "resolveError.form?errorId=" + $j(this).attr("errorId"); return false; });

		// click event for selectAll checkbox
		$j("#selectAll").click(function(){
			if ($j(this).is(":checked")) {
				$j("input[name=errorIds]").attr("checked", "checked");
				updateNumSelected($j("span.numDisplayed").html());
			} else {
				$j("input[name=errorIds]").removeAttr("checked");
				updateNumSelected(0);
			}
		});

		// click event for individual checkboxes
		$j("input[name=errorIds]").live("click", function(){ updateNumSelected(); });

		// click event for reprocessAll button
		$j("#reprocessAll").click(function(){
			var url = "reprocessBatch.htm";
			// TODO use formdata or jquery to manipulate errorIds into something cool
			var params = [];
			$j("input[name=errorIds]:checked").each(function(){
				params.push("errorIds=" + $j(this).val());
			});
			if ($j("#selectAll").is(":checked")) {
				params.push("all=1");
			}
			params.push("query=" + $j("div.dataTables_filter input").val());
			document.location = url + "?" + params.join("&");
		});
	});
	
	function buildRow(label,tdvalue){
		
		var row = document.createElement("tr");
        var cell = document.createElement("td");
        var celllabel = document.createTextNode(label+": ");
        var cellval = document.createTextNode(tdvalue);
        cell.appendChild(celllabel);
        cell.appendChild(cellval);
        row.appendChild(cell);
        return row;
	}

    function generate_table(data) {

        
        // get the reference for the body
        var body = document.getElementById("ttable");

        // creates a <table> element and a <tbody> element
        var tbl     = document.createElement("table");
        var tblBody = document.createElement("tbody");




        /*<td>
         <spring:message code="Person.name" />: <span class="value">{queueItem.name}</span> <br/>
         <spring:message code="Patient.identifier" />: <span class="value">{queueItem.identifier}</span> <br/>
         <spring:message code="Person.gender" />: <span class="value">{queueItem.gender}</span> <br/>
         <spring:message code="Encounter.location" />: <span class="value">{queueItem.location}</span> <br/>
         <spring:message code="Encounter.datetime" />: <span class="value">{queueItem.encounterDate}</span> <br/>
         <spring:message code="amrsmobileforms.resolveErrors.formName" />: <span class="value">{queueItem.formModelName} v{queueItem.formId}</span> <br/>
         <br/>
         <spring:message code="amrsmobileforms.resolveErrors.errorId" />: <span >{queueItem.mobileFormEntryErrorId}</span> <br/>
         <spring:message code="amrsmobileforms.resolveErrors.errorDateCreated" />: <span >{queueItem.dateCreated}</span> <br/>
         <spring:message code="amrsmobileforms.resolveErrors.error" />: <span >{queueItem.error}</span> <br/> <br/>
         <b><spring:message code="amrsmobileforms.resolveErrors.errorDetails" />:</b><div style="height: 40px; overflow-y: scroll; border: 1px solid #BBB;">${queueItem.errorDetails}</div> <br/>

         </td>*/

        // creating all cells
        //build first row 
        var row1 = buildRow("Person Name",data.name);
       
        //build row two 
        var row2 = buildRow("Person Identifier",data.identifier);

        //build row three 
        var row3 = buildRow("Gender",data.gender);

        //build row two 
        var row4 = buildRow("Location",data.location);
        
        var row5 = buildRow("Location",data.encounterDate);
        var row6 = buildRow("Location",data.formModelName);
        var row7 = buildRow("Location",data.formId);
        var row8 = buildRow("Location",data.mobileFormEntryErrorId);
        var row9 = buildRow("Location",data.dateCreated);
        var row10 = buildRow("Location",data.error);
        var row11 = buildRow("Location",data.errorDetails);
       
        
       /*  for(i=1;i<12;i++){
        	
        	tblBody.appendChild(row+i);	
        } */

        tblBody.appendChild(row1);
        tblBody.appendChild(row2);
        tblBody.appendChild(row3); 
        tblBody.appendChild(row4);
        tblBody.appendChild(row5); 
        tblBody.appendChild(row6); 
        tblBody.appendChild(row7); 
        tblBody.appendChild(row8); 
        tblBody.appendChild(row9);
        tblBody.appendChild(row10); 
        tblBody.appendChild(row11); 

        tbl.appendChild(tblBody);
        // appends <table> into <body>
        body.appendChild(tbl);
        // sets the border attribute of tbl to 2;
        tbl.setAttribute("border", "2");
        body ="";
        //---------------------------------------
     
    }

	/**
	 * updates the numSelected element with a specified amount, with some after-effects
	 **/
	function updateNumSelected(amount){
		var numSelected = (typeof amount === "undefined") ? $j("input[name=errorIds]:checked").length : amount;

		$j("span.numSelected").html(numSelected);

		if (numSelected > 0)
			$j("#reprocessAll").removeAttr("disabled");
		else
			$j("#reprocessAll").attr("disabled", "disabled");

		if (numSelected != $j("span.numDisplayed").html())
			$j("#selectAll").removeAttr("checked");
	}

</script>

<div id="dialog-form" title="Error Summary" style="display:none;">
    <p class="validateTips">Error Summary Display Window</p>
    <div id="ttable"></div>

</div>

<div><b class="boxHeader">Mobile Form Entry Errors</b>
	<div class="box">
		<div id="tools">
			<input id="selectAll" type="checkbox"/> Select All <span class="numDisplayed"></span> in Search Results (including unseen)
			 &nbsp; &nbsp;
			<button id="reprocessAll" disabled>Reprocess <span class="numSelected">0</span> Selected Errors</button>
            <button id="test">Test Click</button>
		</div>
		<div id="errors">
			<form method="post">
				<table id="errorTable" cellpadding="8" cellspacing="0">
					<thead>
						<tr>
							<th>Select</th>
                            <th>Action</th>
                            <th>ID</th>
                            <th>Error</th>
							<th>Error Details</th>
							<th>Form Name</th>
							<th>Comment</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
			</form>
		</div>
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>