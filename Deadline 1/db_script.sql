/*==============================================================*/
/* Database: DATABASE_1                                         */
/*==============================================================*/
create database DATABASE_1;

create sequence S_LEILAO;

create sequence S_LICITACAO;

create sequence S_MSG_MURAL;

create sequence S_UTILIZADOR;

/*==============================================================*/
/* Table: LEILAO                                                */
/*==============================================================*/
create table LEILAO 
(
   LEILAOID             NUMBER(6)            not null,
   UTILIZADORID         NUMBER(6),
   ARTIGO               NUMBER(13),
   LEI_LEILAOID         NUMBER(6),
   TITULO               VARCHAR2(50)         not null,
   DESCRICAO            CLOB                 not null,
   PRECO_MAX            NUMBER(7,2)          not null,
   DATA_FIM             TIMESTAMP(6)         not null,
   VENCEDOR             NUMBER(6),
   ESTADO               NUMBER(6),
   constraint PK_LEILAO primary key (LEILAOID)
);

/*==============================================================*/
/* Index: RELATIONSHIP_1_FK                                     */
/*==============================================================*/
create index RELATIONSHIP_1_FK on LEILAO (
   UTILIZADORID ASC
);

/*==============================================================*/
/* Index: RELATIONSHIP_3_FK                                     */
/*==============================================================*/
create index RELATIONSHIP_3_FK on LEILAO (
   VENCEDOR ASC
);

/*==============================================================*/
/* Index: RELATIONSHIP_9_FK                                     */
/*==============================================================*/
create index RELATIONSHIP_9_FK on LEILAO (
   LEI_LEILAOID ASC
);

/*==============================================================*/
/* Table: LICITACAO                                             */
/*==============================================================*/
create table LICITACAO 
(
   LICITACAOID          NUMBER(6)            not null,
   UTILIZADORID         NUMBER(6),
   LEILAOID             NUMBER(6),
   VALOR                NUMBER(7,2)          not null,
   constraint PK_LICITACAO primary key (LICITACAOID)
);

/*==============================================================*/
/* Index: RELATIONSHIP_2_FK                                     */
/*==============================================================*/
create index RELATIONSHIP_2_FK on LICITACAO (
   UTILIZADORID ASC
);

/*==============================================================*/
/* Index: RELATIONSHIP_4_FK                                     */
/*==============================================================*/
create index RELATIONSHIP_4_FK on LICITACAO (
   LEILAOID ASC
);

/*==============================================================*/
/* Table: MSG_MURAL                                             */
/*==============================================================*/
create table MSG_MURAL 
(
   MSGID                NUMBER(6)            not null,
   UTILIZADORID         NUMBER(6),
   LEILAOID             NUMBER(6),
   MENSAGEM             CLOB                 not null,
   constraint PK_MSG_MURAL primary key (MSGID)
);

/*==============================================================*/
/* Index: RELATIONSHIP_7_FK                                     */
/*==============================================================*/
create index RELATIONSHIP_7_FK on MSG_MURAL (
   LEILAOID ASC
);

/*==============================================================*/
/* Index: RELATIONSHIP_8_FK                                     */
/*==============================================================*/
create index RELATIONSHIP_8_FK on MSG_MURAL (
   UTILIZADORID ASC
);

/*==============================================================*/
/* Table: MSG_NOTIFICACAO                                       */
/*==============================================================*/
create table MSG_NOTIFICACAO 
(
   UTILIZADORID         NUMBER(6),
   MSGID                NUMBER(6),
   ENTREGUE             SMALLINT,
   constraint PK_MSG_NOTIFICACAO primary key (MSGID, UTILIZADORID)
);

/*==============================================================*/
/* Index: RELATIONSHIP_5_FK                                     */
/*==============================================================*/
create index RELATIONSHIP_5_FK on MSG_NOTIFICACAO (
   MSGID ASC
);

/*==============================================================*/
/* Index: RELATIONSHIP_6_FK                                     */
/*==============================================================*/
create index RELATIONSHIP_6_FK on MSG_NOTIFICACAO (
   UTILIZADORID ASC
);

/*==============================================================*/
/* Table: UTILIZADOR                                            */
/*==============================================================*/
create table UTILIZADOR 
(
   UTILIZADORID         NUMBER(6)            not null,
   UTILIZADOR           VARCHAR2(50)         not null,
   PASSWORD             VARCHAR2(50)         not null,
   PREVILEGIOS          SMALLINT             not null,
   BANIDO               SMALLINT,
   constraint PK_UTILIZADOR primary key (UTILIZADORID)
);

alter table LEILAO
   add constraint FK_LEILAO_RELATIONS_UTILIZAD foreign key (UTILIZADORID)
      references UTILIZADOR (UTILIZADORID);

alter table LEILAO
   add constraint FK_LEILAO_RELATIONS_VENCE foreign key (VENCEDOR)
      references UTILIZADOR (UTILIZADORID);

alter table LEILAO
   add constraint FK_LEILAO_RELATIONS_LEILAO foreign key (LEI_LEILAOID)
      references LEILAO (LEILAOID);

alter table LICITACAO
   add constraint FK_LICITACA_RELATIONS_UTILIZAD foreign key (UTILIZADORID)
      references UTILIZADOR (UTILIZADORID);

alter table LICITACAO
   add constraint FK_LICITACA_RELATIONS_LEILAO foreign key (LEILAOID)
      references LEILAO (LEILAOID);

alter table MSG_MURAL
   add constraint FK_MSG_MURA_RELATIONS_LEILAO foreign key (LEILAOID)
      references LEILAO (LEILAOID);

alter table MSG_MURAL
   add constraint FK_MSG_MURA_RELATIONS_UTILIZAD foreign key (UTILIZADORID)
      references UTILIZADOR (UTILIZADORID);

alter table MSG_NOTIFICACAO
   add constraint FK_MSG_NOTI_RELATIONS_MSG_MURA foreign key (MSGID)
      references MSG_MURAL (MSGID);

alter table MSG_NOTIFICACAO
   add constraint FK_MSG_NOTI_RELATIONS_UTILIZAD foreign key (UTILIZADORID)
      references UTILIZADOR (UTILIZADORID);

INSERT INTO utilizador VALUES (0, 'admin', 'admin', 1, 0);
INSERT INTO utilizador VALUES (s_utilizador.nextval, 'dummy1', 'dummy1', 0, 0);
INSERT INTO utilizador VALUES (s_utilizador.nextval, 'dummy2', 'dummy2', 0, 0);