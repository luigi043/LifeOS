# LifeOS — Phase 2 Implementation Plan
**Generated:** 2026-05-15 | **Status:** Phase 1 Foundation ✅ Complete

---

## Current State Assessment

Phase 1 is done. The modular architecture is fully scaffolded:

- ✅ All 11 feature modules exist (`today`, `finance`, `workout`, `nutrition`, `grocery`, `mentalhealth`, `profile`, `habits`, `analytics`, `settings`, `onboarding`)
- ✅ Core modules wired (`database`, `domain`, `data`, `network`, `datastore`, `designsystem`, `common`)
- ✅ `UserProfile` domain model + Room entity + DAO live and functional
- ✅ `Money`, `StabilityScore`, `CurrencyAmount` domain models defined
- ✅ Hilt, Room, Coroutines, Compose all integrated via convention plugins
- ⚠️ Every feature screen is a placeholder — no real data, no entities, no use cases
- ⚠️ `LifeOsDatabase` has only `UserProfileEntity` (version 1) — all feature tables missing

**Phase 2 goal:** wire the 5 core features with real data. Each section below defines exactly what to build.

---

## Priority Build Order

```
1. Finance Tracking     ← foundation for StabilityScore budget leg
2. Workout Tracking     ← most self-contained feature
3. Meal Planning        ← depends on Nutrition entities
4. Grocery List         ← depends on Meal Planning recipes
5. Progress Tracking    ← depends on data from all above
```

---

## 1. Finance Tracking

### Goal
Track expenses, income, budgets, and subscriptions with weekly/monthly analytics.

### Domain Models to create
`core/domain/src/main/kotlin/com/luigi/lifeos/core/domain/model/`

```kotlin
// Expense.kt
data class Expense(
    val id: Long = 0,
    val amount: Money,
    val currency: String,
    val categoryId: Long,
    val note: String,
    val isRecurring: Boolean,
    val recurringPeriod: RecurringPeriod?,
    val dateEpochMillis: Long,
    val createdAtEpochMillis: Long,
)

// Income.kt
data class Income(
    val id: Long = 0,
    val amount: Money,
    val currency: String,
    val source: String,
    val isRecurring: Boolean,
    val recurringPeriod: RecurringPeriod?,
    val dateEpochMillis: Long,
)

// Budget.kt
data class Budget(
    val id: Long = 0,
    val categoryId: Long?,       // null = overall budget
    val limitAmount: Money,
    val currency: String,
    val periodStart: Long,
    val periodEnd: Long,
)

// ExpenseCategory.kt
data class ExpenseCategory(
    val id: Long = 0,
    val name: String,            // "Food", "Transport", "Subscriptions"
    val iconName: String,
    val colorHex: String,
)

// Enums
enum class RecurringPeriod { DAILY, WEEKLY, MONTHLY, YEARLY }
```

### Room Entities to create
`core/database/src/main/java/com/luigi/lifeos/core/database/entity/`

```
ExpenseEntity.kt         ← @Entity(tableName = "expenses")
IncomeEntity.kt          ← @Entity(tableName = "income")
BudgetEntity.kt          ← @Entity(tableName = "budgets")
ExpenseCategoryEntity.kt ← @Entity(tableName = "expense_categories")
```

### DAOs to create
`core/database/src/main/java/com/luigi/lifeos/core/database/dao/`

```kotlin
// ExpenseDao.kt
@Dao interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date_epoch_millis DESC")
    fun observeAll(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE date_epoch_millis BETWEEN :from AND :to")
    fun observeByPeriod(from: Long, to: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT SUM(amount_minor) FROM expenses WHERE date_epoch_millis BETWEEN :from AND :to")
    fun totalSpentInPeriod(from: Long, to: Long): Flow<Long?>

    @Upsert suspend fun upsert(expense: ExpenseEntity)
    @Delete suspend fun delete(expense: ExpenseEntity)
}

// BudgetDao.kt — similar pattern
```

### Repositories to create
`feature/finance/src/main/java/com/luigi/lifeos/feature/finance/data/`

```
FinanceRepository.kt          ← interface (in domain if cross-feature)
FinanceRepositoryImpl.kt      ← Room-backed implementation
```

### Use Cases to create
`feature/finance/src/main/java/com/luigi/lifeos/feature/finance/domain/usecase/`

