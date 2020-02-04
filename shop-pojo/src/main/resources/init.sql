CREATE TABLE shop_coupon(
	coupon_id BIGINT(50) not null PRIMARY KEY,
	coupon_price DECIMAL(10,2) ,
	user_id BIGINT(50),
	is_used int(1),
	used_time TIMESTAMP,
	order_id BIGINT(50)
);

CREATE TABLE shop_goods(
	goods_id BIGINT(50) not null PRIMARY key ,
	goods_name VARCHAR(255),
	goods_number int(11),
	goods_price DECIMAL(10,2),
	goods_desc VARCHAR(255),
	add_time TIMESTAMP
);

CREATE TABLE shop_goods_number_log(
	goods_id BIGINT(50),
	order_id BIGINT(50),
	goods_number int(11),
	log_time TIMESTAMP
);

CREATE TABLE shop_mq_consumer_log(
	msg_id VARCHAR(50) not null,
	group_name VARCHAR(100),
	msg_topic VARCHAR(100),
	msg_tag VARCHAR(100),
	msg_key VARCHAR(100),
	msg_body VARCHAR(100),
	consumer_status int(1),
	consumer_times int(1),
	consumer_timestamp TIMESTAMP,
	remark VARCHAR(255),
    PRIMARY key(group_name, msg_topic, msg_tag)
);

CREATE TABLE shop_mq_producer_log(
	id VARCHAR(100) not null PRIMARY key,
	group_name VARCHAR(100),
	msg_topic VARCHAR(100),
	msg_tag VARCHAR(100),
	msg_key VARCHAR(100),
	msg_body VARCHAR(100),
	msg_status int(1),
	create_time TIMESTAMP
);

CREATE TABLE shop_order(
	order_id BIGINT(50) not null PRIMARY key,
	user_id BIGINT(50),
	order_status int(1),
	pay_status int(1),
	shipping_status int(1),
	address VARCHAR(255),
	consignee VARCHAR(255),
	goods_id BIGINT(50),
	goods_number int(11),
	goods_price DECIMAL(10,2),
	goods_amount DECIMAL(10,0),
	shipping_fee DECIMAL(10,2),
	order_amount DECIMAL(10,2),
	coupon_id BIGINT(50),
	coupon_paid DECIMAL(10,2),
	money_paid DECIMAL(10,2),
	pay_mount DECIMAL(10,2),
	add_time TIMESTAMP,
	confirm_time TIMESTAMP,
	pay_time TIMESTAMP
);

CREATE TABLE shop_pay(
	pay_id BIGINT(50) not null PRIMARY KEY,
	order_id BIGINT(50),
	pay_amount DECIMAL(10,2),
	is_paid int(1)
);

CREATE TABLE shop_user(
	user_id BIGINT(50) not null PRIMARY key,
	user_name varchar(255),
	user_password varchar(255),
	user_mobile VARCHAR(255),
	user_score int(11),
	user_reg_time TIMESTAMP,
	user_money DECIMAL(10,0)
);

CREATE TABLE shop_user_money_log(
	user_id BIGINT(50),
	order_id BIGINT(50),
	money_log_type int(1),
	use_money DECIMAL(10,2),
	create_time TIMESTAMP
);