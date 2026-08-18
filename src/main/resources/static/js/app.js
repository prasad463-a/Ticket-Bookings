/**
 * RailVoyage - Railway Ticket Booking System Engine
 * Single Page Application JavaScript Controller
 */

// Configuration & API Helper
const CONFIG = {
  // Dynamically use current origin (works seamlessly on localhost:8080 and online host like Render)
  API_BASE_URL: window.location.origin,
  STORAGE_TOKEN: 'railvoyage_token',
  STORAGE_USER: 'railvoyage_user'
};

// Global App State
const state = {
  user: JSON.parse(localStorage.getItem(CONFIG.STORAGE_USER)) || null,
  token: localStorage.getItem(CONFIG.STORAGE_TOKEN) || null,
  trains: [],
  myBookings: [],
  selectedTrainForBooking: null,
  selectedSeatConfig: null,
  activeBooking: null
};

// Helper: HTTP Request Wrapper
async function apiRequest(endpoint, method = 'GET', data = null, requiresAuth = false) {
  const headers = {
    'Content-Type': 'application/json'
  };

  if (requiresAuth || state.token) {
    if (state.token) {
      headers['Authorization'] = `Bearer ${state.token}`;
    }
  }

  const options = {
    method,
    headers
  };

  if (data && (method === 'POST' || method === 'PUT')) {
    options.body = JSON.stringify(data);
  }

  try {
    const response = await fetch(`${CONFIG.API_BASE_URL}${endpoint}`, options);
    
    // Parse response
    const contentType = response.headers.get('content-type');
    let responseData;
    if (contentType && contentType.includes('application/json')) {
      responseData = await response.json();
    } else {
      responseData = await response.text();
    }

    if (!response.ok) {
      let errorMsg = `HTTP Error ${response.status}`;
      if (typeof responseData === 'object' && responseData !== null) {
        if (responseData.message) {
          errorMsg = responseData.message;
        } else if (responseData.errors && Array.isArray(responseData.errors)) {
          errorMsg = responseData.errors.map(e => e.defaultMessage || e).join(', ');
        } else if (responseData.error) {
          errorMsg = responseData.error;
        }
      } else if (typeof responseData === 'string' && responseData.length > 0) {
        errorMsg = responseData;
      }
      throw new Error(errorMsg);
    }

    return responseData;
  } catch (err) {
    console.error(`API Error [${method} ${endpoint}]:`, err);
    throw err;
  }
}

