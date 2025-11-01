// ===== API Configuration =====
const API_BASE_URL = window.location.origin + '/api';

// ===== State Management =====
let authToken = localStorage.getItem('authToken');
let currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null');
let children = [];
let activities = [];
let selectedActivityForAssign = null;
let currentLanguage = localStorage.getItem('language') || 'en';
let selectedColor = '#FF6B6B';
let currentColoringActivity = null;
let canvas = null;
let ctx = null;
let isDrawing = false;

// ===== Translations =====
const translations = {
    en: {
        'nav.dashboard': 'Dashboard',
        'nav.children': 'Children',
        'nav.coloring': 'Coloring',
        'nav.activities': 'Activities',
        'nav.stats': 'Stats',
        'nav.profile': 'Profile',
        'nav.about': 'About',
        'nav.logout': 'Logout',
        'nav.admin': 'Admin'
    },
    az: {
        'nav.dashboard': 'İdarə Paneli',
        'nav.children': 'Uşaqlar',
        'nav.coloring': 'Rəngləmə',
        'nav.activities': 'Fəaliyyətlər',
        'nav.stats': 'Statistika',
        'nav.profile': 'Profil',
        'nav.about': 'Haqqında',
        'nav.logout': 'Çıxış',
        'nav.admin': 'Admin'
    },
    tr: {
        'nav.dashboard': 'Kontrol Paneli',
        'nav.children': 'Çocuklar',
        'nav.coloring': 'Boyama',
        'nav.activities': 'Aktiviteler',
        'nav.stats': 'İstatistikler',
        'nav.profile': 'Profil',
        'nav.about': 'Hakkında',
        'nav.logout': 'Çıkış',
        'nav.admin': 'Admin'
    }
};

function translate(key) {
    return translations[currentLanguage][key] || key;
}

function updateTranslations() {
    document.querySelectorAll('[data-key]').forEach(el => {
        const key = el.getAttribute('data-key');
        if (translations[currentLanguage][key]) {
            el.textContent = translate(key);
        }
    });
}

// ===== Initialize App =====
document.addEventListener('DOMContentLoaded', () => {
    initializeApp();
});

function initializeApp() {
    setupEventListeners();
    
    // Set language
    const langSelector = document.getElementById('language-selector');
    if (langSelector) {
        langSelector.value = currentLanguage;
        updateTranslations();
    }
    
    if (authToken && currentUser) {
        showDashboard();
    } else {
        showAuth();
    }
}

// ===== Event Listeners =====
function setupEventListeners() {
    // Auth forms
    document.getElementById('login-form').addEventListener('submit', handleLogin);
    document.getElementById('register-form').addEventListener('submit', handleRegister);
    document.getElementById('show-register').addEventListener('click', (e) => {
        e.preventDefault();
        switchAuthForm('register');
    });
    document.getElementById('show-login').addEventListener('click', (e) => {
        e.preventDefault();
        switchAuthForm('login');
    });

    // Navigation
    document.querySelectorAll('.nav-btn[data-page]').forEach(btn => {
        btn.addEventListener('click', () => {
            const page = btn.getAttribute('data-page');
            navigateToPage(page);
        });
    });

    document.getElementById('logout-btn').addEventListener('click', handleLogout);
    document.getElementById('mobile-menu-toggle').addEventListener('click', toggleMobileMenu);

    // Child management
    document.getElementById('add-child-btn').addEventListener('click', () => openChildModal());
    document.getElementById('child-form').addEventListener('submit', handleChildSubmit);
    document.getElementById('close-modal').addEventListener('click', closeChildModal);
    document.getElementById('cancel-modal').addEventListener('click', closeChildModal);

    // Assign activity
    document.getElementById('assign-form').addEventListener('submit', handleAssignActivity);
    document.getElementById('close-assign-modal').addEventListener('click', closeAssignModal);
    document.getElementById('cancel-assign-modal').addEventListener('click', closeAssignModal);

    // Activity filters
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            const category = btn.getAttribute('data-category');
            filterActivities(category);
        });
    });

    // Close modals on outside click
    document.getElementById('child-modal').addEventListener('click', (e) => {
        if (e.target.id === 'child-modal') closeChildModal();
    });
    document.getElementById('assign-modal').addEventListener('click', (e) => {
        if (e.target.id === 'assign-modal') closeAssignModal();
    });

    // Language selector
    const langSelector = document.getElementById('language-selector');
    if (langSelector) {
        langSelector.addEventListener('change', (e) => {
            currentLanguage = e.target.value;
            localStorage.setItem('language', currentLanguage);
            updateTranslations();
        });
    }

    // Profile page
    const changeAvatarBtn = document.getElementById('change-avatar-btn');
    if (changeAvatarBtn) {
        changeAvatarBtn.addEventListener('click', () => {
            const selector = document.getElementById('avatar-selector');
            if (selector) {
                selector.classList.remove('hidden');
                generateAvatarOptions();
            }
        });
    }
    
    const cancelAvatarBtn = document.getElementById('cancel-avatar');
    if (cancelAvatarBtn) {
        cancelAvatarBtn.addEventListener('click', () => {
            const selector = document.getElementById('avatar-selector');
            if (selector) {
                selector.classList.add('hidden');
            }
        });
    }

    // Coloring page
    const backToColoringBtn = document.getElementById('back-to-coloring');
    if (backToColoringBtn) {
        backToColoringBtn.addEventListener('click', () => {
            const categories = document.getElementById('coloring-categories');
            const canvasSection = document.getElementById('coloring-canvas-section');
            if (categories) categories.classList.remove('hidden');
            if (canvasSection) canvasSection.classList.add('hidden');
        });
    }
    
    const clearCanvasBtn = document.getElementById('clear-canvas');
    if (clearCanvasBtn) {
        clearCanvasBtn.addEventListener('click', clearCanvas);
    }
    
    const saveColoringBtn = document.getElementById('save-coloring');
    if (saveColoringBtn) {
        saveColoringBtn.addEventListener('click', saveColoring);
    }

    // Color palette - will be set up when coloring page loads
    setupColorPalette();

    // Admin: Add coloring activity
    const addColoringBtn = document.getElementById('add-coloring-activity-btn');
    if (addColoringBtn) {
        addColoringBtn.addEventListener('click', () => openColoringActivityModal());
    }
    const coloringForm = document.getElementById('coloring-activity-form');
    if (coloringForm) {
        coloringForm.addEventListener('submit', handleColoringActivitySubmit);
    }
    const closeColoringModalBtn = document.getElementById('close-coloring-modal');
    if (closeColoringModalBtn) {
        closeColoringModalBtn.addEventListener('click', closeColoringActivityModal);
    }
    const cancelColoringModalBtn = document.getElementById('cancel-coloring-modal');
    if (cancelColoringModalBtn) {
        cancelColoringModalBtn.addEventListener('click', closeColoringActivityModal);
    }
}

