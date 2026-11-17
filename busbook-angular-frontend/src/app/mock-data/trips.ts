import { Trip } from '../features/trips/models/trip';

export const MOCK_TRIPS: Trip[] = [
    {
        id: 1,
        villeDepart: 'Agadir',
        villeArrivee: 'Casablanca',
        dateDepart: '2026-11-20T08:00:00',
        dateArrivee: '2026-11-20T14:00:00',
        prix: 120,
        bus: {
            id: 1,
            immatriculation: '12345-A-6',
            capacite: 40,
            type: 'Confort',
        },
    },
    {
        id: 2,
        villeDepart: 'Agadir',
        villeArrivee: 'Casablanca',
        dateDepart: '2026-11-20T10:30:00',
        dateArrivee: '2026-11-20T16:30:00',
        prix: 150,
        bus: {
            id: 2,
            immatriculation: '67890-B-6',
            capacite: 50,
            type: 'VIP',
        },
    },
    {
        id: 3,
        villeDepart: 'Agadir',
        villeArrivee: 'Marrakech',
        dateDepart: '2026-11-20T09:00:00',
        dateArrivee: '2026-11-20T12:30:00',
        prix: 90,
        bus: {
            id: 3,
            immatriculation: '11111-C-6',
            capacite: 40,
            type: 'Standard',
        },
    },
];