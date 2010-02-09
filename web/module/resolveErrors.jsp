<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="View Mobile Form Errors" otherwise="/login.htm" redirect="/module/amrsmobileforms/resolveErrors.list"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp"%>

<h2><spring:message code="amrsmobileforms.resolveErrors.title"/></h2>

<c:set var="errorSize" value="${fn:length(formEntryErrors)}" />

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
		</style>

		<div><b class="boxHeader">Mobile Form Entry Errors</b>
			<div class="box">
			
				<form method="post">
					<table cellpadding="8" cellspacing="0">
						<tr>
							<th>Error</th>
							<th>Error Details</th>
							<th>Form Name</th>
							<th></th>
							<th></th>
							
						</tr>
						<c:forEach items="${formEntryErrors}" var="error" >
							<tr>
								<td>${error.error}</td>
								<td>${error.errorDetails}</td>
								<td>${error.formName}</td>
								<td>
									<c:set var="isCommented" value="${fn:length(error.comment)}" />
									<c:choose>
										<c:when test="${isCommented < 1}">
											<a href="commentOnError.form?errorId=${error.id}"><input type="button" value='Comment' class="closeButton"/></a>
										</c:when>
										<c:otherwise>
											<a href="resolveError.form?errorId=${error.id}"><input type="button" value='Resolve' class="closeButton"/></a>
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</c:forEach>
					</table>
				</form>
			</div>
		</div>
	</c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/template/footer.jsp" %>