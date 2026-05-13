# :core:data

Aggregator module for cross-feature repository implementations.

**What lives here:**
- Repositories that span multiple feature domains
  (e.g. `StabilityScoreRepository` reads from finance, workout, nutrition).
- Mappers between domain models and entity / DTO types
  when those mappers are shared across features.

**What does NOT live here:**
- Feature-private repos — those live in the feature module itself.
- Room entities — those live in `:core:database` (because the unified
  Room database needs them all in one classpath).

Add cross-feature repos and their Hilt bindings under `repository/`
and `di/` as features get ported.
