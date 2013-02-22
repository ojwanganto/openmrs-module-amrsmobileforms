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
	.tblformat tr:nth-child(odd) {
		background-color: #009d8e;
		color: #FFFFFF;
	}
	.tblformat tr:nth-child(even) {
		background-color: #d3d3d3;
		color: #000000;
	}
</style>
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
                  
                  var tableExist = $j("#errorsummary");
                  if(tableExist!=null){
                	  $j("#errorsummary").remove();  
                  }
                  generate_table(errObj,"ttable",1);



            });


            
            $j( "#dialog-form" ).dialog({
                height: 600,
                width: 1000,
                modal: false,
                buttons:{
                    "Submit Comment":function(){
                    	 var commentt = $j("#comment").val();
                    	 
                    	 if(commentt != null){
                    		 DWRAMRSMobileFormsService.saveComment(errorId,commentt,alertResult);
                        	  
                    	 }
                    	 else{
                    		 alert("Please the field for comment is empty!");
                    		 $j("#comment").focus();
                    	 }
                    	 

                    },
                    Cancel: function() {
                        $j(this).dialog( "close" );
                    }
                }

            });

            return false;

        });
		
		
		
		

        $j("button.resolve").live("click", function(){ 
        	//document.location = "resolveError.form?errorId=" + $j(this).attr("errorId"); return false;
        	
        	var errorId = $j(this).attr("errorId");
        	
        	 var tableExist = $j("#resolveerror");
             if(tableExist!=null){
           	  $j("#resolveerror").remove();  
             }
        	
        	
              DWRAMRSMobileFormsService.populateCommentForm(errorId ,function(data){
                   var errObj= data[0];
                   generate_ResolveError_table(errObj);
                  

             });


             
             $j( "#resolveError" ).dialog({
                 height: 600,
                 width: 1000,
                 modal: false,
                 buttons:{
                     "Submit Comment":function(){
                    	 
                    		var provider = document.getElementById('provider').value;
                           	var newPatient = document.getElementById('patient').value;
                           	var patientId = document.getElementById('patientid').value;
                           	var dob = document.getElementById('dob').value;
                           	var newHousehold = document.getElementById('household').value;
                           	var householdId = document.getElementById('householdid').value;
                           	var errorItemAction = getSelectedRadio();
               	 
                    /*String householdId,
			Integer errorId, 
			String errorItemAction,
			String birthDate, 
			String patientIdentifier,
			String providerId, 
			String householdIdentifier  */	 
                    	 
                   
                     	 if(errorItemAction != null){
  DWRAMRSMobileFormsService.resolveError(householdId,errorId,errorItemAction,dob,patientId,provider,newHousehold,resolveErrorResult);
                         	  
                     	 }
                     	 else{
                     		 alert("Please select the action to take");
                     		
                     	 } 
                     	 

                     },
                     Cancel: function() {
                         $j(this).dialog( "close" );
                     }
                 }

             });

             return false;

 
        });

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
	
	function resolveErrorResult(data){
		alert(data);
		document.location.reload(true);
	}
	
	function alertResult(data){
		alert(data);
		document.location.reload(true);
	}
	
	function buildRow(label,tdvalue){
		
		var row = document.createElement("tr");
        var cell = document.createElement("td");
        var cell2 = document.createElement("td");
        var celllabel = document.createTextNode(label+": ");
        var cellval = document.createTextNode(tdvalue);
        cell.appendChild(celllabel);
        cell2.appendChild(cellval);
        row.appendChild(cell);
        row.appendChild(cell2);
        return row;
	}
	
function buildRowWithElement(label,tdvalue){
		
		var row = document.createElement("tr");
        var cell = document.createElement("td");
        var cell2 = document.createElement("td");
        var div = document.createElement("div");
        div.setAttribute('style','height: 40px; overflow-y: scroll; border: 1px solid #BBB;');
       
        var celllabel = document.createTextNode(label+": ");
        var cellval = document.createTextNode(tdvalue);
        div.appendChild(cellval);
        cell.appendChild(celllabel);
        cell2.appendChild(div);
        row.appendChild(cell);
        row.appendChild(cell2);
        return row;
	}
	
function buildTextArea(label,id){
	var row = document.createElement("tr");
    var cell = document.createElement("td");
    var cell2 = document.createElement("td");
    var celllabel = document.createTextNode(label+": ");
    
    
    var textarea = document.createElement("textarea");
    textarea.setAttribute('cols','120');
    textarea.setAttribute('rows','3');
    textarea.setAttribute('id',id);
    
    cell.appendChild(celllabel);
    cell2.appendChild(textarea);
    
    row.appendChild(cell);
    row.appendChild(cell2);
    return row;
    
    
}