// ===== Auth Functions =====
function isAdmin() {
    return currentUser && currentUser.roles && currentUser.roles.includes('ROLE_ADMIN');
}

function switchAuthForm(formType) {
    const loginCard = document.getElementById('login-card');
    const registerCard = document.getElementById('register-card');
    
    if (formType === 'register') {
        loginCard.classList.add('hidden');
        registerCard.classList.remove('hidden');
    } else {
        registerCard.classList.add('hidden');
        loginCard.classList.remove('hidden');
    }
}

async function handleLogin(e) {
    e.preventDefault();
    const form = e.target;
    const btn = form.querySelector('.btn-primary');
    
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    setButtonLoading(btn, true);

    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            let errorMessage = 'Login failed';
            try {
                const errorData = await response.json();
                errorMessage = errorData.message || errorData.error || JSON.stringify(errorData);
            } catch {
                const errorText = await response.text();
                errorMessage = errorText || 'Login failed';
            }
            throw new Error(errorMessage);
        }

        const data = await response.json();
        
        authToken = data.token;
        currentUser = {
            username: data.username,
            id: data.id,
            email: data.email || 'N/A',
            roles: data.roles || []
        };

        localStorage.setItem('authToken', authToken);
        localStorage.setItem('currentUser', JSON.stringify(currentUser));

        showAlert('Login successful! Welcome back!', 'success');
        showDashboard();

    } catch (error) {
        showAlert(error.message || 'Login failed. Please check your credentials.', 'error');
    } finally {
        setButtonLoading(btn, false);
    }
}

async function handleRegister(e) {
    e.preventDefault();
    const form = e.target;
    const btn = form.querySelector('.btn-primary');
    
    const username = document.getElementById('register-username').value;
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;

    setButtonLoading(btn, true);

    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, email, password })
        });

        if (!response.ok) {
            let errorMessage = 'Registration failed';
            try {
                const errorData = await response.json();
                errorMessage = errorData.message || errorData.error || JSON.stringify(errorData);
            } catch {
                const errorText = await response.text();
                errorMessage = errorText || 'Registration failed';
            }
            throw new Error(errorMessage);
        }

        const result = await response.text();
        showAlert(result || 'Registration successful! Please login.', 'success');
        setTimeout(() => {
            switchAuthForm('login');
            form.reset();
            // Pre-fill username
            document.getElementById('login-username').value = username;
        }, 1500);

    } catch (error) {
        showAlert(error.message || 'Registration failed. Please try again.', 'error');
    } finally {
        setButtonLoading(btn, false);
    }
}

