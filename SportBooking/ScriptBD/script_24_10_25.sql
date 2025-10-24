--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.15
-- Dumped by pg_dump version 9.6.15

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET default_with_oids = false;

--
-- Name: categoria; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.categoria (
    id bigint NOT NULL,
    nombre character varying(100) NOT NULL,
    descripcion text
);


--
-- Name: categoria_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.categoria_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: categoria_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.categoria_id_seq OWNED BY public.categoria.id;


--
-- Name: group; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."group" (
    id integer NOT NULL,
    groupname character varying(255),
    module_id integer
);


--
-- Name: group_module; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.group_module (
    group_id integer NOT NULL,
    module_id integer NOT NULL
);


--
-- Name: horario; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.horario (
    id bigint NOT NULL,
    fecha date,
    dia_semana smallint,
    inicio time without time zone,
    fin time without time zone,
    vigente_desde date,
    vigente_hasta date,
    CONSTRAINT horario_check CHECK ((fin > inicio))
);


--
-- Name: horario_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.horario_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: horario_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.horario_id_seq OWNED BY public.horario.id;


--
-- Name: module; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.module (
    id integer NOT NULL,
    moduleid character varying(255),
    modulename character varying(255)
);


--
-- Name: monedero; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.monedero (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    saldo numeric(12,2) DEFAULT 0 NOT NULL
);


--
-- Name: monedero_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.monedero_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: monedero_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.monedero_id_seq OWNED BY public.monedero.id;


--
-- Name: pista; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.pista (
    id bigint NOT NULL,
    nombre character varying(120),
    descripcion text,
    capacidad integer,
    precio_hora numeric(10,2),
    estado character varying(20) DEFAULT 'ACTIVA'::character varying,
    categoria_id bigint
);


--
-- Name: pista_horario; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.pista_horario (
    id bigint NOT NULL,
    pista_id bigint NOT NULL,
    horario_id bigint NOT NULL,
    bloqueada boolean DEFAULT false,
    precio_override numeric(10,2)
);


--
-- Name: pista_horario_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.pista_horario_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: pista_horario_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.pista_horario_id_seq OWNED BY public.pista_horario.id;


--
-- Name: pista_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.pista_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: pista_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.pista_id_seq OWNED BY public.pista.id;


--
-- Name: reserva; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.reserva (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    pista_id bigint NOT NULL,
    horario_id bigint NOT NULL,
    estado character varying(20) DEFAULT 'PENDIENTE'::character varying,
    importe numeric(10,2)
);


--
-- Name: reserva_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.reserva_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: reserva_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.reserva_id_seq OWNED BY public.reserva.id;


--
-- Name: user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."user" (
    id integer NOT NULL,
    username character varying(255),
    password character varying(255),
    email character varying(255),
    group_id integer
);


--
-- Name: user_group; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_group (
    user_id integer NOT NULL,
    group_id integer NOT NULL
);


--
-- Name: categoria id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.categoria ALTER COLUMN id SET DEFAULT nextval('public.categoria_id_seq'::regclass);


--
-- Name: horario id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.horario ALTER COLUMN id SET DEFAULT nextval('public.horario_id_seq'::regclass);


--
-- Name: monedero id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.monedero ALTER COLUMN id SET DEFAULT nextval('public.monedero_id_seq'::regclass);


--
-- Name: pista id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pista ALTER COLUMN id SET DEFAULT nextval('public.pista_id_seq'::regclass);


--
-- Name: pista_horario id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pista_horario ALTER COLUMN id SET DEFAULT nextval('public.pista_horario_id_seq'::regclass);


--
-- Name: reserva id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reserva ALTER COLUMN id SET DEFAULT nextval('public.reserva_id_seq'::regclass);


--
-- Name: categoria categoria_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.categoria
    ADD CONSTRAINT categoria_pkey PRIMARY KEY (id);


--
-- Name: group_module group_module_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.group_module
    ADD CONSTRAINT group_module_pkey PRIMARY KEY (group_id, module_id);


