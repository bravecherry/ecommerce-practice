create table products
(
    product_id       varchar(255) primary key,
    seller_id        bigint       not null,
    category         varchar(255) not null,
    product_name     varchar(255) not null,
    sales_start_date date,
    sales_end_date   date,
    product_status   varchar(50),
    brand            varchar(255),
    manufacturer     varchar(255),
    sales_price      integer      not null,
    stock_quantity   integer   default 0,
    created_at       timestamp default current_timestamp(),
    updated_at       timestamp
);

create index idx_products_product_status on products (product_status);
create index idx_products_category on products (category);
create index idx_products_brand on products (brand);
create index idx_products_manufacturer on products (manufacturer);
create index idx_products_seller_id on products (seller_id);

create table transaction_reports
(
    transaction_date     date,
    transaction_type     varchar(50)    not null,
    transaction_count    bigint         not null,
    total_amount         bigint         not null,
    customer_count       bigint         not null,
    order_count          bigint         not null,
    payment_method_count bigint         not null,
    avg_product_count    decimal(15, 0) not null,
    total_item_quantity  bigint         not null,
    primary key (transaction_date, transaction_type)
);

create table brand_reports
(
    stat_date              date           not null,
    brand                  varchar(255)   not null,
    product_count          integer        not null,
    avg_sales_price        decimal(15, 0) not null,
    max_sales_price        decimal(15, 0) not null,
    min_sales_price        decimal(15, 0) not null,
    total_stock_quantity   integer        not null,
    avg_stock_quantity     decimal(15, 0) not null,
    potential_sales_amount decimal(20, 0) not null,
    primary key (stat_date, brand)
);

create table category_reports
(
    stat_date              date           not null,
    category               varchar(255)   not null,
    product_count          integer        not null,
    avg_sales_price        decimal(15, 0) not null,
    max_sales_price        decimal(15, 0) not null,
    min_sales_price        decimal(15, 0) not null,
    total_stock_quantity   integer        not null,
    potential_sales_amount decimal(20, 0) not null,
    primary key (stat_date, category)
);

create table manufacturer_reports
(
    stat_date              date           not null,
    manufacturer           varchar(255)   not null,
    product_count          integer        not null,
    avg_sales_price        decimal(15, 0) not null,
    potential_sales_amount decimal(20, 0) not null,
    primary key (stat_date, manufacturer)
);

create table product_status_reports
(
    stat_date          date           not null,
    product_status     varchar(255)   not null,
    product_count      integer        not null,
    avg_stock_quantity decimal(15, 0) not null,
    primary key (stat_date, product_status)
);
