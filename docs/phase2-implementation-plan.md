# LifeOS — Phase 2 Implementation Plan
> Generated: 2026-05-15 | Scope: Core Features

---

## Current State (Phase 1 Complete)

| Layer | Status |
|---|---|
| Modular architecture (11 feature modules + core) | ✅ Done |
| Core DB — Room, v1, UserProfile entity only | ✅ Done |
| Domain models: `Money`, `StabilityScore`, `UserProfile` | ✅ Done |
| Design system: `LifeOsCard`, `LifeOsButton`, `LifeOsTextField`, `LifeOsProgressTile` | ✅ Done |
| Core infra: Dispatchers, Result, DataStore, Hilt, Retrofit | ✅ Done |
| All 5 feature screens | ⚠️ Placeholder only (tagline text, no data) |
| Database entities for features | ❌ Not started |
| DAOs / Repositories for features | ❌ Not started |
| Domain use cases for features | ❌ Not started |

---

## Phase 2 Goals

```
1. Finance Tracking
2. Meal Planning (Nutrition)
3. Grocery List
4. Workouts
5. Progress Tracking
```

---

## Implementation Order

**Why this order matters:** each layer must be built bottom-up (DB → Domain → Data → Presentation).
Finance and Workout are independent; Grocery depends on Nutrition (recipes → ingredients).

```
Phase 2A  →  Finance Tracking   (independent)
Phase 2B  →  Workout System     (independent)
Phase 2C  →  Meal Planning      (Nutrition)
Phase 2D  →  Grocery List       (depends on Nutrition recipes)
Phase 2E  →  Progress Tracking  (reads data from all above)
```

---

## Phase 2A — Finance Tracking

### Domain Models to add in `:core:domain`

```kotlin
// model/finance/
Expense(id, amount: Money, category: ExpenseCategory, note, date, isRecurring)
Income(id, amount: Money, source: String, date, isRecurring)
Budget(id, category: ExpenseCategory, limitAmount: Money, period: BudgetPeriod)
Subscription(id, name, amount: Money, billingDate, isActive)
ExpenseCategory  // enum: FOOD, TRANSPORT, HEALTH, ENTERTAINMENT, HOUSING, OTHER
BudgetPeriod     // enum: WEEKLY, MONTHLY
```

### Repository interfaces in `:core:domain`

```kotlin
interface ExpenseRepository {
    fun getExpenses(from: Instant, to: Instant): Flow<List<Expense>>
    suspend fun addExpense(expense: Expense)
    suspend fun deleteExpense(id: Long)
}

interface BudgetRepository {
    fun getBudgets(): Flow<List<Budget>>
    suspend fun upsertBudget(budget: Budget)
}

interface IncomeRepository {
    fun getMonthlyIncome(month: YearMonth): Flow<List<Income>>
    suspend fun addIncome(income: Income)
}
```

### Use Cases in `:core:domain`

```kotlin
GetMonthlySpendingUseCase  // returns total + per-category breakdown
GetBudgetStatusUseCase     // remaining budget per category this period
GetWeeklyExpensesUseCase
GetSubscriptionCostUseCase // total recurring monthly cost
```

### Database entities in `:core:database`

```kotlin
// Add to LifeOsDatabase (bump version to 2, add Migration_1_2)
@Entity ExpenseEntity(id, amountMinorUnits, currency, category, note, dateEpoch, isRecurring)
@Entity IncomeEntity(id, amountMinorUnits, currency, source, dateEpoch, isRecurring)
@Entity BudgetEntity(id, category, limitMinorUnits, currency, period)
@Entity SubscriptionEntity(id, name, amountMinorUnits, currency, billingDay, isActive)

// DAOs:
ExpenseDao   // insert, delete, getByDateRange, getSumByCategory
IncomeDao    // insert, getByMonth
BudgetDao    // upsert, getAll
SubscriptionDao // getAll, getActive
```

### UI in `:feature:finance`

```
FinanceScreen (tab layout)
├── OverviewTab
│   ├── BudgetRemainingCard       (total remaining this month)
│   ├── SpendingByCategoryChart   (MPAndroidChart PieChart)
│   └── RecentExpensesList
├── ExpensesTab
│   ├── AddExpenseFAB → AddExpenseSheet (amount, category, note, date)
│   └── ExpenseListByDay
├── BudgetsTab
│   └── BudgetProgressList        (per-category bar progress)
└── SubscriptionsTab
    └── SubscriptionList          (name, monthly cost, next billing)
```

**FinanceUiState:**
```kotlin
data class FinanceUiState(
    val totalSpentThisMonth: Money,
    val totalBudget: Money,
    val budgetRemaining: Money,
    val spendingByCategory: Map<ExpenseCategory, Money>,
    val recentExpenses: List<Expense>,
    val subscriptions: List<Subscription>,
    val isLoading: Boolean,
)
```

---

## Phase 2B — Workout System

### Domain Models

