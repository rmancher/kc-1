-- Foreign Key Constraint(s) 
ALTER TABLE VALID_PROTO_SUB_REV_TYPE 
    ADD CONSTRAINT FK2_VALID_PROTO_SUB_REV_TYPE 
            FOREIGN KEY (PROTOCOL_REVIEW_TYPE_CODE) 
                REFERENCES PROTOCOL_REVIEW_TYPE (PROTOCOL_REVIEW_TYPE_CODE) ;

ALTER TABLE VALID_PROTO_SUB_REV_TYPE 
    ADD CONSTRAINT FK_VALID_PROTO_SUB_REV_TYPE 
            FOREIGN KEY (SUBMISSION_TYPE_CODE) 
                REFERENCES SUBMISSION_TYPE (SUBMISSION_TYPE_CODE) ;

