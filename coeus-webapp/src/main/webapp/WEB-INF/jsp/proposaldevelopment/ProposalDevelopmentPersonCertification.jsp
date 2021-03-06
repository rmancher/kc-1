<%--
   - Kuali Coeus, a comprehensive research administration system for higher education.
   - 
   - Copyright 2005-2015 Kuali, Inc.
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/WEB-INF/jsp/kraTldHeader.jsp"%>
<kul:page lookup="true" docTitle="Proposal Person Certification" transactionalDocument="true" htmlFormAction="proposalDevelopment">
	<script language="javascript" src="scripts/kuali_application.js"></script>
	<link type="text/css" href="css/jquery/questionnaire.css" rel="stylesheet">
	<script>var $j = jQuery.noConflict();</script>
	<script type="text/javascript" src="scripts/questionnaireAnswer.js"></script>

	
	<div id="workarea">
	<c:out value = "${proposalPersonProperty}"/>
	<c:out value = "${status.index}"/>
	<kra-summary:proposalDevelopmentPersonCertification  />
	<kul:panelFooter />
	<div id="globalbuttons" class="globalbuttons"> 
 		<html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" styleClass="globalbuttons" onclick="window.close()" title="close" alt="close"/>
	</div>
	</div>
</kul:page>
