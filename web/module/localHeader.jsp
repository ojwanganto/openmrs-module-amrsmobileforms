<ul id="menu">
	<li class="first">
		<a href="${pageContext.request.contextPath}/admin"><spring:message code="admin.title.short"/></a>
	</li>
	
	<openmrs:hasPrivilege privilege="View Mobile Form Entry Properties">
		<li <c:if test='<%= request.getRequestURI().contains("mobileformentry/properties") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/mobileformentry/propertiesPage.form">
				<spring:message code="mobileformentry.properties"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	
	<openmrs:hasPrivilege privilege="View Mobile Form Entry Properties">
		<li <c:if test='<%= request.getRequestURI().contains("mobileformentry/mobileResources") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/mobileformentry/mobileResources.list">
				<spring:message code="mobileformentry.mobileResources"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	
	<openmrs:hasPrivilege privilege="Manage Economic Objects">
		<li <c:if test='<%= request.getRequestURI().contains("mobileformentry/economicObject") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/mobileformentry/economicObject.form">
				<spring:message code="mobileformentry.economicObjects"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	
	<openmrs:hasPrivilege privilege="View Mobile Form Errors">
		<li <c:if test='<%= request.getRequestURI().contains("mobileformentry/resolveErrors") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/mobileformentry/resolveErrors.list">
				<spring:message code="mobileformentry.resolveErrors.title"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	
</ul>