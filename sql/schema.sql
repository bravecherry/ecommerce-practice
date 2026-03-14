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
    created_at       timestamp default current_timestamp,
    updated_at       timestamp
);

create index idx_products_product_status on products (product_status);
create index idx_products_category on products (category);
create index idx_products_brand on products (brand);
create index idx_products_manufacturer on products (manufacturer);
create index idx_products_seller_id on products (seller_id);

-- auto_increment 는 병렬 처리라던가 성능 관점에서 최선의 선택은 아니다.
-- 병렬 처리를 하고 다양한 주문을 빠르게 받는다는 관점에서는 ID 값이 auto_increment 이면 불리한 편이다.
create table payment
(
    payment_id     bigserial primary key,
    payment_method varchar(50) not null,
    payment_status varchar(50) not null,
    payment_date   timestamp   not null,
    amount         integer     not null,
    order_id       bigint      not null unique
);

create index idx_payment_order_id on payment (order_id);

create table orders
(
    order_id     bigserial primary key,
    order_date   timestamp   not null,
    order_status varchar(50) not null,
    customer_id  bigint
);

create index idx_orders_customer_id on orders (customer_id);

create table order_items
(
    order_item_id bigserial primary key,
    quantity      integer      not null,
    unit_price    integer      not null,
    product_id    varchar(255) not null,
    order_id      bigint       not null
);

create index idx_order_items_order_id on order_items (order_id);
create index idx_order_items_product_id on order_items (product_id);

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
    product_count          bigint         not null,
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
    product_count          bigint         not null,
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
    product_count          bigint         not null,
    avg_sales_price        decimal(15, 0) not null,
    potential_sales_amount decimal(20, 0) not null,
    primary key (stat_date, manufacturer)
);

create table product_status_reports
(
    stat_date          date           not null,
    product_status     varchar(255)   not null,
    product_count      bigint         not null,
    avg_stock_quantity decimal(15, 0) not null,
    primary key (stat_date, product_status)
);
