---------------------------------------------------------------------------------------------------
-- Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved-
-- Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
-- Proprietary and confidential
---------------------------------------------------------------------------------------------------

CREATE TABLE core_entity_sharing_group (entity_id character varying(40), group_id character varying(40));

ALTER TABLE entity_tag_permission ADD COLUMN group_id character varying(40);

INSERT INTO core_entity_sharing_group (entity_id, group_id) SELECT id, sharing_group_id from core_entity;

update entity_tag_permission set group_id = ce.sharing_group_id from core_entity ce where entity_id=ce.id;