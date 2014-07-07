/*
 * Copyright 2005-2014 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.s2s.generator.impl;

import gov.grants.apply.forms.edCertificationDebarmentV11.CertificationDebarmentDocument;
import gov.grants.apply.forms.edCertificationDebarmentV11.CertificationDebarmentDocument.CertificationDebarment;
import gov.grants.apply.system.attachmentsV10.AttachedFileDataType;
import org.apache.xmlbeans.XmlObject;
import org.kuali.coeus.common.api.org.OrganizationContract;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.propdev.api.attachment.NarrativeContract;
import org.kuali.kra.s2s.generator.FormGenerator;
import org.kuali.kra.s2s.generator.S2SBaseFormGenerator;
import org.kuali.kra.s2s.generator.bo.DepartmentalPerson;
import org.kuali.kra.s2s.service.S2SUtilService;
import org.kuali.kra.s2s.util.S2SConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Calendar;

/**
 * Class for generating the XML object for grants.gov EDcertificationDebarmentV1_1. Form is generated using XMLBean classes and is
 * based on EDCertificationDebarment schema.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
@FormGenerator("EDCertificationDebarmentV1_1Generator")
public class EDCertificationDebarmentV1_1Generator extends S2SBaseFormGenerator {
    private static final int ED_CERTIFICATION_DEBARMENT = 58;

    @Autowired
    @Qualifier("s2SUtilService")
    private S2SUtilService s2sUtilService;

    /**
     * 
     * This method is used to get CertificationDebarment information such as Organization Name,Authorized Representative
     * Title,Authorized Representative Name,Signature and Attachment for this form.
     * 
     * @return certificationDebarmentDocument(CertificationDebarmentDocument){@link XmlObject} of type
     *         CertificationDebarmentDocument.
     */
    private CertificationDebarmentDocument getCertificationDebarment() {

        CertificationDebarmentDocument certificationDebarmentDocument = CertificationDebarmentDocument.Factory.newInstance();
        CertificationDebarment certificationDebarment = CertificationDebarment.Factory.newInstance();
        certificationDebarment.setFormVersion(S2SConstants.FORMVERSION_1_1);
        OrganizationContract organization = null;
        if (pdDoc.getDevelopmentProposal().getApplicantOrganization() != null) {
            organization = pdDoc.getDevelopmentProposal().getApplicantOrganization().getOrganization();
        }
        if (organization != null && organization.getOrganizationName() != null) {
            if (organization.getOrganizationName().length() > 60) {
                certificationDebarment.setOrganizationName(organization.getOrganizationName().substring(0, 60));
            }
            else {
                certificationDebarment.setOrganizationName(organization.getOrganizationName());
            }
        }
        DepartmentalPerson departmentalPerson = s2sUtilService.getDepartmentalPerson(pdDoc);
        String authorizedRepresentativeTitle = "";
        String authorizedRepresentativeSignature = "";

        if (departmentalPerson != null) {
            if (departmentalPerson.getPrimaryTitle() != null && !departmentalPerson.getPrimaryTitle().equals("")) {
                if (departmentalPerson.getPrimaryTitle().length() > 45) {
                    authorizedRepresentativeTitle = departmentalPerson.getPrimaryTitle().substring(0, 45);
                }
                else {
                    authorizedRepresentativeTitle = departmentalPerson.getPrimaryTitle();
                }
            }

            if (departmentalPerson.getFullName() != null && !departmentalPerson.getFullName().equals("")) {
                authorizedRepresentativeSignature = departmentalPerson.getFullName();
            }
        }

        certificationDebarment.setAuthorizedRepresentativeTitle(authorizedRepresentativeTitle);
        certificationDebarment.setAuthorizedRepresentativeName(globLibV20Generator.getHumanNameDataType(departmentalPerson));
        certificationDebarment.setAuthorizedRepresentativeSignature(authorizedRepresentativeSignature);
        certificationDebarment.setSubmittedDate(Calendar.getInstance());
        AttachedFileDataType attachedFileDataType = null;
        for (NarrativeContract narrative : pdDoc.getDevelopmentProposal().getNarratives()) {
            if (narrative.getNarrativeType().getCode() != null
                    && Integer.parseInt(narrative.getNarrativeType().getCode()) == ED_CERTIFICATION_DEBARMENT) {
                attachedFileDataType = getAttachedFileType(narrative);
                if(attachedFileDataType != null){
                	certificationDebarment.setAttachment(attachedFileDataType);
                	break;
                }
            }
        }
        certificationDebarmentDocument.setCertificationDebarment(certificationDebarment);
        return certificationDebarmentDocument;
    }

    /**
     * This method creates {@link XmlObject} of type {@link CertificationDebarmentDocument} by populating data from the given
     * {@link ProposalDevelopmentDocument}
     * 
     * @param proposalDevelopmentDocument for which the {@link XmlObject} needs to be created
     * @return {@link XmlObject} which is generated using the given {@link ProposalDevelopmentDocument}
     * @see org.kuali.kra.s2s.generator.S2SFormGenerator#getFormObject(ProposalDevelopmentDocument)
     */
    public XmlObject getFormObject(ProposalDevelopmentDocument proposalDevelopmentDocument) {
        this.pdDoc = proposalDevelopmentDocument;
        return getCertificationDebarment();
    }

    public S2SUtilService getS2sUtilService() {
        return s2sUtilService;
    }

    public void setS2sUtilService(S2SUtilService s2sUtilService) {
        this.s2sUtilService = s2sUtilService;
    }
}