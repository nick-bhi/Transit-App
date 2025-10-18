create table if not exists arrivals (
  id bigserial primary key,
  trip_id text not null,
  route_id text not null,
  stop_id text not null,
  actual_arrival_ts bigint not null,
  scheduled_arrival_ts bigint not null,
  delay_seconds int not null
);
create index if not exists arrivals_stop_time_idx on arrivals(stop_id, actual_arrival_ts);
