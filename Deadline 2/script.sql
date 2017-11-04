/*==============================================================*/
/* DBMS name:      ORACLE Version 11g                           */
/* Created on:     16/12/2016 15:19:54                          */
/*==============================================================*/


alter table LEILAO
   drop constraint FK_LEILAO_CRIA2_UTILIZAD;

alter table LEILAO
   drop constraint FK_LEILAO_VENCE_UTILIZAD;

alter table LICITACAO
   drop constraint FK_LICITACA_FAZ_UTILIZAD;

alter table LICITACAO
   drop constraint FK_LICITACA_PERTENCE2_LEILAO;

alter table MSG_MURAL
   drop constraint FK_MSG_MURA_CRIA_UTILIZAD;

alter table MSG_MURAL
   drop constraint FK_MSG_MURA_PERTENCE_LEILAO;

alter table MSG_NOTIFICACAO
   drop constraint FK_MSG_NOTI_GERA_MSG_MURA;

alter table MSG_NOTIFICACAO
   drop constraint FK_MSG_NOTI_RECEBE_UTILIZAD;

alter table REGISTO
   drop constraint FK_REGISTO_CORRESPON_LEILAO;

drop index VENCE_FK;

drop index CRIA2_FK;

drop table LEILAO cascade constraints;

drop index PERTENCE2_FK;

drop index FAZ_FK;

drop table LICITACAO cascade constraints;

drop index CRIA_FK;

drop index PERTENCE_FK;

drop table MSG_MURAL cascade constraints;

drop index RECEBE_FK;

drop index GERA_FK;

drop table MSG_NOTIFICACAO cascade constraints;

drop index CORRESPONDE_FK;

drop table REGISTO cascade constraints;

drop table UTILIZADOR cascade constraints;

/*==============================================================*/
/* Table: LEILAO                                                */
/*==============================================================*/
create table LEILAO 
(
   LEILAOID             INTEGER              not null,
   UTILIZADORID         INTEGER              not null,
   VENCEDORID           INTEGER,
   TITULO               VARCHAR2(50)         not null,
   DESCRICAO            CLOB                 not null,
   PRECO_MAX            NUMBER(7,2)          not null,
   DATA_FIM             TIMESTAMP            not null,
   ARTIGO               INTEGER              not null,
   ESTADO               SMALLINT,
   constraint PK_LEILAO primary key (LEILAOID)
);

/*==============================================================*/
/* Index: CRIA2_FK                                              */
/*==============================================================*/
create index CRIA2_FK on LEILAO (
   UTILIZADORID ASC
);

/*==============================================================*/
/* Index: VENCE_FK                                              */
/*==============================================================*/
create index VENCE_FK on LEILAO (
   VENCEDORID ASC
);

/*==============================================================*/
/* Table: LICITACAO                                             */
/*==============================================================*/
create table LICITACAO 
(
   UTILIZADORID         INTEGER              not null,
   LEILAOID             INTEGER              not null,
   LICITACAOID          INTEGER              not null,
   VALOR                NUMBER(7,2)          not null,
   constraint PK_LICITACAO primary key (LICITACAOID)
);

/*==============================================================*/
/* Index: FAZ_FK                                                */
/*==============================================================*/
create index FAZ_FK on LICITACAO (
   UTILIZADORID ASC
);

/*==============================================================*/
/* Index: PERTENCE2_FK                                          */
/*==============================================================*/
create index PERTENCE2_FK on LICITACAO (
   LEILAOID ASC
);

/*==============================================================*/
/* Table: MSG_MURAL                                             */
/*==============================================================*/
create table MSG_MURAL 
(
   MSGID                INTEGER              not null,
   UTILIZADORID         INTEGER              not null,
   LEILAOID             INTEGER              not null,
   MENSAGEM             CLOB                 not null,
   constraint PK_MSG_MURAL primary key (MSGID)
);

/*==============================================================*/
/* Index: PERTENCE_FK                                           */
/*==============================================================*/
create index PERTENCE_FK on MSG_MURAL (
   LEILAOID ASC
);

