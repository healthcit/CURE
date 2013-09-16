CREATE OR REPLACE VIEW form_instance_data_by_owner_and_child_ordinal AS 
 WITH RECURSIVE owner_data(form_id, form_name, entity_id, sharing_group_id, instance_id, parent_instance_id) AS (
                 SELECT s.form_id, f.description, s.entity_id, s.sharing_group_id, s.instance_id, s.parent_instance_id
                   FROM sharing_group_form_instance s, forms f
                  WHERE s.form_id::text = f.id::text AND s.parent_instance_id IS NULL
        UNION 
                 SELECT s.form_id, f.description, s.entity_id, s.sharing_group_id, s.instance_id, s.parent_instance_id
                   FROM sharing_group_form_instance s, owner_data c, forms f
                  WHERE s.sharing_group_id::text = c.sharing_group_id::text AND s.parent_instance_id = c.instance_id AND f.id::text = s.form_id::text AND f.parent_id::text = c.form_id::text
        )
 SELECT owner_data.form_id, owner_data.form_name, owner_data.entity_id, owner_data.sharing_group_id, owner_data.instance_id, owner_data.parent_instance_id, row_number() OVER (PARTITION BY owner_data.sharing_group_id, owner_data.form_id, owner_data.parent_instance_id ORDER BY owner_data.instance_id) AS ordinal
   FROM owner_data
  ORDER BY owner_data.form_id;

ALTER TABLE form_instance_data_by_owner_and_child_ordinal OWNER TO cacure;

