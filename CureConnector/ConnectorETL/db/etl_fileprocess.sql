--
-- PostgreSQL database dump
--

-- Started on 2013-04-12 21:56:42

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 8 (class 2615 OID 440404)
-- Name: fileprocess; Type: SCHEMA; Schema: -; Owner: etl
--

CREATE SCHEMA fileprocess;


ALTER SCHEMA fileprocess OWNER TO etl;

SET search_path = fileprocess, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 1549 (class 1259 OID 440405)
-- Dependencies: 1850 1851 8
-- Name: clients; Type: TABLE; Schema: fileprocess; Owner: etl; Tablespace: 
--

CREATE TABLE clients (
    idx integer NOT NULL,
    client_name character varying(250) DEFAULT 'client1'::character varying NOT NULL,
    description character varying(250) DEFAULT 'No Description'::character varying NOT NULL
);


ALTER TABLE fileprocess.clients OWNER TO etl;

--
-- TOC entry 1550 (class 1259 OID 440413)
-- Dependencies: 8
-- Name: email_tmp; Type: TABLE; Schema: fileprocess; Owner: etl; Tablespace: 
--

CREATE TABLE email_tmp (
    id integer NOT NULL,
    submission_id integer NOT NULL,
    mail_part_type bigint NOT NULL,
    mail_part_content character varying(10000),
    tmp_file_name character varying(100),
    pre_transaction_id bigint NOT NULL
);


ALTER TABLE fileprocess.email_tmp OWNER TO etl;

--
-- TOC entry 1551 (class 1259 OID 440419)
-- Dependencies: 8 1550
-- Name: email_tmp_id_seq; Type: SEQUENCE; Schema: fileprocess; Owner: etl
--

CREATE SEQUENCE email_tmp_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE fileprocess.email_tmp_id_seq OWNER TO etl;

--
-- TOC entry 1938 (class 0 OID 0)
-- Dependencies: 1551
-- Name: email_tmp_id_seq; Type: SEQUENCE OWNED BY; Schema: fileprocess; Owner: etl
--

ALTER SEQUENCE email_tmp_id_seq OWNED BY email_tmp.id;


--
-- TOC entry 1939 (class 0 OID 0)
-- Dependencies: 1551
-- Name: email_tmp_id_seq; Type: SEQUENCE SET; Schema: fileprocess; Owner: etl
--

SELECT pg_catalog.setval('email_tmp_id_seq', 391, true);


--
-- TOC entry 1552 (class 1259 OID 440421)
-- Dependencies: 8 1550
-- Name: email_tmp_submission_id_seq; Type: SEQUENCE; Schema: fileprocess; Owner: etl
--

CREATE SEQUENCE email_tmp_submission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE fileprocess.email_tmp_submission_id_seq OWNER TO etl;

--
-- TOC entry 1940 (class 0 OID 0)
-- Dependencies: 1552
-- Name: email_tmp_submission_id_seq; Type: SEQUENCE OWNED BY; Schema: fileprocess; Owner: etl
--

ALTER SEQUENCE email_tmp_submission_id_seq OWNED BY email_tmp.submission_id;


--
-- TOC entry 1941 (class 0 OID 0)
-- Dependencies: 1552
-- Name: email_tmp_submission_id_seq; Type: SEQUENCE SET; Schema: fileprocess; Owner: etl
--

SELECT pg_catalog.setval('email_tmp_submission_id_seq', 1, false);


--
-- TOC entry 1553 (class 1259 OID 440423)
-- Dependencies: 8
-- Name: merge_complex_idx_seq; Type: SEQUENCE; Schema: fileprocess; Owner: etl
--

CREATE SEQUENCE merge_complex_idx_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE fileprocess.merge_complex_idx_seq OWNER TO etl;

--
-- TOC entry 1942 (class 0 OID 0)
-- Dependencies: 1553
-- Name: merge_complex_idx_seq; Type: SEQUENCE SET; Schema: fileprocess; Owner: etl
--

SELECT pg_catalog.setval('merge_complex_idx_seq', 2075, true);


--
-- TOC entry 1554 (class 1259 OID 440425)
-- Dependencies: 1854 1855 1856 1857 8
-- Name: merge_complex; Type: TABLE; Schema: fileprocess; Owner: etl; Tablespace: 
--

