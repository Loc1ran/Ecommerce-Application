DO $$ BEGIN
    IF current_database() <> 'ecommerce_resume_bench' THEN
        RAISE EXCEPTION 'Refusing to seed a non-benchmark database';
    END IF;
    IF EXISTS (SELECT 1 FROM products) OR EXISTS (SELECT 1 FROM categories) THEN
        RAISE EXCEPTION 'Benchmark requires empty product/category tables';
    END IF;
END $$;
BEGIN;
INSERT INTO categories(id, name)
SELECT n, 'Category ' || n FROM generate_series(1, 100) n;
INSERT INTO products(id, name, price, description, category_id)
SELECT n, 'Product ' || lpad(n::text, 5, '0'),
       (100 + (n % 10000))::numeric / 100,
       'Deterministic benchmark product ' || n, 1 + (n % 100)
FROM generate_series(1, 50000) n;
SELECT setval(pg_get_serial_sequence('products', 'id'), 50000);
SELECT setval(pg_get_serial_sequence('categories', 'id'), 100);
COMMIT;
ANALYZE products;
ANALYZE categories;