function buildResolveOptions(label,id,optval,name,id2,addText){
	var row = document.createElement("tr");
    var cell = document.createElement("td");
    var cell2 = document.createElement("td");
    var celllabel = document.createTextNode(label);
    
    
    var radio = document.createElement("input");
    radio.setAttribute('type','radio');
    radio.setAttribute('id',id);
    radio.setAttribute('name',name);
    radio.setAttribute('value',optval);
    
    if(addText){
    	
    var inputtext = document.createElement("input");
    inputtext.setAttribute('type','text');
    inputtext.setAttribute('id',id2);
    }
    
    cell.appendChild(radio);
    cell.appendChild(celllabel);
    if(addText){
    	cell2.appendChild(inputtext);
    }
    
    
    row.appendChild(cell);
    row.appendChild(cell2);
    return row;
    
    
}



function createOpemrsSpecificInputs(label,id,optval,name,id2){
	var row = document.createElement("tr");
    var cell = document.createElement("td");
    var cell2 = document.createElement("td");
    var celllabel = document.createTextNode(label);
    
    
    var radio = document.createElement("input");
    radio.setAttribute('type','radio');
    radio.setAttribute('id',id);
    radio.setAttribute('name',name);
    radio.setAttribute('value',name);
    
    	
    /* var inputtext = document.createElement("openmrs_tag:userField");
    inputtext.setAttribute('formFieldName',id2);
    inputtext.setAttribute('searchLabelCode','amrsmobileforms.resolveErrors.action.findProvider');
    inputtext.setAttribute('initialValue','');
    inputtext.setAttribute('callback','setErrorAction'); */
    

    var inputtext = document.createElement("input");
    inputtext.setAttribute('id','providerId');
    inputtext.setAttribute('type','text');
    inputtext.setAttribute('class','ui-autocomplete-input');
    inputtext.setAttribute("role","textbox");
    inputtext.setAttribute('aria-autocomplete','list');
    inputtext.setAttribute('aria-haspopup','true');
    
    
    cell.appendChild(radio);
    cell.appendChild(celllabel);

    	cell2.appendChild(inputtext);

    
    
    row.appendChild(cell);
    row.appendChild(cell2);
    return row;
    
    
}

/* Displays error summary on table 
@param data - returned from the database
@param bodyDiv - div to hold the table with data
@param option (1 = comment, 2 = resolve) - selects between comment and resolve data 
*/

    function generate_table(data,bodyDiv,option) {

        var body = document.getElementById(bodyDiv);
        
        var tbl     = document.createElement("table");
        tbl.setAttribute('width','100%');
        tbl.setAttribute('class','tblformat');
     
		tbl.setAttribute('id','errorsummary');
	

        var tblBody = document.createElement("tbody");
        var row1 = buildRow("Person Name",data.name);      
        var row2 = buildRow("Person Identifier",data.identifier);
        var row3 = buildRow("Gender",data.gender);
        var row4 = buildRow("Location",data.location);
        
        var row5 = buildRow("Encounter Date",data.encounterDate);
        var row6 = buildRow("Form Name",data.formModelName);
        var row7 = buildRow("Form ID",data.formId);
        var row8 = buildRow("Error ID",data.mobileFormEntryErrorId);
        var row9 = buildRow("Date Created",data.dateCreated);
        var row10 = buildRow("Error",data.error);
        
        var totalHousehold = buildRow("Total Household",data.totalHousehold);
        var totalEligible = buildRow("Total Eligible",data.totalEligible);
        var providerId = buildRow("Provider Id",data.providerId);
        
        var row13 = buildRowWithElement("Error Details",data.errorDetails);
        //var row14 = buildRowWithElement("XML Form",data.formName);
        
        var comment = buildTextArea("Comment","comment");
        
     
  
        
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
        tblBody.appendChild(totalHousehold);
        tblBody.appendChild(totalEligible);
        tblBody.appendChild(providerId);
        tblBody.appendChild(row13);
        //tblBody.appendChild(row14);
        
        tblBody.appendChild(comment);
        
        tbl.appendChild(tblBody);
        
        body.appendChild(tbl);
        tbl.setAttribute("border", "1");
        
        //---------------------------------------
     
    }
    
    