```kotlin
// model/workout/
WorkoutPlan(id, name, muscleGroups: List<MuscleGroup>, exercises: List<Exercise>, createdAt)
Exercise(id, name, muscleGroup: MuscleGroup, sets, reps, durationSeconds?, restSeconds)
WorkoutLog(id, planId, completedAt, durationMinutes, exercises: List<ExerciseLog>)
ExerciseLog(exerciseId, setsCompleted, repsCompleted, weightKg?)
WeightLog(id, weightKg, date)
MuscleGroup // enum: CHEST, BACK, SHOULDERS, BICEPS, TRICEPS, LEGS, CORE, CARDIO
```

### Repository interfaces

```kotlin
interface WorkoutRepository {
    fun getPlans(): Flow<List<WorkoutPlan>>
    suspend fun savePlan(plan: WorkoutPlan)
    fun getWorkoutHistory(limit: Int = 30): Flow<List<WorkoutLog>>
    suspend fun logWorkout(log: WorkoutLog)
}

interface WeightLogRepository {
    fun getWeightHistory(): Flow<List<WeightLog>>
    suspend fun logWeight(log: WeightLog)
}
```

### Use Cases

```kotlin
GetTodayWorkoutUseCase      // returns plan scheduled for today (or last)
GetWorkoutStreakUseCase      // consecutive days with logged workouts
GetProgressByMuscleUseCase  // volume per muscle group over N weeks
```

### Database entities (DB version 3)

```kotlin
@Entity WorkoutPlanEntity(id, name, muscleGroupsJson, createdAt)
@Entity ExerciseEntity(id, planId FK, name, muscleGroup, sets, reps, durationSec, restSec)
@Entity WorkoutLogEntity(id, planId FK, completedAt, durationMinutes)
@Entity ExerciseLogEntity(id, workoutLogId FK, exerciseId FK, setsCompleted, repsCompleted, weightKg)
@Entity WeightLogEntity(id, weightKg, dateEpoch)

// DAOs: WorkoutPlanDao, WorkoutLogDao, WeightLogDao
```

### UI in `:feature:workout`

```
WorkoutScreen
├── TodayCard          (today's plan name + muscle groups + Start button)
├── ActiveWorkoutSheet (launched on Start — set/rep tracker + timer)
├── PlansTab           (list of saved plans, tap to start or edit)
├── HistoryTab         (last 30 workouts, duration, muscle groups hit)
└── ProgressTab
    ├── WeightChart    (line chart — MPAndroidChart)
    └── VolumeByMuscle (bar chart, weekly)
```

---

## Phase 2C — Meal Planning (Nutrition)

### Domain Models

```kotlin
// model/nutrition/
Recipe(id, name, servings, ingredients: List<Ingredient>, macros: Macros, prepMinutes, imageUrl?)
Ingredient(id, name, quantity, unit: MeasurementUnit, estimatedCostMinorUnits)
MealLog(id, date, mealType: MealType, recipeId, calories, macros: Macros)
Macros(proteinG, carbsG, fatG, calories)
HydrationLog(id, date, amountMl)
MealType // enum: BREAKFAST, LUNCH, DINNER, SNACK
MeasurementUnit // enum: G, KG, ML, L, CUP, PIECE, TBSP, TSP
```

### Repository interfaces

```kotlin
interface RecipeRepository {
    fun getRecipes(): Flow<List<Recipe>>
    suspend fun saveRecipe(recipe: Recipe)
    suspend fun deleteRecipe(id: Long)
}

interface MealLogRepository {
    fun getDailyLog(date: LocalDate): Flow<List<MealLog>>
    fun getWeeklyCalories(week: LocalDate): Flow<Map<LocalDate, Int>>
    suspend fun logMeal(log: MealLog)
}

interface HydrationRepository {
    fun getTodayHydration(): Flow<Int>  // total ml
    suspend fun addHydration(log: HydrationLog)
}
```

### Use Cases

```kotlin
GetDailyMacroSummaryUseCase  // total protein/carbs/fat vs target
PlanWeeklyMealsUseCase       // distribute recipes across 7 days
GetCalorieSurplusUseCase     // actual vs target per day
```

### Database entities (DB version 4)

```kotlin
@Entity RecipeEntity(id, name, servings, prepMinutes, imageUrl)
@Entity IngredientEntity(id, recipeId FK, name, quantity, unit, estimatedCostMinorUnits)
@Entity MealLogEntity(id, dateEpoch, mealType, recipeId FK, calories, proteinG, carbsG, fatG)
@Entity HydrationLogEntity(id, dateEpoch, amountMl)

// DAOs: RecipeDao, MealLogDao, HydrationDao
```

### UI in `:feature:nutrition`

```
NutritionScreen
├── DailyTab
│   ├── MacroProgressCard  (protein / carbs / fat rings)
│   ├── HydrationCard      (water ml tracker + quick-add buttons)
│   └── MealSections       (BREAKFAST / LUNCH / DINNER / SNACK + log button)
├── RecipesTab
│   ├── RecipeList         (searchable, tap to view detail)
│   └── AddRecipeFAB       → AddRecipeSheet
└── WeeklyTab
    ├── CalorieBarChart    (7-day, actual vs target)
    └── MealPlanGrid       (7×4 grid — assign recipes to day/meal slots)
```

---

## Phase 2D — Grocery List