// Toast Notifications
function showToast(message, type = 'info') {
  const container = document.getElementById('toast-container');
  if (!container) return;

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  
  const icon = type === 'success' ? '✓' : (type === 'error' ? '✕' : 'ℹ');
  toast.innerHTML = `<strong>${icon}</strong> <span>${message}</span>`;
  
  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

// Initialization & Event Binding
document.addEventListener('DOMContentLoaded', () => {
  initUI();
  checkBackendHealth();
  loadInitialData();
  
  // Set default date picker to today
  const dateInput = document.getElementById('search-date');
  if (dateInput) {
    const today = new Date().toISOString().split('T')[0];
    dateInput.min = today;
    dateInput.value = today;
  }
});

function initUI() {
  updateUserNav();

  // Route Chips Event
  document.querySelectorAll('.route-chip').forEach(chip => {
    chip.addEventListener('click', () => {
      const src = chip.getAttribute('data-src');
      const dest = chip.getAttribute('data-dest');
      document.getElementById('search-source').value = src;
      document.getElementById('search-destination').value = dest;
      handleSearchTrains();
    });
  });

  // Search Form
  const searchForm = document.getElementById('train-search-form');
  if (searchForm) {
    searchForm.addEventListener('submit', (e) => {
      e.preventDefault();
      handleSearchTrains();
    });
  }

  // Auth Form
  const authForm = document.getElementById('auth-form');
  if (authForm) {
    authForm.addEventListener('submit', handleAuthSubmit);
  }

  // Booking Form
  const bookingForm = document.getElementById('booking-form');
  if (bookingForm) {
    bookingForm.addEventListener('submit', handleCreateBooking);
  }

  // Payment Form
  const paymentForm = document.getElementById('payment-form');
  if (paymentForm) {
    paymentForm.addEventListener('submit', handleProcessPayment);
  }

  // Add Train Form (Admin)
  const addTrainForm = document.getElementById('admin-train-form');
  if (addTrainForm) {
    addTrainForm.addEventListener('submit', handleAdminSaveTrain);
  }

  // User Profile Form
  const profileForm = document.getElementById('user-profile-form');
  if (profileForm) {
    profileForm.addEventListener('submit', handleUpdateProfile);
  }
}

// Health check to verify Spring Boot connection
async function checkBackendHealth() {
  const badge = document.getElementById('backend-status-badge');
  if (!badge) return;
  try {
    await fetch(`${CONFIG.API_BASE_URL}/train/getall`, { method: 'GET' });
    badge.style.background = 'rgba(16, 185, 129, 0.2)';
    badge.style.color = '#10b981';
    badge.style.borderColor = 'rgba(16, 185, 129, 0.4)';
    badge.innerHTML = '⚡ Backend Online';
  } catch (e) {
    badge.style.background = 'rgba(244, 63, 94, 0.2)';
    badge.style.color = '#fb7185';
    badge.style.borderColor = 'rgba(244, 63, 94, 0.4)';
    badge.innerHTML = '⚠️ Backend Offline';
  }
}

// Auth State Management
function updateUserNav() {
  const loggedOutNav = document.getElementById('nav-logged-out');
  const loggedInNav = document.getElementById('nav-logged-in');
  const adminBtn = document.getElementById('nav-admin-btn');
  const adminUsersBtn = document.getElementById('nav-admin-users-btn');
  const userNameEl = document.getElementById('nav-user-name');
  const userRoleEl = document.getElementById('nav-user-role');
  const avatarEl = document.getElementById('nav-user-avatar');

  if (state.user && state.token) {
    if (loggedOutNav) loggedOutNav.style.display = 'none';
    if (loggedInNav) loggedInNav.style.display = 'flex';
    
    if (userNameEl) userNameEl.textContent = state.user.name || state.user.email;
    if (avatarEl) avatarEl.textContent = (state.user.name || 'U').charAt(0).toUpperCase();

    const isAdmin = state.user.role === 'ADMIN' || state.user.role === 'ROLE_ADMIN';
    if (userRoleEl) {
      userRoleEl.textContent = isAdmin ? 'ADMIN' : 'PASSENGER';
      userRoleEl.className = `role-tag ${isAdmin ? 'role-admin' : 'role-user'}`;
    }

    if (adminBtn) adminBtn.style.display = isAdmin ? 'inline-flex' : 'none';
    if (adminUsersBtn) adminUsersBtn.style.display = isAdmin ? 'inline-flex' : 'none';
  } else {
    if (loggedOutNav) loggedOutNav.style.display = 'flex';
    if (loggedInNav) loggedInNav.style.display = 'none';
    if (adminBtn) adminBtn.style.display = 'none';
    if (adminUsersBtn) adminUsersBtn.style.display = 'none';
  }
}

function switchAuthTab(mode) {
  const loginTab = document.getElementById('tab-login');
  const registerTab = document.getElementById('tab-register');
  const registerFields = document.querySelectorAll('.register-field');
  const authSubmitBtn = document.getElementById('auth-submit-btn');
  const authForm = document.getElementById('auth-form');

  if (!authForm) return;

  if (mode === 'login') {
    if (loginTab) loginTab.classList.add('active');
    if (registerTab) registerTab.classList.remove('active');
    registerFields.forEach(el => el.style.display = 'none');
    if (authSubmitBtn) authSubmitBtn.textContent = 'Sign In';
    authForm.dataset.mode = 'login';
    
    document.getElementById('auth-name').removeAttribute('required');
    document.getElementById('auth-phone').removeAttribute('required');
  } else {
    if (registerTab) registerTab.classList.add('active');
    if (loginTab) loginTab.classList.remove('active');
    registerFields.forEach(el => el.style.display = 'flex');
    if (authSubmitBtn) authSubmitBtn.textContent = 'Create Account';
    authForm.dataset.mode = 'register';

    document.getElementById('auth-name').setAttribute('required', 'true');
    document.getElementById('auth-phone').setAttribute('required', 'true');
  }
}

async function handleAuthSubmit(e) {
  e.preventDefault();
  const authForm = document.getElementById('auth-form');
  const mode = authForm.dataset.mode || 'login';
  const email = document.getElementById('auth-email').value.trim();
  const password = document.getElementById('auth-password').value;

  try {
    if (mode === 'login') {
      const response = await apiRequest('/user/login', 'POST', { email, password });
      state.token = response.token;
      state.user = {
        id: response.id,
        name: response.name,
        email: response.email,
        role: response.role
      };

      localStorage.setItem(CONFIG.STORAGE_TOKEN, state.token);
      localStorage.setItem(CONFIG.STORAGE_USER, JSON.stringify(state.user));

      showToast(`Welcome back, ${state.user.name || 'User'}!`, 'success');
      closeModal('auth-modal');
      updateUserNav();
    } else {
      const name = document.getElementById('auth-name').value.trim();
      const phone = document.getElementById('auth-phone').value.trim();

      if (!name) {
        showToast('Please enter your full name', 'error');
        return;
      }

      if (!/^[0-9]{10}$/.test(phone)) {
        showToast('Phone number must contain exactly 10 digits', 'error');
        return;
      }
      if (password.length < 6) {
        showToast('Password must contain at least 6 characters', 'error');
        return;
      }

      // Register new user
      await apiRequest('/user/register', 'POST', { name, email, password, phone });
      showToast('Registration successful! Logging you in...', 'success');

      // Auto login after registration
      const loginResponse = await apiRequest('/user/login', 'POST', { email, password });
      state.token = loginResponse.token;
      state.user = {
        id: loginResponse.id,
        name: loginResponse.name,
        email: loginResponse.email,
        role: loginResponse.role
      };

      localStorage.setItem(CONFIG.STORAGE_TOKEN, state.token);
      localStorage.setItem(CONFIG.STORAGE_USER, JSON.stringify(state.user));

      closeModal('auth-modal');
      updateUserNav();
    }
  } catch (err) {
    showToast(err.message || 'Authentication failed', 'error');
  }
}

function handleLogout() {
  state.user = null;
  state.token = null;
  localStorage.removeItem(CONFIG.STORAGE_TOKEN);
  localStorage.removeItem(CONFIG.STORAGE_USER);
  updateUserNav();
  showToast('Logged out successfully', 'info');
}

// User Profile & Settings Management
async function openUserProfileModal() {
  if (!state.user || !state.token) {
    showToast('Please login to view profile', 'info');
    openModal('auth-modal');
    return;
  }

  try {
    // Fetch fresh user profile from backend GET /user/get/{id}
    const freshUser = await apiRequest(`/user/get/${state.user.id}`, 'GET', null, true);
    
    document.getElementById('profile-display-name').textContent = freshUser.name || freshUser.email;
    document.getElementById('profile-display-role').textContent = freshUser.role;
    document.getElementById('profile-display-id').textContent = `User ID: #${freshUser.id}`;
    document.getElementById('profile-avatar').textContent = (freshUser.name || 'U').charAt(0).toUpperCase();

    document.getElementById('profile-name').value = freshUser.name || '';
    document.getElementById('profile-email').value = freshUser.email || '';
    document.getElementById('profile-phone').value = freshUser.phone || '';
    document.getElementById('profile-password').value = '';

    openModal('user-profile-modal');
  } catch (err) {
    showToast(err.message || 'Failed to load user profile', 'error');
  }
}

async function handleUpdateProfile(e) {
  e.preventDefault();
  if (!state.user || !state.token) return;

  const name = document.getElementById('profile-name').value.trim();
  const email = document.getElementById('profile-email').value.trim();
  const phone = document.getElementById('profile-phone').value.trim();
  const password = document.getElementById('profile-password').value;

  if (!/^[0-9]{10}$/.test(phone)) {
    showToast('Phone number must contain exactly 10 digits', 'error');
    return;
  }

  const payload = {
    name,
    email,
    phone
  };

  if (password && password.length >= 6) {
    payload.password = password;
  } else if (password && password.length < 6) {
    showToast('New password must contain at least 6 characters', 'error');
    return;
  }

  try {
    const updatedUser = await apiRequest(`/user/update/${state.user.id}`, 'PUT', payload, true);
    
    state.user.name = updatedUser.name;
    state.user.email = updatedUser.email;
    state.user.phone = updatedUser.phone;
    state.user.role = updatedUser.role;

    localStorage.setItem(CONFIG.STORAGE_USER, JSON.stringify(state.user));

    updateUserNav();
    closeModal('user-profile-modal');
    showToast('Profile updated successfully!', 'success');
  } catch (err) {
    showToast(err.message || 'Failed to update profile', 'error');
  }
}

// Train Search & Listing
async function loadInitialData() {
  await handleSearchTrains();
}

async function handleSearchTrains() {
  const source = document.getElementById('search-source').value.trim();
  const destination = document.getElementById('search-destination').value.trim();
  const container = document.getElementById('train-cards-container');

  if (!container) return;
  container.innerHTML = '<div class="empty-state">⚡ Loading trains...</div>';

  try {
    let endpoint = '/train/getall';
    if (source && destination) {
      endpoint = `/train/search?source=${encodeURIComponent(source)}&destination=${encodeURIComponent(destination)}`;
    }

    const trains = await apiRequest(endpoint, 'GET');
    state.trains = trains;
    renderTrainCards(trains);
  } catch (err) {
    container.innerHTML = `<div class="empty-state"><div class="empty-icon">⚠️</div><p>${err.message || 'Failed to fetch trains'}</p></div>`;
  }
}

function renderTrainCards(trains) {
  const container = document.getElementById('train-cards-container');
  if (!container) return;

  if (!trains || trains.length === 0) {
    container.innerHTML = `
      <div class="empty-state">
        <div class="empty-icon">🚆</div>
        <h3>No Trains Found</h3>
        <p style="color: var(--text-muted);">Try searching for a different route or clear filters.</p>
      </div>`;
    return;
  }

  container.innerHTML = trains.map(train => `
    <div class="train-card">
      <div class="train-info">
        <div class="train-icon-badge">🚆</div>
        <div class="train-meta">
          <span class="train-number">#${train.trainNumber}</span>
          <h3>${train.trainName}</h3>
        </div>
      </div>
      
      <div class="train-route">
        <div class="route-node">
          <div class="route-city">${train.source}</div>
          <div class="route-label">Source</div>
        </div>
        <div class="route-line"></div>
        <div class="route-node">
          <div class="route-city">${train.destination}</div>
          <div class="route-label">Destination</div>
        </div>
      </div>

      <div class="seat-status-box">
        <div class="available-count">${train.availableSeats} Seats</div>
        <div class="total-count">of ${train.totalSeats} Total</div>
      </div>

      <div style="display: flex; gap: 0.5rem;">
        <button class="btn btn-primary btn-sm" onclick="openBookingModal(${train.trainId})">
          Book Now ➔
        </button>
        ${(state.user && (state.user.role === 'ADMIN' || state.user.role === 'ROLE_ADMIN')) ? `
          <button class="btn btn-secondary btn-sm" onclick="editTrain(${train.trainId})" title="Edit Train">✏️</button>
          <button class="btn btn-danger btn-sm" onclick="deleteTrain(${train.trainId})" title="Delete Train">🗑️</button>
        ` : ''}
      </div>
    </div>
  `).join('');
}

// Seat Chooser Helpers
function toggleCustomSeatInput() {
  const select = document.getElementById('book-seat-number');
  const group = document.getElementById('custom-seat-group');
  if (select && group) {
    group.style.display = select.value === 'CUSTOM' ? 'flex' : 'none';
  }
}

function updateSeatOptions() {
  // Can dynamically filter seat numbers if needed
}

// Booking Flow
function openBookingModal(trainId) {
  if (!state.token || !state.user) {
    showToast('Please login to book tickets', 'info');
    openModal('auth-modal');
    return;
  }

  const train = state.trains.find(t => t.trainId === trainId);
  if (!train) return;

  state.selectedTrainForBooking = train;
  
  document.getElementById('book-train-name').textContent = `${train.trainName} (#${train.trainNumber})`;
  document.getElementById('book-train-route').textContent = `${train.source} → ${train.destination}`;
  document.getElementById('book-available-seats').textContent = `${train.availableSeats} Available`;
  
  const journeyDate = document.getElementById('search-date').value || new Date().toISOString().split('T')[0];
  document.getElementById('book-date').value = journeyDate;
  
  updateBookingPrice();

  openModal('booking-modal');
}

function updateBookingPrice() {
  const seats = parseInt(document.getElementById('book-seats').value || 1);
  const basePricePerSeat = 450; // Standard base fare estimate
  const totalPrice = seats * basePricePerSeat;
  document.getElementById('book-price').textContent = `₹${totalPrice}`;
}

async function handleCreateBooking(e) {
  e.preventDefault();
  
  if (!state.selectedTrainForBooking) return;
  const numSeats = parseInt(document.getElementById('book-seats').value || 1);
  const bookingDate = document.getElementById('book-date').value;

  if (numSeats > state.selectedTrainForBooking.availableSeats) {
    showToast('Not enough available seats', 'error');
    return;
  }

  // Capture user chosen Coach & Seat Number
  const chosenCoach = document.getElementById('book-coach')?.value || 'S1';
  const seatSel = document.getElementById('book-seat-number')?.value;
  let chosenSeat = seatSel;
  if (seatSel === 'CUSTOM') {
    chosenSeat = document.getElementById('book-custom-seat')?.value.trim() || `${chosenCoach}-12`;
  } else {
    chosenSeat = `${chosenCoach}-${seatSel}`;
  }

  state.selectedSeatConfig = {
    coach: chosenCoach,
    seatNumber: chosenSeat
  };

  try {
    const bookingPayload = {
      bookingDate: bookingDate,
      numSeatBooked: numSeats,
      status: "PENDING_PAYMENT",
      user: { id: state.user.id },
      train: { trainId: state.selectedTrainForBooking.trainId }
    };

    const createdBooking = await apiRequest('/booking/book', 'POST', bookingPayload, true);
    state.activeBooking = createdBooking;

    closeModal('booking-modal');
    openPaymentModal(createdBooking, numSeats * 450);
  } catch (err) {
    showToast(err.message || 'Booking failed', 'error');
  }
}

// Payment Flow
function openPaymentModal(booking, totalAmount) {
  document.getElementById('pay-amount').textContent = `₹${totalAmount}`;
  document.getElementById('payment-form').dataset.bookingId = booking.id;
  document.getElementById('payment-form').dataset.amount = totalAmount;
  openModal('payment-modal');
}

async function handleProcessPayment(e) {
  e.preventDefault();
  const form = e.target;
  const bookingId = parseInt(form.dataset.bookingId);
  const amount = parseFloat(form.dataset.amount);
  const paymentMode = document.getElementById('pay-mode').value;

  try {
    const paymentPayload = {
      amount: amount,
      paymentMode: paymentMode,
      paymentStatus: "SUCCESS"
    };

    await apiRequest(`/payment/add/${bookingId}`, 'POST', paymentPayload, true);
    
    // Generate ticket with user's explicitly selected coach & seat number
    const pnrCode = 'PNR' + Math.floor(10000000 + Math.random() * 90000000);
    const coachStr = state.selectedSeatConfig?.coach || 'S1';
    const seatStr = state.selectedSeatConfig?.seatNumber || `${coachStr}-24`;
    const journeyDate = state.activeBooking ? state.activeBooking.bookingDate : new Date().toISOString().split('T')[0];

    const ticketPayload = {
      pnr: pnrCode,
      fare: amount,
      journeyDate: journeyDate,
      coach: coachStr,
      seatNumber: seatStr
    };

    const generatedTicket = await apiRequest(`/ticket/add/${bookingId}`, 'POST', ticketPayload, true);
    
    closeModal('payment-modal');
    showToast('Payment successful! Ticket generated with your chosen seat.', 'success');
    
    // Display E-Ticket Modal
    renderETicket(generatedTicket, state.selectedTrainForBooking || {});
    await handleSearchTrains(); // Refresh available seats
  } catch (err) {
    showToast(err.message || 'Payment processing failed', 'error');
  }
}

// E-Ticket Display
function renderETicket(ticket, train) {
  const container = document.getElementById('ticket-modal-body');
  if (!container) return;

  const user = state.user || {};
  const trainName = train.trainName || 'Express Special';
  const trainNum = train.trainNumber || '12951';
  const source = train.source || 'Station A';
  const destination = train.destination || 'Station B';

  container.innerHTML = `
    <div class="ticket-card" id="printable-ticket">
      <div class="ticket-header">
        <div class="brand">🚆 RAILVOYAGE EXPRESS TICKET</div>
        <div class="pnr-tag">PNR: ${ticket.pnr || 'PNR998231'}</div>
      </div>
      
      <div class="ticket-body">
        <div class="ticket-row">
          <div class="ticket-col">
            <span class="ticket-label">Passenger Name</span>
            <span class="ticket-value">${user.name || 'Passenger'}</span>
          </div>
          <div class="ticket-col" style="text-align: right;">
            <span class="ticket-label">Journey Date</span>
            <span class="ticket-value">${ticket.journeyDate || '2026-07-27'}</span>
          </div>
        </div>

        <div class="ticket-row" style="background: #f1f5f9; padding: 1rem; border-radius: 8px;">
          <div class="ticket-col">
            <span class="ticket-label">Train Name & Number</span>
            <span class="ticket-value">${trainName} (#${trainNum})</span>
          </div>
          <div class="ticket-col" style="text-align: right;">
            <span class="ticket-label">Route</span>
            <span class="ticket-value">${source} ➔ ${destination}</span>
          </div>
        </div>

        <div class="ticket-row">
          <div class="ticket-col">
            <span class="ticket-label">Coach</span>
            <span class="ticket-value-lg">${ticket.coach || 'S1'}</span>
          </div>
          <div class="ticket-col">
            <span class="ticket-label">Seat Number</span>
            <span class="ticket-value-lg">${ticket.seatNumber || 'S1-24'}</span>
          </div>
          <div class="ticket-col" style="text-align: right;">
            <span class="ticket-label">Total Fare</span>
            <span class="ticket-value-lg" style="color: #059669;">₹${ticket.fare || '450'}</span>
          </div>
        </div>
      </div>

      <div class="ticket-stub">
        <div>
          <div class="ticket-label">Boarding Pass Code</div>
          <div class="barcode">*${ticket.pnr || 'PNR12345678'}*</div>
        </div>
        <img class="qr-code-img" src="https://api.qrserver.com/v1/create-qr-code/?size=80x80&data=${encodeURIComponent(ticket.pnr || 'PNR')}" alt="QR Code" />
      </div>
    </div>
  `;

  openModal('ticket-modal');
}

// User Bookings View
async function loadMyBookings() {
  if (!state.token || !state.user) {
    showToast('Please login to view your bookings', 'info');
    openModal('auth-modal');
    return;
  }

  const container = document.getElementById('my-bookings-container');
  container.innerHTML = '<div class="empty-state">⚡ Loading your bookings...</div>';

  try {
    // Call user specific bookings endpoint
    let bookings = [];
    try {
      bookings = await apiRequest(`/booking/user/${state.user.id}`, 'GET', null, true);
    } catch (e) {
      // Fallback to getall if user specific endpoint fails
      bookings = await apiRequest('/booking/getall', 'GET', null, true);
    }

    state.myBookings = bookings;

    if (!bookings || bookings.length === 0) {
      container.innerHTML = `
        <div class="empty-state">
          <div class="empty-icon">🎟️</div>
          <h3>No Bookings Found</h3>
          <p style="color: var(--text-muted);">You haven't reserved any train tickets yet.</p>
        </div>`;
      return;
    }

    container.innerHTML = bookings.map(b => `
      <div class="train-card">
        <div class="train-info">
          <div class="train-icon-badge">🎟️</div>
          <div class="train-meta">
            <span class="train-number">Booking #${b.id}</span>
            <h3>${b.train ? b.train.trainName : 'Railway Express'}</h3>
            <span style="font-size: 0.8rem; color: var(--text-muted);">${b.bookingDate} • ${b.numSeatBooked} Seat(s)</span>
          </div>
        </div>

        <div style="text-align: right;">
          <span class="role-tag ${b.status === 'CONFIRMED' ? 'role-user' : (b.status === 'CANCELLED' ? 'role-admin' : 'role-admin')}" style="font-size: 0.85rem;">
            ${b.status}
          </span>
        </div>

        <div style="display: flex; gap: 0.5rem;">
          ${b.ticket ? `
            <button class="btn btn-secondary btn-sm" onclick="viewExistingTicket(${b.id})">
              View Ticket
            </button>
          ` : ''}
          ${b.status !== 'CANCELLED' ? `
            <button class="btn btn-danger btn-sm" onclick="cancelBooking(${b.id})">
              Cancel
            </button>
          ` : ''}
        </div>
      </div>
    `).join('');
  } catch (err) {
    container.innerHTML = `<div class="empty-state"><div class="empty-icon">⚠️</div><p>${err.message || 'Failed to fetch bookings'}</p></div>`;
  }
}

function viewExistingTicket(bookingId) {
  const booking = state.myBookings.find(b => b.id === bookingId);
  if (booking && booking.ticket) {
    renderETicket(booking.ticket, booking.train || {});
  }
}

async function cancelBooking(bookingId) {
  if (!confirm('Are you sure you want to cancel this booking?')) return;

  try {
    await apiRequest(`/booking/cancel/${bookingId}`, 'DELETE', null, true);
    showToast('Booking cancelled successfully', 'success');
    await loadMyBookings();
    await handleSearchTrains();
  } catch (err) {
    showToast(err.message || 'Cancellation failed', 'error');
  }
}

// Admin Train Management
function openAdminTrainModal(train = null) {
  const form = document.getElementById('admin-train-form');
  const title = document.getElementById('admin-train-title');
  
  if (train) {
    title.textContent = 'Edit Train Details';
    form.dataset.trainId = train.trainId;
    document.getElementById('admin-train-number').value = train.trainNumber;
    document.getElementById('admin-train-name').value = train.trainName;
    document.getElementById('admin-train-source').value = train.source;
    document.getElementById('admin-train-dest').value = train.destination;
    document.getElementById('admin-train-total-seats').value = train.totalSeats;
    document.getElementById('admin-train-avail-seats').value = train.availableSeats;
  } else {
    title.textContent = 'Add New Train';
    delete form.dataset.trainId;
    form.reset();
  }

  openModal('admin-train-modal');
}

async function handleAdminSaveTrain(e) {
  e.preventDefault();
  const form = e.target;
  const trainId = form.dataset.trainId ? parseInt(form.dataset.trainId) : null;

  const payload = {
    trainNumber: parseInt(document.getElementById('admin-train-number').value),
    trainName: document.getElementById('admin-train-name').value.trim(),
    source: document.getElementById('admin-train-source').value.trim(),
    destination: document.getElementById('admin-train-dest').value.trim(),
    totalSeats: parseInt(document.getElementById('admin-train-total-seats').value),
    availableSeats: parseInt(document.getElementById('admin-train-avail-seats').value)
  };

  try {
    if (trainId) {
      payload.trainId = trainId;
      await apiRequest('/train/update', 'PUT', payload, true);
      showToast('Train updated successfully', 'success');
    } else {
      await apiRequest('/train/add', 'POST', payload, true);
      showToast('Train added successfully', 'success');
    }

    closeModal('admin-train-modal');
    await handleSearchTrains();
  } catch (err) {
    showToast(err.message || 'Failed to save train', 'error');
  }
}

function editTrain(trainId) {
  const train = state.trains.find(t => t.trainId === trainId);
  if (train) openAdminTrainModal(train);
}

async function deleteTrain(trainId) {
  if (!confirm('Are you sure you want to delete this train?')) return;
  try {
    await apiRequest(`/train/delete/${trainId}`, 'DELETE', null, true);
    showToast('Train deleted successfully', 'success');
    await handleSearchTrains();
  } catch (err) {
    showToast(err.message || 'Failed to delete train', 'error');
  }
}

// Admin Users Management
async function openAdminUsersModal() {
  openModal('admin-users-modal');
  await loadAllUsers();
}

async function loadAllUsers() {
  const tbody = document.getElementById('admin-users-table-body');
  if (!tbody) return;
  tbody.innerHTML = '<tr><td colspan="6" style="padding: 1rem; text-align: center;">⚡ Loading users...</td></tr>';

  try {
    const users = await apiRequest('/user/getalluser', 'GET', null, true);
    if (!users || users.length === 0) {
      tbody.innerHTML = '<tr><td colspan="6" style="padding: 1rem; text-align: center; color: var(--text-muted);">No users found.</td></tr>';
      return;
    }

    tbody.innerHTML = users.map(u => `
      <tr style="border-bottom: 1px solid var(--border-light);">
        <td style="padding: 0.75rem; font-weight: 600;">#${u.id}</td>
        <td style="padding: 0.75rem;">${u.name || 'N/A'}</td>
        <td style="padding: 0.75rem;">${u.email}</td>
        <td style="padding: 0.75rem;">${u.phone || 'N/A'}</td>
        <td style="padding: 0.75rem;">
          <span class="role-tag ${u.role === 'ADMIN' || u.role === 'ROLE_ADMIN' ? 'role-admin' : 'role-user'}">${u.role}</span>
        </td>
        <td style="padding: 0.75rem;">
          ${u.id !== state.user?.id ? `
            <button class="btn btn-danger btn-sm" onclick="deleteUser(${u.id})">Delete</button>
          ` : '<span style="font-size: 0.75rem; color: var(--text-muted);">(You)</span>'}
        </td>
      </tr>
    `).join('');
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="6" style="padding: 1rem; text-align: center; color: #fb7185;">⚠️ ${err.message || 'Failed to fetch users'}</td></tr>`;
  }
}

async function deleteUser(userId) {
  if (!confirm(`Are you sure you want to delete user #${userId}?`)) return;
  try {
    await apiRequest(`/user/delete/${userId}`, 'DELETE', null, true);
    showToast('User deleted successfully', 'success');
    await loadAllUsers();
  } catch (err) {
    showToast(err.message || 'Failed to delete user', 'error');
  }
}

// Navigation & View Switching
function switchView(viewId) {
  document.querySelectorAll('.view-section').forEach(sec => sec.classList.remove('active-view'));
  document.querySelectorAll('.nav-item').forEach(nav => nav.classList.remove('active'));

  const activeView = document.getElementById(viewId);
  if (activeView) activeView.classList.add('active-view');

  if (viewId === 'my-bookings-view') {
    loadMyBookings();
  }
}

// Modal Toggle Helpers
function openModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) modal.classList.add('active');
}

function closeModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) modal.classList.remove('active');
}

function printTicket() {
  window.print();
}
