# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table contato (
  id                        bigserial not null,
  nome                      varchar(60) not null,
  email                     varchar(60) not null,
  email2                    varchar(255),
  assunto                   varchar(30) not null,
  mensagem                  varchar(200) not null,
  data_cadastro             timestamp,
  constraint pk_contato primary key (id))
;

create table token (
  token                     varchar(255) not null,
  usuario_id                bigint,
  type                      varchar(8),
  date_creation             timestamp,
  email                     varchar(255),
  constraint ck_token_type check (type in ('password','email')),
  constraint pk_token primary key (token))
;

create table usuario (
  id                        bigserial not null,
  confirmacao_token         varchar(255),
  validado                  boolean,
  nome                      varchar(60) not null,
  email                     varchar(30) not null,
  senha                     varchar(60) not null,
  privilegio                integer not null,
  status                    boolean not null,
  data_cadastro             timestamp,
  data_alteracao            timestamp,
  constraint uq_usuario_email unique (email),
  constraint pk_usuario primary key (id))
;




# --- !Downs

drop table if exists contato cascade;

drop table if exists token cascade;

drop table if exists usuario cascade;

