# ============================================
# N_Agent - Railway Deployment Script (Windows)
# ============================================
# Prerequisites:
#   - Railway CLI installed (npm install -g @railway/cli)
#   - Railway account created
# Usage: .\deploy-railway.ps1 -Environment beta
# ============================================

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet("beta","prod")]
    [string]$Environment = "beta"
)

$ErrorActionPreference = "Stop"

$ProjectName = "n-agent-$Environment"

Write-Host "🚀 Deploying N_Agent to Railway.app" -ForegroundColor Cyan
Write-Host "Environment: $Environment" -ForegroundColor Yellow
Write-Host "Project: $ProjectName" -ForegroundColor Yellow
Write-Host ""

# Check Railway CLI
if (!(Get-Command railway -ErrorAction SilentlyContinue)) {
    Write-Host "❌ Railway CLI not found. Install with: npm install -g @railway/cli" -ForegroundColor Red
    exit 1
}

# Login to Railway
Write-Host "📝 Logging in to Railway..." -ForegroundColor Cyan
railway login

# Create or link project
Write-Host "📦 Creating/linking Railway project..." -ForegroundColor Cyan
try {
    railway init --name $ProjectName
} catch {
    railway link
}

# Add MongoDB service
Write-Host "🗄️ Adding MongoDB plugin..." -ForegroundColor Cyan
try {
    railway add --plugin mongodb
} catch {
    Write-Host "MongoDB already added" -ForegroundColor Yellow
}

# Set environment variables
Write-Host "⚙️ Setting environment variables..." -ForegroundColor Cyan

if ($Environment -eq "beta") {
    railway variables set SPRING_PROFILES_ACTIVE=beta
    railway variables set AI_MODEL=gpt-4o-mini
    railway variables set AI_TEMPERATURE=0.2
    railway variables set AI_MAX_TOKENS=2000
} else {
    railway variables set SPRING_PROFILES_ACTIVE=prod

    $aiModel = $env:AI_MODEL
    if ([string]::IsNullOrEmpty($aiModel)) { $aiModel = "gpt-4o-mini" }
    railway variables set AI_MODEL=$aiModel

    $aiTemp = $env:AI_TEMPERATURE
    if ([string]::IsNullOrEmpty($aiTemp)) { $aiTemp = "0.2" }
    railway variables set AI_TEMPERATURE=$aiTemp

    $aiMaxTokens = $env:AI_MAX_TOKENS
    if ([string]::IsNullOrEmpty($aiMaxTokens)) { $aiMaxTokens = "2000" }
    railway variables set AI_MAX_TOKENS=$aiMaxTokens
}

# Prompt for required secrets
Write-Host ""
Write-Host "🔑 Please enter your secrets:" -ForegroundColor Cyan

$openaiKey = Read-Host "OpenAI API Key"
railway variables set OPEN_AI_KEY="$openaiKey"

$githubAppId = Read-Host "GitHub App ID"
railway variables set GITHUB_APP_ID="$githubAppId"

$githubWebhookSecret = Read-Host "GitHub Webhook Secret"
railway variables set GITHUB_WEBHOOK_SECRET="$githubWebhookSecret"

# Upload private key as base64
Write-Host ""
Write-Host "📤 Uploading GitHub private key..." -ForegroundColor Cyan
$privateKeyPath = "..\..\code-analisys-agent.2025-12-17.private-key.pem"

if (!(Test-Path $privateKeyPath)) {
    Write-Host "❌ Private key not found at $privateKeyPath" -ForegroundColor Red
    Write-Host "ℹ️  Place your private key in the project root" -ForegroundColor Yellow
    exit 1
}

$privateKeyContent = Get-Content $privateKeyPath -Raw
$privateKeyBytes = [System.Text.Encoding]::UTF8.GetBytes($privateKeyContent)
$privateKeyBase64 = [Convert]::ToBase64String($privateKeyBytes)

railway variables set GITHUB_PRIVATE_KEY_BASE64="$privateKeyBase64"
railway variables set GITHUB_PRIVATE_KEY_PATH=/app/keys/private-key.pem

# Set other variables
railway variables set SERVER_PORT=8080
railway variables set MONGODB_URI='${{MongoDB.MONGO_URL}}'

# Create Procfile if not exists
if (!(Test-Path "Procfile")) {
    Write-Host "📝 Creating Procfile..." -ForegroundColor Cyan
    @"
web: java -Dserver.port=`$PORT `$JAVA_OPTS -jar target/*.jar
"@ | Out-File -FilePath "Procfile" -Encoding UTF8
}

# Create railway.json
Write-Host "📝 Creating railway.json configuration..." -ForegroundColor Cyan
@"
{
  "`$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "NIXPACKS",
    "buildCommand": "mvn clean package -DskipTests"
  },
  "deploy": {
    "startCommand": "java -Dserver.port=`$PORT `$JAVA_OPTS -jar target/*.jar",
    "healthcheckPath": "/code-agent/test/health",
    "healthcheckTimeout": 300,
    "restartPolicyType": "ON_FAILURE",
    "restartPolicyMaxRetries": 3
  }
}
"@ | Out-File -FilePath "railway.json" -Encoding UTF8

Write-Host ""
Write-Host "✅ Configuration complete!" -ForegroundColor Green
Write-Host ""
Write-Host "📦 Building and deploying to Railway..." -ForegroundColor Cyan
railway up

Write-Host ""
Write-Host "✨ Deployment complete!" -ForegroundColor Green
Write-Host ""
Write-Host "🔗 Get your deployment URL:" -ForegroundColor Cyan
Write-Host "   railway domain" -ForegroundColor Yellow
Write-Host ""
Write-Host "📊 View logs:" -ForegroundColor Cyan
Write-Host "   railway logs" -ForegroundColor Yellow
Write-Host ""
Write-Host "⚙️ Update webhook URL in GitHub App settings:" -ForegroundColor Cyan
Write-Host "   https://YOUR-APP.railway.app/code-agent/webhook" -ForegroundColor Yellow

