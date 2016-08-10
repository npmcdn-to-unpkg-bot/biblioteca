# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table artigo (
  id                        bigint not null,
  titulo                    varchar(150) not null,
  resumo                    varchar(400) not null,
  nome_arquivo              varchar(200) not null,
  data_cadastro             timestamp not null,
  data_alteracao            timestamp,
  constraint pk_artigo primary key (id))
;

create table contato (
  id                        bigint not null,
  nome                      varchar(60) not null,
  email                     varchar(60) not null,
  assunto                   varchar(30) not null,
  mensagem                  varchar(200) not null,
  data_cadastro             timestamp not null,
  constraint pk_contato primary key (id))
;

create table curso (
  id                        bigint not null,
  nome                      varchar(100) not null,
  descricao                 varchar(400) not null,
  data_inicio               timestamp not null,
  site                      varchar(80) not null,
  nome_capa                 varchar(150) not null,
  constraint pk_curso primary key (id))
;

create table escolaridade (
  id                        bigint not null,
  nome                      varchar(30) not null,
  constraint pk_escolaridade primary key (id))
;

create table evento (
  id                        bigint not null,
  nome                      varchar(100) not null,
  data_inicio               timestamp not null,
  data_fim                  timestamp not null,
  site                      varchar(80) not null,
  local                     varchar(120) not null,
  instituicao               varchar(100) not null,
  constraint pk_evento primary key (id))
;

create table fonte (
  id                        bigint not null,
  nome                      varchar(30) not null,
  constraint pk_fonte primary key (id))
;

create table genero (
  id                        bigint not null,
  nome                      varchar(14) not null,
  constraint pk_genero primary key (id))
;

create table inscricao (
  id                        bigint not null,
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
  id                        bigint not null,
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
  id                        bigint not null,
  nome                      varchar(3) not null,
  constraint pk_modalidade primary key (id))
;

create table noticia (
  id                        bigint not null,
  titulo                    varchar(250) not null,
  resumo                    varchar(400) not null,
  url                       varchar(400) not null,
  data_cadastro             timestamp not null,
  data_alteracao            timestamp,
  nome_capa                 varchar(250) not null,
  constraint pk_noticia primary key (id))
;

create table pais (
  id                        bigint not null,
  nome                      varchar(50) not null,
  constraint pk_pais primary key (id))
;

create table publicacao (
  id                        bigint not null,
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
  id                        bigint not null,
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
  id                        bigint not null,
  titulo                    varchar(150) not null,
  descricao                 varchar(400) not null,
  url                       varchar(400) not null,
  data_cadastro             timestamp not null,
  data_alteracao            timestamp,
  constraint pk_video primary key (id))
;

create sequence artigo_seq;

create sequence contato_seq;

create sequence curso_seq;

create sequence escolaridade_seq;

create sequence evento_seq;

create sequence fonte_seq;

create sequence genero_seq;

create sequence inscricao_seq;

create sequence livro_seq;

create sequence modalidade_seq;

create sequence noticia_seq;

create sequence pais_seq;

create sequence publicacao_seq;

create sequence token_seq;

create sequence usuario_seq;

create sequence video_seq;

alter table inscricao add constraint fk_inscricao_genero_1 foreign key (genero_id) references genero (id) on delete restrict on update restrict;
create index ix_inscricao_genero_1 on inscricao (genero_id);
alter table inscricao add constraint fk_inscricao_escolaridade_2 foreign key (escolaridade_id) references escolaridade (id) on delete restrict on update restrict;
create index ix_inscricao_escolaridade_2 on inscricao (escolaridade_id);
alter table inscricao add constraint fk_inscricao_pais_3 foreign key (pais_id) references pais (id) on delete restrict on update restrict;
create index ix_inscricao_pais_3 on inscricao (pais_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists artigo;

drop table if exists contato;

drop table if exists curso;

drop table if exists escolaridade;

drop table if exists evento;

drop table if exists fonte;

drop table if exists genero;

drop table if exists inscricao;

drop table if exists livro;

drop table if exists modalidade;

drop table if exists noticia;

drop table if exists pais;

drop table if exists publicacao;

drop table if exists token;

drop table if exists usuario;

drop table if exists video;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists artigo_seq;

drop sequence if exists contato_seq;

drop sequence if exists curso_seq;

drop sequence if exists escolaridade_seq;

drop sequence if exists evento_seq;

drop sequence if exists fonte_seq;

drop sequence if exists genero_seq;

drop sequence if exists inscricao_seq;

drop sequence if exists livro_seq;

drop sequence if exists modalidade_seq;

drop sequence if exists noticia_seq;

drop sequence if exists pais_seq;

drop sequence if exists publicacao_seq;

drop sequence if exists token_seq;

drop sequence if exists usuario_seq;

drop sequence if exists video_seq;

