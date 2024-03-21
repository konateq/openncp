# Create database
CREATE DATABASE IF NOT EXISTS `ehealth_properties`;

USE `ehealth_properties`;

--  *********************************************************************
--  Update Database Script
--  *********************************************************************
--  Change Log: src/main/resources/openncp-properties/db.changelog.xml
--  Ran at: 11/23/23, 9:47 AM
--  Against: null@offline:mysql?changeLogFile=D:\projects\work\dg_sante\ehealth\openncp-common-components\openncp-database-initializer\target/liquibase/databasechangelog-mysql.csv
--  Liquibase version: 4.10.0
--  *********************************************************************

--  Changeset src/main/resources/openncp-properties/db.changelog.xml::1::subigre
CREATE TABLE EHNCP_PROPERTY (NAME VARCHAR(255) NOT NULL, VALUE VARCHAR(255) NULL, IS_SMP BIT(1) NULL, CONSTRAINT PK_EHNCP_PROPERTY PRIMARY KEY (NAME));

CREATE TABLE EHNCP_ROLE (ID BIGINT AUTO_INCREMENT NOT NULL, NAME VARCHAR(50) NOT NULL, `DESCRIPTION` VARCHAR(255) NOT NULL, CONSTRAINT PK_EHNCP_ROLE PRIMARY KEY (ID), UNIQUE (NAME));

CREATE TABLE EHNCP_USER (ID BIGINT AUTO_INCREMENT NOT NULL, USERNAME VARCHAR(50) NOT NULL, PASSWORD VARCHAR(255) NOT NULL, ENABLED BIT(1) DEFAULT 1 NOT NULL, EMAIL VARCHAR(45) NULL, VERIFICATION_CODE VARCHAR(64) NULL, RESET_DATE datetime(6) NULL, RESET_KEY VARCHAR(255) NULL, CONSTRAINT PK_EHNCP_USER PRIMARY KEY (ID), UNIQUE (USERNAME));

CREATE TABLE EHNCP_USER_ROLE (USER_ID BIGINT NOT NULL, ROLE_ID BIGINT NOT NULL, CONSTRAINT FK_EHNCP_USER_ROLE_USER FOREIGN KEY (USER_ID) REFERENCES EHNCP_USER(ID), CONSTRAINT FK_EHNCP_USER_ROLE_ROLE FOREIGN KEY (ROLE_ID) REFERENCES EHNCP_ROLE(ID));

ALTER TABLE EHNCP_USER_ROLE ADD PRIMARY KEY (USER_ID, ROLE_ID);

--  Changeset src/main/resources/openncp-properties/db.changelog.xml::2::miorial
CREATE TABLE EHNCP_ANOMALY (ID BIGINT AUTO_INCREMENT NOT NULL, `DESCRIPTION` VARCHAR(2000) NULL, TYPE VARCHAR(20) NULL, EVENT_DATE datetime NULL, EVENT_START_DATE datetime NULL, EVENT_END_DATE datetime NULL, CONSTRAINT PK_EHNCP_ANOMALY PRIMARY KEY (ID));