CREATE TABLE merge_complex (
    idx integer DEFAULT nextval('merge_complex_idx_seq'::regclass) NOT NULL,
    form_id character varying(100),
    form_name character varying(100),
    question_id character varying(100),
    question_sn character varying(100),
    answer_id character varying(100),
    answer character varying(10000),
    question_text text,
    answer_text text,
    checksum character varying(100) NOT NULL,
    job_id character varying(100),
    table_id character varying(100),
    table_sn character varying(100),
    table_text text,
    answer_sn character varying(100),
    row_id character varying(100),
    is_identifying character varying(100),
    table_type character varying(100) DEFAULT 'NORMAL'::character varying NOT NULL,
    instance_index bigint DEFAULT 1,
    parent_instance_index bigint,
    was_modified boolean DEFAULT true
);


ALTER TABLE fileprocess.merge_complex OWNER TO etl;

--
-- TOC entry 1555 (class 1259 OID 440434)
-- Dependencies: 8
-- Name: merge_simple_idx_seq; Type: SEQUENCE; Schema: fileprocess; Owner: etl
--

CREATE SEQUENCE merge_simple_idx_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE fileprocess.merge_simple_idx_seq OWNER TO etl;

--
-- TOC entry 1943 (class 0 OID 0)
-- Dependencies: 1555
-- Name: merge_simple_idx_seq; Type: SEQUENCE SET; Schema: fileprocess; Owner: etl
--

SELECT pg_catalog.setval('merge_simple_idx_seq', 967, true);


--
-- TOC entry 1556 (class 1259 OID 440436)
-- Dependencies: 1858 1859 1860 8
-- Name: merge_simple; Type: TABLE; Schema: fileprocess; Owner: etl; Tablespace: 
--

CREATE TABLE merge_simple (
    idx integer DEFAULT nextval('merge_simple_idx_seq'::regclass) NOT NULL,
    form_id character varying(100),
    form_name character varying(100),
    question_id character varying(100),
    question_sn character varying(100),
    answer_id character varying(100),
    answer character varying(10000),
    question_text text,
    checksum character varying(100) NOT NULL,
    job_id character varying(100),
    table_id character varying(100),
    table_sn character varying(100),
    table_text text,
    instance_index bigint DEFAULT 1,
    parent_instance_index bigint,
    was_modified boolean DEFAULT true
);


ALTER TABLE fileprocess.merge_simple OWNER TO etl;

--
-- TOC entry 1557 (class 1259 OID 440444)
-- Dependencies: 1861 1862 8
-- Name: pre_transaction_details; Type: TABLE; Schema: fileprocess; Owner: etl; Tablespace: 
--

CREATE TABLE pre_transaction_details (
    idx integer NOT NULL,
    pre_transaction_id bigint NOT NULL,
    start_ts timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    end_ts timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    status character varying(100) NOT NULL,
    error_description character varying(1000) NOT NULL,
    key_field character varying(1000),
    value_field character varying(1000)
);


ALTER TABLE fileprocess.pre_transaction_details OWNER TO etl;

--
-- TOC entry 1558 (class 1259 OID 440452)
-- Dependencies: 8 1557
-- Name: pre_transaction_details_idx_seq; Type: SEQUENCE; Schema: fileprocess; Owner: etl
--

CREATE SEQUENCE pre_transaction_details_idx_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE fileprocess.pre_transaction_details_idx_seq OWNER TO etl;

--
-- TOC entry 1944 (class 0 OID 0)
-- Dependencies: 1558
-- Name: pre_transaction_details_idx_seq; Type: SEQUENCE OWNED BY; Schema: fileprocess; Owner: etl
--

ALTER SEQUENCE pre_transaction_details_idx_seq OWNED BY pre_transaction_details.idx;


--
-- TOC entry 1945 (class 0 OID 0)
-- Dependencies: 1558
-- Name: pre_transaction_details_idx_seq; Type: SEQUENCE SET; Schema: fileprocess; Owner: etl
--

SELECT pg_catalog.setval('pre_transaction_details_idx_seq', 532, true);


--
-- TOC entry 1559 (class 1259 OID 440454)
-- Dependencies: 1864 1865 1866 1867 1868 8
-- Name: pre_transactions; Type: TABLE; Schema: fileprocess; Owner: etl; Tablespace: 
--

