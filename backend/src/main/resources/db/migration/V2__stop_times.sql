create table stop_times (
  id bigserial primary key,
  trip_id text not null,
  route_id text not null,
  stop_id text not null references stops(id),
  stop_sequence int not null,
  scheduled_arrival_ts bigint not null
);

create index on stop_times(stop_id);
create index on stop_times(route_id, scheduled_arrival_ts);
