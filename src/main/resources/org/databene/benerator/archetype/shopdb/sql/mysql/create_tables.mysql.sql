CREATE TABLE db_category (
  id        varchar(9)  NOT NULL,
  name      varchar(30) NOT NULL,
  parent_id varchar(9)  default NULL,
  PRIMARY KEY (id),
  KEY db_category_parent_fk (parent_id),
  CONSTRAINT db_category_parent_fk FOREIGN KEY (parent_id) REFERENCES db_category (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE db_product (
  ean_code     varchar(13)  NOT NULL,
  name         varchar(30)  NOT NULL,
  category_id  varchar(9)   NOT NULL,
  price        float(8,2)   NOT NULL,
  manufacturer varchar(30)  NOT NULL,
  notes        varchar(256)     NULL,
  description  text             NULL,
  image        blob             NULL,
  PRIMARY KEY  (ean_code),
  KEY db_product_category_fk (category_id),
  CONSTRAINT db_product_category_fk FOREIGN KEY (category_id) REFERENCES db_category (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE db_role (
  name varchar(16) NOT NULL,
  PRIMARY KEY  (name)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE db_user (
  id       int(10)     NOT NULL,
  name     varchar(30) NOT NULL,
  email    varchar(50) NOT NULL,
  password varchar(16) NOT NULL,
  role_id  varchar(16) NOT NULL,
  active   boolean     NOT NULL default '1',
  PRIMARY KEY  (id),
  KEY db_user_role_fk (role_id),
  CONSTRAINT db_user_role_fk FOREIGN KEY (role_id) REFERENCES db_role (name),
  constraint active_flag check (active in (0,1))
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE db_customer (
  id         int(10)     NOT NULL,
  category   char(1)      NOT NULL,
  salutation varchar(10) NULL,
  first_name varchar(30) NOT NULL,
  last_name  varchar(30) NOT NULL,
  birth_date date,
  PRIMARY KEY  (id),
  CONSTRAINT db_customer_user_fk FOREIGN KEY (id) REFERENCES db_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE db_order (
  id          int(10)  NOT NULL,
  customer_id int(10)  NOT NULL,
  total_price float(8,2)  NOT NULL,
  created_at  DATETIME NOT NULL,
  PRIMARY KEY (id),
  KEY db_order_customer_fk (customer_id),
  CONSTRAINT db_order_customer_fk FOREIGN KEY (customer_id) REFERENCES db_customer (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE db_order_item (
  id              int(10)     NOT NULL,
  order_id        int(10)     NOT NULL,
  number_of_items int(10)     NOT NULL default 1,
  product_ean_code      varchar(13) NOT NULL,
  total_price     float(8,2)  NOT NULL,
  PRIMARY KEY  (id),
  KEY db_order_item_order_fk (order_id),
  KEY db_order_item_product_fk (product_ean_code),
  CONSTRAINT db_order_item_order_fk FOREIGN KEY (order_id) REFERENCES db_order (id),
  CONSTRAINT db_order_item_product_fk FOREIGN KEY (product_ean_code) REFERENCES db_product (ean_code)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
