# :ai

Adaptive planning, meal optimization, workout adaptation, wellness insights.

Per spec, this module exposes pure use-case interfaces (`OptimiseMeals`,
`AdaptWorkout`, `DetectBurnout`) that take typed inputs from other modules
and return typed recommendations. Feature modules consume these — they
should NOT speak directly to OpenAI / a custom model.

This separation keeps the AI implementation swappable:
on-device (PaLM/MediaPipe) ⇄ remote (OpenAI/Anthropic) ⇄ deterministic
heuristics — same interface.