--
-- Name: group group_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."group"
    ADD CONSTRAINT group_pkey PRIMARY KEY (id);


--
-- Name: horario horario_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.horario
    ADD CONSTRAINT horario_pkey PRIMARY KEY (id);


--
-- Name: module module_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.module
    ADD CONSTRAINT module_pkey PRIMARY KEY (id);


--
-- Name: monedero monedero_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.monedero
    ADD CONSTRAINT monedero_pkey PRIMARY KEY (id);


--
-- Name: monedero monedero_user_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.monedero
    ADD CONSTRAINT monedero_user_id_key UNIQUE (user_id);


--
-- Name: pista_horario pista_horario_pista_id_horario_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pista_horario
    ADD CONSTRAINT pista_horario_pista_id_horario_id_key UNIQUE (pista_id, horario_id);


--
-- Name: pista_horario pista_horario_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pista_horario
    ADD CONSTRAINT pista_horario_pkey PRIMARY KEY (id);


--
-- Name: pista pista_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pista
    ADD CONSTRAINT pista_pkey PRIMARY KEY (id);


--
-- Name: reserva reserva_pista_id_horario_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reserva
    ADD CONSTRAINT reserva_pista_id_horario_id_key UNIQUE (pista_id, horario_id);


--
-- Name: reserva reserva_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reserva
    ADD CONSTRAINT reserva_pkey PRIMARY KEY (id);


--
-- Name: user_group user_group_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_group
    ADD CONSTRAINT user_group_pkey PRIMARY KEY (user_id, group_id);


--
-- Name: user user_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- Name: group fk_group_module; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."group"
    ADD CONSTRAINT fk_group_module FOREIGN KEY (module_id) REFERENCES public.module(id);


--
-- Name: group_module fk_group_module_group; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.group_module
    ADD CONSTRAINT fk_group_module_group FOREIGN KEY (group_id) REFERENCES public."group"(id);


--
-- Name: group_module fk_group_module_module; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.group_module
    ADD CONSTRAINT fk_group_module_module FOREIGN KEY (module_id) REFERENCES public.module(id);


--
-- Name: user fk_user_group; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT fk_user_group FOREIGN KEY (group_id) REFERENCES public."group"(id);


--
-- Name: user_group fk_user_group_group; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_group
    ADD CONSTRAINT fk_user_group_group FOREIGN KEY (group_id) REFERENCES public."group"(id);


--
-- Name: user_group fk_user_group_user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_group
    ADD CONSTRAINT fk_user_group_user FOREIGN KEY (user_id) REFERENCES public."user"(id);


--
-- Name: monedero monedero_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.monedero
    ADD CONSTRAINT monedero_user_id_fkey FOREIGN KEY (user_id) REFERENCES public."user"(id) ON DELETE CASCADE;


--
-- Name: pista pista_categoria_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pista
    ADD CONSTRAINT pista_categoria_id_fkey FOREIGN KEY (categoria_id) REFERENCES public.categoria(id);


--
-- Name: pista_horario pista_horario_horario_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pista_horario
    ADD CONSTRAINT pista_horario_horario_id_fkey FOREIGN KEY (horario_id) REFERENCES public.horario(id) ON DELETE CASCADE;


--
-- Name: pista_horario pista_horario_pista_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pista_horario
    ADD CONSTRAINT pista_horario_pista_id_fkey FOREIGN KEY (pista_id) REFERENCES public.pista(id) ON DELETE CASCADE;


--
-- Name: reserva reserva_horario_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reserva
    ADD CONSTRAINT reserva_horario_id_fkey FOREIGN KEY (horario_id) REFERENCES public.horario(id) ON DELETE CASCADE;


--
-- Name: reserva reserva_pista_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reserva
    ADD CONSTRAINT reserva_pista_id_fkey FOREIGN KEY (pista_id) REFERENCES public.pista(id) ON DELETE CASCADE;


--
-- Name: reserva reserva_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reserva
    ADD CONSTRAINT reserva_user_id_fkey FOREIGN KEY (user_id) REFERENCES public."user"(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