CREATE TABLE pre_transactions (
    idx integer NOT NULL,
    submmission_id bigint DEFAULT 1 NOT NULL,
    xml_file character varying(250) NOT NULL,
    converted_xml_file character varying(250),
    start_ts timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    end_ts timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    status integer NOT NULL,
    client_id bigint DEFAULT 1 NOT NULL,
    client_name character varying(100) DEFAULT 'Client1'::character varying,
    xml_file_name character varying(100),
    validated_xml_file character varying(250),
    orig_xml_file_name character varying(100),
    prop_file_name character varying(250)
);


ALTER TABLE fileprocess.pre_transactions OWNER TO etl;

--
-- TOC entry 1560 (class 1259 OID 440465)
-- Dependencies: 1559 8
-- Name: pre_transactions_idx_seq; Type: SEQUENCE; Schema: fileprocess; Owner: etl
--

CREATE SEQUENCE pre_transactions_idx_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE fileprocess.pre_transactions_idx_seq OWNER TO etl;

--
-- TOC entry 1946 (class 0 OID 0)
-- Dependencies: 1560
-- Name: pre_transactions_idx_seq; Type: SEQUENCE OWNED BY; Schema: fileprocess; Owner: etl
--

ALTER SEQUENCE pre_transactions_idx_seq OWNED BY pre_transactions.idx;


--
-- TOC entry 1947 (class 0 OID 0)
-- Dependencies: 1560
-- Name: pre_transactions_idx_seq; Type: SEQUENCE SET; Schema: fileprocess; Owner: etl
--

SELECT pg_catalog.setval('pre_transactions_idx_seq', 414, true);


--
-- TOC entry 1561 (class 1259 OID 440467)
-- Dependencies: 8
-- Name: process_log; Type: TABLE; Schema: fileprocess; Owner: etl; Tablespace: 
--

CREATE TABLE process_log (
    form_id character varying(100) NOT NULL,
    status character varying(100) NOT NULL,
    entity_id character varying(100) NOT NULL
);


ALTER TABLE fileprocess.process_log OWNER TO etl;

--
-- TOC entry 1562 (class 1259 OID 440470)
-- Dependencies: 8
-- Name: sn_mapping; Type: TABLE; Schema: fileprocess; Owner: etl; Tablespace: 
--

CREATE TABLE sn_mapping (
    id serial,
    hcit_sn character varying(250),
    table_type character varying(50)
);


ALTER TABLE fileprocess.sn_mapping OWNER TO etl;

--
-- TOC entry 1948 (class 0 OID 0)
-- Dependencies: 1562
-- Name: TABLE sn_mapping; Type: COMMENT; Schema: fileprocess; Owner: etl
--

COMMENT ON TABLE sn_mapping IS 'InnoDB free: 25600 kB';


--
-- TOC entry 1563 (class 1259 OID 440476)
-- Dependencies: 8 1562
-- Name: sn_mapping_id_seq; Type: SEQUENCE; Schema: fileprocess; Owner: etl
--

CREATE SEQUENCE sn_mapping_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE fileprocess.sn_mapping_id_seq OWNER TO etl;

--
-- TOC entry 1949 (class 0 OID 0)
-- Dependencies: 1563
-- Name: sn_mapping_id_seq; Type: SEQUENCE OWNED BY; Schema: fileprocess; Owner: etl
--

ALTER SEQUENCE sn_mapping_id_seq OWNED BY sn_mapping.id;


--
-- TOC entry 1950 (class 0 OID 0)
-- Dependencies: 1563
-- Name: sn_mapping_id_seq; Type: SEQUENCE SET; Schema: fileprocess; Owner: etl
--

SELECT pg_catalog.setval('sn_mapping_id_seq', 1019, true);


--
-- TOC entry 1564 (class 1259 OID 440478)
-- Dependencies: 1871 1872 8
-- Name: status; Type: TABLE; Schema: fileprocess; Owner: etl; Tablespace: 
--

CREATE TABLE status (
    idx integer NOT NULL,
    status integer DEFAULT 1 NOT NULL,
    status_description character varying(250) DEFAULT 'No Description'::character varying NOT NULL
);