function handleLogout() {
    authToken = null;
    currentUser = null;
    children = [];
    activities = [];
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    
    const adminNav = document.getElementById('nav-admin-btn');
    if (adminNav) adminNav.classList.add('hidden');

    showAuth();
    showAlert('Logged out successfully', 'info');
}

// ===== Navigation =====
function showAuth() {
    document.getElementById('auth-container').classList.remove('hidden');
    document.getElementById('dashboard-container').classList.add('hidden');
}

function showDashboard() {
    document.getElementById('auth-container').classList.add('hidden');
    document.getElementById('dashboard-container').classList.remove('hidden');
    
    const adminNav = document.getElementById('nav-admin-btn');
    if (adminNav) {
        if (isAdmin()) {
            adminNav.classList.remove('hidden');
        } else {
            adminNav.classList.add('hidden');
        }
    }

    navigateToPage('dashboard');
    loadDashboardData();
}

function navigateToPage(pageName) {
    // Update nav buttons
    document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    const activeBtn = document.querySelector(`.nav-btn[data-page="${pageName}"]`);
    if (activeBtn) {
        activeBtn.classList.add('active');
    }

    // Show/hide pages
    document.querySelectorAll('.page').forEach(page => {
        page.classList.remove('active');
    });
    document.getElementById(`page-${pageName}`).classList.add('active');

    // Hide admin buttons by default
    const addColoringBtn = document.getElementById('add-coloring-activity-btn');
    if (addColoringBtn) addColoringBtn.classList.add('hidden');

    // Load page-specific data
    switch(pageName) {
        case 'dashboard':
            loadDashboardData();
            break;
        case 'children':
            loadChildren();
            break;
        case 'activities':
            loadActivities();
            break;
        case 'stats':
            loadStats();
            break;
        case 'coloring':
            loadColoringActivities();
            if (isAdmin() && addColoringBtn) {
                addColoringBtn.classList.remove('hidden');
            }
            break;
        case 'profile':
            loadProfile();
            break;
        case 'about':
            // About page is static, no data to load
            break;
        case 'admin':
            // Admin page is static for now
            break;
    }

    // Close mobile menu
    const mobileMenu = document.querySelector('.nav-menu');
    if (mobileMenu) mobileMenu.classList.remove('active');
}

function toggleMobileMenu() {
    document.querySelector('.nav-menu').classList.toggle('active');
}

// ===== Dashboard =====
async function loadDashboardData() {
    try {
        await Promise.all([
            loadChildren(),
            loadActivities('popular')
        ]);

        renderDashboardStats();
        renderDashboardChildren();
    } catch (error) {
        console.error('Error loading dashboard:', error);
        showAlert('Failed to load dashboard data', 'error');
    }
}

function renderDashboardStats() {
    const statsContainer = document.getElementById('dashboard-stats');
    const totalChildren = children.length;
    const totalPoints = children.reduce((sum, child) => sum + (child.totalPoints || 0), 0);
    const avgStreak = children.length > 0 
        ? Math.round(children.reduce((sum, child) => sum + (child.flameStreak || 0), 0) / children.length)
        : 0;

    statsContainer.innerHTML = `
        <div class="stat-card">
            <h3>Total Children</h3>
            <div class="stat-value">${totalChildren}</div>
        </div>
        <div class="stat-card">
            <h3>Total Points</h3>
            <div class="stat-value">${totalPoints}</div>
        </div>
        <div class="stat-card">
            <h3>Average Streak</h3>
            <div class="stat-value">${avgStreak} 🔥</div>
        </div>
        <div class="stat-card">
            <h3>Activities Available</h3>
            <div class="stat-value">${activities.length}</div>
        </div>
    `;
}

function renderDashboardChildren() {
    const container = document.getElementById('dashboard-children');
    
    if (children.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">👶</div>
                <h3>No children yet</h3>
                <p>Add your first child to get started!</p>
            </div>
        `;
        return;
    }

    container.innerHTML = children.slice(0, 3).map(child => createChildCard(child)).join('');
}

// ===== Children Management =====
async function loadChildren() {
    try {
        const response = await fetch(`${API_BASE_URL}/children`, {
            headers: getAuthHeaders()
        });

        if (!response.ok) throw new Error('Failed to load children');

        children = await response.json();
        renderChildren();
    } catch (error) {
        console.error('Error loading children:', error);
        showAlert('Failed to load children', 'error');
    }
}

function renderChildren() {
    const container = document.getElementById('children-grid') || document.getElementById('dashboard-children');
    if (!container) return;

    if (children.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">👶</div>
                <h3>No children yet</h3>
                <p>Add your first child to get started!</p>
            </div>
        `;
        return;
    }

    container.innerHTML = children.map(child => createChildCard(child)).join('');
    
    // Add event listeners for edit/delete
    container.querySelectorAll('.btn-edit').forEach((btn, index) => {
        btn.addEventListener('click', () => openChildModal(children[index]));
    });
    container.querySelectorAll('.btn-delete').forEach((btn, index) => {
        btn.addEventListener('click', () => deleteChild(children[index].id));
    });
}

