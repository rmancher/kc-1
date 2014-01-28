DELIMITER /
CREATE TABLE IACUC_PROTOCOL_REVIEWERS  ( 
    PROTOCOL_REVIEWER_ID    DECIMAL(12,0) NOT NULL,
    PROTOCOL_ID             DECIMAL(12,0) NOT NULL,
    SUBMISSION_ID_FK        DECIMAL(12,0) NOT NULL,
    PROTOCOL_NUMBER         VARCHAR(20) NOT NULL,
    SEQUENCE_NUMBER         DECIMAL(4,0) NOT NULL,
    SUBMISSION_NUMBER       DECIMAL(4,0) NOT NULL,
    PERSON_ID               VARCHAR(40) NULL,
    NON_EMPLOYEE_FLAG       CHAR(1) NOT NULL,
    REVIEWER_TYPE_CODE      VARCHAR(3) NOT NULL,
    UPDATE_TIMESTAMP        DATE NOT NULL,
    UPDATE_USER             VARCHAR(60) NOT NULL,
    VER_NBR                 DECIMAL(8,0) DEFAULT 1 NOT NULL,
    OBJ_ID                  VARCHAR(36) NOT NULL,
    ROLODEX_ID              DECIMAL(6,0) NULL
) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/
ALTER TABLE IACUC_PROTOCOL_REVIEWERS
ADD CONSTRAINT PK_IACUC_PROTOCOL_REVIEWERS 
PRIMARY KEY (PROTOCOL_REVIEWER_ID) 
/
DELIMITER ;