function generate_ResolveError_table(data) {

        
        // get the reference for the body
        var body = document.getElementById("resolveErrorTable");
        
        var tbl     = document.createElement("table");
        tbl.setAttribute('width','100%');
     
		 tbl.setAttribute('id','resolveerror');
		 tbl.setAttribute('class','tblformat');
	
        var tblBody = document.createElement("tbody");
	
   
    	 var row0 = buildRow("Comment",data.comment);
    	 var row01 = buildRow("Commented By",data.commentedBy);
    	 var row02 = buildRow("Date Commented",data.dateCommented);
   	

        var row1 = buildRow("Person Name",data.name);      
        var row2 = buildRow("Person Identifier",data.identifier);
        var row3 = buildRow("Gender",data.gender);
        var row4 = buildRow("Location",data.location);
        
        var row5 = buildRow("Encounter Date",data.encounterDate);
        var row6 = buildRow("Form Name",data.formModelName);
        var row7 = buildRow("Form ID",data.formId);
        var row8 = buildRow("Error ID",data.mobileFormEntryErrorId);
        var row9 = buildRow("Date Created",data.dateCreated);
        var row10 = buildRow("Error",data.error);
        
        var totalhousehold = buildRow("Total Household",data.totalHousehold);
        var totaleligible = buildRow("Total Eligible",data.totalEligible);
        var providerId = buildRow("Provider Id",data.providerId);
        var row11 = buildRowWithElement("Error Details",data.errorDetails);
        var row12 = buildRowWithElement("XML Form",data.formName);
        //createOpemrsSpecificInputs(label,id,name,id2)
        //(label,id,optval,name,id2,addText)
       	var row13 = buildResolveOptions('Select Provider for this Patient Encounter','optprovider','linkProvider','errorItemAction','provider',true);
       	var row14 = buildResolveOptions('Create a new patient using the data from this form','optpatient','createPatient','errorItemAction','patient',true);
       	var row15 = buildResolveOptions('Assign a new patient Identifier to this Patient','optpatientid','newIdentifier','errorItemAction','patientid',true);
       	var row16 = buildResolveOptions('Assign a Birth Date to this Patient','optdob','assignBirthdate','errorItemAction','dob',true);
       	var row17 = buildResolveOptions('Household for this Patient','opthousehold','linkHousehold','errorItemAction','household',true);
       	var row18 = buildResolveOptions('Assign a new household Identifier to this household','opthouseholdid','newHousehold','errorItemAction','householdid',true);
       	var row19 = buildResolveOptions('Delete comment','optdelcomment','deleteComment','errorItemAction','provider2',false);
       	var row20 = buildResolveOptions('Delete this error item because it is invalid','optdelerror','deleteError','errorItemAction','provider2',false);
       	var row21 = buildResolveOptions('Leave this error item as is and process it at a later point','none','noChange','errorItemAction','provider2',false);
       	
        	tblBody.appendChild(row0);
        	tblBody.appendChild(row01);
        	tblBody.appendChild(row02);
       	
        
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
        
        tblBody.appendChild(totalhousehold); 
        tblBody.appendChild(totaleligible); 
        tblBody.appendChild(providerId);
        tblBody.appendChild(row11);
       
        tblBody.appendChild(row12); 
        tblBody.appendChild(row13);
        tblBody.appendChild(row14);
        tblBody.appendChild(row15);
        tblBody.appendChild(row16);
        tblBody.appendChild(row17);
        tblBody.appendChild(row18);
        tblBody.appendChild(row19);
        tblBody.appendChild(row20);
        tblBody.appendChild(row21);
        tbl.appendChild(tblBody);
        
        body.appendChild(tbl);
        tbl.setAttribute("border", "1");
        
        //---------------------------------------
     
    }
    
    function getSelectedRadio(){
    	var selOpt = document.getElementsByName('errorItemAction');
    	
    	for(var i = 0;i<selOpt.length;i++){
    		if(selOpt[i].checked){
    			return selOpt[i].value;
    		}
    		
    	}
    	return null;
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

<div class="box" id="dialog-form" title="Error Summary" style="display:none;">
    <div id="ttable"></div>

</div>

<div class="box" id="resolveError" title="Error Resolution Dialog" style="display:none;">
    <div id="resolveErrorTable">
    
    </div>

</div>

<div><b class="boxHeader">Mobile Form Entry Errors</b>
	<div class="box">
		<div id="tools">
			<input id="selectAll" type="checkbox"/> Select All <span class="numDisplayed"></span> in Search Results (including unseen)
			 &nbsp; &nbsp;
			<button id="reprocessAll" disabled>Reprocess <span class="numSelected">0</span> Selected Errors</button>
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