```
AddExpenseUseCase.kt
GetExpensesUseCase.kt
GetBudgetSummaryUseCase.kt    ← returns remaining budget + % used
GetMonthlyAnalyticsUseCase.kt ← totals by category
```

### ViewModel State to build
```kotlin
data class FinanceUiState(
    val totalSpentThisMonth: Money,
    val budgetRemaining: Money,
    val budgetUsedPercent: Float,
    val recentExpenses: List<Expense>,
    val expensesByCategory: Map<ExpenseCategory, Money>,
    val isLoading: Boolean,
)
```

### UI Screens to build
- `FinanceScreen` — overview card (budget gauge, recent expenses list)
- `AddExpenseScreen` — amount, category, note, date picker
- `ExpenseDetailScreen` — view/edit/delete
- `BudgetScreen` — budget progress bars by category
- `FinanceAnalyticsScreen` — monthly bar chart (MPAndroidChart)

### Database Migration
- Bump `LifeOsDatabase` version: **1 → 2**
- Add migration: `MIGRATION_1_2` adding `expenses`, `income`, `budgets`, `expense_categories` tables

---

## 2. Workout Tracking

### Goal
Log workouts, track sets/reps/weights, view history, track body weight progress.

### Domain Models to create

```kotlin
// WorkoutPlan.kt
data class WorkoutPlan(
    val id: Long = 0,
    val name: String,           // "Push Day A", "Leg Day"
    val muscleGroups: List<MuscleGroup>,
    val estimatedDurationMin: Int,
    val exercises: List<PlannedExercise>,
)

// Exercise.kt
data class Exercise(
    val id: Long = 0,
    val name: String,
    val primaryMuscle: MuscleGroup,
    val secondaryMuscles: List<MuscleGroup>,
    val equipmentType: EquipmentType,
    val instructions: String,
)

// WorkoutLog.kt — a completed session
data class WorkoutLog(
    val id: Long = 0,
    val planId: Long?,
    val startedAtEpochMillis: Long,
    val completedAtEpochMillis: Long?,
    val durationSeconds: Int,
    val sets: List<ExerciseSet>,
    val note: String,
)

// ExerciseSet.kt
data class ExerciseSet(
    val exerciseId: Long,
    val setNumber: Int,
    val reps: Int?,
    val weightKg: Double?,
    val durationSeconds: Int?,  // for timed exercises
    val isPersonalRecord: Boolean,
)

// WeightLog.kt
data class WeightLog(
    val id: Long = 0,
    val weightKg: Double,
    val dateEpochMillis: Long,
)

enum class MuscleGroup {
    CHEST, BACK, SHOULDERS, BICEPS, TRICEPS,
    FOREARMS, CORE, GLUTES, QUADS, HAMSTRINGS, CALVES, FULL_BODY
}
enum class EquipmentType { BARBELL, DUMBBELL, MACHINE, BODYWEIGHT, CABLE, BAND }
```

### Room Entities
```
WorkoutPlanEntity.kt
ExerciseEntity.kt
WorkoutLogEntity.kt
ExerciseSetEntity.kt
WeightLogEntity.kt
```

### Key DAOs
```kotlin
// WorkoutLogDao
@Query("SELECT * FROM workout_logs ORDER BY started_at DESC LIMIT :limit")
fun observeRecentLogs(limit: Int = 20): Flow<List<WorkoutLogEntity>>

// WeightLogDao
@Query("SELECT * FROM weight_logs ORDER BY date_epoch_millis DESC")
fun observeAll(): Flow<List<WeightLogEntity>>

@Query("SELECT * FROM weight_logs WHERE date_epoch_millis > :since")
fun observeSince(since: Long): Flow<List<WeightLogEntity>>
```

### Use Cases
```
LogWorkoutUseCase.kt
GetWorkoutHistoryUseCase.kt
GetWeightProgressUseCase.kt    ← returns trend data for chart
GetPersonalRecordsUseCase.kt
```

### UI Screens
- `WorkoutScreen` — today's plan card + quick-start button
- `ActiveWorkoutScreen` — live session: exercise list, set logger, timer
- `WorkoutHistoryScreen` — calendar or list of past sessions
- `WeightProgressScreen` — line chart of body weight over time
- `ExerciseLibraryScreen` — searchable exercise list by muscle group

