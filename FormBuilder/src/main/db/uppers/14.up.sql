---------------------------------------------------------------------------------------------------
-- Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved-
-- Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
-- Proprietary and confidential
---------------------------------------------------------------------------------------------------

alter table form add column form_library_form_id bigint;
ALTER TABLE ONLY form ADD CONSTRAINT fb_form_library_form_fk FOREIGN KEY (form_library_form_id) REFERENCES form(id);