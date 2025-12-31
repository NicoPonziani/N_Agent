#!/bin/bash

# ============================================
# N_Agent - Railway Deployment Script
# ============================================
# Prerequisites:
#   - Railway CLI installed (npm install -g @railway/cli)
#   - Railway account created
# Usage: ./deploy-railway.sh [beta|prod]
# ============================================

set -e

ENVIRONMENT=${1:-beta}
PROJECT_NAME="n-agent-${ENVIRONMENT}"

echo "🚀 Deploying N_Agent to Railway.app"
echo "Environment: ${ENVIRONMENT}"
echo "Project: ${PROJECT_NAME}"

# Check Railway CLI
if ! command -v railway &> /dev/null; then
    echo "❌ Railway CLI not found. Install with: npm install -g @railway/cli"
    exit 1
fi

# Login to Railway
echo "📝 Logging in to Railway..."
railway login

# Create or link project
echo "📦 Creating/linking Railway project..."
railway init --name "${PROJECT_NAME}" || railway link

# Add MongoDB service
echo "🗄️ Adding MongoDB plugin..."
railway add --plugin mongodb || echo "MongoDB already added"

# Set environment variables
echo "⚙️ Setting environment variables..."

if [ "${ENVIRONMENT}" = "beta" ]; then
    railway variables set SPRING_PROFILES_ACTIVE=beta
    railway variables set AI_MODEL=gpt-4o-mini
    railway variables set AI_TEMPERATURE=0.2
    railway variables set AI_MAX_TOKENS=2000
else
    railway variables set SPRING_PROFILES_ACTIVE=prod
    railway variables set AI_MODEL=${AI_MODEL:-gpt-4o-mini}
    railway variables set AI_TEMPERATURE=${AI_TEMPERATURE:-0.2}
    railway variables set AI_MAX_TOKENS=${AI_MAX_TOKENS:-2000}
fi

# Prompt for required secrets
echo ""
echo "🔑 Please enter your secrets:"

read -p "OpenAI API Key: " OPENAI_KEY
railway variables set OPEN_AI_KEY="${OPENAI_KEY}"

read -p "GitHub App ID: " GITHUB_APP_ID
railway variables set GITHUB_APP_ID="${GITHUB_APP_ID}"

read -p "GitHub Webhook Secret: " GITHUB_WEBHOOK_SECRET
railway variables set GITHUB_WEBHOOK_SECRET="${GITHUB_WEBHOOK_SECRET}"

# Upload private key as base64
echo ""
echo "📤 Uploading GitHub private key..."
PRIVATE_KEY_PATH="code-analisys-agent.2025-12-17.private-key.pem"

if [ ! -f "${PRIVATE_KEY_PATH}" ]; then
    echo "❌ Private key not found at ${PRIVATE_KEY_PATH}"
    exit 1
fi

PRIVATE_KEY_BASE64=$(cat "${PRIVATE_KEY_PATH}" | base64 -w 0 2>/dev/null || cat "${PRIVATE_KEY_PATH}" | base64)
railway variables set GITHUB_PRIVATE_KEY_BASE64="${PRIVATE_KEY_BASE64}"
railway variables set GITHUB_PRIVATE_KEY_PATH=/app/keys/private-key.pem

# Set other variables
railway variables set SERVER_PORT=8080
railway variables set MONGODB_URI='${{MongoDB.MONGO_URL}}'

# Create Procfile if not exists
if [ ! -f "Procfile" ]; then
    echo "📝 Creating Procfile..."
    cat > Procfile << 'EOF'
web: java -Dserver.port=$PORT $JAVA_OPTS -jar target/*.jar
EOF
fi

# Create railway.json
echo "📝 Creating railway.json configuration..."
cat > railway.json << EOF
{
  "\$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "NIXPACKS",
    "buildCommand": "mvn clean package -DskipTests"
  },
  "deploy": {
    "startCommand": "java -Dserver.port=\$PORT \$JAVA_OPTS -jar target/*.jar",
    "healthcheckPath": "/code-agent/test/health",
    "healthcheckTimeout": 300,
    "restartPolicyType": "ON_FAILURE",
    "restartPolicyMaxRetries": 3
  }
}
EOF

echo ""
echo "✅ Configuration complete!"
echo ""
echo "📦 Building and deploying to Railway..."
railway up

echo ""
echo "✨ Deployment complete!"
echo ""
echo "🔗 Get your deployment URL:"
echo "   railway domain"
echo ""
echo "📊 View logs:"
echo "   railway logs"
echo ""
echo "⚙️ Update webhook URL in GitHub App settings:"
echo "   https://YOUR-APP.railway.app/code-agent/webhook"

