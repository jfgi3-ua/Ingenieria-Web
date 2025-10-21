-- Group [Group]
create table "public"."group" (
   "id"  int4  not null,
   "groupname"  varchar(255),
  primary key ("id")
);


-- Module [Module]
create table "public"."module" (
   "id"  int4  not null,
   "moduleid"  varchar(255),
   "modulename"  varchar(255),
  primary key ("id")
);


-- User [User]
create table "public"."user" (
   "id"  int4  not null,
   "username"  varchar(255),
   "password"  varchar(255),
   "email"  varchar(255),
  primary key ("id")
);


-- Reserva [ent1]
create table "public"."reserva" (
   "id"  int4  not null,
   "estado"  varchar(255),
   "importe"  int4,
  primary key ("id")
);


-- Pista [ent2]
create table "public"."pista" (
   "id"  int4  not null,
   "nombre"  varchar(255),
   "estado"  varchar(255),
   "capacidad"  int4,
   "precio_hora"  varchar(255),
  primary key ("id")
);


-- Categoria [ent3]
create table "public"."categoria" (
   "id"  int4  not null,
   "nombre"  varchar(255),
  primary key ("id")
);


-- Horario [ent4]
create table "public"."horario" (
   "id"  int4  not null,
   "fecha"  date,
   "dia_semana"  int4,
   "inicio"  timestamp,
   "fin"  timestamp,
  primary key ("id")
);


-- Monedero [ent6]
create table "public"."monedero" (
   "id"  int4  not null,
   "saldo"  float8,
  primary key ("id")
);


-- Group_DefaultModule [Group2DefaultModule_DefaultModule2Group]
alter table "public"."group"  add column  "module_id"  int4;
alter table "public"."group"   add constraint fk_group_module foreign key ("module_id") references "public"."module" ("id");


-- Group_Module [Group2Module_Module2Group]
create table "public"."group_module" (
   "group_id"  int4 not null,
   "module_id"  int4 not null,
  primary key ("group_id", "module_id")
);
alter table "public"."group_module"   add constraint fk_group_module_group foreign key ("group_id") references "public"."group" ("id");
alter table "public"."group_module"   add constraint fk_group_module_module foreign key ("module_id") references "public"."module" ("id");


-- User_DefaultGroup [User2DefaultGroup_DefaultGroup2User]
alter table "public"."user"  add column  "group_id"  int4;
alter table "public"."user"   add constraint fk_user_group foreign key ("group_id") references "public"."group" ("id");


-- User_Group [User2Group_Group2User]
create table "public"."user_group" (
   "user_id"  int4 not null,
   "group_id"  int4 not null,
  primary key ("user_id", "group_id")
);
alter table "public"."user_group"   add constraint fk_user_group_user foreign key ("user_id") references "public"."user" ("id");
alter table "public"."user_group"   add constraint fk_user_group_group foreign key ("group_id") references "public"."group" ("id");


-- User_Reserva [rel1]
alter table "public"."reserva"  add column  "user_id"  int4;
alter table "public"."reserva"   add constraint fk_reserva_user foreign key ("user_id") references "public"."user" ("id");


-- Reserva_Pista [rel2]
alter table "public"."reserva"  add column  "pista_id"  int4;
alter table "public"."reserva"   add constraint fk_reserva_pista foreign key ("pista_id") references "public"."pista" ("id");


-- Pista_Categoria [rel3]
alter table "public"."pista"  add column  "categoria_id"  int4;
alter table "public"."pista"   add constraint fk_pista_categoria foreign key ("categoria_id") references "public"."categoria" ("id");


-- Reserva_Horario [rel4]
alter table "public"."reserva"  add column  "horario_id"  int4;
alter table "public"."reserva"   add constraint fk_reserva_horario foreign key ("horario_id") references "public"."horario" ("id");


-- Horario_Pista [rel6]
create table "public"."horario_pista" (
   "horario_id"  int4 not null,
   "pista_id"  int4 not null,
  primary key ("horario_id", "pista_id")
);
alter table "public"."horario_pista"   add constraint fk_horario_pista_horario foreign key ("horario_id") references "public"."horario" ("id");
alter table "public"."horario_pista"   add constraint fk_horario_pista_pista foreign key ("pista_id") references "public"."pista" ("id");


-- Monedero_User [rel7]
alter table "public"."user"  add column  "monedero_id"  int4;
alter table "public"."user"   add constraint fk_user_monedero foreign key ("monedero_id") references "public"."monedero" ("id");


