# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table artigo (
  id                            bigserial not null,
  titulo                        varchar(150) not null,
  resumo                        varchar(400) not null,
  nome_arquivo                  varchar(300) not null,
  data_cadastro                 date not null,
  data_alteracao                date,
  constraint pk_artigo primary key (id)
);

create table contato (
  id                            bigserial not null,
  nome                          varchar(80) not null,
  email                         varchar(80) not null,
  assunto                       varchar(50) not null,
  mensagem                      varchar(400) not null,
  data_cadastro                 date not null,
  constraint pk_contato primary key (id)
);

create table curso (
  id                            bigserial not null,
  nome                          varchar(100) not null,
  descricao                     varchar(400) not null,
  data_inicio                   date not null,
  site                          varchar(300) not null,
  nome_capa                     varchar(150) not null,
  constraint pk_curso primary key (id)
);

create table evento (
  id                            bigserial not null,
  nome                          varchar(150) not null,
  data_inicio                   date not null,
  data_fim                      date not null,
  site                          varchar(300) not null,
  local                         varchar(150) not null,
  instituicao                   varchar(100) not null,
  constraint pk_evento primary key (id)
);

create table livro (
  id                            bigserial not null,
  titulo                        varchar(150) not null,
  sub_titulo                    varchar(250) not null,
  isbn                          varchar(20),
  editora                       varchar(100) not null,
  autores                       varchar(250) not null,
  edicao                        integer,
  paginas                       integer,
  ano                           integer,
  nome_arquivo                  varchar(200) not null,
  data_cadastro                 date not null,
  data_alteracao                date,
  constraint uq_livro_isbn unique (isbn),
  constraint pk_livro primary key (id)
);

create table noticia (
  id                            bigserial not null,
  titulo                        varchar(250) not null,
  resumo                        varchar(400) not null,
  url                           varchar(400) not null,
  data_cadastro                 date not null,
  data_alteracao                date,
  nome_capa                     varchar(250) not null,
  constraint pk_noticia primary key (id)
);

create table publicacao (
  id                            bigserial not null,
  titulo                        varchar(250) not null,
  resumo                        varchar(400) not null,
  url                           varchar(400) not null,
  data_cadastro                 date not null,
  data_alteracao                date,
  nome_capa                     varchar(250) not null,
  constraint pk_publicacao primary key (id)
);

create table token (
  token                         varchar(255) not null,
  usuario_id                    bigint,
  type                          varchar(8),
  date_creation                 date,
  email                         varchar(255),
  constraint ck_token_type check (type in ('password','email')),
  constraint pk_token primary key (token)
);

create table usuario (
  id                            bigserial not null,
  confirmacao_token             varchar(255),
  validado                      boolean,
  nome                          varchar(60) not null,
  email                         varchar(40) not null,
  senha                         varchar(60) not null,
  privilegio                    integer not null,
  status                        boolean not null,
  data_cadastro                 date,
  data_alteracao                date,
  constraint uq_usuario_email unique (email),
  constraint pk_usuario primary key (id)
);

create table video (
  id                            bigserial not null,
  titulo                        varchar(250) not null,
  descricao                     varchar(400) not null,
  url                           varchar(400) not null,
  data_cadastro                 date not null,
  data_alteracao                date,
  constraint pk_video primary key (id)
);


# --- !Downs

drop table if exists artigo cascade;

drop table if exists contato cascade;

drop table if exists curso cascade;

drop table if exists evento cascade;

drop table if exists livro cascade;

drop table if exists noticia cascade;

drop table if exists publicacao cascade;

drop table if exists token cascade;

drop table if exists usuario cascade;

drop table if exists video cascade;