function createChildCard(child) {
    const avatar = child.avatarUrl || `👶`;
    const initials = child.name.split(' ').map(n => n[0]).join('').toUpperCase();
    
    return `
        <div class="child-card">
            <div class="child-card-header">
                <div class="child-avatar" style="${child.avatarUrl ? `background-image: url('${child.avatarUrl}'); background-size: cover;` : ''}">
                    ${!child.avatarUrl ? initials : ''}
                </div>
                <div class="child-info">
                    <h3>${escapeHtml(child.name)}</h3>
                    <p>Age ${child.age}</p>
                </div>
            </div>
            <div class="child-stats">
                <div class="child-stat">
                    <div class="child-stat-value">${child.totalPoints || 0}</div>
                    <div class="child-stat-label">Points</div>
                </div>
                <div class="child-stat">
                    <div class="child-stat-value">${child.flameStreak || 0} 🔥</div>
                    <div class="child-stat-label">Streak</div>
                </div>
            </div>
            <div class="child-actions">
                <button class="btn-edit">Edit</button>
                <button class="btn-delete">Delete</button>
            </div>
        </div>
    `;
}

function openChildModal(child = null) {
    const modal = document.getElementById('child-modal');
    const form = document.getElementById('child-form');
    const title = document.getElementById('modal-title');
    
    if (child) {
        title.textContent = 'Edit Child';
        document.getElementById('child-name').value = child.name;
        document.getElementById('child-age').value = child.age;
        document.getElementById('child-avatar').value = child.avatarUrl || '';
        form.dataset.childId = child.id;
    } else {
        title.textContent = 'Add New Child';
        form.reset();
        delete form.dataset.childId;
    }
    
    modal.classList.add('active');
}

function closeChildModal() {
    document.getElementById('child-modal').classList.remove('active');
    document.getElementById('child-form').reset();
}

async function handleChildSubmit(e) {
    e.preventDefault();
    const form = e.target;
    const btn = form.querySelector('.btn-primary');
    
    const childData = {
        name: document.getElementById('child-name').value,
        age: parseInt(document.getElementById('child-age').value),
        avatarUrl: document.getElementById('child-avatar').value || null
    };

    setButtonLoading(btn, true);

    try {
        const childId = form.dataset.childId;
        const url = childId 
            ? `${API_BASE_URL}/children/${childId}`
            : `${API_BASE_URL}/children`;
        
        const method = childId ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method,
            headers: {
                ...getAuthHeaders(),
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(childData)
        });

        if (!response.ok) throw new Error('Failed to save child');

        showAlert(childId ? 'Child updated successfully!' : 'Child added successfully!', 'success');
        closeChildModal();
        await loadChildren();
        if (document.getElementById('page-dashboard').classList.contains('active')) {
            loadDashboardData();
        }

    } catch (error) {
        showAlert(error.message || 'Failed to save child', 'error');
    } finally {
        setButtonLoading(btn, false);
    }
}

