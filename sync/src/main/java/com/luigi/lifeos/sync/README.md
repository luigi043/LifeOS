# :sync

Cloud synchronization — Supabase/Firebase reads + writes wrapped in a
single sync orchestrator and scheduled via WorkManager.

Phases (per spec roadmap):
- Phase 3: account system, multi-device sync, backup
- Phase 5: cross-platform sync via KMP shared client

Keep this module **agnostic of feature shape** — features expose generic
"upload these dirty rows" / "apply these remote changes" hooks rather
than sync touching their internals directly.
