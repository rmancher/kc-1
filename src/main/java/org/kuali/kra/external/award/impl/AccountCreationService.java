/*
 * Copyright 2005-2010 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.kra.external.award.impl;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by Apache CXF 2.2.9
 * Wed Aug 04 14:02:35 MST 2010
 * Generated source version: 2.2.9
 * 
 */
 
@WebService(targetNamespace = "KC", name = "accountCreationService")
@XmlSeeAlso({ObjectFactory.class})
public interface AccountCreationService {

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "createAccount", targetNamespace = "KC", className = "kc.CreateAccount")
    @WebMethod
    @ResponseWrapper(localName = "createAccountResponse", targetNamespace = "KC", className = "kc.CreateAccountResponse")
    public AccountCreationStatusDTO createAccount(
        @WebParam(name = "accountParametersDTO", targetNamespace = "")
        AccountParametersDTO accountParametersDTO
    );
}

