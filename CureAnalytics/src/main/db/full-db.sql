------------------------------------------------------------------------------
-- Copyright (c) 2013 HealthCare It, Inc.
-- All rights reserved. This program and the accompanying materials
-- are made available under the terms of the BSD 3-Clause license
-- which accompanies this distribution, and is available at
-- http://directory.fsf.org/wiki/License:BSD_3Clause
-- 
-- Contributors:
--     HealthCare It, Inc - initial API and implementation
------------------------------------------------------------------------------
--
-- PostgreSQL database dump
--

-- Dumped from database version 8.4.11
-- Dumped by pg_dump version 9.0.4
-- Started on 2013-02-27 12:20:53

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 7 (class 2615 OID 17013)
-- Name: CaHope; Type: SCHEMA; Schema: -; Owner: cahope
--

CREATE SCHEMA "CaHope";


ALTER SCHEMA "CaHope" OWNER TO cahope;

SET search_path = "CaHope", pg_catalog;

--
-- TOC entry 20 (class 1255 OID 17026)
-- Dependencies: 7 315
-- Name: update_lastmodified_column(); Type: FUNCTION; Schema: CaHope; Owner: cahope
--

CREATE FUNCTION update_lastmodified_column() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
  BEGIN
    NEW.timestamp = NOW();
    RETURN NEW;
  END;
$$;


ALTER FUNCTION "CaHope".update_lastmodified_column() OWNER TO cahope;

--
-- TOC entry 1502 (class 1259 OID 17014)
-- Dependencies: 7
-- Name: report_template_id_seq; Type: SEQUENCE; Schema: CaHope; Owner: cahope
--

CREATE SEQUENCE report_template_id_seq
    START WITH 21
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE "CaHope".report_template_id_seq OWNER TO cahope;

--
-- TOC entry 1806 (class 0 OID 0)
-- Dependencies: 1502
-- Name: report_template_id_seq; Type: SEQUENCE SET; Schema: CaHope; Owner: cahope
--

SELECT pg_catalog.setval('report_template_id_seq', 27, true);


SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 1503 (class 1259 OID 17016)
-- Dependencies: 1785 1786 7
-- Name: report_templates; Type: TABLE; Schema: CaHope; Owner: cahope; Tablespace: 
--

CREATE TABLE report_templates (
    id bigint DEFAULT nextval('report_template_id_seq'::regclass) NOT NULL,
    report character varying(10000),
    title text,
    "timestamp" timestamp without time zone DEFAULT now(),
    owner_id bigint,
    shared boolean
);


ALTER TABLE "CaHope".report_templates OWNER TO cahope;

--
-- TOC entry 1506 (class 1259 OID 17037)
-- Dependencies: 7
-- Name: role; Type: TABLE; Schema: CaHope; Owner: cahope; Tablespace: 
--

CREATE TABLE role (
    id bigint NOT NULL,
    name character varying(20),
    display_name character varying(20)
);


ALTER TABLE "CaHope".role OWNER TO cahope;

--
-- TOC entry 1505 (class 1259 OID 17030)
-- Dependencies: 7
-- Name: user; Type: TABLE; Schema: CaHope; Owner: cahope; Tablespace: 
--

CREATE TABLE "user" (
    id bigint NOT NULL,
    username character varying(25),
    password character varying(130),
    created_date date,
    email_addr character varying(25)
);


ALTER TABLE "CaHope"."user" OWNER TO cahope;

--
-- TOC entry 1507 (class 1259 OID 17042)
-- Dependencies: 7
-- Name: user_role; Type: TABLE; Schema: CaHope; Owner: cahope; Tablespace: 
--

CREATE TABLE user_role (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);


ALTER TABLE "CaHope".user_role OWNER TO cahope;

--
-- TOC entry 1504 (class 1259 OID 17028)
-- Dependencies: 7
-- Name: user_seq; Type: SEQUENCE; Schema: CaHope; Owner: cahope
--

CREATE SEQUENCE user_seq
    START WITH 6
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE "CaHope".user_seq OWNER TO cahope;

--
-- TOC entry 1807 (class 0 OID 0)
-- Dependencies: 1504
-- Name: user_seq; Type: SEQUENCE SET; Schema: CaHope; Owner: cahope
--

SELECT pg_catalog.setval('user_seq', 6, false);