### Domain Models

```kotlin
// model/grocery/
GroceryList(id, name, weekStartDate, items: List<GroceryItem>, estimatedTotal: Money)
GroceryItem(id, listId, name, quantity, unit, estimatedCostMinorUnits, isChecked, category: GroceryCategory)
GroceryCategory // enum: PRODUCE, PROTEINS, DAIRY, GRAINS, FROZEN, OTHER
```

### Key Logic — Smart Merge Engine

When generating a grocery list from a weekly meal plan:
1. Fetch all recipes for the selected week
2. Collect all `Ingredient` lists
3. Group by ingredient name (normalised lowercase)
4. Sum quantities (same unit) or flag for manual merge (different units)
5. Attach estimated cost per item
6. Output a merged `GroceryList`

```kotlin
class GenerateGroceryListUseCase @Inject constructor(
    private val mealLogRepo: MealLogRepository,
    private val recipeRepo: RecipeRepository,
    private val groceryRepo: GroceryRepository,
) {
    suspend operator fun invoke(weekStart: LocalDate): GroceryList
}
```

### Repository interfaces

```kotlin
interface GroceryRepository {
    fun getLists(): Flow<List<GroceryList>>
    fun getActiveList(): Flow<GroceryList?>
    suspend fun saveList(list: GroceryList)
    suspend fun toggleItem(listId: Long, itemId: Long)
    suspend fun deleteList(id: Long)
}
```

### Database entities (DB version 5)

```kotlin
@Entity GroceryListEntity(id, name, weekStartEpoch, estimatedTotalMinorUnits)
@Entity GroceryItemEntity(id, listId FK, name, quantity, unit, estimatedCostMinorUnits, isChecked, category)

// DAOs: GroceryListDao, GroceryItemDao
```

### UI in `:feature:grocery`

```
GroceryScreen
├── ActiveListCard     (current week total cost estimate)
├── ItemsByCategory    (grouped, checkbox per item)
├── GenerateListFAB    → picks week → calls GenerateGroceryListUseCase
└── PastListsTab       (archived lists with cost history)
```

---

## Phase 2E — Progress Tracking

This feature **reads from all other modules** — no new DB entities needed.
It uses the existing `StabilityScore` domain model + data from repos above.

### Use Cases

```kotlin
// In :core:domain (cross-feature)
GetDailyStabilityScoreUseCase  // weights: workout 25%, nutrition 20%, hydration 15%, budget 20%, sleep 10%, mood 10%
GetProgressSummaryUseCase      // 30-day rolling stats per domain
```

### UI in `:feature:analytics`

```
AnalyticsScreen
├── StabilityScoreCard        (large number + contributor breakdown)
├── TrendChart                (30-day stability line, MPAndroidChart)
├── DomainCards (row)
│   ├── FinanceCard           (budget adherence %)
│   ├── WorkoutCard           (sessions this week / streak)
│   ├── NutritionCard         (avg daily calories hit %)
│   └── GroceryCard           (weekly spend vs estimate)
└── WeeklyReportSection       (text summary — today vs last week)
```

**AnalyticsUiState:**
```kotlin
data class AnalyticsUiState(
    val stabilityScore: StabilityScore,
    val trendData: List<Pair<LocalDate, Int>>,   // date → score
    val workoutStreak: Int,
    val budgetAdherence: Double,     // 0..1
    val avgCalorieHit: Double,       // 0..1
    val weeklyGrocerySpend: Money,
)
```

---

## Database Migration Path

| Version | Change |
|---|---|
| 1 | UserProfile (Phase 1) |
| 2 | + Expense, Income, Budget, Subscription |
| 3 | + WorkoutPlan, Exercise, WorkoutLog, ExerciseLog, WeightLog |
| 4 | + Recipe, Ingredient, MealLog, HydrationLog |
| 5 | + GroceryList, GroceryItem |

Each version bump = one `Migration(n, n+1)` class in `:core:database/migration/`.

---

## Shared Patterns for All Features

Every feature follows the same structure:

```
:feature:X
  └── src/main/java/com/luigi/lifeos/feature/X/
      ├── navigation/   XNavigation.kt
      └── ui/
          ├── XScreen.kt
          ├── XViewModel.kt
          └── components/   (private composables)

:core:domain
  └── model/X/   (models)
  └── repository/X/   (interfaces)
  └── usecase/X/   (use cases)

:core:database
  └── entity/X/   (Room entities)
  └── dao/X/      (Room DAOs)

:core:data
  └── repository/X/   (XRepositoryImpl)
  └── di/   (bind impl → interface via Hilt)
```

---

## Next Immediate Steps

1. **Add domain models** for Finance (`Expense`, `Budget`, `Income`, `Subscription`) in `:core:domain`
2. **Add DB entities + DAOs** for Finance in `:core:database`, bump to v2, write `Migration_1_2`
3. **Add `RepositoryImpl`** in `:core:data`, bind via Hilt
4. **Implement `FinanceScreen`** with real `StateFlow` data
5. Repeat for Workout, Nutrition, Grocery
6. Implement `AnalyticsScreen` last (reads from all repos)