ALTER TABLE fileprocess.status OWNER TO etl;

--
-- TOC entry 1565 (class 1259 OID 440483)
-- Dependencies: 8
-- Name: submission_id_seq; Type: SEQUENCE; Schema: fileprocess; Owner: etl
--

CREATE SEQUENCE submission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE fileprocess.submission_id_seq OWNER TO etl;

--
-- TOC entry 1951 (class 0 OID 0)
-- Dependencies: 1565
-- Name: submission_id_seq; Type: SEQUENCE SET; Schema: fileprocess; Owner: etl
--

SELECT pg_catalog.setval('submission_id_seq', 174, true);


--
-- TOC entry 1566 (class 1259 OID 440485)
-- Dependencies: 1873 1874 1875 1876 1877 1878 8
-- Name: submissions; Type: TABLE; Schema: fileprocess; Owner: etl; Tablespace: 
--

CREATE TABLE submissions (
    idx integer NOT NULL,
    client_id integer DEFAULT 1 NOT NULL,
    client_name character varying(250) DEFAULT 'client1'::character varying NOT NULL,
    property_file_name character varying(250) NOT NULL,
    prop_short_file_name character varying(250),
    xml_file_count integer,
    start_ts timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    end_ts timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    description character varying(250) DEFAULT 'No Description'::character varying NOT NULL,
    notification_email character varying(100),
    status character varying(50) DEFAULT 'UN_PROCESSED'::character varying NOT NULL
);


ALTER TABLE fileprocess.submissions OWNER TO etl;

--
-- TOC entry 1567 (class 1259 OID 440497)
-- Dependencies: 8
-- Name: transactions_idx_seq; Type: SEQUENCE; Schema: fileprocess; Owner: etl
--

CREATE SEQUENCE transactions_idx_seq
    START WITH 176
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE fileprocess.transactions_idx_seq OWNER TO etl;

--
-- TOC entry 1952 (class 0 OID 0)
-- Dependencies: 1567
-- Name: transactions_idx_seq; Type: SEQUENCE SET; Schema: fileprocess; Owner: etl
--

SELECT pg_catalog.setval('transactions_idx_seq', 595, true);


--
-- TOC entry 1568 (class 1259 OID 440499)
-- Dependencies: 1879 1880 1881 1882 1883 1884 8
-- Name: transactions; Type: TABLE; Schema: fileprocess; Owner: etl; Tablespace: 
--

CREATE TABLE transactions (
    idx integer DEFAULT nextval('transactions_idx_seq'::regclass) NOT NULL,
    ptxn_id bigint DEFAULT 1 NOT NULL,
    xml_file character varying(250) NOT NULL,
    converted_xml_file character varying(250),
    start_ts timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    end_ts timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    status integer NOT NULL,
    form_id character varying(100) NOT NULL,
    xml_file_name character varying(250) NOT NULL,
    group_id character varying(100) NOT NULL,
    partial_complete boolean DEFAULT false NOT NULL,
    error character varying(250),
    entity_id character varying(100),
    instance_index bigint DEFAULT 1,
    parent_instance_index bigint,
    parent_form_id character varying(100),
    process_order bigint DEFAULT 1,
	binary_search_tree_path character varying(500)
);


ALTER TABLE fileprocess.transactions OWNER TO etl;

--
-- TOC entry 1569 (class 1259 OID 440511)
-- Dependencies: 8
-- Name: txn_descriptions_idx_seq; Type: SEQUENCE; Schema: fileprocess; Owner: etl
--

CREATE SEQUENCE txn_descriptions_idx_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE fileprocess.txn_descriptions_idx_seq OWNER TO etl;

--
-- TOC entry 1953 (class 0 OID 0)
-- Dependencies: 1569
-- Name: txn_descriptions_idx_seq; Type: SEQUENCE SET; Schema: fileprocess; Owner: etl
--

SELECT pg_catalog.setval('txn_descriptions_idx_seq', 1, false);


--
-- TOC entry 1570 (class 1259 OID 440513)
-- Dependencies: 1885 1886 1887 8
-- Name: transactions_detail; Type: TABLE; Schema: fileprocess; Owner: etl; Tablespace: 
--

