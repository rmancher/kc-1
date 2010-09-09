CREATE TABLE VALID_PROTO_SUB_TYPE_QUAL (
    VALID_PROTO_SUB_TYPE_QUAL_ID DECIMAL (12, 0) NOT NULL, 
    SUBMISSION_TYPE_CODE VARCHAR (3) NOT NULL, 
    SUBMISSION_TYPE_QUAL_CODE VARCHAR (3) NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR (60) NOT NULL, 
    VER_NBR DECIMAL (8, 0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR (36) NULL) ;

-- Primary Key Constraint  
ALTER TABLE VALID_PROTO_SUB_TYPE_QUAL 
    ADD CONSTRAINT PK_VALID_PROTO_SUB_TYPE_QUAL 
            PRIMARY KEY (VALID_PROTO_SUB_TYPE_QUAL_ID) ;

-- *************** UNIQUE CONSTRAINT DEFINED FOR COMPOSITE KEY COLUMNS ************  
ALTER TABLE VALID_PROTO_SUB_TYPE_QUAL 
    ADD CONSTRAINT UQ_VALID_PROTO_SUB_TYPE_QUAL 
            UNIQUE (SUBMISSION_TYPE_CODE, SUBMISSION_TYPE_QUAL_CODE) ;