### Database Migration
- Version: **2 → 3** (after Finance migration)

---

## 3. Meal Planning

### Goal
Plan weekly meals, track macros and calories, manage recipes with nutritional data.

### Domain Models to create

```kotlin
// Recipe.kt
data class Recipe(
    val id: Long = 0,
    val name: String,
    val servings: Int,
    val prepTimeMin: Int,
    val cookTimeMin: Int,
    val ingredients: List<RecipeIngredient>,
    val instructions: List<String>,
    val tags: List<String>,        // "high-protein", "budget", "quick"
    val estimatedCostMinor: Long?, // ties into Finance system
)

// Ingredient.kt
data class Ingredient(
    val id: Long = 0,
    val name: String,
    val defaultUnit: MeasurementUnit,
    val caloriesPer100g: Double,
    val proteinPer100g: Double,
    val carbsPer100g: Double,
    val fatPer100g: Double,
    val averagePriceMinorPerUnit: Long?, // for cost estimation
)

// RecipeIngredient.kt
data class RecipeIngredient(
    val ingredientId: Long,
    val quantity: Double,
    val unit: MeasurementUnit,
)

// MealPlan.kt
data class MealPlan(
    val id: Long = 0,
    val weekStartEpochMillis: Long,
    val days: Map<DayOfWeek, DayMeals>,
)

data class DayMeals(
    val breakfast: Long?,  // recipeId or null
    val lunch: Long?,
    val dinner: Long?,
    val snacks: List<Long>,
)

// MealLog.kt — what was actually eaten
data class MealLog(
    val id: Long = 0,
    val recipeId: Long?,
    val mealType: MealType,
    val servingsEaten: Double,
    val dateEpochMillis: Long,
    val manualCalories: Int?,       // for untracked foods
    val manualProteinG: Double?,
)

// NutritionSummary.kt — computed daily aggregate
data class NutritionSummary(
    val dateEpochMillis: Long,
    val totalCalories: Int,
    val totalProteinG: Double,
    val totalCarbsG: Double,
    val totalFatG: Double,
    val calorieTarget: Int,         // from UserProfile / goals
)

enum class MealType { BREAKFAST, LUNCH, DINNER, SNACK }
enum class MeasurementUnit { GRAM, KG, ML, L, CUP, TABLESPOON, TEASPOON, PIECE, SLICE }
```

### Room Entities
```
RecipeEntity.kt
IngredientEntity.kt
RecipeIngredientEntity.kt     ← junction table (recipeId, ingredientId, qty, unit)
MealPlanEntity.kt
MealPlanDayEntity.kt          ← each day row with FK to meal plan
MealLogEntity.kt
```

### Use Cases
```
GetDailyNutritionSummaryUseCase.kt
LogMealUseCase.kt
GetMealPlanForWeekUseCase.kt
GetRecipesUseCase.kt           ← filterable by tag, budget, macros
CalculateRecipeMacrosUseCase.kt
```

### UI Screens
- `NutritionScreen` — today's macro ring chart + meal log
- `MealPlanScreen` — weekly grid (Mon-Sun × Breakfast/Lunch/Dinner)
- `RecipeDetailScreen` — ingredients, macros, instructions
- `AddMealLogScreen` — pick recipe or enter manual calories
- `RecipeLibraryScreen` — searchable, filterable cards

### Database Migration
- Version: **3 → 4**

---

## 4. Grocery List

### Goal
Auto-generate grocery lists from meal plans, track pantry stock, estimate costs.

### Domain Models to create

```kotlin
// ShoppingList.kt
data class ShoppingList(
    val id: Long = 0,
    val name: String,
    val createdAtEpochMillis: Long,
    val weekStartEpochMillis: Long?,  // null = manual list
    val items: List<ShoppingItem>,
    val estimatedTotalMinor: Long,
    val currency: String,
)

// ShoppingItem.kt
data class ShoppingItem(
    val id: Long = 0,
    val listId: Long,
    val ingredientId: Long?,   // null = free-text item
    val name: String,
    val quantity: Double,
    val unit: MeasurementUnit,
    val estimatedCostMinor: Long?,
    val isChecked: Boolean,
    val isPantryItem: Boolean, // already have it at home
)

// PantryItem.kt
data class PantryItem(
    val id: Long = 0,
    val ingredientId: Long,
    val quantityOnHand: Double,
    val unit: MeasurementUnit,
    val expiresAtEpochMillis: Long?,
)
```

