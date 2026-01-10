# Docker / PostgreSQL Setup

This project ships with a simple Docker/Postgres setup to run the `users_db` database locally.

## 1. Start PostgreSQL with Docker Compose

From the project root:

```bash
docker-compose up -d db
```

This will:

- Start the Postgres container defined as service `db` in `docker-compose.yml`
- Create the `users_db` database
- Run `docker/postgres/initdb/init.sql` on first startup to create the `users` table and any indexes

> **Note:** Files under `docker/postgres/initdb/` run only the first time the Postgres volume is initialized.
> If you change `init.sql` and want to re-run it, you must remove the volume (see below).

## 2. Inspect and connect to the database

### Check container status and logs

```bash
docker-compose ps
docker-compose logs db
```

### Connect with `psql` from the host

You can connect to the database using psql:

```bash
psql -h localhost -U <db_user> -d users_db -p <db_port>
```

Replace `<db_user>` / `<db_port>` with the values you configured via environment variables (for example `DB_USER` and `DB_PORT`).

Once inside `psql`:

```sql
\dt           -- list tables
\d+ users     -- describe users table
SELECT * FROM users LIMIT 10;
```

## 3. Reinitializing the database (dev only)

If you need to drop all data and re-run `init.sql` (for example, after changing the schema or seed data), you must remove the data volume:

```bash
# WARNING: this deletes ALL data in the Postgres volume

docker-compose down -v

docker-compose up -d db
```

The `init.sql` script will then run again on the fresh database.

## 4. Notes about `init.sql` and schema

The script `docker/postgres/initdb/init.sql`:

- Connects to the `users_db` database
- Ensures the `pgcrypto` extension exists (if used)
- Creates the `users` table if it does not already exist, with the following structure (simplified):
  - `id SERIAL PRIMARY KEY`
  - `first_name`, `last_name`, `email`, `username`, `password_hash`
  - `active_status`, `email_verified`, `user_role`
  - `created_date`, `last_login_date`, `disable_date`
  - `phone_number`, `profile_picture_url`, `failed_login_attempts`
- Creates an index on `email` for fast lookup: `idx_users_email`.

The Spring Boot application maps to this table via the `User` JPA entity in `src/main/java/com/olmaitsolutions/edu/boja/apiusers/apiusers/model/User.java`.

## 5. Running the app against the Docker DB

After the DB is up:

```bash
mvn spring-boot:run
```

Ensure the datasource URL and credentials in `src/main/resources/application.yaml` match the container configuration (host, port, database name, username, password). You can also override them via `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, and `DB_PASSWORD` environment variables when starting the app.

Once both DB and app are running, you can interact with the API at `http://localhost:8080/api/users`.
