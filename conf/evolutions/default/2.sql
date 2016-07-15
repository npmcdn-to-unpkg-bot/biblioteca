# --- Sample dataset

# --- !Ups

insert into usuario (nome, email, senha, privilegio, status, validado) values ('Administrador', 'admin', '40BD001563085FC35165329EA1FF5C5ECBDBBEEF', 1, TRUE, TRUE);

insert into escolaridade (nome) values ('Ensino Médio Completo');
insert into escolaridade (nome) values ('Ensino Técnico');
insert into escolaridade (nome) values ('Graduação (Bacharelado)');
insert into escolaridade (nome) values ('Graduação (Licenciatura)');
insert into escolaridade (nome) values ('Especialização (Lato Sensu)');
insert into escolaridade (nome) values ('Mestrado (Stricto Sensu)');
insert into escolaridade (nome) values ('Doutorado (Stricto Sensu)');

insert into genero (nome) values ('Masculino');
insert into genero (nome) values ('Feminino');