CREATE TABLE transactions_detail (
    idx integer DEFAULT nextval('txn_descriptions_idx_seq'::regclass) NOT NULL,
    txn_id integer NOT NULL,
    description character varying(250) DEFAULT 'No Description'::character varying NOT NULL,
    entry_ts timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL
);


ALTER TABLE fileprocess.transactions_detail OWNER TO etl;

--
-- TOC entry 1571 (class 1259 OID 440519)
-- Dependencies: 8
-- Name: usr_grp_mapping; Type: TABLE; Schema: fileprocess; Owner: etl; Tablespace: 
--

CREATE TABLE usr_grp_mapping (
    id integer NOT NULL,
    user_email character varying(250),
    user_id character varying(250) NOT NULL,
    group_id character varying(250) NOT NULL
);


ALTER TABLE fileprocess.usr_grp_mapping OWNER TO etl;

--
-- TOC entry 1572 (class 1259 OID 440525)
-- Dependencies: 8 1571
-- Name: usr_grp_mapping_id_seq; Type: SEQUENCE; Schema: fileprocess; Owner: etl
--

CREATE SEQUENCE usr_grp_mapping_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE fileprocess.usr_grp_mapping_id_seq OWNER TO etl;

--
-- TOC entry 1954 (class 0 OID 0)
-- Dependencies: 1572
-- Name: usr_grp_mapping_id_seq; Type: SEQUENCE OWNED BY; Schema: fileprocess; Owner: etl
--

ALTER SEQUENCE usr_grp_mapping_id_seq OWNED BY usr_grp_mapping.id;


--
-- TOC entry 1955 (class 0 OID 0)
-- Dependencies: 1572
-- Name: usr_grp_mapping_id_seq; Type: SEQUENCE SET; Schema: fileprocess; Owner: etl
--

SELECT pg_catalog.setval('usr_grp_mapping_id_seq', 15, true);


--
-- TOC entry 1852 (class 2604 OID 440527)
-- Dependencies: 1551 1550
-- Name: id; Type: DEFAULT; Schema: fileprocess; Owner: etl
--

ALTER TABLE email_tmp ALTER COLUMN id SET DEFAULT nextval('email_tmp_id_seq'::regclass);


--
-- TOC entry 1853 (class 2604 OID 440528)
-- Dependencies: 1552 1550
-- Name: submission_id; Type: DEFAULT; Schema: fileprocess; Owner: etl
--

ALTER TABLE email_tmp ALTER COLUMN submission_id SET DEFAULT nextval('email_tmp_submission_id_seq'::regclass);


--
-- TOC entry 1863 (class 2604 OID 440529)
-- Dependencies: 1558 1557
-- Name: idx; Type: DEFAULT; Schema: fileprocess; Owner: etl
--

ALTER TABLE pre_transaction_details ALTER COLUMN idx SET DEFAULT nextval('pre_transaction_details_idx_seq'::regclass);


--
-- TOC entry 1869 (class 2604 OID 440530)
-- Dependencies: 1560 1559
-- Name: idx; Type: DEFAULT; Schema: fileprocess; Owner: etl
--

ALTER TABLE pre_transactions ALTER COLUMN idx SET DEFAULT nextval('pre_transactions_idx_seq'::regclass);


--
-- TOC entry 1870 (class 2604 OID 440531)
-- Dependencies: 1563 1562
-- Name: id; Type: DEFAULT; Schema: fileprocess; Owner: etl
--

ALTER TABLE sn_mapping ALTER COLUMN id SET DEFAULT nextval('sn_mapping_id_seq'::regclass);


--
-- TOC entry 1888 (class 2604 OID 440532)
-- Dependencies: 1572 1571
-- Name: id; Type: DEFAULT; Schema: fileprocess; Owner: etl
--

ALTER TABLE usr_grp_mapping ALTER COLUMN id SET DEFAULT nextval('usr_grp_mapping_id_seq'::regclass);


--
-- TOC entry 1923 (class 0 OID 440405)
-- Dependencies: 1549
-- Data for Name: clients; Type: TABLE DATA; Schema: fileprocess; Owner: etl
--

INSERT INTO clients (idx, client_name, description) VALUES (1, 'Client1', 'No Description');


--
-- TOC entry 1924 (class 0 OID 440413)
-- Dependencies: 1550
-- Data for Name: email_tmp; Type: TABLE DATA; Schema: fileprocess; Owner: etl
--



