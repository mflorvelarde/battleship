# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table game (
  id                            bigint not null,
  facebook_id                   varchar(255),
  current_player_fb_id          varchar(255),
  date                          timestamp,
  gameswon                      bigint,
  gameslost                     bigint,
  constraint pk_game primary key (id)
);
create sequence game_seq;

create table player (
  id                            bigint not null,
  name                          varchar(255),
  facebook_id                   varchar(255),
  constraint pk_player primary key (id)
);
create sequence player_seq;

create table statistics (
  id                            bigint not null,
  actorref                      bigint,
  wins                          integer,
  looses                        integer,
  constraint uq_statistics_actorref unique (actorref),
  constraint pk_statistics primary key (id)
);
create sequence statistics_seq;

alter table game add constraint fk_game_gameswon foreign key (gameswon) references player (id) on delete restrict on update restrict;
create index ix_game_gameswon on game (gameswon);

alter table game add constraint fk_game_gameslost foreign key (gameslost) references player (id) on delete restrict on update restrict;
create index ix_game_gameslost on game (gameslost);

alter table statistics add constraint fk_statistics_actorref foreign key (actorref) references player (id) on delete restrict on update restrict;


# --- !Downs

alter table game drop constraint if exists fk_game_gameswon;
drop index if exists ix_game_gameswon;

alter table game drop constraint if exists fk_game_gameslost;
drop index if exists ix_game_gameslost;

alter table statistics drop constraint if exists fk_statistics_actorref;

drop table if exists game;
drop sequence if exists game_seq;

drop table if exists player;
drop sequence if exists player_seq;

drop table if exists statistics;
drop sequence if exists statistics_seq;

