create table courier (
    id bigint primary key,
    name text not null,
    speed int not null,
    location_x int not null,
    location_y int not null,
    created_at timestamptz not null,
    created_by text not null,
    modified_at timestamptz,
    modified_by text,
    version bigint not null
);

create table orders (
    id bigint primary key,
    volume int not null,
    status text not null,
    location_x int not null,
    location_y int not null,
    courier_id bigint references courier(id),
    created_at timestamptz not null,
    created_by text not null,
    modified_at timestamptz,
    modified_by text,
    version bigint not null
);

create table storage_place (
    id bigint primary key,
    courier_id bigint not null references courier(id) on delete cascade,
    place_type text not null,
    order_id bigint references orders(id),
    constraint storage_place_type_chk check (place_type in ('BACKPACK', 'TRUNK')),
    constraint storage_place_unique unique (courier_id, place_type)
);