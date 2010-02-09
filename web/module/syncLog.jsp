<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="View Mobile Form Entry Properties" otherwise="/login.htm" redirect="/module/amrsmobileforms/propertiesPage.form"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp"%>

<h2><spring:message code="amrsmobileforms.sync.title"/></h2>
<br />
<spring:message code="amrsmobileforms.sync.info" />
<br/>

<b class="boxHeader"><spring:message code="amrsmobileforms.sync.successful"/></b>
<table cellpadding="4" cellspacing="0" border="0" class="box">
	<tr>
		<th style="white-space: nowrap"><spring:message code="amrsmobileforms.sync.provider" /></th>
		<th><spring:message code="amrsmobileforms.sync.syncDate" /></th>
		<th><spring:message code="amrsmobileforms.sync.syncDevice" /></th>
		<th><spring:message code="amrsmobileforms.sync.household" /></th>
	</tr>
	<c:forEach items="${logs}" var="log" varStatus="varStatus">
		<tr class="<c:choose><c:when test="${varStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
			<td style="white-space: nowrap">${log.providerId}</td>
			<td>${log.dateCreated}</td>
			<td>${log.deviceId}</td>
			<td>${log.household.householdIdentifier}</td>
		</tr>
	</c:forEach>
</table>
<br />
<%@ include file="/WEB-INF/template/footer.jsp"%>