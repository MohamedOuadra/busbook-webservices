import { Routes } from '@angular/router';
import { MainLayout } from './layouts/main-layout/main-layout';
import { AuthLayout } from './layouts/auth-layout/auth-layout';
import { AdminLayout } from './layouts/admin-layout/admin-layout';
import { Home } from './features/home/pages/home/home';
import { Reservation } from './features/reservations/pages/reservation/reservation';
import { PaymentPage } from './features/payment/pages/payment/payment';
import { TicketPage } from './features/tickets/pages/ticket/ticket';
import { MyReservationsPage } from './features/reservations/pages/my-reservations/my-reservations';
import { VerifyTicketPage } from './features/tickets/pages/verify-ticket/verify-ticket';
import { LoginPage } from './features/auth/pages/login/login';
import { RegisterPage } from './features/auth/pages/register/register';
import { SettingsPage } from './features/settings/pages/settings/settings';
import { AdminDashboard } from './features/admin/pages/dashboard/dashboard';
import { AdminTrips } from './features/admin/pages/trips/admin-trips';
import { AdminBuses } from './features/admin/pages/buses/admin-buses';
import { AdminReservations } from './features/admin/pages/reservations/admin-reservations';
import { adminGuard } from './core/guards/admin.guard';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  // ── Auth Layout (login & register) ──────────────────────────────────────
  {
    path: 'login',
    component: AuthLayout,
    children: [{ path: '', component: LoginPage }],
  },
  {
    path: 'register',
    component: AuthLayout,
    children: [{ path: '', component: RegisterPage }],
  },

  // ── Admin Layout (protégé par adminGuard) ───────────────────────────────
  {
    path: 'admin',
    component: AdminLayout,
    canActivate: [adminGuard],
    children: [
      { path: '',             component: AdminDashboard,    title: 'Admin — Dashboard' },
      { path: 'trips',        component: AdminTrips,        title: 'Admin — Trajets' },
      { path: 'buses',        component: AdminBuses,        title: 'Admin — Bus' },
      { path: 'reservations', component: AdminReservations, title: 'Admin — Réservations' },
    ],
  },

  // ── Main Layout (pages publiques + routes protégées par authGuard) ──────
  {
    path: '',
    component: MainLayout,
    children: [
      { path: '',                       component: Home },
      { path: 'trips',                  redirectTo: '', pathMatch: 'full' },
      { path: 'reservation/:tripId',    component: Reservation,        canActivate: [authGuard] },
      { path: 'payment/:reservationId', component: PaymentPage,        canActivate: [authGuard] },
      { path: 'ticket/:ticketNumber',   component: TicketPage,         canActivate: [authGuard] },
      { path: 'my-reservations',        component: MyReservationsPage, canActivate: [authGuard] },
      { path: 'verify-ticket',          component: VerifyTicketPage },
      { path: 'settings',               component: SettingsPage,       canActivate: [authGuard] },
    ],
  },

  { path: '**', redirectTo: '' },
];