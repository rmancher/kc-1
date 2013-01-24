set define off
set sqlblanklines on
spool KC-RELEASE-5_1_0-Upgrade-ORACLE-Install.log
@../../current/5.1.0/sequences/KC_SEQ_BUDGET_SUBAWARD_PER_DET.sql
@../../current/5.1.0/sequences/KC_SEQ_PERSON_SIGNATURE.sql
@../../current/5.1.0/tables/KC_TBL_AWARD_SUBCONTRACT_GOALS_EXP.sql
@../../current/5.1.0/tables/KC_TBL_AWARD_TEMPLATE_COMMENT.sql
@../../current/5.1.0/tables/KC_TBL_AWARD_UNIT_CONTACTS.sql
@../../current/5.1.0/tables/KC_TBL_BUDGET_DETAILS.sql
@../../current/5.1.0/tables/KC_TBL_BUDGET_PERIODS.sql
@../../current/5.1.0/tables/KC_TBL_BUDGET_SUB_AWARDS.sql
@../../current/5.1.0/tables/KC_TBL_BUDGET_SUB_AWARD_PERIOD_DETAIL.sql
@../../current/5.1.0/tables/KC_TBL_COEUS_SUB_MODULE.sql
@../../current/5.1.0/tables/KC_TBL_COI_DISCLOSURE_NOTEPAD.sql
@../../current/5.1.0/tables/KC_TBL_COI_NOTIFICATION.sql
@../../current/5.1.0/tables/KC_TBL_COMM_MEMBER_EXPERTISE.sql
@../../current/5.1.0/tables/KC_TBL_COMM_RESEARCH_AREAS.sql
@../../current/5.1.0/tables/KC_TBL_EPS_PROPOSAL.sql
@../../current/5.1.0/tables/KC_TBL_IACUC_PROTOCOL_NOTEPAD.sql
@../../current/5.1.0/tables/KC_TBL_IACUC_PROTOCOL_NOTIFICATION.sql
@../../current/5.1.0/tables/KC_TBL_IACUC_PROTOCOL_OLR_STATUS.sql
@../../current/5.1.0/tables/KC_TBL_IACUC_PROTO_OLR_DT_REC_TYPE.sql
@../../current/5.1.0/tables/KC_TBL_NEGOTIATION_NOTIFICATION.sql
@../../current/5.1.0/tables/KC_TBL_PERSON_EXT_T.sql
@../../current/5.1.0/tables/KC_TBL_PERSON_SIGNATURE.sql
@../../current/5.1.0/tables/KC_TBL_PROPOSAL.sql
@../../current/5.1.0/tables/KC_TBL_PROPOSAL_LOG.sql
@../../current/5.1.0/tables/KC_TBL_PROTOCOL_NOTEPAD.sql
@../../current/5.1.0/tables/KC_TBL_PROTOCOL_NOTIFICATION.sql
@../../current/5.1.0/tables/KC_TBL_QUESTIONNAIRE_USAGE.sql
@../../current/5.1.0/tables/KC_TBL_S2S_OPPORTUNITY.sql
@../../current/5.1.0/tables/KC_TBL_S2S_PROVIDERS.sql
@../../current/5.1.0/tables/KC_TBL_SPONSOR.sql
@../../current/5.1.0/tables/KC_TBL_WATERMARK.sql
@../../current/5.1.0/dml/KC_DML_01_KCCOI-324_B000.sql
@../../current/5.1.0/dml/KC_DML_01_KCIAC-375_B000.sql
@../../current/5.1.0/dml/KC_DML_01_KCIAC-446_B000.sql
@../../current/5.1.0/dml/KC_DML_01_KCIAC-449_B000.sql
@../../current/5.1.0/dml/KC_DML_01_KRACOEUS-5693_B000.sql
@../../current/5.1.0/dml/KC_DML_01_KRACOEUS-5812_B000.sql
@../../current/5.1.0/dml/KC_DML_01_KRACOEUS-6004_B000.sql
@../../current/5.1.0/dml/KC_DML_01_KRACOEUS-6005_B000.sql
@../../current/5.1.0/dml/KC_DML_01_KRACOEUS-6013_B000.sql
@../../current/5.1.0/dml/KC_DML_01_KRACOEUS-6045_B000.sql
@../../current/5.1.0/dml/KC_DML_01_KRACOEUS-6056_B000.sql
@../../current/5.1.0/dml/KC_DML_01_KRACOEUS-6076_B000.sql
@../../current/5.1.0/dml/KC_DML_01_KRACOEUS-6105_B000.sql
@../../current/5.1.0/constraints/KC_FK1_BUDGET_SUBAWARD_PER_DET.sql
@../../current/5.1.0/constraints/KC_FK1_BUDGET_SUB_AWARDS.sql
@../../current/5.1.0/constraints/KC_FK2_BUDGET_DETAILS.sql
@../../current/5.1.0/constraints/KC_FK_COMM_MEMBER_EXPERTISE.sql
@../../current/5.1.0/constraints/KC_FK_COMM_RESEARCH_AREAS.sql
@../../current/5.1.0/constraints/KC_FK_S2S_PROVIDERS.sql
@../../current/5.1.0/constraints/KC_IX_KRACOEUS-6157.sql
@../../current/5.1.0/constraints/KC_UQ_PERSON_SIGNATURE.sql
commit;
exit