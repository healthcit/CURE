---------------------------------------------------------------------------------------------------
-- Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved-
-- Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
-- Proprietary and confidential
---------------------------------------------------------------------------------------------------

ALTER table form add column parent_id bigint;
ALTER table form ADD column relationship_name character varying(255);
ALTER table form ADD column multiple_instances boolean;