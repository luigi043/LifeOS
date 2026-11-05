export const currencies = ['EUR', 'USD', 'GBP', 'BRL'];
export const categories = ['Food', 'Transport', 'Housing', 'Health', 'Shopping', 'Other'];
export function parseMinor(text) {
  const normalized = String(text).trim().replace(',', '.');
  if (!/^\d+(?:\.\d{1,2})?$/.test(normalized)) return null;
  const [whole, fraction = ''] = normalized.split('.');
  const value = BigInt(whole) * 100n + BigInt(fraction.padEnd(2, '0'));
  return value <= BigInt(Number.MAX_SAFE_INTEGER) ? Number(value) : null;
}
export function money(minor, currency = 'EUR') {
  return new Intl.NumberFormat('en-GB', { style: 'currency', currency }).format(minor / 100);
}
export function createExpense(input, now = new Date()) {
  const amountMinor = parseMinor(input.amount);
  if (amountMinor === null || amountMinor <= 0) throw new Error('Enter a positive amount with up to two decimal places.');
  if (!currencies.includes(input.currency)) throw new Error('Choose a supported currency.');
  if (!categories.includes(input.category)) throw new Error('Choose a category.');
  return { id: crypto.randomUUID(), amountMinor, currency: input.currency, category: input.category,
    note: String(input.note || '').trim().slice(0, 200), recurring: Boolean(input.recurring), date: now.toISOString() };
}
export function monthlySpend(expenses, currency, now = new Date()) {
  return expenses.filter(e => e.currency === currency && new Date(e.date).getFullYear() === now.getFullYear()
    && new Date(e.date).getMonth() === now.getMonth()).reduce((total, e) => total + e.amountMinor, 0);
}
export function budgetError(monthly, food) {
  const total = monthly.trim() ? parseMinor(monthly) : null;
  const foodTotal = food.trim() ? parseMinor(food) : null;
  if ((monthly.trim() && total === null) || (food.trim() && foodTotal === null)) return 'Use positive numbers with up to two decimal places, or leave blank.';
  if (total !== null && foodTotal !== null && foodTotal > total) return 'Food budget must not exceed the monthly budget.';
  return null;
}
export function sample() {
  return { version: 1, profile: { name: 'Alex Demo', goal: 'General health', diet: 'Omnivore', currency: 'EUR', budget: 10000, foodBudget: 5000 },
    currency: 'EUR', dark: false, expenses: [createExpense({ amount: '12.40', currency: 'EUR', category: 'Food', note: 'Sample groceries' }),
      createExpense({ amount: '3.20', currency: 'EUR', category: 'Transport', note: 'Sample bus ticket' })] };
}
export function validStored(value) {
  return value?.version === 1 && currencies.includes(value.currency) && Array.isArray(value.expenses)
    && value.expenses.every(e => Number.isSafeInteger(e.amountMinor) && e.amountMinor > 0 && currencies.includes(e.currency)
      && categories.includes(e.category) && typeof e.id === 'string' && !Number.isNaN(Date.parse(e.date)))
    && (value.profile === null || (typeof value.profile?.name === 'string' && currencies.includes(value.profile.currency)
      && (value.profile.budget === null || Number.isSafeInteger(value.profile.budget))));
}
