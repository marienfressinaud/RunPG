# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table chapitre (
  numero                    integer not null,
  nom                       varchar(255),
  constraint pk_chapitre primary key (numero))
;

create table etat_quete (
  id                        integer not null,
  vitesse_moyenne           integer,
  distance                  integer,
  duree                     integer,
  fichier_parcours          varchar(255),
  etat                      integer,
  joueur_pseudo             varchar(255),
  quete_id                  integer,
  constraint ck_etat_quete_etat check (etat in (0,1,2,3)),
  constraint pk_etat_quete primary key (id))
;

create table joueur (
  pseudo                    varchar(255) not null,
  password                  varchar(255),
  score                     integer,
  xp_vitesse                integer,
  xp_endurance              integer,
  constraint pk_joueur primary key (pseudo))
;

create table quete (
  id                        integer not null,
  titre                     varchar(255),
  sequence_deb              varchar(255),
  sequence_fin              varchar(255),
  obj_distance              integer,
  obj_duree                 integer,
  suivante_id               integer,
  chapitre_numero           integer,
  constraint pk_quete primary key (id))
;

create sequence chapitre_seq;

create sequence etat_quete_seq;

create sequence joueur_seq;

create sequence quete_seq;

alter table etat_quete add constraint fk_etat_quete_joueur_1 foreign key (joueur_pseudo) references joueur (pseudo) on delete restrict on update restrict;
create index ix_etat_quete_joueur_1 on etat_quete (joueur_pseudo);
alter table etat_quete add constraint fk_etat_quete_quete_2 foreign key (quete_id) references quete (id) on delete restrict on update restrict;
create index ix_etat_quete_quete_2 on etat_quete (quete_id);
alter table quete add constraint fk_quete_suivante_3 foreign key (suivante_id) references quete (id) on delete restrict on update restrict;
create index ix_quete_suivante_3 on quete (suivante_id);
alter table quete add constraint fk_quete_chapitre_4 foreign key (chapitre_numero) references chapitre (numero) on delete restrict on update restrict;
create index ix_quete_chapitre_4 on quete (chapitre_numero);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists chapitre;

drop table if exists etat_quete;

drop table if exists joueur;

drop table if exists quete;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists chapitre_seq;

drop sequence if exists etat_quete_seq;

drop sequence if exists joueur_seq;

drop sequence if exists quete_seq;

