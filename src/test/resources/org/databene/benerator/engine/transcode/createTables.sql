create table ROLE (
  ID     int         not null,
  NAME   varchar(30) not null,
  constraint ROLE_PK primary key (ID)
);

create table USER (
  ID            int         not null,
  ROLE_FK       int			not null,
  NAME          varchar(30) not null,
  constraint USER_PK primary key (ID),
  constraint ROLE_USER_FK foreign key (ROLE_FK) references ROLE (ID)
);

create table COUNTRY (
  ID     int         not null,
  NAME   varchar(30) not null,
  constraint COUNTRY_PK primary key (ID)
);

create table STATE (
  ID               int         not null,
  COUNTRY_FK       int,
  NAME             varchar(30) not null,
  constraint STATE_PK primary key (ID),
  constraint STATE_COUNTRY_FK foreign key (COUNTRY_FK) references COUNTRY (ID)
);

create table CITY (
  ID       int         not null,
  STATE_FK int,
  NAME     varchar(30) not null,
  constraint CITY_PK primary key (ID),
  constraint CITY_STATE_FK foreign key (STATE_FK) references STATE (ID)
);

