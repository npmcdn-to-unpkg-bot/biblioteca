# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table artigo (
  id                        bigserial not null,
  titulo                    varchar(150) not null,
  resumo                    varchar(400) not null,
  resumo2                   varchar(255),
  nome_arquivo              varchar(200) not null,
  data_cadastro             timestamp not null,
  data_alteracao            timestamp,
  constraint pk_artigo primary key (id))
;

create table contato (
  id                        bigserial not null,
  nome                      varchar(60) not null,
  email                     varchar(60) not null,
  assunto                   varchar(30) not null,
  mensagem                  varchar(200) not null,
  data_cadastro             timestamp not null,
  constraint pk_contato primary key (id))
;

create table curso (
  id                        bigserial not null,
  nome                      varchar(100) not null,
  descricao                 varchar(400) not null,
  data_inicio               timestamp not null,
  site                      varchar(80) not null,
  nome_capa                 varchar(150) not null,
  constraint pk_curso primary key (id))
;

create table escolaridade (
  id                        bigserial not null,
  nome                      varchar(30) not null,
  constraint pk_escolaridade primary key (id))
;

create table evento (
  id                        bigserial not null,
  nome                      varchar(100) not null,
  data_inicio               timestamp not null,
  data_fim                  timestamp not null,
  site                      varchar(80) not null,
  local                     varchar(120) not null,
  instituicao               varchar(100) not null,
  constraint pk_evento primary key (id))
;

create table fonte (
  id                        bigserial not null,
  nome                      varchar(30) not null,
  constraint pk_fonte primary key (id))
;

create table genero (
  id                        bigserial not null,
  nome                      varchar(14) not null,
  constraint pk_genero primary key (id))
;

create table inscricao (
  id                        bigserial not null,
  nome                      varchar(60) not null,
  genero_id                 bigint,
  data_nascimento           timestamp not null,
  escolaridade_id           bigint,
  profissao                 varchar(30) not null,
  instituicao               varchar(45) not null,
  pais_id                   bigint,
  estado                    varchar(45) not null,
  cidade                    varchar(45) not null,
  telefone                  varchar(12) not null,
  cpf                       varchar(15) not null,
  email                     varchar(35) not null,
  modalidade                varchar(3) not null,
  fonte                     varchar(25) not null,
  descricao_fonte           varchar(60),
  data_inscricao            timestamp not null,
  data_alteracao            timestamp,
  constraint pk_inscricao primary key (id))
;

create table livro (
  id                        bigserial not null,
  titulo                    varchar(150) not null,
  sub_titulo                varchar(254) not null,
  isbn                      varchar(17),
  editora                   varchar(100) not null,
  autores                   varchar(254) not null,
  edicao                    integer,
  paginas                   integer,
  ano                       integer,
  nome_arquivo              varchar(200) not null,
  data_cadastro             timestamp not null,
  data_alteracao            timestamp,
  constraint uq_livro_isbn unique (isbn),
  constraint pk_livro primary key (id))
;

create table modalidade (
  id                        bigserial not null,
  nome                      varchar(3) not null,
  constraint pk_modalidade primary key (id))
;

create table noticia (
  id                        bigserial not null,
  titulo                    varchar(250) not null,
  resumo                    varchar(400) not null,
  url                       varchar(400) not null,
  data_cadastro             timestamp not null,
  data_alteracao            timestamp,
  nome_capa                 varchar(250) not null,
  constraint pk_noticia primary key (id))
;

create table pais (
  id                        bigserial not null,
  nome                      varchar(50) not null,
  constraint pk_pais primary key (id))
;

create table publicacao (
  id                        bigserial not null,
  titulo                    varchar(250) not null,
  resumo                    varchar(400) not null,
  url                       varchar(400) not null,
  data_cadastro             timestamp not null,
  data_alteracao            timestamp,
  nome_capa                 varchar(250) not null,
  constraint pk_publicacao primary key (id))
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
  email                     varchar(40) not null,
  senha                     varchar(60) not null,
  privilegio                integer not null,
  status                    boolean not null,
  data_cadastro             timestamp,
  data_alteracao            timestamp,
  constraint uq_usuario_email unique (email),
  constraint pk_usuario primary key (id))
;

create table video (
  id                        bigserial not null,
  titulo                    varchar(150) not null,
  descricao                 varchar(400) not null,
  url                       varchar(400) not null,
  data_cadastro             timestamp not null,
  data_alteracao            timestamp,
  constraint pk_video primary key (id))
;

alter table inscricao add constraint fk_inscricao_genero_1 foreign key (genero_id) references genero (id);
create index ix_inscricao_genero_1 on inscricao (genero_id);
alter table inscricao add constraint fk_inscricao_escolaridade_2 foreign key (escolaridade_id) references escolaridade (id);
create index ix_inscricao_escolaridade_2 on inscricao (escolaridade_id);
alter table inscricao add constraint fk_inscricao_pais_3 foreign key (pais_id) references pais (id);
create index ix_inscricao_pais_3 on inscricao (pais_id);



# --- !Downs

drop table if exists artigo cascade;

drop table if exists contato cascade;

drop table if exists curso cascade;

drop table if exists escolaridade cascade;

drop table if exists evento cascade;

drop table if exists fonte cascade;

drop table if exists genero cascade;

drop table if exists inscricao cascade;

drop table if exists livro cascade;

drop table if exists modalidade cascade;

drop table if exists noticia cascade;

drop table if exists pais cascade;

drop table if exists publicacao cascade;

drop table if exists token cascade;

drop table if exists usuario cascade;

drop table if exists video cascade;