### Room Entities
```
ShoppingListEntity.kt
ShoppingItemEntity.kt
PantryItemEntity.kt
```

### Use Cases
```
GenerateGroceryListUseCase.kt    ← from MealPlan → merged ingredient list
                                    subtracts PantryItem quantities
                                    estimates costs via Ingredient.averagePrice

CheckShoppingItemUseCase.kt      ← marks item, optionally adds to pantry
GetActiveShoppingListUseCase.kt
EstimateListCostUseCase.kt       ← projects total spend (feeds Finance system)
```

### Key Algorithm — `GenerateGroceryListUseCase`
```
1. Load MealPlan for target week
2. Collect all RecipeIngredients for planned meals
3. Merge duplicate ingredients (sum quantities, normalise units)
4. Load PantryItems and subtract quantities already on hand
5. For remaining items: look up Ingredient.averagePriceMinorPerUnit
6. Return ShoppingList with estimated total
```

### UI Screens
- `GroceryScreen` — active list with check-off UX
- `GenerateListScreen` — pick week's meal plan → auto-generate
- `PantryScreen` — inventory with expiry warnings
- `ListHistoryScreen` — past grocery runs with actual costs

### Database Migration
- Version: **4 → 5**

---

## 5. Progress Tracking

### Goal
Unified view of progress across all features: weight trend, workout consistency, budget adherence, nutrition averages, Stability Score history.

### Domain Models to create

```kotlin
// StabilityScoreLog.kt — persist daily scores for trending
data class StabilityScoreLog(
    val id: Long = 0,
    val dateEpochMillis: Long,
    val score: Int,               // 0..100
    val workoutContribution: Double,
    val nutritionContribution: Double,
    val hydrationContribution: Double,
    val budgetContribution: Double,
    val sleepContribution: Double,
    val habitsContribution: Double,
)

// HydrationLog.kt
data class HydrationLog(
    val id: Long = 0,
    val amountMl: Int,
    val dateEpochMillis: Long,
)

// ProgressSnapshot.kt — for Today dashboard
data class ProgressSnapshot(
    val date: Long,
    val stabilityScore: StabilityScore,
    val caloriesConsumed: Int,
    val calorieTarget: Int,
    val waterMl: Int,
    val waterTargetMl: Int,
    val workoutCompleted: Boolean,
    val budgetRemainingMinor: Long,
    val activeStreaks: Map<String, Int>,   // habitName → daysStreak
)
```

### Use Cases
```
GetTodaySnapshotUseCase.kt        ← aggregates across all feature repos
                                    this is the Today screen engine
ComputeAndSaveStabilityScoreUseCase.kt
GetWeightTrendUseCase.kt          ← last N days weight logs → sparkline data
GetWorkoutStreakUseCase.kt        ← consecutive workout days
GetBudgetAdherenceUseCase.kt     ← % of months budget was met
GetNutritionAveragesUseCase.kt   ← last 7/30 day macro averages
```

### Repository — `StabilityScoreRepository` (already signalled in ARCHITECTURE.md)
Lives in `:core:data` since it reads from multiple feature DAOs:
```kotlin
interface StabilityScoreRepository {
    fun observeHistory(days: Int): Flow<List<StabilityScoreLog>>
    suspend fun saveScore(log: StabilityScoreLog)
}
```

### UI Screens
- `TodayScreen` — daily dashboard (this is the main app entry point)
  - Stability Score ring
  - Budget remaining bar
  - Calories / macros progress
  - Hydration tracker (tap to add water)
  - Today's workout card
  - Active habit streaks
- `AnalyticsScreen` — multi-tab: Weight / Workouts / Finance / Nutrition
  - Line chart: weight trend
  - Bar chart: workouts per week
  - Pie chart: expense categories
  - Stacked bar: macro breakdown over time
- `HabitsScreen` — habit list with streak indicators

### Database Migration
- Version: **5 → 6**

---

## Database Migration Summary

