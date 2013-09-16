---------------------------------------------------------------------------------------------------
-- Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved-
-- Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
-- Proprietary and confidential
---------------------------------------------------------------------------------------------------

CREATE TABLE entity_tag_permission (entity_id character varying(40), tag_id character varying(500), permission character varying(10));

CREATE TABLE TAG (tag_id character varying(500));

ALTER TABLE forms ADD COLUMN tag_id character varying(500);

ALTER TABLe entity_tag_permission ADD CONSTRAINT entity_tag_permission_fk FOREIGN KEY (entity_id) REFERENCES core_entity (id) ON DELETE CASCADE;
