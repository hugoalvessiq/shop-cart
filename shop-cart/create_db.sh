#!/bin/bash

# Load variables from .env
set -o allexport
source .env
set +o allexport


# Checks if the bank already exists
EXISTS=$(PGPASSWORD=$DB_PASSWORD psql -U $DB_USER -d postgres -tAc "SELECT 1 FROM pg_database WHERE datname='$DB_NAME'")

if [ "$EXISTS" != "1" ]; then
    echo "🔧 Creating database '$DB_NAME'..."
    PGPASSWORD=$DB_PASSWORD createdb -U $DB_USER $DB_NAME
    echo "✅ Bank created successfully."
else
    echo "ℹ️  Database '$DB_NAME' already exists. Nothing to do."
fi