--
-- TOC entry 1925 (class 0 OID 440425)
-- Dependencies: 1554
-- Data for Name: merge_complex; Type: TABLE DATA; Schema: fileprocess; Owner: etl
--



--
-- TOC entry 1926 (class 0 OID 440436)
-- Dependencies: 1556
-- Data for Name: merge_simple; Type: TABLE DATA; Schema: fileprocess; Owner: etl
--



--
-- TOC entry 1927 (class 0 OID 440444)
-- Dependencies: 1557
-- Data for Name: pre_transaction_details; Type: TABLE DATA; Schema: fileprocess; Owner: etl
--



--
-- TOC entry 1928 (class 0 OID 440454)
-- Dependencies: 1559
-- Data for Name: pre_transactions; Type: TABLE DATA; Schema: fileprocess; Owner: etl
--



--
-- TOC entry 1929 (class 0 OID 440467)
-- Dependencies: 1561
-- Data for Name: process_log; Type: TABLE DATA; Schema: fileprocess; Owner: etl
--



--
-- TOC entry 1930 (class 0 OID 440470)
-- Dependencies: 1562
-- Data for Name: sn_mapping; Type: TABLE DATA; Schema: fileprocess; Owner: etl
--



--
-- TOC entry 1931 (class 0 OID 440478)
-- Dependencies: 1564
-- Data for Name: status; Type: TABLE DATA; Schema: fileprocess; Owner: etl
--

INSERT INTO status (idx, status, status_description) VALUES (1, 0, 'UN_PROCESSED');
INSERT INTO status (idx, status, status_description) VALUES (2, 9, 'XML_FILE_NOT_FOUND');
INSERT INTO status (idx, status, status_description) VALUES (3, 10, 'XML_FILE_FOUND');
INSERT INTO status (idx, status, status_description) VALUES (4, 19, 'XSD_INVALID');
INSERT INTO status (idx, status, status_description) VALUES (5, 20, 'XSD_VALID');
INSERT INTO status (idx, status, status_description) VALUES (6, 29, 'XML_DATA_INVALID');
INSERT INTO status (idx, status, status_description) VALUES (7, 30, 'XML_DATA_VALID');
INSERT INTO status (idx, status, status_description) VALUES (8, 39, 'XML_FILE_SPLIT_FAIL');
INSERT INTO status (idx, status, status_description) VALUES (9, 40, 'XML_FILE_SPLIT_PASS');
INSERT INTO status (idx, status, status_description) VALUES (10, 49, 'CONVERT_XML_FAIL');
INSERT INTO status (idx, status, status_description) VALUES (11, 50, 'CONVERT_XML_PASS/PRE_PROCESSED');
INSERT INTO status (idx, status, status_description) VALUES (12, 59, 'GET_COUCH_DATA_FAIL');
INSERT INTO status (idx, status, status_description) VALUES (13, 60, 'GET_COUCH_DATA_PASS');
INSERT INTO status (idx, status, status_description) VALUES (14, 69, 'MERGE_XML_FAIL');
INSERT INTO status (idx, status, status_description) VALUES (15, 70, 'MERGE_XML_PASS');
INSERT INTO status (idx, status, status_description) VALUES (16, 79, 'COUCH_UPLOAD_FAIL');
INSERT INTO status (idx, status, status_description) VALUES (17, 80, 'COUCH_UPLOAD_PASS');
INSERT INTO status (idx, status, status_description) VALUES (18, 89, 'CACURE_UPDATE_FAIL');
INSERT INTO status (idx, status, status_description) VALUES (19, 90, 'CACURE_UPDATE_PASS');
INSERT INTO status (idx, status, status_description) VALUES (20, 99, 'NOTIFICATION_EMAIL_FAIL');
INSERT INTO status (idx, status, status_description) VALUES (21, 100, 'NOTIFICATION_EMAIL_PASS/PROCESSED');


--
-- TOC entry 1932 (class 0 OID 440485)
-- Dependencies: 1566
-- Data for Name: submissions; Type: TABLE DATA; Schema: fileprocess; Owner: etl
--