/*==============================================================*/
/* Index: CRIA_FK                                               */
/*==============================================================*/
create index CRIA_FK on MSG_MURAL (
   UTILIZADORID ASC
);

/*==============================================================*/
/* Table: MSG_NOTIFICACAO                                       */
/*==============================================================*/
create table MSG_NOTIFICACAO 
(
   MSGID                INTEGER              not null,
   UTILIZADORID         INTEGER              not null,
   ENTREGUE             SMALLINT,
   constraint PK_MSG_NOTIFICACAO primary key (MSGID, UTILIZADORID)
);

/*==============================================================*/
/* Index: GERA_FK                                               */
/*==============================================================*/
create index GERA_FK on MSG_NOTIFICACAO (
   MSGID ASC
);

/*==============================================================*/
/* Index: RECEBE_FK                                             */
/*==============================================================*/
create index RECEBE_FK on MSG_NOTIFICACAO (
   UTILIZADORID ASC
);

/*==============================================================*/
/* Table: REGISTO                                               */
/*==============================================================*/
create table REGISTO 
(
   REGISTOID            INTEGER              not null,
   LEILAOID             INTEGER              not null,
   TITULO               VARCHAR2(50)         not null,
   DESCRICAO            CLOB                 not null,
   PRECO_MAX            NUMBER(7,2)          not null,
   DATA_FIM             TIMESTAMP            not null,
   ARTIGO               INTEGER              not null,
   ESTADO               SMALLINT,
   constraint PK_REGISTO primary key (REGISTOID)
);

/*==============================================================*/
/* Index: CORRESPONDE_FK                                        */
/*==============================================================*/
create index CORRESPONDE_FK on REGISTO (
   LEILAOID ASC
);

/*==============================================================*/
/* Table: UTILIZADOR                                            */
/*==============================================================*/
create table UTILIZADOR 
(
   UTILIZADORID         INTEGER              not null,
   UTILIZADOR           VARCHAR2(50)         not null,
   PASSWORD             VARCHAR2(50)         not null,
   PREVILEGIOS          SMALLINT             not null,
   BANIDO               SMALLINT,
   FB_ID		NUMBER(20),
   FB_TOKEN             VARCHAR2(250),
   constraint PK_UTILIZADOR primary key (UTILIZADORID)
);

alter table LEILAO
   add constraint FK_LEILAO_CRIA2_UTILIZAD foreign key (UTILIZADORID)
      references UTILIZADOR (UTILIZADORID);

alter table LEILAO
   add constraint FK_LEILAO_VENCE_UTILIZAD foreign key (VENCEDORID)
      references UTILIZADOR (UTILIZADORID);

alter table LICITACAO
   add constraint FK_LICITACA_FAZ_UTILIZAD foreign key (UTILIZADORID)
      references UTILIZADOR (UTILIZADORID);

alter table LICITACAO
   add constraint FK_LICITACA_PERTENCE2_LEILAO foreign key (LEILAOID)
      references LEILAO (LEILAOID);

alter table MSG_MURAL
   add constraint FK_MSG_MURA_CRIA_UTILIZAD foreign key (UTILIZADORID)
      references UTILIZADOR (UTILIZADORID);

alter table MSG_MURAL
   add constraint FK_MSG_MURA_PERTENCE_LEILAO foreign key (LEILAOID)
      references LEILAO (LEILAOID);

alter table MSG_NOTIFICACAO
   add constraint FK_MSG_NOTI_GERA_MSG_MURA foreign key (MSGID)
      references MSG_MURAL (MSGID);

alter table MSG_NOTIFICACAO
   add constraint FK_MSG_NOTI_RECEBE_UTILIZAD foreign key (UTILIZADORID)
      references UTILIZADOR (UTILIZADORID);

alter table REGISTO
   add constraint FK_REGISTO_CORRESPON_LEILAO foreign key (LEILAOID)
      references LEILAO (LEILAOID);

