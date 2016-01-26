# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table material (
  id                        bigserial not null,
  titulo                    varchar(30) not null,
  assunto                   varchar(30) not null,
  categoria                 varchar(30) not null,
  descricao                 varchar(30) not null,
  tipo                      varchar(30) not null,
  fonte                     varchar(30) not null,
  ano                       varchar(30) not null,
  idioma                    varchar(30) not null,
  autor                     varchar(30) not null,
  constraint pk_material primary key (id))
;

create table usuario (
  id                        bigserial not null,
  nome                      varchar(30) not null,
  email                     varchar(30) not null,
  senha                     varchar(60) not null,
  privilegio                integer not null,
  data_cadastro             timestamp,
  data_alteracao            timestamp,
  status                    boolean not null,
  constraint uq_usuario_email unique (email),
  constraint pk_usuario primary key (id))
;




# --- !Downs

drop table if exists material cascade;

drop table if exists usuario cascade;