async function deleteChild(childId) {
    if (!confirm('Are you sure you want to delete this child?')) return;

    try {
        const response = await fetch(`${API_BASE_URL}/children/${childId}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });

        if (!response.ok) throw new Error('Failed to delete child');

        showAlert('Child deleted successfully', 'success');
        await loadChildren();
        if (document.getElementById('page-dashboard').classList.contains('active')) {
            loadDashboardData();
        }

    } catch (error) {
        showAlert(error.message || 'Failed to delete child', 'error');
    }
}

// ===== Activities =====
async function loadActivities(category = null) {
    try {
        let url = `${API_BASE_URL}/activities`;
        if (category === 'popular') {
            url = `${API_BASE_URL}/activities/popular`;
        } else if (category) {
            url += `?category=${category}`;
        }

        const response = await fetch(url, {
            headers: getAuthHeaders()
        });

        if (!response.ok) throw new Error('Failed to load activities');

        activities = await response.json();
        renderActivities();
    } catch (error) {
        console.error('Error loading activities:', error);
        showAlert('Failed to load activities', 'error');
    }
}

function renderActivities() {
    const container = document.getElementById('activities-grid');
    if (!container) return;

    if (activities.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">🎯</div>
                <h3>No activities found</h3>
                <p>Try selecting a different category</p>
            </div>
        `;
        return;
    }

    container.innerHTML = activities.map(activity => createActivityCard(activity)).join('');
    
    // Add event listeners for assign buttons
    container.querySelectorAll('.btn-assign').forEach((btn, index) => {
        btn.addEventListener('click', () => openAssignModal(activities[index]));
    });
}

function createActivityCard(activity) {
    const categoryColors = {
        COLORING: '#ec4899',
        MATH: '#6366f1',
        ENGLISH: '#10b981',
        WORLD_VIEW: '#f59e0b'
    };

    const categoryLabels = {
        COLORING: '🎨 Coloring',
        MATH: '🔢 Math',
        ENGLISH: '📚 English',
        WORLD_VIEW: '🌍 World View'
    };

    return `
        <div class="activity-card">
            <div class="activity-card-header">
                <span class="activity-category" style="background: ${categoryColors[activity.category] || '#6366f1'}; color: white;">
                    ${categoryLabels[activity.category] || activity.category}
                </span>
                <h3>${escapeHtml(activity.title)}</h3>
                <p>${escapeHtml(activity.description || 'No description available')}</p>
            </div>
            <div class="activity-footer">
                <div class="activity-points">⭐ ${activity.pointsValue || 0} points</div>
                <button class="btn-assign" data-activity-id="${activity.id}">Assign</button>
            </div>
        </div>
    `;
}

function filterActivities(category) {
    if (category) {
        loadActivities(category);
    } else {
        loadActivities();
    }
}

// ===== Assign Activity =====
function openAssignModal(activity) {
    selectedActivityForAssign = activity;
    const modal = document.getElementById('assign-modal');
    const childSelect = document.getElementById('assign-child');
    const activityInput = document.getElementById('assign-activity');
    
    // Populate children dropdown
    childSelect.innerHTML = '<option value="">Choose a child...</option>' +
        children.map(child => 
            `<option value="${child.id}">${escapeHtml(child.name)} (Age ${child.age})</option>`
        ).join('');
    
    activityInput.value = activity.title;
    document.getElementById('assign-activity-id').value = activity.id;
    
    modal.classList.add('active');
}

function closeAssignModal() {
    document.getElementById('assign-modal').classList.remove('active');
    document.getElementById('assign-form').reset();
    selectedActivityForAssign = null;
}

async function handleAssignActivity(e) {
    e.preventDefault();
    const form = e.target;
    const btn = form.querySelector('.btn-primary');
    
    const childId = document.getElementById('assign-child').value;
    const activityId = document.getElementById('assign-activity-id').value;

    if (!childId || !activityId) {
        showAlert('Please select a child', 'error');
        return;
    }

    setButtonLoading(btn, true);

    try {
        const response = await fetch(`${API_BASE_URL}/activities/assign`, {
            method: 'POST',
            headers: {
                ...getAuthHeaders(),
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                childId: parseInt(childId),
                activityId: parseInt(activityId)
            })
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Failed to assign activity');
        }

        showAlert('Activity assigned successfully!', 'success');
        closeAssignModal();

    } catch (error) {
        showAlert(error.message || 'Failed to assign activity', 'error');
    } finally {
        setButtonLoading(btn, false);
    }
}

// ===== Stats =====
async function loadStats() {
    try {
        const response = await fetch(`${API_BASE_URL}/stats/daily-winners?limit=10`, {
            headers: getAuthHeaders()
        });

        if (!response.ok) throw new Error('Failed to load stats');

        const winners = await response.json();
        renderLeaderboard(winners);
    } catch (error) {
        console.error('Error loading stats:', error);
        showAlert('Failed to load leaderboard', 'error');
    }
}

function renderLeaderboard(winners) {
    const container = document.getElementById('leaderboard');
    
    if (!winners || winners.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">🏆</div>
                <h3>No winners yet</h3>
                <p>Start completing activities to see the leaderboard!</p>
            </div>
        `;
        return;
    }

    container.innerHTML = winners.map((winner, index) => {
        // Handle both nested child object and flat structure
        const childId = winner.child?.id || winner.childId;
        const childName = winner.child?.name || children.find(c => c.id === childId)?.name || 'Unknown';
        const points = winner.dailyPoints || winner.points || 0;
        
        return `
            <div class="leaderboard-item">
                <div class="leaderboard-rank">${index + 1}</div>
                <div class="leaderboard-info">
                    <h4>${escapeHtml(childName)}</h4>
                    <p>Daily points: ${points}</p>
                </div>
                <div class="leaderboard-points">${points} ⭐</div>
            </div>
        `;
    }).join('');
}

// ===== Coloring Functions =====
async function loadColoringActivities() {
    try {
        const response = await fetch(`${API_BASE_URL}/activities?category=COLORING`, {
            headers: getAuthHeaders()
        });

        if (!response.ok) throw new Error('Failed to load coloring activities');

        const coloringActivities = await response.json();
        renderColoringActivities(coloringActivities);
    } catch (error) {
        console.error('Error loading coloring activities:', error);
        showAlert('Failed to load coloring activities', 'error');
    }
}

function renderColoringActivities(activities) {
    const container = document.getElementById('coloring-categories');
    if (!container) return;

    if (activities.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">🎨</div>
                <h3>No coloring activities available</h3>
                <p>${isAdmin() ? 'Add a new activity to get started!' : 'Check back later for new activities!'}</p>
            </div>
        `;
        return;
    }

    container.innerHTML = activities.map(activity => `
        <div class="coloring-activity-card" data-activity-id="${activity.id}">
            <div style="width: 100%; height: 200px; background: var(--bg-primary); border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 64px; margin-bottom: 16px;">
                🎨
            </div>
            <h3>${escapeHtml(activity.title)}</h3>
            <p>${escapeHtml(activity.description || 'Fun coloring activity!')}</p>
            <p style="color: var(--accent); font-weight: 700; margin-top: 8px;">⭐ ${activity.pointsValue || 0} points</p>
            <button class="btn btn-primary" style="margin-top: 12px; width: 100%;">Start Coloring</button>
        </div>
    `).join('');

    // Add click handlers
    container.querySelectorAll('.coloring-activity-card').forEach(card => {
        const btn = card.querySelector('.btn');
        btn.addEventListener('click', () => {
            const activityId = card.dataset.activityId;
            const activity = activities.find(a => a.id == activityId);
            startColoring(activity);
        });
    });
}

function startColoring(activity) {
    if (!activity) return; 
    
    currentColoringActivity = activity;
    const titleEl = document.getElementById('coloring-title');
    const categoriesEl = document.getElementById('coloring-categories');
    const canvasSectionEl = document.getElementById('coloring-canvas-section');
    
    if (titleEl) titleEl.textContent = activity.title || 'Coloring Activity';
    if (categoriesEl) categoriesEl.classList.add('hidden');
    if (canvasSectionEl) canvasSectionEl.classList.remove('hidden');

    // Initialize canvas
    canvas = document.getElementById('coloring-canvas');
    if (!canvas) {
        showAlert('Canvas element not found', 'error');
        return;
    }
    
    ctx = canvas.getContext('2d');
    if (!ctx) {
        showAlert('Could not get canvas context', 'error');
        return;
    }
    
    // Set canvas size
    const maxWidth = Math.min(600, window.innerWidth - 100);
    canvas.width = maxWidth;
    canvas.height = maxWidth;
    canvas.style.width = maxWidth + 'px';
    canvas.style.height = maxWidth + 'px';
    
    // Draw a simple coloring template
    drawColoringTemplate();
    
    // Setup drawing
    setupCanvasDrawing();
}

function drawColoringTemplate() {
    // Clear canvas
    ctx.fillStyle = 'white';
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    
    // Draw some shapes to color
    ctx.strokeStyle = '#333';
    ctx.lineWidth = 3;
    
    // Draw a house
    ctx.beginPath();
    ctx.rect(150, 200, 300, 200);
    ctx.stroke();
    
    // Draw roof
    ctx.beginPath();
    ctx.moveTo(150, 200);
    ctx.lineTo(300, 100);
    ctx.lineTo(450, 200);
    ctx.closePath();
    ctx.stroke();
    
    // Draw door
    ctx.beginPath();
    ctx.rect(250, 300, 100, 100);
    ctx.stroke();
    
    // Draw sun
    ctx.beginPath();
    ctx.arc(500, 100, 40, 0, Math.PI * 2);
    ctx.stroke();
    
    // Draw some clouds
    for (let i = 0; i < 3; i++) {
        ctx.beginPath();
        ctx.arc(100 + i * 30, 80, 20, 0, Math.PI * 2);
        ctx.arc(115 + i * 30, 80, 25, 0, Math.PI * 2);
        ctx.arc(130 + i * 30, 80, 20, 0, Math.PI * 2);
        ctx.stroke();
    }
}

function setupCanvasDrawing() {
    if (!canvas) return; 
    
    // Remove old listeners to avoid duplicates
    const newCanvas = canvas.cloneNode(true);
    canvas.parentNode.replaceChild(newCanvas, canvas);
    canvas = newCanvas;
    
    canvas.addEventListener('mousedown', startDrawing);
    canvas.addEventListener('mousemove', draw);
    canvas.addEventListener('mouseup', stopDrawing);
    canvas.addEventListener('mouseout', stopDrawing);
    canvas.addEventListener('mouseleave', stopDrawing);
    
    // Touch events for mobile
    canvas.addEventListener('touchstart', handleTouch, { passive: false });
    canvas.addEventListener('touchmove', handleTouch, { passive: false });
    canvas.addEventListener('touchend', stopDrawing);
    
    setupColorPalette();
}

function startDrawing(e) {
    if (!canvas || !ctx) return;
    isDrawing = true;
    const rect = canvas.getBoundingClientRect();
    const touch = e.touches && e.touches[0] ? e.touches[0] : null;
    const x = touch ? (touch.clientX - rect.left) * (canvas.width / rect.width) : (e.clientX - rect.left) * (canvas.width / rect.width);
    const y = touch ? (touch.clientY - rect.top) * (canvas.height / rect.height) : (e.clientY - rect.top) * (canvas.height / rect.height);
    ctx.beginPath();
    ctx.moveTo(x, y);
}

function draw(e) {
    if (!isDrawing || !canvas || !ctx) return; 
    
    const rect = canvas.getBoundingClientRect();
    const touch = e.touches && e.touches[0] ? e.touches[0] : null;
    const x = touch ? (touch.clientX - rect.left) * (canvas.width / rect.width) : (e.clientX - rect.left) * (canvas.width / rect.width);
    const y = touch ? (touch.clientY - rect.top) * (canvas.height / rect.height) : (e.clientY - rect.top) * (canvas.height / rect.height);
    
    ctx.lineWidth = 15;
    ctx.lineCap = 'round';
    ctx.lineJoin = 'round';
    ctx.strokeStyle = selectedColor;
    ctx.lineTo(x, y);
    ctx.stroke();
    ctx.beginPath();
    ctx.moveTo(x, y);
    
    if (e.touches) e.preventDefault();
}

function handleTouch(e) {
    if (!canvas) return; 
    e.preventDefault();
    const touch = (e.touches && e.touches[0]) || (e.changedTouches && e.changedTouches[0]);
    if (!touch) return;
    
    const rect = canvas.getBoundingClientRect();
    const mouseEvent = new MouseEvent(
        e.type === 'touchstart' ? 'mousedown' : 
        e.type === 'touchmove' ? 'mousemove' : 'mouseup',
        {
            clientX: touch.clientX,
            clientY: touch.clientY,
            bubbles: true,
            cancelable: true
        }
    );
    canvas.dispatchEvent(mouseEvent);
}

function stopDrawing() {
    if (isDrawing) {
        ctx.beginPath();
        isDrawing = false;
    }
}

function clearCanvas() {
    if (!canvas || !ctx) return;
    if (confirm('Are you sure you want to clear the canvas?')) {
        drawColoringTemplate();
    }
}

function setupColorPalette() {
    // Setup color buttons
    document.querySelectorAll('.color-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.color-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            if (btn.dataset.color) {
                selectedColor = btn.dataset.color;
            }
        });
    });
    
    const customColorInput = document.getElementById('custom-color');
    if (customColorInput) {
        customColorInput.addEventListener('change', (e) => {
            selectedColor = e.target.value;
            document.querySelectorAll('.color-btn').forEach(b => b.classList.remove('active'));
            e.target.classList.add('active');
        });
    }
}