| Version | Change |
|---------|--------|
| 1 | UserProfile (current) |
| 2 | + expenses, income, budgets, expense_categories |
| 3 | + workout_plans, exercises, workout_logs, exercise_sets, weight_logs |
| 4 | + recipes, ingredients, recipe_ingredients, meal_plans, meal_plan_days, meal_logs |
| 5 | + shopping_lists, shopping_items, pantry_items |
| 6 | + stability_score_logs, hydration_logs |

Each migration lives in:
`core/database/src/main/java/com/luigi/lifeos/core/database/migration/MigrationX_Y.kt`

---

## Cross-System Wiring Points

These connections make LifeOS more than the sum of its parts:

| From | To | Data Flow |
|------|----|-----------|
| Finance | StabilityScore | budget adherence → score contributor |
| Workout | StabilityScore | session logged → score contributor |
| Nutrition | StabilityScore | calorie target met → score contributor |
| Grocery | Finance | estimated list cost → budget forecast |
| MealPlan | Grocery | planned recipes → auto-generated list |
| Nutrition | Workout | calorie surplus/deficit → energy hint |
| UserProfile | Nutrition | weight + goal → calorie target |
| UserProfile | Finance | foodBudget → grocery budget |

---

## Recommended Build Sequence (Sprint-Level)

### Sprint 1 — Finance Core
- [ ] Finance domain models + Room entities
- [ ] Migration 1→2
- [ ] ExpenseDao, IncomeDao, BudgetDao
- [ ] FinanceRepositoryImpl
- [ ] AddExpenseUseCase, GetBudgetSummaryUseCase
- [ ] FinanceViewModel real state
- [ ] FinanceScreen: budget card + expenses list
- [ ] AddExpenseScreen

### Sprint 2 — Workout Core
- [ ] Workout domain models + Room entities
- [ ] Migration 2→3
- [ ] WorkoutLogDao, WeightLogDao
- [ ] Pre-seed exercise library (JSON asset → Room)
- [ ] LogWorkoutUseCase, GetWeightProgressUseCase
- [ ] WorkoutScreen: today's plan + quick-start
- [ ] ActiveWorkoutScreen (set logger + timer)
- [ ] WeightProgressScreen (line chart)

### Sprint 3 — Nutrition + Meal Planning
- [ ] Recipe, Ingredient, MealLog domain models
- [ ] Migration 3→4
- [ ] RecipeDao, MealLogDao, MealPlanDao
- [ ] CalculateRecipeMacrosUseCase
- [ ] NutritionScreen: macro rings + meal log
- [ ] MealPlanScreen: weekly grid
- [ ] Basic recipe library (seed data)

### Sprint 4 — Grocery List
- [ ] ShoppingList, PantryItem domain models
- [ ] Migration 4→5
- [ ] GenerateGroceryListUseCase (merge + pantry subtract)
- [ ] GroceryScreen: check-off list
- [ ] GenerateListScreen: from meal plan

### Sprint 5 — Today Dashboard + Progress
- [ ] StabilityScoreLog, HydrationLog, ProgressSnapshot
- [ ] Migration 5→6
- [ ] StabilityScoreRepository in :core:data
- [ ] GetTodaySnapshotUseCase (aggregates everything)
- [ ] ComputeAndSaveStabilityScoreUseCase
- [ ] TodayScreen: full dashboard
- [ ] AnalyticsScreen: multi-chart view
- [ ] HydrationLog quick-add UX

---

## Notes on Architecture Decisions

**Why feature-local repositories?**
Single-feature DAOs (ExpenseDao, WorkoutLogDao) are accessed through repositories inside their own feature module. Only cross-feature aggregation (StabilityScoreRepository, GetTodaySnapshotUseCase) lives in `:core:data`. This keeps feature modules independently deployable.

**Pre-seeded data strategy**
Exercise library and starter recipes should ship as JSON assets in the APK, loaded on first launch via a WorkManager one-shot task. Avoids requiring network on first use.

**Calorie / macro precision**
Store calories as Int (whole kcal), macros as Double (grams with 1 decimal). No need for Money-style minor-unit treatment — nutritional data is never summed into financial calculations directly.

**Unit normalisation in grocery merge**
`GenerateGroceryListUseCase` must normalise units before merging: `2 cups + 500ml` of milk need a conversion table. Start with a simple `UnitConverter` utility in `:core:common` covering the most common pairs; flag exotic units as "cannot merge" rather than crash.
