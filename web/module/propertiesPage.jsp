<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="View Mobile Form Entry Properties" otherwise="/login.htm" redirect="/module/amrsmobileforms/propertiesPage.form"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp"%>

<h2><spring:message code="amrsmobileforms.title"/></h2>
<br />
<table cellpadding="4" cellspacing="0">
	<tr>
		<th><spring:message code="SystemInfo.name"/></th>
		<th><spring:message code="SystemInfo.value"/></th>
	</tr>
	<c:forEach items="${systemVars}" var="var" varStatus="status">
		<tr class="<c:choose><c:when test="${status.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
			<td>${var.key}</td>
			<td>${var.value}</td>
		</tr>
	</c:forEach>
</table>

<br/><br/>
<%@ include file="/WEB-INF/template/footer.jsp" %>