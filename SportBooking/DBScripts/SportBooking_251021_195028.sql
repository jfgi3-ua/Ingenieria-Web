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