async function saveColoring() {
    if (!currentColoringActivity || !canvas) {
        showAlert('No coloring activity in progress', 'error');
        return;
    }
    
    try {
        // Convert canvas to image
        const imageData = canvas.toDataURL('image/png');
        
        // Save to localStorage
        localStorage.setItem(`coloring_${currentColoringActivity.id}`, imageData);
        
        showAlert('Coloring saved successfully! 🎨', 'success');
        
        // Optionally you can assign and complete this activity here
        // For now, just save locally
    } catch (error) {
        console.error('Error saving coloring:', error);
        showAlert('Failed to save coloring', 'error');
    }
}

// ===== Admin Functions for Coloring =====
function openColoringActivityModal(activity = null) {
    const modal = document.getElementById('coloring-activity-modal');
    const form = document.getElementById('coloring-activity-form');
    const title = document.getElementById('coloring-modal-title');
    
    if (activity) {
        title.textContent = 'Edit Coloring Activity';
        document.getElementById('coloring-activity-title').value = activity.title;
        document.getElementById('coloring-activity-description').value = activity.description || '';
        document.getElementById('coloring-activity-points').value = activity.pointsValue || 0;
        form.dataset.activityId = activity.id;
    } else {
        title.textContent = 'Add New Coloring Activity';
        form.reset();
        delete form.dataset.activityId;
    }
    
    modal.classList.add('active');
}