--
-- TOC entry 1801 (class 0 OID 17037)
-- Dependencies: 1506
-- Data for Name: role; Type: TABLE DATA; Schema: CaHope; Owner: cahope
--

INSERT INTO role VALUES (1, 'ROLE_USER', 'User');
INSERT INTO role VALUES (2, 'ROLE_ADMIN', 'Administrator');


--
-- TOC entry 1800 (class 0 OID 17030)
-- Dependencies: 1505
-- Data for Name: user; Type: TABLE DATA; Schema: CaHope; Owner: cahope
--

INSERT INTO "user" VALUES (1, 'admin', 'd033e22ae348aeb5660fc2140aec35850c4da997', '2012-06-04', 'admin@admin.com');


--
-- TOC entry 1802 (class 0 OID 17042)
-- Dependencies: 1507
-- Data for Name: user_role; Type: TABLE DATA; Schema: CaHope; Owner: cahope
--

INSERT INTO user_role VALUES (1, 1);
INSERT INTO user_role VALUES (1, 2);


--
-- TOC entry 1788 (class 2606 OID 17025)
-- Dependencies: 1503 1503
-- Name: pk_report_template_id; Type: CONSTRAINT; Schema: CaHope; Owner: cahope; Tablespace: 
--

ALTER TABLE ONLY report_templates
    ADD CONSTRAINT pk_report_template_id PRIMARY KEY (id);


--
-- TOC entry 1794 (class 2606 OID 17041)
-- Dependencies: 1506 1506
-- Name: roles_pkey; Type: CONSTRAINT; Schema: CaHope; Owner: cahope; Tablespace: 
--

ALTER TABLE ONLY role
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- TOC entry 1790 (class 2606 OID 17036)
-- Dependencies: 1505 1505
-- Name: unique_username; Type: CONSTRAINT; Schema: CaHope; Owner: cahope; Tablespace: 
--

ALTER TABLE ONLY "user"
    ADD CONSTRAINT unique_username UNIQUE (username);


--
-- TOC entry 1792 (class 2606 OID 17034)
-- Dependencies: 1505 1505
-- Name: users_pri_key; Type: CONSTRAINT; Schema: CaHope; Owner: cahope; Tablespace: 
--

ALTER TABLE ONLY "user"
    ADD CONSTRAINT users_pri_key PRIMARY KEY (id);


--
-- TOC entry 1798 (class 2620 OID 17027)
-- Dependencies: 1503 20
-- Name: update_lastmodified_modtime; Type: TRIGGER; Schema: CaHope; Owner: cahope
--

CREATE TRIGGER update_lastmodified_modtime
    BEFORE UPDATE ON report_templates
    FOR EACH ROW
    EXECUTE PROCEDURE update_lastmodified_column();


--
-- TOC entry 1796 (class 2606 OID 17045)
-- Dependencies: 1506 1507 1793
-- Name: fk_roleId_user_role; Type: FK CONSTRAINT; Schema: CaHope; Owner: cahope
--

ALTER TABLE ONLY user_role
    ADD CONSTRAINT "fk_roleId_user_role" FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE;


--
-- TOC entry 1797 (class 2606 OID 17050)
-- Dependencies: 1791 1507 1505
-- Name: fk_userId_user_role; Type: FK CONSTRAINT; Schema: CaHope; Owner: cahope
--

ALTER TABLE ONLY user_role
    ADD CONSTRAINT "fk_userId_user_role" FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE;


--
-- TOC entry 1795 (class 2606 OID 17055)
-- Dependencies: 1503 1505 1791
-- Name: report_owner_fk; Type: FK CONSTRAINT; Schema: CaHope; Owner: cahope
--

ALTER TABLE ONLY report_templates
    ADD CONSTRAINT report_owner_fk FOREIGN KEY (owner_id) REFERENCES "user"(id) ON DELETE CASCADE;


--
-- TOC entry 1805 (class 0 OID 0)
-- Dependencies: 7
-- Name: CaHope; Type: ACL; Schema: -; Owner: cahope
--

REVOKE ALL ON SCHEMA "CaHope" FROM PUBLIC;
REVOKE ALL ON SCHEMA "CaHope" FROM cahope;
GRANT ALL ON SCHEMA "CaHope" TO cahope;
GRANT ALL ON SCHEMA "CaHope" TO PUBLIC;


-- Completed on 2013-02-27 12:20:58

--
-- PostgreSQL database dump complete
--

