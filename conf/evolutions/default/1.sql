# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table contato (
  id                        bigserial not null,
  nome                      varchar(60) not null,
  email                     varchar(60) not null,
  assunto                   varchar(30) not null,
  mensagem                  varchar(200) not null,
  data_cadastro             timestamp,
  constraint pk_contato primary key (id))
;

create table usuario (
  id                        bigserial not null,
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

drop table if exists usuario cascade;

