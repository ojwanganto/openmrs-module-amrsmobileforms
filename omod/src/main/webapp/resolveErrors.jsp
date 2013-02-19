<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="View Mobile Form Errors" otherwise="/login.htm" redirect="/module/amrsmobileforms/resolveErrors.list"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp"%>

<openmrs:htmlInclude file="/moduleResources/amrsmobileforms/js/jquery.dataTables.min.js" />

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
                    { aTargets: [3], mData: "location"},
                    { aTargets: [4], mData: "provider"},
                    { aTargets: [5], mData: "error" },
                    { aTargets: [6], mData: "errorDetails" },
                    { aTargets: [7], mData: "formName" },
                    { aTargets: [8], mData: "comment", sClass: "centered" }
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
		$j("button.comment").live("click", function(){ document.location = "resolveErrorComment.form?errorId=" + $j(this).attr("errorId"); return false; });
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
                            <th>Location</th>
                            <th>Provider</th>
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