create table brands
(
    brand_id   int auto_increment
        primary key,
    code       varchar(20) not null,
    brand_name varchar(20) not null
);

create table service_categories
(
    category_id   int auto_increment
        primary key,
    category_name varchar(50) not null
);

create table models
(
    model_id   int auto_increment
        primary key,
    code       varchar(125) not null,
    brand_id   int          not null,
    model_name varchar(125) not null,
    constraint vehicle_models_vehicle_brands_brand_id_fk
        foreign key (brand_id) references brands (brand_id)
);

create table services
(
    service_id  int auto_increment
        primary key,
    service     varchar(50)                       not null,
    price       double                            not null,
    currency    enum ('EUR', 'BGN', 'USD', 'GBP') not null,
    available   tinyint(1)                        not null,
    category_id int                               null,
    constraint services_categories_category_id_fk
        foreign key (category_id) references service_categories (category_id)
);

create table tokens
(
    token_id int auto_increment
        primary key,
    token varchar(30) null,
    token_creation_date datetime null
);

create table users
(
    user_id int auto_increment
        primary key,
    username varchar(20) not null,
    password varchar(20) not null,
    email varchar(60) not null,
    phone_number varchar(10) not null,
    user_role enum ('ADMINISTRATOR', 'EMPLOYEE', 'CUSTOMER') not null,
    user_names varchar(50) not null,
    user_status enum ('ACTIVE', 'INACTIVE') not null
);

create table user_token
(
    user_id int not null,
    token_id int not null,
    constraint user_token_tokens_token_id_fk
        foreign key (token_id) references tokens (token_id),
    constraint user_token_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

create table vehicles
(
    vehicle_id    int auto_increment
        primary key,
    license_plate varchar(8)  not null,
    vin           varchar(17) not null,
    creation_year year        not null,
    model_id      int         not null,
    user_id       int         not null,
    constraint vehicles_users_user_id_fk
        foreign key (user_id) references users (user_id),
    constraint vehicles_vehicle_models_model_id_fk
        foreign key (model_id) references models (model_id)
);

create table visits
(
    visit_id    int auto_increment
        primary key,
    total_price double                            not null,
    vehicle_id  int                               not null,
    visit_date  date                              not null,
    currency    enum ('EUR', 'BGN', 'USD', 'GBP') not null,
    status      enum ('OPEN', 'CLOSED')           not null,
    constraint visits_vehicles_vehicle_id_fk
        foreign key (vehicle_id) references vehicles (vehicle_id)
);

create table visit_service
(
    visit_service_id int auto_increment
        primary key,
    visit_id         int         not null,
    service_id       int         not null,
    service_name     varchar(50) not null,
    service_price    double      not null,
    constraint visit_service_services_service_id_fk
        foreign key (service_id) references smart_garage.services (service_id),
    constraint visit_service_visits_visit_id_fk
        foreign key (visit_id) references smart_garage.visits (visit_id)
);