# :notifications

Houses `@HiltWorker` classes for scheduled local notifications:
meal reminders, workout reminders, bill due alerts, hydration nudges, etc.

Workers run via WorkManager — the LifeOsApplication wires the
HiltWorkerFactory so `@HiltWorker` resolves dependencies correctly.

Each worker should be **idempotent** — WorkManager retries on failure
and a worker that fires the same notification twice is a UX bug.
