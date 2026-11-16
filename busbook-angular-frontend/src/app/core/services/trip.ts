import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

import { Trip } from '../../features/trips/models/trip';
import { TripSearch } from '../../features/trips/models/trip-search';
import { MOCK_TRIPS } from '../../mock-data/trips';

@Injectable({
    providedIn: 'root',
})
export class TripService {

    getTrips(): Observable<Trip[]> {
        return of(MOCK_TRIPS);
    }

    searchTrips(criteria: TripSearch): Observable<Trip[]> {
        const results = MOCK_TRIPS.filter((trip) => {
            const matchesDeparture =
                trip.villeDepart.toLowerCase() === criteria.departure.toLowerCase();

            const matchesDestination =
                trip.villeArrivee.toLowerCase() === criteria.destination.toLowerCase();

            const matchesDate =
                trip.dateDepart.startsWith(criteria.date);

            return matchesDeparture && matchesDestination && matchesDate;
        });

        return of(results);
    }

    getTripById(id: number): Observable<Trip | undefined> {
        const trip = MOCK_TRIPS.find((trip) => trip.id === id);

        return of(trip);
    }
}