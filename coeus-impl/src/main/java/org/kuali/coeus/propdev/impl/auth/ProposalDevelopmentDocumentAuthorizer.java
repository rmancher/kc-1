/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.coeus.propdev.impl.auth;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.coeus.common.framework.auth.KcKradTransactionalDocumentAuthorizerBase;
import org.kuali.coeus.common.framework.auth.UnitAuthorizationService;
import org.kuali.coeus.propdev.impl.attachment.NarrativeRight;
import org.kuali.coeus.propdev.impl.attachment.NarrativeUserRights;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentConstants;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.propdev.impl.hierarchy.ProposalHierarchyException;
import org.kuali.coeus.propdev.impl.hierarchy.ProposalHierarchyService;
import org.kuali.coeus.propdev.impl.person.ProposalPerson;
import org.kuali.coeus.propdev.impl.state.ProposalState;
import org.kuali.coeus.common.framework.auth.perm.KcAuthorizationService;
import org.kuali.coeus.common.framework.auth.perm.Permissionable;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.coeus.sys.framework.workflow.KcDocumentRejectionService;
import org.kuali.coeus.sys.framework.workflow.KcWorkflowService;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.infrastructure.PermissionConstants;
import org.kuali.coeus.propdev.impl.attachment.Narrative;
import org.kuali.coeus.propdev.impl.person.attachment.ProposalPersonBiography;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.krad.document.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Proposal Development Document Authorizer.  Primarily responsible for determining if
 * a user has permission to create/modify/view proposals.
 *
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class ProposalDevelopmentDocumentAuthorizer extends KcKradTransactionalDocumentAuthorizerBase {

    private static final Log LOG = LogFactory.getLog(ProposalDevelopmentDocumentAuthorizer.class);

    private KcAuthorizationService kcAuthorizationService;

    private UnitAuthorizationService unitAuthorizationService;

    private KcWorkflowService kcWorkflowService;

    private KcDocumentRejectionService kcDocumentRejectionService;

    private ProposalHierarchyService proposalHierarchyService;

    @Override
    public Set<String> getEditModes(Document document, Person user, Set<String> currentEditModes) {
        Set<String> editModes = new HashSet<String>();
         
        ProposalDevelopmentDocument proposalDoc = (ProposalDevelopmentDocument) document;
        DevelopmentProposal developmentProposal = proposalDoc.getDevelopmentProposal();
		String proposalNbr = developmentProposal.getProposalNumber();
        
        // The getEditMode() method is invoked when a proposal is accessed for creation and when it
        // is accessed for modification.  New proposals under creation don't have a proposal number.
        // For a new proposal, we have to know if the user has the permission to create a proposal.
        // For a current proposal, we have to know if the user the permission to modify or view the proposal.
        
        if (proposalNbr == null) {
            if (isAuthorizedToCreate(document, user)) {
                editModes.add(AuthorizationConstants.EditMode.FULL_ENTRY);
                setPermissions(user, proposalDoc, editModes);
            } 
            else {
                editModes.add(AuthorizationConstants.EditMode.UNVIEWABLE);
            }
        } 
        else {
            if (canEdit(document, user)) {  
                editModes.add(AuthorizationConstants.EditMode.FULL_ENTRY);
                setPermissions(user, proposalDoc, editModes);
            }
            else if (isAuthorizedToView(document, user)) {
                editModes.add(AuthorizationConstants.EditMode.VIEW_ONLY);
                setPermissions(user, proposalDoc, editModes);
            }
            else {
                editModes.add(AuthorizationConstants.EditMode.UNVIEWABLE);
            }
    
	        if (isBudgetComplete(developmentProposal)) {
	            if (editModes.contains("addBudget")) {
	                editModes.add("modifyCompletedBudgets");
	            }
        	    editModes.remove("modifyProposalBudget");
            	editModes.remove("addBudget");
        	}
        }
        
        return editModes;
    }

    @Override
    public boolean canDeleteDocument(Document document, Person user) {
    	if(document != null && document instanceof Permissionable)
    		return getKcAuthorizationService().hasPermission(user.getPrincipalId(), (Permissionable)document, PermissionConstants.DELETE_PROPOSAL);
    	else 
    		return false;
    }
    
    
    
    /**
     * Set the permissions to be used during the creation of the web pages.  
     * The JSP files can access the editModeMap (editingMode) to determine what
     * to display to the user.  For example, a JSP file may contain the following:
     * 
     *     <kra:section permission="modifyProposal">
     *         .
     *         .
     *         .
     *     </kra:section>
     * 
     * In the above example, the contents are only rendered if the user is allowed
     * to modify the proposal.  Note that permissions are always signified as 
     * either TRUE or FALSE.
     * 
     * @param user the user
     * @param doc the Proposal Development Document
     * @param editModes the edit mode map
     */
    private void setPermissions(Person user, ProposalDevelopmentDocument doc, Set<String> editModes) {
        final String userId = user.getPrincipalId();

        if (editModes.contains(AuthorizationConstants.EditMode.FULL_ENTRY)) {
            editModes.add("modifyProposal");
        }
        
        if (isAuthorizedToAddBudget(doc, user)) {
            editModes.add("addBudget");
        }
                
        if (isAuthorizedToOpenBudget(doc, user)) {
            editModes.add("openBudgets");
        }
                
        if (isAuthorizedToModifyBudget(doc, user)) {
            editModes.add("modifyProposalBudget");
        }
                
        if (isAuthorizedToModifyProposalRoles(doc, user)) {
            editModes.add("modifyPermissions");
        }
                
        if (isAuthorizedToAddNarrative(doc, user)) {
            editModes.add("addNarratives");
        }
                   
        if (isAuthorizedToCertify(doc, user)) {
            editModes.add("certify");
        }
                
        if (isAuthorizedToPrint(doc, user)) {
            editModes.add("printProposal");
        }
                
        if (isAuthorizedToAlterProposalData(doc, user)) {
            editModes.add("alterProposalData");
        }
                
        if (isAuthorizedToShowAlterProposalData(doc, user)) {
            editModes.add("showAlterProposalData");
        }
                
        if (isAuthorizedToSubmitToSponsor(doc, user)) {
            editModes.add("submitToSponsor");
        }
        if (isAuthorizedToMaintainProposalHierarchy(doc, user)) {
            editModes.add("maintainProposalHierarchy");
        }
        
        if (isAuthorizedToRejectProposal(doc, user)) {
            editModes.add("rejectProposal");
        }

        if (isAuthorizedToAddAddressBook(doc,user)) {
            editModes.add("addAddressBook");
        }
        
        setNarrativePermissions(user, doc, editModes);
    } 
    
    private void setNarrativePermissions(Person user, ProposalDevelopmentDocument doc, Set<String> editModes) {

        List<Narrative> narratives = doc.getDevelopmentProposal().getNarratives();
        for (Narrative narrative : narratives) {
            String prefix = "proposalAttachment." + narrative.getModuleNumber() + ".";
            if (isAuthorizedToViewNarrative(narrative, user)) {
                editModes.add(prefix + "download");
            }
            if (isAuthorizedToReplaceNarrative(narrative, user)) {
                editModes.add(prefix + "replace");
            }
            if (isAuthorizedToDeleteNarrative(narrative, user)) {
                editModes.add(prefix + "delete");
            }
            if (isAuthorizedToModifyNarrative(narrative, user)) {
                editModes.add(prefix + "modifyStatus");
            }
            if (isAuthorizedToModifyNarrative(narrative, user)) {
                editModes.add(prefix + "modifyRights");
            }
        }
        
        narratives = doc.getDevelopmentProposal().getInstituteAttachments();
        for (Narrative narrative : narratives) {
            String prefix = "instituteAttachment." + narrative.getModuleNumber() + ".";
            if (isAuthorizedToViewNarrative(narrative, user)) {
                editModes.add(prefix + "download");
            }
            if (isAuthorizedToReplaceNarrative(narrative, user) ) {
                editModes.add(prefix + "replace");
            }
            if (isAuthorizedToDeleteNarrative(narrative, user)) {
                editModes.add(prefix + "delete");
            }
            if (isAuthorizedToModifyNarrative(narrative, user)) {
                editModes.add(prefix + "modifyRights");
            }
        }
        

        int i = 0;
        boolean canReplace = isAuthorizedToReplacePersonnelAttachement(doc, user);
        for (ProposalPersonBiography ppb : doc.getDevelopmentProposal().getPropPersonBios()) {
            ppb.setPositionNumber(i);
            String prefix = "biographyAttachments." + ppb.getPositionNumber() + ".";
            if (canReplace) {
                editModes.add(prefix + "replace");
            }
            
            i++;
        }
    }
    
    public boolean canOpen(Document document, Person user) {
        ProposalDevelopmentDocument proposalDocument = (ProposalDevelopmentDocument) document;
        if (proposalDocument.getDevelopmentProposal().getProposalNumber() == null) {
            return isAuthorizedToCreate(document, user);
        }
        return isAuthorizedToView(document, user);
    }
    
    @Override
    public boolean canEdit(Document document, Person user) {
        return isAuthorizedToModify(document, user);
    }
    
    @Override
    public boolean canSave(Document document, Person user) {
        return canEdit(document, user);
    }
    
    @Override
    public boolean canCancel(Document document, Person user) {
        return canEdit(document, user) && super.canCancel(document, user);
    }
    
    @Override
    public boolean canReload(Document document, Person user) {
        WorkflowDocument workflow = document.getDocumentHeader().getWorkflowDocument();
        return canEdit(document, user) && !workflow.isInitiated() || workflow.isCanceled();
    }
    
    @Override
    public boolean canRoute(Document document, Person user) {
        return isAuthorizedToSubmitToWorkflow(document, user) && isAuthorizedToHierarchyChildWorkflowAction(document, user);
    }
    
    @Override
    public boolean canAnnotate(Document document, Person user) {
        return canRoute(document, user) && isAuthorizedToHierarchyChildWorkflowAction(document, user);
    }
    
    @Override
    public boolean canCopy(Document document, Person user) {
        return false;
    }
    
    @Override
    public boolean canApprove( Document document, Person user ) {
        return super.canApprove(document,user) && isAuthorizedToHierarchyChildWorkflowAction(document, user);
    }
    
    @Override
    public boolean canDisapprove( Document document, Person user ) {
        return super.canDisapprove(document, user) && isAuthorizedToHierarchyChildWorkflowAction(document, user);
    }
    
    @Override
    public boolean canBlanketApprove( Document document, Person user ) {
        return super.canBlanketApprove(document, user) && isAuthorizedToHierarchyChildWorkflowAction(document, user);
    }
    
    @Override
    public boolean canAcknowledge( Document document, Person user ) {
        return super.canAcknowledge(document, user) && isAuthorizedToHierarchyChildAckWorkflowAction(document, user);
    }
    
    protected boolean isBudgetComplete(DevelopmentProposal developmentProposal) {
    	return developmentProposal.getFinalBudget() != null;
    }
    
    @Override
    public boolean canAddNoteAttachment(Document document, String attachmentTypeCode, Person user) {
        return isAuthorizedToAddNote(document, user);
    }
    
    @Override
    public boolean canDeleteNoteAttachment(Document document, String attachmentTypeCode, String createdBySelfOnly, Person user) {
        boolean retVal = false;
        Boolean allowNotesDeletion = getParameterService().getParameterValueAsBoolean(ProposalDevelopmentDocument.class, KeyConstants.ALLOW_PROPOSAL_DEVELOPMENT_NOTES_DELETION); 
        if(allowNotesDeletion != null && allowNotesDeletion) {
            retVal = super.canDeleteNoteAttachment(document, attachmentTypeCode, createdBySelfOnly, user);
        }
        return retVal;
    }
    
    @Override
    public boolean canViewNoteAttachment(Document document, String attachmentTypeCode, Person user) {
        return isAuthorizedToView(document, user);
    }

    @Override
    public boolean canFyi( Document document, Person user ) {
        return super.canFyi(document, user) && isAuthorizedToHierarchyChildWorkflowAction(document, user);
    }

    @Override
    public boolean canSendNoteFyi(Document document, Person user) {
        return false;
    }
    
    @Override
    public boolean canRecall(Document document, Person user) {
        return isAuthorizedToRecallProposal(document, user);
    }

    public boolean hasCertificationPermissions(ProposalDevelopmentDocument document, Person user, ProposalPerson proposalPerson){
        if (getParameterService().getParameterValueAsBoolean(ProposalDevelopmentDocument.class, ProposalDevelopmentConstants.Parameters.KEY_PERSON_CERTIFICATION_SELF_CERTIFY_ONLY)) {

            // null person indicates non employee and only people with proxy perms can certify for them so return false below
            boolean isKeyPersonnel = proposalPerson.getPerson() != null && proposalPerson.getPerson().getPersonId().equals(user.getPrincipalId());
            boolean canCertify = getKcAuthorizationService().hasPermission(user.getPrincipalId(), document, PermissionConstants.CERTIFY);

            return isKeyPersonnel || canCertify;
        }
        return true;
    }

    protected boolean isAuthorizedToReplaceNarrative(Narrative narrative, Person user) {
        final ProposalDevelopmentDocument pdDocument = (ProposalDevelopmentDocument) narrative.getDevelopmentProposal().getDocument();

        boolean hasPermission = false;
        if (!pdDocument.getDevelopmentProposal().getSubmitFlag() && getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.MODIFY_NARRATIVE)) {
            hasPermission = hasNarrativeRight(user.getPrincipalId(), narrative, NarrativeRight.MODIFY_NARRATIVE_RIGHT);
        }

        return hasPermission;
    }

    protected boolean isAuthorizedToModifyNarrative(Narrative narrative, Person user) {
        final ProposalDevelopmentDocument pdDocument = (ProposalDevelopmentDocument) narrative.getDevelopmentProposal().getDocument();

        boolean rejectedDocument = getKcDocumentRejectionService().isDocumentOnInitialNode(pdDocument.getDocumentNumber());
        boolean hasPermission = false;
        boolean inWorkflow = getKcWorkflowService().isInWorkflow(pdDocument);
        if ((!inWorkflow || rejectedDocument) && !pdDocument.getDevelopmentProposal().getSubmitFlag()) {
            hasPermission = getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.MODIFY_NARRATIVE);
        } else if(inWorkflow && !rejectedDocument && !pdDocument.getDevelopmentProposal().getSubmitFlag()) {
            if(getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.MODIFY_NARRATIVE)) {
                hasPermission = getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.MODIFY_NARRATIVE);
            }
        }
        return hasPermission;
    }

    protected boolean isAuthorizedToViewNarrative(Narrative narrative, Person user) {
        final ProposalDevelopmentDocument pdDocument = (ProposalDevelopmentDocument) narrative.getDevelopmentProposal().getDocument();

        // First, the user must have the VIEW_NARRATIVE permission.  This is really
        // a sanity check.  If they have the VIEW or MODIFY_NARRATIVE_RIGHT, then they are
        // required to have the VIEW_NARRATIVE permission.

        boolean hasPermission = false;
        if (getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.VIEW_NARRATIVE)) {
            hasPermission = hasNarrativeRight(user.getPrincipalId(), narrative, NarrativeRight.VIEW_NARRATIVE_RIGHT)
                    || hasNarrativeRight(user.getPrincipalId(), narrative, NarrativeRight.MODIFY_NARRATIVE_RIGHT);
        }

        if (!hasPermission) {
            hasPermission = getUnitAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument.getDevelopmentProposal().getOwnedByUnitNumber(), Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT,
                    PermissionConstants.VIEW_NARRATIVE)
                    || getKcWorkflowService().hasWorkflowPermission(user.getPrincipalId(), pdDocument);
        }

        return hasPermission;
    }

    public boolean isAuthorizedToDeleteNarrative(Narrative narrative, Person user) {
        final ProposalDevelopmentDocument pdDocument = (ProposalDevelopmentDocument) narrative.getDevelopmentProposal().getDocument();

        // First, the user must have the MODIFY_NARRATIVE permission.  This is really
        // a sanity check.  If they have the MODIFY_NARRATIVE_RIGHT, then they are
        // required to have the MODIFY_NARRATIVE permission.

        KcDocumentRejectionService documentRejectionService = getKcDocumentRejectionService();
        boolean rejectedDocument = documentRejectionService.isDocumentOnInitialNode(pdDocument.getDocumentNumber());
        boolean hasPermission = false;

        boolean inWorkflow = getKcWorkflowService().isInWorkflow(pdDocument);

        if ((!inWorkflow || rejectedDocument) && !pdDocument.getDevelopmentProposal().getSubmitFlag()) {
            if (getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.MODIFY_NARRATIVE)) {
                hasPermission = hasNarrativeRight(user.getPrincipalId(), narrative, NarrativeRight.MODIFY_NARRATIVE_RIGHT);
            }
        }
        return hasPermission;
    }

    /**
     * Does the user have the given narrative right for the given narrative?
     * @param userId the username of the user
     * @param narrative the narrative
     * @param narrativeRight the narrative right we are looking for
     * @return true if the user has the narrative right for the narrative
     */
    protected final boolean hasNarrativeRight(String userId, Narrative narrative, NarrativeRight narrativeRight) {
        List<NarrativeUserRights> userRightsList = narrative.getNarrativeUserRights();
        for (NarrativeUserRights userRights : userRightsList) {
            if (StringUtils.equals(userId, userRights.getUserId())) {
                if (StringUtils.equals(userRights.getAccessType(), narrativeRight.getAccessType())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isAuthorizedToModifyBudget(Document document, Person user) {
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);

        boolean rejectedDocument = getKcDocumentRejectionService().isDocumentOnInitialNode(pdDocument.getDocumentNumber());
        return ( (!getKcWorkflowService().isInWorkflow(pdDocument) || rejectedDocument) &&
                getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.MODIFY_BUDGET));
    }

    protected boolean isAuthorizedToOpenBudget(Document document, Person user) {
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);

        return getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.VIEW_BUDGET)
                || getKcWorkflowService().hasWorkflowPermission(user.getPrincipalId(), pdDocument);
    }

    protected boolean isAuthorizedToAddBudget(Document document, Person user) {
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);

        boolean hasPermission = false;

        boolean rejectedDocument = getKcDocumentRejectionService().isDocumentOnInitialNode(pdDocument.getDocumentNumber());

        if ((!getKcWorkflowService().isInWorkflow(pdDocument) || rejectedDocument) && !pdDocument.isViewOnly() && !pdDocument.getDevelopmentProposal().getSubmitFlag() && !pdDocument.getDevelopmentProposal().isParent()) {
            hasPermission = getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.MODIFY_BUDGET);
        }
        return hasPermission;
    }

    protected boolean isAuthorizedToAddNarrative(Document document, Person user) {
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);

        boolean rejectedDocument = getKcDocumentRejectionService().isDocumentOnInitialNode(pdDocument.getDocumentNumber());
        boolean hasPermission = false;
        if ((!getKcWorkflowService().isInWorkflow(pdDocument) || rejectedDocument) && !pdDocument.isViewOnly() && !pdDocument.getDevelopmentProposal().getSubmitFlag()) {
            hasPermission = getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.MODIFY_NARRATIVE);
        }
        return hasPermission;
    }

    protected boolean isAuthorizedToCertify(Document document, Person user) {
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);
        return getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.CERTIFY);
    }

    protected boolean isAuthorizedToReplacePersonnelAttachement(Document document, Person user) {
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);
        boolean result = false;
        if (pdDocument.getDevelopmentProposal().getProposalState() != null) {
            boolean hasPerm = getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.MODIFY_NARRATIVE);
            boolean isInProgress = StringUtils.equalsIgnoreCase(pdDocument.getDevelopmentProposal().getProposalState().getDescription(), "In Progress");
            boolean isApprovalPending = StringUtils.equalsIgnoreCase(pdDocument.getDevelopmentProposal().getProposalState().getDescription(), "Approval Pending");
            boolean isRevisionRequested = StringUtils.equalsIgnoreCase(pdDocument.getDevelopmentProposal().getProposalState().getDescription(), "Revisions Requested");
            result = hasPerm && (isInProgress || isApprovalPending || isRevisionRequested);
        }
        return result;
    }

    protected boolean isAuthorizedToRecallProposal(Document document, Person user) {
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);
        return pdDocument.getDocumentHeader().hasWorkflowDocument() && pdDocument.getDocumentHeader().getWorkflowDocument().isEnroute()
                && getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.RECALL_DOCUMENT);
    }

    protected boolean isAuthorizedToRejectProposal(Document document, Person user) {
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);
        WorkflowDocument workDoc = pdDocument.getDocumentHeader().getWorkflowDocument();
        return (!workDoc.isCompletionRequested()) && (! getKcDocumentRejectionService().isDocumentOnInitialNode(pdDocument)) && (workDoc.isApprovalRequested()) && (workDoc.isEnroute());
    }

    protected boolean isAuthorizedToSubmitToWorkflow(Document document, Person user) {
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);
        return !getKcWorkflowService().isInWorkflow(pdDocument) &&
                getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.SUBMIT_PROPOSAL) &&
                !pdDocument.getDevelopmentProposal().isChild();
    }

    protected boolean isAuthorizedToHierarchyChildWorkflowAction(Document document, Person user) {
        boolean authorized = true;
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);

        if(pdDocument.getDevelopmentProposal().isChild() ) {
            try {
                final WorkflowDocument parentWDoc  = getProposalHierarchyService().getParentWorkflowDocument(pdDocument);
                    if(!parentWDoc.isInitiated()) {
                        authorized = false;
                    }
            } catch (ProposalHierarchyException e) {
                LOG.error( String.format( "Could not find parent workflow document for proposal document number:%s, which claims to be a child. Returning false.", pdDocument.getDocumentHeader().getDocumentNumber()), e);
                authorized = false;
            }
        }

        return authorized;
    }

    protected boolean isAuthorizedToHierarchyChildAckWorkflowAction(Document document, Person user) {
        boolean authorized = true;
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);

        if(pdDocument.getDevelopmentProposal().isChild() ) {
            try {
                WorkflowDocument parentWDoc  = getProposalHierarchyService().getParentWorkflowDocument(pdDocument);
                if((!parentWDoc.isAcknowledgeRequested()) || parentWDoc.isInitiated()) {
                    authorized = false;
                }
            } catch (ProposalHierarchyException e) {
                LOG.error( String.format( "Could not find parent workflow document for proposal document number:%s, which claims to be a child. Returning false.", pdDocument.getDocumentHeader().getDocumentNumber()),e);
                authorized = false;
            }
        }

        return authorized;
    }

    protected boolean isAuthorizedToMaintainProposalHierarchy(Document document, Person user) {
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);
        return getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.MAINTAIN_PROPOSAL_HIERARCHY);
    }

    protected boolean isAuthorizedToAlterProposalData(Document document, Person user) {
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);
        //standard is authorized calculation without taking child status into account.
        boolean ret = getKcWorkflowService().isEnRoute(pdDocument) &&
                !pdDocument.getDevelopmentProposal().getSubmitFlag() &&
                getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.ALTER_PROPOSAL_DATA);

        //check to see if the parent is enroute, if so deny the edit attempt.
        if(pdDocument.getDevelopmentProposal().isChild() ) {
            try {
                if (getProposalHierarchyService().getParentWorkflowDocument(pdDocument).isEnroute()) {
                    ret = false;
                }
            } catch (ProposalHierarchyException e) {
                LOG.error(String.format( "Exception looking up parent of DevelopmentProposal %s, authorizer is going to deny edit access to this child.", pdDocument.getDevelopmentProposal().getProposalNumber()), e);
                ret = false;
            }
        }
        return ret;
    }

    protected boolean isAuthorizedToShowAlterProposalData(Document document, Person user) {
        return getKcWorkflowService().isInWorkflow(document);
    }

    protected boolean isAuthorizedToModifyProposalRoles(Document document, Person user) {
        return (hasFullAuthorization(document, user) || hasAddViewerAuthorization(document, user));
    }

    /**
     * This method checks if the user has full (pre-workflow/pre-submission) proposal access maintenance rights
     * @param user the user requesting access
     * @param document the document object
     * @return true if the user has full (pre-workflow/pre-submission) proposal access maintenance rights
     */
    protected boolean hasFullAuthorization(Document document, Person user) {
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);
        return !pdDocument.isViewOnly()
                && getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.MAINTAIN_PROPOSAL_ACCESS)
                && !getKcWorkflowService().isInWorkflow(pdDocument)
                && !pdDocument.getDevelopmentProposal().getSubmitFlag();
    }

    /**
     * This method checks if the user has rights to add proposal viewers.
     * @param user the user requesting access
     * @param document the document object
     */
    protected boolean hasAddViewerAuthorization (Document document, Person user) {
        boolean hasPermission = false;
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);

        if (getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.ADD_PROPOSAL_VIEWER)
                && getKcWorkflowService().isInWorkflow(pdDocument)) {
            // once workflowed (OSP Administrator and Aggregator have ADD_PROPOSAL_VIEWER permission)
            hasPermission = true;
        } else if (getKcWorkflowService().hasWorkflowPermission(user.getPrincipalId(), pdDocument)
                && getKcWorkflowService().isEnRoute(pdDocument)) {
            // Approvers (users in workflow) have permission while EnRoute
            hasPermission = true;
        }

        return hasPermission;
    }

    protected boolean isAuthorizedToCreate(Document document, Person user) {
        return getUnitAuthorizationService().hasPermission(user.getPrincipalId(), Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, PermissionConstants.CREATE_PROPOSAL);
    }

    protected boolean isAuthorizedToSubmitToSponsor(Document document, Person user) {
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);
        return getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.SUBMIT_TO_SPONSOR) &&
                !pdDocument.getDevelopmentProposal().isChild();
    }

    protected boolean isAuthorizedToPrint(Document document, Person user) {
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);
        return getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.PRINT_PROPOSAL);
    }

    protected boolean isAuthorizedToView(Document document, Person user) {
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);
        return getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.VIEW_PROPOSAL)
                || getKcWorkflowService().hasWorkflowPermission(user.getPrincipalId(), pdDocument);
    }

    protected boolean isAuthorizedToAddNote(Document document, Person user) {

        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);

        String proposalNbr = pdDocument.getDevelopmentProposal().getProposalNumber();

        final boolean hasPermission;
        if (proposalNbr == null) {
            hasPermission = hasPermissionByOwnedByUnit(document, user);
        } else {

            hasPermission = getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.VIEW_PROPOSAL)
                    || getKcWorkflowService().hasWorkflowPermission(user.getPrincipalId(), document);
        }
        return hasPermission;
    }

    protected boolean isAuthorizedToAddAddressBook(Document doc, Person user) {
        final boolean hasPermission;
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) doc);

        hasPermission = getPermissionService().hasPermission(user.getPrincipalId(), Constants.MODULE_NAMESPACE_UNIT,PermissionConstants.ADD_ADDRESS_BOOK);
        return hasPermission;
    }

    protected boolean isAuthorizedToModify(Document document, Person user) {

        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);
        final DevelopmentProposal proposal = pdDocument.getDevelopmentProposal();

        if (!isEditableState(proposal.getProposalStateTypeCode())) {
            return false;
        }

        final String proposalNbr = proposal.getProposalNumber();

        final boolean hasPermission;
        if (proposalNbr == null) {
            hasPermission = hasPermissionByOwnedByUnit(document, user);
        } else {
            /*
             * After the initial save, the proposal can only be modified if it is not in workflow
             * and the user has the require permission.
             */
            final boolean hasBeenRejected = getKcDocumentRejectionService().isDocumentOnInitialNode(document);
            hasPermission = !pdDocument.isViewOnly() &&
                    getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.MODIFY_PROPOSAL) &&
                    (!getKcWorkflowService().isInWorkflow(document) || hasBeenRejected) &&
                    !proposal.getSubmitFlag();
        }
        return hasPermission;
    }

    protected boolean hasPermissionByOwnedByUnit(Document document, Person user) {
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);
        final DevelopmentProposal proposal = pdDocument.getDevelopmentProposal();

        String unitNumber = proposal.getOwnedByUnitNumber();

        // If the unit number is not specified, we will let the save operation continue because it
        // will fail with an error.  But if the user tries to save a proposal for a wrong unit, then
        // we will indicate that the user does not have permission to do that.
        return (unitNumber != null && getUnitAuthorizationService().hasPermission(user.getPrincipalId(), unitNumber, Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, PermissionConstants.CREATE_PROPOSAL)
                || unitNumber == null);
    }

    protected boolean isEditableState(String propsalState) {
        return !ProposalState.CANCELED.equals(propsalState) && !ProposalState.DISAPPROVED.equals(propsalState);
    }

	public KcAuthorizationService getKcAuthorizationService() {
		if (kcAuthorizationService == null) {
            kcAuthorizationService = KcServiceLocator.getService(KcAuthorizationService.class);
        }
		return kcAuthorizationService;
	}

	public void setKcAuthorizationService(KcAuthorizationService kcAuthorizationService) {
		this.kcAuthorizationService = kcAuthorizationService;
	}

    public UnitAuthorizationService getUnitAuthorizationService() {
        if (unitAuthorizationService == null) {
            unitAuthorizationService = KcServiceLocator.getService(UnitAuthorizationService.class);
        }
        return unitAuthorizationService;
    }

    public void setUnitAuthorizationService(UnitAuthorizationService unitAuthorizationService) {
        this.unitAuthorizationService = unitAuthorizationService;
    }

    public KcWorkflowService getKcWorkflowService() {
        if (kcWorkflowService == null) {
            kcWorkflowService = KcServiceLocator.getService(KcWorkflowService.class);
        }
        return kcWorkflowService;
    }

    public void setKcWorkflowService(KcWorkflowService kcWorkflowService) {
        this.kcWorkflowService = kcWorkflowService;
    }

    public KcDocumentRejectionService getKcDocumentRejectionService() {
        if (kcDocumentRejectionService == null) {
            kcDocumentRejectionService = KcServiceLocator.getService(KcDocumentRejectionService.class);
        }
        return kcDocumentRejectionService;
    }

    public void setKcDocumentRejectionService(KcDocumentRejectionService kcDocumentRejectionService) {
        this.kcDocumentRejectionService = kcDocumentRejectionService;
    }

    protected ProposalHierarchyService getProposalHierarchyService (){
        if (kcDocumentRejectionService == null) {
            proposalHierarchyService = KcServiceLocator.getService(ProposalHierarchyService.class);
        }
        return proposalHierarchyService;
    }

    public void setProposalHierarchyService (ProposalHierarchyService proposalHierarchyService){
        this.proposalHierarchyService = proposalHierarchyService;
    }
}