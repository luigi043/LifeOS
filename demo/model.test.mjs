import { test } from 'node:test';
import assert from 'node:assert/strict';
import { parseMinor, createExpense, monthlySpend, budgetError, sample, validStored } from './model.mjs';
test('exact cents, comma decimals and invalid input', () => {
  assert.equal(parseMinor('0.29'), 29); assert.equal(parseMinor('12,40'), 1240);
  for (const invalid of ['-1', 'NaN', '1e3', '1.001', '1,000.00', '999999999999999999999']) assert.equal(parseMinor(invalid), null);
});
test('expense validation and original currency', () => {
  assert.throws(() => createExpense({ amount: '0', currency: 'EUR', category: 'Food' }));
  assert.equal(createExpense({ amount: '0,29', currency: 'USD', category: 'Food' }).amountMinor, 29);
});
test('month totals never mix currencies or include previous months', () => {
  const now = new Date(2026, 10, 5);
  const items = [createExpense({ amount: '10', currency: 'EUR', category: 'Food' }, now),
    createExpense({ amount: '20', currency: 'USD', category: 'Food' }, now),
    createExpense({ amount: '30', currency: 'EUR', category: 'Food' }, new Date(2026, 9, 31))];
  assert.equal(monthlySpend(items, 'EUR', now), 1000);
});
test('budget relationships and corrupted storage', () => {
  assert.ok(budgetError('100', '101')); assert.equal(budgetError('100,29', '50'), null);
  assert.equal(validStored(sample()), true); assert.equal(validStored({ version: 1, currency: 'invalid', expenses: [] }), false);
});
