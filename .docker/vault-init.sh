#!/bin/sh

# Start Vault server in dev mode (background)
vault server -dev -dev-root-token-id=dev-root-token -dev-listen-address=0.0.0.0:8200 &

# Wait for Vault to be ready
export VAULT_ADDR='http://127.0.0.1:8200'
export VAULT_TOKEN='dev-root-token'

until vault status > /dev/null 2>&1; do
  echo "Waiting for Vault to start..."
  sleep 1
done

# Initialize secret engine and path (idempotent)
vault secrets enable -path=inventarbuddy kv-v2 2>/dev/null || true

# Read credentials from file and populate Vault --------------------------------------------------------
CREDENTIALS_FILE="/vault/config/env-credentials.local"

if [ -f "$CREDENTIALS_FILE" ]; then
  echo "📄 Reading credentials from $CREDENTIALS_FILE"

  # Process file with line continuation support
  current_line=""

  while IFS= read -r line || [ -n "$line" ]; do
    # Remove carriage returns and trim whitespace
    line=$(echo "$line" | tr -d '\r' | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')

    # Skip empty lines and comments (only if not continuing)
    if [ -z "$current_line" ]; then
      if [ -z "$line" ] || [ "${line#\#}" != "$line" ]; then
        continue
      fi
    fi

    # Check if line ends with backslash (continuation)
    if [ "${line%\\}" != "$line" ]; then
      # Remove trailing backslash and append to current_line
      line="${line%\\}"
      current_line="$current_line $line"
      continue
    else
      # No continuation, complete the line
      current_line="$current_line $line"
    fi

    # Process the complete line
    if [ -n "$current_line" ]; then
      # Trim the accumulated line
      current_line=$(echo "$current_line" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')

      # Extract path (first word) and key-value pairs (everything after)
      path=$(echo "$current_line" | awk '{print $1}')
      kvpairs=$(echo "$current_line" | cut -d' ' -f2-)

      if [ -n "$path" ] && [ -n "$kvpairs" ] && [ "$path" != "$kvpairs" ]; then
        echo "  ➜ Setting credentials at: $path"

        # Execute vault kv put with proper quoting using eval
        eval "vault kv put \"$path\" $kvpairs" || echo "    ⚠️  Failed to set credentials at: $path"
      fi
    fi

    # Reset for next entry
    current_line=""

  done < "$CREDENTIALS_FILE"

  echo "✅ All credentials loaded from file!"
else
  echo "⚠️  Credentials file not found: $CREDENTIALS_FILE"
fi #---------------------------------------------------------------------------------------------------

vault kv put inventarbuddy/local/ initialized=true

echo "✅ Vault ready with inventarbuddy engine!"

# Keep container running (wait for Vault process)
wait