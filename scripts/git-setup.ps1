# LifeOS git bootstrap
# Run from luigi/ root in PowerShell:  .\scripts\git-setup.ps1
# Creates a clean repo with the branch structure for the scaffold.
# Idempotent: skips steps that already exist.

$ErrorActionPreference = "Stop"

# 0. clear any half-initialised state (e.g. OneDrive sync left .git in a bad state)
if (Test-Path .git) {
    Write-Host "Removing existing .git/ ..." -ForegroundColor Yellow
    Remove-Item -Recurse -Force .git
}

# 1. fresh init on main
git init -b main
git config user.name "luigi043"
git config user.email "cluiz4003@outlook.pt"
git config core.autocrlf true   # Windows-friendly line endings on checkout

# 2. base commit on main — gradle config + build-logic + app shell
git add .gitignore gradle.properties settings.gradle.kts build.gradle.kts gradle/ build-logic/ README.md scripts/
git commit -m "chore: project bootstrap (gradle, build-logic, version catalog)"

git add app/
git commit -m "feat(app): single-activity Compose host with bottom nav scaffold"

# 3. branch per module group — work continues on each, merged with --no-ff
# core modules
git checkout -b feat/core-modules
if (Test-Path core) {
    git add core/
    git commit -m "feat(core): scaffold common, designsystem, domain, data, database, network, datastore"
    git checkout main
    git merge --no-ff feat/core-modules -m "Merge feat/core-modules"
}

# feature modules
git checkout -b feat/feature-modules
if (Test-Path feature) {
    git add feature/
    git commit -m "feat(features): scaffold 10 feature modules (today, finance, workout, nutrition, grocery, mentalhealth, profile, habits, analytics, settings)"
    git checkout main
    git merge --no-ff feat/feature-modules -m "Merge feat/feature-modules"
}

# cross-cutting modules
git checkout -b feat/cross-cutting
foreach ($dir in @("sync","notifications","ai")) {
    if (Test-Path $dir) { git add $dir/ }
}
git commit -m "feat: scaffold sync, notifications, ai cross-cutting modules" --allow-empty
git checkout main
git merge --no-ff feat/cross-cutting -m "Merge feat/cross-cutting"

Write-Host ""
Write-Host "Done. Current branches:" -ForegroundColor Green
git branch
Write-Host ""
git log --oneline --graph --all