--
-- TOC entry 1933 (class 0 OID 440499)
-- Dependencies: 1568
-- Data for Name: transactions; Type: TABLE DATA; Schema: fileprocess; Owner: etl
--



--
-- TOC entry 1934 (class 0 OID 440513)
-- Dependencies: 1570
-- Data for Name: transactions_detail; Type: TABLE DATA; Schema: fileprocess; Owner: etl
--



--
-- TOC entry 1935 (class 0 OID 440519)
-- Dependencies: 1571
-- Data for Name: usr_grp_mapping; Type: TABLE DATA; Schema: fileprocess; Owner: etl
--



--
-- TOC entry 1890 (class 2606 OID 440534)
-- Dependencies: 1549 1549
-- Name: clients_pkey; Type: CONSTRAINT; Schema: fileprocess; Owner: etl; Tablespace: 
--

ALTER TABLE ONLY clients
    ADD CONSTRAINT clients_pkey PRIMARY KEY (idx);


--
-- TOC entry 1892 (class 2606 OID 440536)
-- Dependencies: 1550 1550
-- Name: email_tmp_pk; Type: CONSTRAINT; Schema: fileprocess; Owner: etl; Tablespace: 
--

ALTER TABLE ONLY email_tmp
    ADD CONSTRAINT email_tmp_pk PRIMARY KEY (id);


--
-- TOC entry 1894 (class 2606 OID 440538)
-- Dependencies: 1554 1554
-- Name: pk_merge_complex; Type: CONSTRAINT; Schema: fileprocess; Owner: etl; Tablespace: 
--

ALTER TABLE ONLY merge_complex
    ADD CONSTRAINT pk_merge_complex PRIMARY KEY (idx);


--
-- TOC entry 1896 (class 2606 OID 440540)
-- Dependencies: 1556 1556
-- Name: pk_merge_simple; Type: CONSTRAINT; Schema: fileprocess; Owner: etl; Tablespace: 
--

ALTER TABLE ONLY merge_simple
    ADD CONSTRAINT pk_merge_simple PRIMARY KEY (idx);


--
-- TOC entry 1900 (class 2606 OID 440542)
-- Dependencies: 1557 1557
-- Name: pre_transaction_details_pkey; Type: CONSTRAINT; Schema: fileprocess; Owner: etl; Tablespace: 
--

ALTER TABLE ONLY pre_transaction_details
    ADD CONSTRAINT pre_transaction_details_pkey PRIMARY KEY (idx);


--
-- TOC entry 1904 (class 2606 OID 440544)
-- Dependencies: 1559 1559
-- Name: pre_transactions_pkey; Type: CONSTRAINT; Schema: fileprocess; Owner: etl; Tablespace: 
--

ALTER TABLE ONLY pre_transactions
    ADD CONSTRAINT pre_transactions_pkey PRIMARY KEY (idx);


--
-- TOC entry 1898 (class 2606 OID 440546)
-- Dependencies: 1556 1556
-- Name: simple_checksum_unique; Type: CONSTRAINT; Schema: fileprocess; Owner: etl; Tablespace: 
--

ALTER TABLE ONLY merge_simple
    ADD CONSTRAINT simple_checksum_unique UNIQUE (checksum);


--
-- TOC entry 1906 (class 2606 OID 440548)
-- Dependencies: 1562 1562
-- Name: sn_mapping_pkey; Type: CONSTRAINT; Schema: fileprocess; Owner: etl; Tablespace: 
--

ALTER TABLE ONLY sn_mapping
    ADD CONSTRAINT sn_mapping_pkey PRIMARY KEY (id);


--
-- TOC entry 1908 (class 2606 OID 440550)
-- Dependencies: 1564 1564
-- Name: status_pkey; Type: CONSTRAINT; Schema: fileprocess; Owner: etl; Tablespace: 
--

ALTER TABLE ONLY status
    ADD CONSTRAINT status_pkey PRIMARY KEY (idx);


--
-- TOC entry 1910 (class 2606 OID 440552)
-- Dependencies: 1564 1564
-- Name: status_status_key; Type: CONSTRAINT; Schema: fileprocess; Owner: etl; Tablespace: 
--

ALTER TABLE ONLY status
    ADD CONSTRAINT status_status_key UNIQUE (status);


