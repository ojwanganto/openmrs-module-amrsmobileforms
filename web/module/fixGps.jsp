<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="View Mobile Form Errors" otherwise="/login.htm" redirect="/module/amrsmobileforms/resolveErrors.list"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp"%>

<h2><spring:message code="amrsmobileforms.resolveErrors.title"/></h2>

<c:set var="errorSize" value="${fn:length(formEntyGPSErrors)}" />

<c:choose>
	<c:when test="${errorSize < 1}">
		<br/>
		<i>(<spring:message code="amrsmobileforms.resolveErrors.empty"/>)</i>
		<br/>
	</c:when>
	<c:otherwise>
		
		<style type="text/css">
			#resolveErrorsTable tr td .value {
			  font-weight: bold;
			}
			#resolveErrorsTable tr.secondRow {
			  border-bottom: 1px solid black;
			}
			.resolveButton {
				border: 1px solid gray;
				background-color: #E0E0F0;
				font-size: .75em;
				color: black;
				float: right;
				width: 52px;
				margin: 2px;
				padding: 1px;
				cursor: pointer;
			}.commentButton {
				border: 1px solid gray;
				background-color: lightpink;
				font-size: .75em;
				color: black;
				float: right;
				margin: 2px;
				padding: 1px;
				cursor: pointer;
			}
		</style>

		<div><b class="boxHeader">Mobile Form Entry Errors: (${errorSize})</b>
			<div class="box">
			
				<form method="post">
					<table cellpadding="8" cellspacing="0">
						<tr>
							<th>Error ID</th>
							<th>Error</th>
							<th>Error Details</th>
							<th>Form Name</th>
						</tr>
						<c:forEach items="${formEntyGPSErrors}" var="error" >
							<tr>
								<td><input type="hidden" name="errorId" value="${error.id}"/>${error.id}</td>
								<td>${error.error}</td>
								<td>${error.errorDetails}</td>
								<td>${error.formName}</td>
							</tr>
						</c:forEach>
					</table>
					<input type="submit" value="Fix GPS Format" />
				</form>
			</div>
		</div>
	</c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/template/footer.jsp" %>