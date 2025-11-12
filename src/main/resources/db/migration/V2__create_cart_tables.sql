CREATE TABLE carts(
    id UUID NOT NULL PRIMARY KEY,
    date_created DATE DEFAULT CURRENT_DATE
);

CREATE TABLE cart_items(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cart_id UUID NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    CONSTRAINT cart_items_carts_id_fk
        FOREIGN KEY (cart_id) REFERENCES carts (id) ON DELETE CASCADE ,
    CONSTRAINT cart_items_products_id_fk
        FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT cart_items_unique_cart_product UNIQUE (cart_id, product_id)
)