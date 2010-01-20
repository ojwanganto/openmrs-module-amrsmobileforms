<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Resolve Mobile Form Entry Error" otherwise="/login.htm" redirect="/module/mobileformentry/resolveErrors.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<h2>
	<spring:message code="mobileformentry.resolveErrors.title" />
</h2>

<c:set var="errorSize" value="${fn:length(errorFormResolve)}" />

<c:choose>
	<c:when test="${errorSize < 1}">
		<br/>
		<i>(<spring:message code="mobileformentry.resolveErrors.errorLoading"/>)</i>
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
		
		<b class="boxHeader"><spring:message code="mobileformentry.resolveErrors.title" />:</b>
		<div class="box">
			<form method="post" action="">
				<table cellpadding="3" cellspacing="0" width="100%" id="resolveErrorsTable">
					<c:forEach var="queueItem" items="${errorFormResolve}" varStatus="queueItemStatus">
						<tr>
							<td><b><spring:message code="mobileformentry.commentOnError.title" />: </b>${queueItem.comment}</td>
						</tr>
						<tr class="<c:choose><c:when test="${queueItemStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
							<td>
								<!-- Info about the patient and encounter -->
								<spring:message code="mobileformentry.commentOnError.commentor" />: 
								<span >
									${queueItem.commentedBy.names}
									<spring:message code="mobileformentry.commentOnError.date" />:
									${queueItem.dateCommented}
								</span> <br/><br/>
								<spring:message code="Person.name" />: <span class="value">${queueItem.name}</span> <br/>
								<spring:message code="Patient.identifier" />: <span class="value">${queueItem.identifier}</span> <br/>
								<spring:message code="Person.gender" />: <span class="value">${queueItem.gender}</span> <br/>
								<br/>
								<spring:message code="Encounter.location" />: <span class="value">${queueItem.location}</span> <br/>
								<spring:message code="Encounter.datetime" />: <span class="value">${queueItem.encounterDate}</span> <br/>
								<spring:message code="mobileformentry.resolveErrors.formName" />: <span class="value">${queueItem.formModelName} v${queueItem.formId}</span> <br/>
								<br/>
								<spring:message code="mobileformentry.resolveErrors.errorId" />: <span >${queueItem.mobileFormEntryErrorId}</span> <br/>
								<spring:message code="mobileformentry.resolveErrors.errorDateCreated" />: <span >${queueItem.dateCreated}</span> <br/>
								<spring:message code="mobileformentry.resolveErrors.error" />: <span >${queueItem.error}</span> <br/><br/>
								<b><spring:message code="mobileformentry.resolveErrors.errorDetails" />: </b><div style="height: 40px; overflow-y: scroll; border: 1px solid #BBB;">${queueItem.errorDetails}</div> <br/>
							</td>
						</tr>
						<tr class="secondRow <c:choose><c:when test="${queueItemStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
							<td>
								<input type="hidden" name="mobileFormEntryErrorId" value="${queueItem.mobileFormEntryErrorId}"/>
								
								<!-- Pick a matching patient -->
								<input type="radio" name="errorItemAction" value="linkHousehold" /> 
								<spring:message code="mobileformentry.resolveErrors.action.createLink"/>:
								<input type="text" name="householdId"/> <br/>
								
								<!-- Have the machinery create a new patient -->
								<input type="radio" name="errorItemAction" value="createPatient" /> 
								<spring:message code="mobileformentry.resolveErrors.action.createPatient"/> <br/>
								
								<!-- This is an invalid comment, delete it -->
								<input type="radio" name="errorItemAction" value="deleteComment" />
								<spring:message code="mobileformentry.resolveErrors.action.deleteComment"/> <br/>
								
								<!-- This is an invalid error, delete it -->
								<input type="radio" name="errorItemAction" value="deleteError" />
								<spring:message code="mobileformentry.resolveErrors.action.deleteError"/> <br/>
								
								<!-- I don't want to do anything to this one now -->
								<input type="radio" name="errorItemAction" value="noChange" checked="checked"/>
								<spring:message code="mobileformentry.resolveErrors.action.noChange"/> <br/>
								
								<br/>
							</td>
						</tr>
					</c:forEach>
						<tr>
							<td colspan="2">
								<input type="submit" name="action" value='<spring:message code="general.submit" />' />
							</td>
						</tr>
				</table>
			</form>
		</div>

	</c:otherwise>
</c:choose>

<br/>

<%@ include file="/WEB-INF/template/footer.jsp"%>
