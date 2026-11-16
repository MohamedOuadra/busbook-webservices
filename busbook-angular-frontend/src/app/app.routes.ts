import { Routes } from '@angular/router';
import { MainLayout } from './layouts/main-layout/main-layout';
import { Home } from './features/home/pages/home/home';
import { Reservation } from './features/reservations/pages/reservation/reservation';
import { PaymentPage } from './features/payment/pages/payment/payment';
import { TicketPage } from './features/tickets/pages/ticket/ticket';
import { MyReservationsPage } from './features/reservations/pages/my-reservations/my-reservations';
import { VerifyTicketPage } from './features/tickets/pages/verify-ticket/verify-ticket';
import { TripsCatalogPage } from './features/trips/pages/trips-catalog/trips-catalog';
import { LoginPage } from './features/auth/pages/login/login';
import { RegisterPage } from './features/auth/pages/register/register';

export const routes: Routes = [
  {
    path: '',
    component: MainLayout,
    children: [
      {
        path: '',
        component: Home,
      },
      {
        path: 'trips',
        component: TripsCatalogPage,
      },
      {
        path: 'login',
        component: LoginPage,
      },
      {
        path: 'register',
        component: RegisterPage,
      },
      {
        path: 'reservation/:tripId',
        component: Reservation,
      },
      {
        path: 'payment/:reservationId',
        component: PaymentPage,
      },
      {
        path: 'ticket/:ticketNumber',
        component: TicketPage,
      },
      {
        path: 'my-reservations',
        component: MyReservationsPage,
      },
      {
        path: 'verify-ticket',
        component: VerifyTicketPage,
      },
    ],
  },
  {
    path: '**',
    redirectTo: '',
  },
];