function closeColoringActivityModal() {
    const modal = document.getElementById('coloring-activity-modal');
    if (modal) {
        modal.classList.remove('active');
        document.getElementById('coloring-activity-form').reset();
    }
}

async function handleColoringActivitySubmit(e) {
    e.preventDefault();
    if (!isAdmin()) {
        showAlert('You are not authorized to perform this action.', 'error');
        return;
    }

    const form = e.target;
    const btn = form.querySelector('button[type="submit"]');
    
    const activityData = {
        title: document.getElementById('coloring-activity-title').value,
        description: document.getElementById('coloring-activity-description').value,
        pointsValue: parseInt(document.getElementById('coloring-activity-points').value),
        category: 'COLORING'
    };

    setButtonLoading(btn, true);

    try {
        const activityId = form.dataset.activityId;
        const url = activityId 
            ? `${API_BASE_URL}/activities/${activityId}`
            : `${API_BASE_URL}/activities`;
        
        const method = activityId ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method,
            headers: {
                ...getAuthHeaders(),
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(activityData)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'Failed to save activity');
        }

        showAlert(activityId ? 'Activity updated successfully!' : 'Activity added successfully!', 'success');
        closeColoringActivityModal();
        await loadColoringActivities();

    } catch (error) {
        showAlert(error.message || 'Failed to save activity', 'error');
    } finally {
        setButtonLoading(btn, false);
    }
}


