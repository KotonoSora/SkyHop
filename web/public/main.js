const root = document.documentElement;
const toggle = document.getElementById('themeToggle');
const storedTheme = localStorage.getItem('skyhop-theme');

if (storedTheme === 'night') {
  root.dataset.theme = 'night';
}

if (toggle) {
  toggle.addEventListener('click', () => {
    const nextTheme = root.dataset.theme === 'night' ? 'day' : 'night';

    if (nextTheme === 'day') {
      delete root.dataset.theme;
      localStorage.setItem('skyhop-theme', 'day');
      return;
    }

    root.dataset.theme = 'night';
    localStorage.setItem('skyhop-theme', 'night');
  });
}