--
-- TOC entry 1912 (class 2606 OID 440554)
-- Dependencies: 1566 1566
-- Name: submissions_pkey; Type: CONSTRAINT; Schema: fileprocess; Owner: etl; Tablespace: 
--

ALTER TABLE ONLY submissions
    ADD CONSTRAINT submissions_pkey PRIMARY KEY (idx);


--
-- TOC entry 1914 (class 2606 OID 440556)
-- Dependencies: 1568 1568
-- Name: transactions_pkey; Type: CONSTRAINT; Schema: fileprocess; Owner: etl; Tablespace: 
--

ALTER TABLE ONLY transactions
    ADD CONSTRAINT transactions_pkey PRIMARY KEY (idx);


--
-- TOC entry 1918 (class 2606 OID 440558)
-- Dependencies: 1570 1570
-- Name: txn_description_pkey; Type: CONSTRAINT; Schema: fileprocess; Owner: etl; Tablespace: 
--

ALTER TABLE ONLY transactions_detail
    ADD CONSTRAINT txn_description_pkey PRIMARY KEY (idx);


--
-- TOC entry 1920 (class 2606 OID 440560)
-- Dependencies: 1571 1571
-- Name: usr_grp_mapping_key; Type: CONSTRAINT; Schema: fileprocess; Owner: etl; Tablespace: 
--

ALTER TABLE ONLY usr_grp_mapping
    ADD CONSTRAINT usr_grp_mapping_key PRIMARY KEY (id);


--
-- TOC entry 1901 (class 1259 OID 440561)
-- Dependencies: 1559
-- Name: fki_submission_id; Type: INDEX; Schema: fileprocess; Owner: etl; Tablespace: 
--

CREATE INDEX fki_submission_id ON pre_transactions USING btree (submmission_id);


--
-- TOC entry 1902 (class 1259 OID 440562)
-- Dependencies: 1559
-- Name: fki_submissions_idx; Type: INDEX; Schema: fileprocess; Owner: etl; Tablespace: 
--

CREATE INDEX fki_submissions_idx ON pre_transactions USING btree (submmission_id);


--
-- TOC entry 1915 (class 1259 OID 440563)
-- Dependencies: 1570
-- Name: fki_transactions_desc; Type: INDEX; Schema: fileprocess; Owner: etl; Tablespace: 
--

CREATE INDEX fki_transactions_desc ON transactions_detail USING btree (txn_id);


--
-- TOC entry 1916 (class 1259 OID 440564)
-- Dependencies: 1570
-- Name: fki_transactions_id; Type: INDEX; Schema: fileprocess; Owner: etl; Tablespace: 
--

CREATE INDEX fki_transactions_id ON transactions_detail USING btree (txn_id);


--
-- TOC entry 1921 (class 2606 OID 440565)
-- Dependencies: 1559 1911 1566
-- Name: fk_submission_id; Type: FK CONSTRAINT; Schema: fileprocess; Owner: etl
--

ALTER TABLE ONLY pre_transactions
    ADD CONSTRAINT fk_submission_id FOREIGN KEY (submmission_id) REFERENCES submissions(idx) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 1922 (class 2606 OID 440570)
-- Dependencies: 1909 1564 1568
-- Name: transactions_status_fkey; Type: FK CONSTRAINT; Schema: fileprocess; Owner: etl
--

ALTER TABLE ONLY transactions
    ADD CONSTRAINT transactions_status_fkey FOREIGN KEY (status) REFERENCES status(status);

    CREATE LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION merge_simple_complex_change_func()
  RETURNS trigger AS
$BODY$
BEGIN
IF NEW.answer = OLD.answer THEN
	NEW.was_modified := 'f';
END IF;
RETURN NEW;
END;
$BODY$ language plpgsql VOLATILE
  COST 100;



CREATE TRIGGER merge_simple_changed_trig
    BEFORE UPDATE ON fileprocess.merge_simple
    FOR EACH ROW
    EXECUTE PROCEDURE merge_simple_complex_change_func();

CREATE TRIGGER merge_complex_changed_trig
    BEFORE UPDATE ON fileprocess.merge_complex
    FOR EACH ROW
    EXECUTE PROCEDURE merge_simple_complex_change_func();


-- Completed on 2013-04-12 21:57:00

--
-- PostgreSQL database dump complete
--