// ===== Profile Functions =====
async function loadProfile() {
    if (!currentUser) {
        showAlert('Please login to view profile', 'error');
        return;
    }
    
    const usernameEl = document.getElementById('profile-username');
    const emailEl = document.getElementById('profile-email');
    const idEl = document.getElementById('profile-id');
    
    if (usernameEl) usernameEl.textContent = currentUser.username || 'N/A';
    if (emailEl) emailEl.textContent = currentUser.email || 'Email not available';
    if (idEl) idEl.textContent = currentUser.id || 'N/A';
    
    // Set avatar
    const avatarDisplay = document.getElementById('profile-avatar-display');
    const avatarInitials = document.getElementById('avatar-initials');
    
    if (!avatarDisplay || !avatarInitials) return;
    
    const avatarUrl = localStorage.getItem(`avatar_${currentUser.id}`);
    if (avatarUrl && avatarUrl.startsWith('http')) {
        avatarDisplay.innerHTML = `<img src="${avatarUrl}" alt="Avatar" onerror="this.parentElement.innerHTML='<span id=\'avatar-initials\'>${(currentUser.username || '').substring(0, 2).toUpperCase() || '👤'}</span>'">`;
    } else if (avatarUrl) {
        // It's an emoji
        avatarDisplay.innerHTML = '';
        avatarInitials.textContent = avatarUrl;
        avatarInitials.style.fontSize = '48px';
    } else {
        const initials = (currentUser.username || '').substring(0, 2).toUpperCase();
        avatarInitials.textContent = initials || '👤';
    }
}

function generateAvatarOptions() {
    const container = document.getElementById('avatar-grid');
    if (!container) return;
    
    const emojis = ['😀', '😃', '😄', '😁', '😆', '😊', '😎', '🤗', '🤩', '😋', '😍', '🤔', '😴', '🥳', '🤠', '🦄', '🐱', '🐶', '🐼', '🦁'];
    
    container.innerHTML = emojis.map((emoji, index) => `
        <div class="avatar-option" data-avatar="${emoji}" data-index="${index}">
            ${emoji}
        </div>
    `).join('');
    
    container.querySelectorAll('.avatar-option').forEach(option => {
        option.addEventListener('click', () => {
            if (!currentUser || !currentUser.id) {
                showAlert('Please login to change avatar', 'error');
                return;
            }
            
            container.querySelectorAll('.avatar-option').forEach(o => o.classList.remove('selected'));
            option.classList.add('selected');
            
            const avatar = option.dataset.avatar;
            const avatarDisplay = document.getElementById('profile-avatar-display');
            const avatarInitials = document.getElementById('avatar-initials');
            
            if (avatarDisplay && avatarInitials) {
                avatarDisplay.innerHTML = '';
                avatarInitials.textContent = avatar;
                avatarInitials.style.fontSize = '48px';
                
                // Save to localStorage
                localStorage.setItem(`avatar_${currentUser.id}`, avatar);
                
                setTimeout(() => {
                    const selector = document.getElementById('avatar-selector');
                    if (selector) {
                        selector.classList.add('hidden');
                    }
                    showAlert('Avatar updated successfully! ✨', 'success');
                }, 500);
            }
        });
    });
}

// ===== Utility Functions =====
function getAuthHeaders() {
    return {
        'Authorization': `Bearer ${authToken}`
    };
}

function setButtonLoading(btn, loading) {
    if (loading) {
        btn.classList.add('loading');
        btn.disabled = true;
    } else {
        btn.classList.remove('loading');
        btn.disabled = false;
    }
}

function showAlert(message, type = 'info') {
    const container = document.getElementById('alert-container');
    if (!container) return;
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.innerHTML = `
        <span>${type === 'success' ? '✓' : type === 'error' ? '✗' : 'ℹ'}</span>
        <span>${escapeHtml(message)}</span>
    `;
    
    container.appendChild(alert);
    
    setTimeout(() => {
        alert.style.animation = 'slideInRight 0.3s ease-out reverse';
        setTimeout(() => alert.remove(), 300);
    }, 3000);
}

function escapeHtml(text) {
    if (text === null || typeof text === 'undefined') {
        return '';
    }
    const div = document.createElement('div');
    div.textContent = text.toString();
    return div.innerHTML;
}