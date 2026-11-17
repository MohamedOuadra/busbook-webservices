import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of, catchError } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Trip } from '../../features/trips/models/trip';
import { TripSearch } from '../../features/trips/models/trip-search';
import { MOCK_TRIPS } from '../../mock-data/trips';

@Injectable({
  providedIn: 'root',
})
export class TripService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.catalogApiUrl}/trips`;

  getTrips(): Observable<Trip[]> {
    return this.http.get<Trip[]>(this.apiUrl).pipe(
      catchError(() => of(MOCK_TRIPS))
    );
  }

  searchTrips(criteria: TripSearch): Observable<Trip[]> {
    let params = new HttpParams();
    if (criteria.departure) params = params.set('villeDepart', criteria.departure);
    if (criteria.destination) params = params.set('villeArrivee', criteria.destination);
    if (criteria.date) params = params.set('dateDepart', criteria.date);

    return this.http.get<Trip[]>(`${this.apiUrl}/search`, { params }).pipe(
      catchError(() => {
        const results = MOCK_TRIPS.filter((trip) => {
          const matchesDeparture =
            !criteria.departure || trip.villeDepart.toLowerCase() === criteria.departure.toLowerCase();
          const matchesDestination =
            !criteria.destination || trip.villeArrivee.toLowerCase() === criteria.destination.toLowerCase();
          const matchesDate =
            !criteria.date || trip.dateDepart.startsWith(criteria.date);

          return matchesDeparture && matchesDestination && matchesDate;
        });
        return of(results);
      })
    );
  }

  getTripById(id: number): Observable<Trip | undefined> {
    return this.http.get<Trip>(`${this.apiUrl}/${id}`).pipe(
      catchError(() => {
        const trip = MOCK_TRIPS.find((t) => t.id === id);
        return of(trip);
      })
    );
  }
}
