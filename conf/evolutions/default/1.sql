# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

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

drop table if exists usuario cascade;

