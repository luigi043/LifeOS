import { currencies, categories, parseMinor, money, createExpense, monthlySpend, budgetError, sample, validStored } from './model.mjs';
const key = 'lifeos-demo-v1';
let state;
let storageAvailable = true;
try { const saved = JSON.parse(localStorage.getItem(key)); state = validStored(saved) ? saved : sample(); }
catch { state = sample(); storageAvailable = false; }
const main = document.querySelector('#main');
const dialog = document.querySelector('#confirm');
const navigation = [['today', 'Today'], ['finance', 'Finance'], ['nutrition', 'Nutrition'], ['workout', 'Workout'], ['grocery', 'Grocery'], ['mentalhealth', 'Mental wellness'], ['habits', 'Habits'], ['analytics', 'Analytics'], ['profile', 'Profile'], ['settings', 'Settings']];
const escape = value => String(value ?? '').replace(/[&<>"']/g, char => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[char]);
let pendingDelete = null;
let step = 0;
let draft = {};
let noticeTimer;
function notice(message) { document.querySelector('#notice').textContent = message; clearTimeout(noticeTimer); noticeTimer = setTimeout(() => { document.querySelector('#notice').textContent = ''; }, 5000); }
function save() { try { localStorage.setItem(key, JSON.stringify(state)); } catch { storageAvailable = false; notice('Browser storage is unavailable. Changes will last for this session only.'); } }
function title(name, description) { return `<div class="eyebrow">LifeOS / Browser companion</div><h1>${name}</h1><p class="lead">${description}</p>`; }
function footer() { return '<footer>This is a separate interactive browser demo with synthetic data, not the native Android runtime. All entries stay in this browser. No bank, health, AI, cloud, or hardware integrations are connected. Use fictional values only.</footer>'; }
function go(route) { if (location.hash === `#${route}`) render(); else location.hash = route; }
function spend() { return monthlySpend(state.expenses, state.currency); }
function limit() { return state.profile?.currency === state.currency ? state.profile.budget : null; }
function budgetPanel() {
  const total = spend(), budget = limit(), remaining = budget === null ? null : budget - total;
  return `<section class="panel"><div class="topline"><span class="eyebrow">${new Intl.DateTimeFormat('en-GB', { month: 'long', year: 'numeric' }).format(new Date())}</span><span class="pill">${state.currency} only</span></div><div class="amount">${remaining === null ? money(total, state.currency) : money(Math.abs(remaining), state.currency)}</div><p>${remaining === null ? 'spent this month. No budget set for this currency.' : remaining < 0 ? 'over your monthly budget' : 'remaining from your monthly budget'}</p>${budget !== null && budget > 0 ? `<progress value="${Math.min(total, budget)}" max="${budget}" aria-label="Budget used"></progress><p class="muted">Spent ${money(total, state.currency)} of ${money(budget, state.currency)}</p>` : ''}<p class="muted">Currency conversion is not performed.</p></section>`;
}
function today() {
  return title(`Today, ${escape(state.profile?.name || 'your way')}`, 'A small, practical view of your money and local profile.') + `<div class="grid">${budgetPanel()}<section class="panel"><h2>Start with one entry</h2><p class="muted">Try comma decimals, change a category, or mark an expense as recurring.</p><button data-go="add">Add expense</button><a class="inline-link" href="#onboarding">Try the six-step setup</a></section></div><div class="banner"><strong>Lifestyle tracking is in development.</strong><br>Meals, workouts, habits, and mood are visible as module previews. No stability score or health assessment is calculated.</div><h2>Explore LifeOS</h2><div class="modules">${navigation.slice(1).map(([route, label]) => `<a href="#${route}">${label}</a>`).join('')}</div>`;
}
function finance() {
  const entries = state.expenses.filter(e => new Date(e.date).getMonth() === new Date().getMonth() && new Date(e.date).getFullYear() === new Date().getFullYear()).slice().reverse();
  return title('Finance', 'Track this month’s expenses. Every entry keeps its original currency.') + `<div class="topline"><span class="pill">Stored in this browser</span><button data-go="add">Add expense</button></div>${budgetPanel()}<h2>Expenses <span class="muted">(${entries.length})</span></h2>${entries.length ? entries.map(e => `<div class="row"><div class="grow"><strong>${escape(e.category)}</strong> ${e.recurring ? '<span class="tag">Recurring label</span>' : ''}<p>${escape(e.note || 'No note')}</p><p class="muted">${new Intl.DateTimeFormat('en-GB', { day: 'numeric', month: 'short' }).format(new Date(e.date))}</p></div><strong>${money(e.amountMinor, e.currency)}</strong><button class="delete" data-delete="${escape(e.id)}" aria-label="Delete ${escape(e.note || e.category)}">Delete</button></div>`).join('') : '<p class="empty">No expenses this month. Add your first expense to see it here.</p>'}<details><summary>Income</summary><p class="muted">Income entry is in development in the Android app and is not simulated here.</p></details>`;
}
function add() {
  return title('Add expense', 'Use a dot or comma for decimals. Amounts are saved as exact cents.') + `<form id="expense-form" novalidate><label for="amount">Amount (${state.currency})</label><input id="amount" name="amount" inputmode="decimal" placeholder="0.00" autocomplete="off" aria-describedby="expense-error" required><label for="category">Category</label><select id="category" name="category">${categories.map(c => `<option>${c}</option>`).join('')}</select><label for="note">Note (optional)</label><input id="note" name="note" maxlength="200" placeholder="What was this for?"><label class="check"><input type="checkbox" name="recurring">Recurring expense</label><p class="muted">This adds a label. It does not create future expenses automatically.</p><p id="expense-error" class="error" role="alert"></p><div class="actions"><button type="submit">Save expense</button><button type="button" data-go="finance" class="secondary">Cancel</button></div></form>`;
}
function profile() {
  const p = state.profile;
  return title('Profile', 'Your local demo preferences.') + (p ? `<dl class="details">${Object.entries({ Name: p.name, Age: p.age || 'Not set', Height: p.height ? `${p.height} cm` : 'Not set', Weight: p.weight ? `${p.weight} kg` : 'Not set', 'Fitness goal': p.goal, Diet: p.diet, Currency: p.currency, 'Monthly budget': p.budget === null ? 'Not set' : money(p.budget, p.currency) }).map(([label, value]) => `<div><dt>${label}</dt><dd>${escape(value)}</dd></div>`).join('')}</dl>` : '<p>No profile saved yet.</p>') + '<div class="actions"><button data-go="onboarding">Run profile setup</button></div>';
}
function settings() {
  return title('Settings', 'Changes affect this browser only.') + `<form id="settings-form"><label class="check"><input type="checkbox" name="dark" ${state.dark ? 'checked' : ''}>Dark mode</label><label for="currency">Currency for new expenses and totals</label><select id="currency" name="currency">${currencies.map(c => `<option ${c === state.currency ? 'selected' : ''}>${c}</option>`).join('')}</select><p class="muted">Existing expenses retain their original currency. Totals include the selected currency only.</p><button type="submit">Save preferences</button></form><div class="banner">No account, cloud sync, notifications, or data export is connected.</div><h2>Demo data</h2><p class="muted">Restore the fictional profile and sample expenses, or start with an empty browser workspace.</p><div class="actions"><button data-reset="sample" class="secondary">Restore sample data</button><button data-reset="empty" class="secondary">Start empty</button></div>`;
}
const moduleCopy = { nutrition: ['Nutrition', 'Recipe management, meal planning, calories, and hydration are in development. No nutrition data is tracked.'], workout: ['Workout', 'Workout planning, exercise logging, and timers are in development. No workouts are recorded.'], grocery: ['Grocery', 'Grocery editing and pantry management are in development. No shopping items are created.'], mentalhealth: ['Mental wellness', 'Mood logging and journaling are in development. No health assessments are provided.'], habits: ['Habits', 'Habit tracking is in development. No streaks are calculated.'], analytics: ['Analytics', 'Analytics are in development. No trends or AI insights are calculated.'] };
function modulePage(route) { const [name, copy] = moduleCopy[route]; return title(name, copy) + '<div class="banner"><strong>Module preview</strong><br>This mirrors the implementation status in the Android app. No simulated tracking results are shown.</div><button data-go="finance">Open expense tracking</button>'; }
const steps = ['Welcome', 'About you', 'Your body', 'Your goals', 'Your budget', 'Review'];
function field(label, name, inputmode = 'text') { return `<label for="${name}">${label}</label><input id="${name}" name="${name}" inputmode="${inputmode}" maxlength="80" value="${escape(draft[name] || '')}">`; }
function select(label, name, options) { return `<label for="${name}">${label}</label><select id="${name}" name="${name}">${options.map(o => `<option ${o === draft[name] ? 'selected' : ''}>${o}</option>`).join('')}</select>`; }
function onboarding() {
  let content = '';
  if (step === 0) content = '<p>Set up a fictional profile, goals, and budget. Optional values can be left blank. This replaces your demo profile without removing expenses.</p>';
  if (step === 1) content = field('Your name', 'name') + field('Age (optional)', 'age', 'numeric');
  if (step === 2) content = field('Height in cm (optional)', 'height', 'decimal') + field('Weight in kg (optional)', 'weight', 'decimal') + '<p class="muted">Profile details only. No targets are calculated.</p>';
  if (step === 3) content = select('Fitness goal', 'goal', ['General health', 'Lose weight', 'Gain muscle', 'Maintain']) + select('Diet', 'diet', ['Omnivore', 'Pescatarian', 'Vegetarian', 'Vegan', 'Keto', 'Mediterranean']);
  if (step === 4) content = field('Monthly budget (optional)', 'budget', 'decimal') + field('Food budget (optional)', 'foodBudget', 'decimal') + select('Currency', 'currency', currencies) + '<p class="muted">Food budget must not exceed the monthly budget.</p>';
  if (step === 5) content = `<dl class="details">${Object.entries(draft).map(([label, value]) => `<div><dt>${escape(label)}</dt><dd>${escape(value || 'Not set')}</dd></div>`).join('')}</dl><p>Save locally in this browser. Use fictional values only.</p>`;
  return title(steps[step], 'Profile setup') + `<p class="steps">Step ${step + 1} of 6</p><progress max="6" value="${step + 1}" aria-label="Setup progress"></progress><form id="setup-form" novalidate>${content}<p class="error" id="setup-error" role="alert"></p><div class="actions">${step ? '<button type="button" class="secondary" id="setup-back">Back</button>' : ''}<button type="submit">${step === 5 ? 'Finish setup' : 'Continue'}</button><button type="button" data-go="today" class="secondary">Cancel</button></div></form>`;
}
function render() {
  let route = location.hash.slice(1) || 'today';
  if (!navigation.some(([r]) => r === route) && !['add', 'onboarding'].includes(route)) route = 'today';
  document.body.classList.toggle('dark', state.dark);
  document.querySelector('#navigation').innerHTML = navigation.map(([r, label]) => `<a href="#${r}" ${route === r || (r === 'finance' && route === 'add') ? 'aria-current="page"' : ''}>${label}</a>`).join('');
  const tabs = [['today','Today','M3 4h18v17H3z M7 2v4 M17 2v4 M3 9h18'],['nutrition','Plan','M3 4h18v17H3z M7 2v4 M17 2v4 M3 9h18 M7 13h3 M14 13h3'],['workout','Progress','M3 18l6-6 4 3 8-10 M16 5h5v5'],['analytics','Insights','M4 20V10 M10 20V4 M16 20v-7 M22 20H2'],['profile','Profile','M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8 M4 22v-2a8 8 0 0 1 16 0v2']];
  document.querySelector('#phone-navigation').innerHTML = tabs.map(([r,label,path]) => `<a href="#${r}" ${route === r ? 'aria-current="page"' : ''}><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" aria-hidden="true"><path d="${path}"/></svg><span>${label}</span></a>`).join('');
  document.querySelector('#phone-navigation').hidden = route === 'onboarding';
  main.innerHTML = ({ today, finance, add, profile, settings, onboarding }[route]?.() || modulePage(route)) + (!storageAvailable ? '<p class="error">Storage is unavailable. This session will not survive a reload.</p>' : '') + footer();
  document.title = `LifeOS | ${route === 'add' ? 'Add expense' : navigation.find(([r]) => r === route)?.[1] || 'Profile setup'}`;
  window.scrollTo(0, 0);
}
main.addEventListener('click', event => {
  const button = event.target.closest('button'); if (!button) return;
  if (button.dataset.go) { if (button.dataset.go === 'onboarding') { step = 0; draft = {}; } go(button.dataset.go); }
  if (button.dataset.delete) { pendingDelete = button.dataset.delete; dialog.showModal(); }
  if (button.id === 'setup-back') { for (const [k, v] of new FormData(document.querySelector('#setup-form'))) draft[k] = v; step--; render(); }
  if (button.dataset.reset) {
    dialog.querySelector('h2').textContent = 'Replace demo data?'; dialog.querySelector('p').textContent = 'This replaces only the LifeOS data saved in this browser.';
    pendingDelete = `reset:${button.dataset.reset}`; dialog.showModal();
  }
});
dialog.addEventListener('close', () => {
  if (dialog.returnValue === 'delete' && pendingDelete) {
    if (pendingDelete.startsWith('reset:')) state = pendingDelete === 'reset:sample' ? sample() : { version: 1, profile: null, currency: 'EUR', dark: false, expenses: [] };
    else state.expenses = state.expenses.filter(e => e.id !== pendingDelete);
    save(); render(); notice('Demo data updated.');
  }
  pendingDelete = null; dialog.querySelector('h2').textContent = 'Delete expense?'; dialog.querySelector('p').textContent = 'This removes the entry from this browser.';
});
main.addEventListener('submit', event => {
  event.preventDefault();
  const form = event.target, data = Object.fromEntries(new FormData(form));
  if (form.id === 'expense-form') {
    try { state.expenses.push(createExpense({ ...data, currency: state.currency })); save(); go('finance'); notice('Expense saved.'); }
    catch (error) { document.querySelector('#expense-error').textContent = error.message; document.querySelector('#amount').focus(); }
  }
  if (form.id === 'settings-form') { state.dark = Boolean(data.dark); state.currency = data.currency; save(); render(); notice('Preferences saved.'); }
  if (form.id === 'setup-form') {
    Object.assign(draft, data);
    let error = null;
    if (step === 1 && (!draft.name?.trim() || (draft.age && (!/^\d+$/.test(draft.age) || Number(draft.age) < 1 || Number(draft.age) > 120)))) error = 'Enter a name and an age between 1 and 120, or leave age blank.';
    if (step === 2 && ['height', 'weight'].some(k => draft[k] && (!/^\d+(?:[.,]\d+)?$/.test(draft[k]) || Number(draft[k].replace(',', '.')) <= 0 || Number(draft[k].replace(',', '.')) > 1000))) error = 'Enter positive measurements up to 1000, or leave blank.';
    if (step === 4) error = budgetError(draft.budget || '', draft.foodBudget || '');
    if (error) { document.querySelector('#setup-error').textContent = error; return; }
    if (step < 5) { step++; render(); }
    else { state.profile = { ...draft, name: draft.name.trim(), currency: draft.currency || 'EUR', budget: draft.budget ? parseMinor(draft.budget) : null, foodBudget: draft.foodBudget ? parseMinor(draft.foodBudget) : null }; state.currency = state.profile.currency; save(); go('today'); notice('Profile saved.'); step = 0; }
  }
});
window.addEventListener('hashchange', render);
save(); render();

document.querySelector('#phone-view').addEventListener('click', () => setView(true));
document.querySelector('#wide-view').addEventListener('click', () => setView(false));
function setView(phone) {
  document.body.classList.toggle('phone-view', phone);
  document.querySelector('#phone-view').setAttribute('aria-pressed', String(phone));
  document.querySelector('#wide-view').setAttribute('aria-pressed', String(!phone));
}
