import { Routes } from '@angular/router';
import { MainLayout } from './layouts/main-layout/main-layout';
import { Home } from './features/home/pages/home/home';
import { Reservation } from './features/reservations/pages/reservation/reservation';

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
                path: 'reservation/:tripId',
                component: Reservation,
            },
        ],
    },
    {
        path: '**',
        redirectTo: '',
    },
];