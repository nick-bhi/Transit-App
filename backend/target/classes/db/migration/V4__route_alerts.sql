create table route_alerts (
  id bigserial primary key,
  start_stop_id text not null,
  end_stop_id text not null,
  route_id text not null,
  alert_threshold_minutes int not null,
  created_at timestamp with time zone not null default now()
);

create index on route_alerts(start_stop_id);
create index on route_alerts(route_id);
create index on route_alerts(created_at);
