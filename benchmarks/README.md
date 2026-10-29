# Reproduce the catalog comparison

Compare `findAll()` with `findAllWithCategory()` on 50,000 products across 100 categories. Each run measures SQL statement count and the time spent fetching products and mapping them to DTOs. Both paths use the same database and must return equivalent data.

Requirements: Java 25, Maven (or the included wrapper), PostgreSQL, and an isolated database named `ecommerce_resume_bench`. Never point this procedure at a live database.

1. Create the empty database and set `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD` for it. Set `SPRING_PROFILES_ACTIVE=benchmark`, a test-only `JWT_SECRET`, `STRIPE_SECRET_KEY=sk_test_benchmark_no_network`, and `STRIPE_WEBHOOK_SECRET_KEY=whsec_benchmark_no_network`. No payment network calls are made by the benchmark.
2. Run `./mvnw -Dtest=StoreApplicationTests test` to apply the four existing Flyway migrations and validate application startup.
3. Execute `psql -v ON_ERROR_STOP=1 -d ecommerce_resume_bench -f benchmarks/seed.sql` using the same host, port and database credentials. The seed refuses a different database name or nonempty product/category tables.
4. Run `./mvnw -Dtest=CatalogBenchmarkTest -Dcatalog.benchmark=true test` (use `mvnw.cmd` on Windows).
5. Preserve `target/catalog-benchmark.json`, the exact source commit/diff, JVM heap settings, CPU/RAM, PostgreSQL configuration, and the seed file. Repeat step 4 three times, retaining each JSON report before the next run overwrites it. Do not run other workloads during measurement.

The test runs three warmup pairs followed by ten measured pairs, alternating which path runs first. Each iteration clears the JPA persistence context and checks the row count and a checksum of all DTO fields. Database and OS caches stay warm; both paths share the same connection pool, logging, and Hibernate statistics settings.

Compare median latency and SQL counts across all three runs. These measurements cover repository calls and DTO mapping on a local database, not HTTP requests or production capacity. Fewer SQL statements do not necessarily mean lower latency.
