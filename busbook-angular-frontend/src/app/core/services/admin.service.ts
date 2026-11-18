import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Trip } from '../../features/trips/models/trip';
import { AuthService } from './auth.service';

export interface Bus {
  id?: number;
  immatriculation: string;
  capacite: number;
  type: string;
}

export interface CreateTripRequest {
  villeDepart: string;
  villeArrivee: string;
  dateDepart: string;
  dateArrivee: string;
  prix: number;
  busId: number;
}

export interface AdminReservation {
  id: number;
  travelerId?: number;
  userId?: number;
  tripId: number;
  seatId?: number;
  seatNumber?: string;
  seatNumbers?: number[];
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'EXPIRED';
  totalPrice?: number;
  createdAt: string;
  expiresAt?: string;
  confirmedAt?: string;
}

export interface DashboardStats {
  totalTrips: number;
  totalBuses: number;
  totalReservations: number;
  confirmedReservations: number;
}

/**
 * Service admin — communique avec catalog-service (8081) et reservation-service (8082).
 */
@Injectable({ providedIn: 'root' })
export class AdminService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);

  private catalogUrl = `${environment.catalogApiUrl}`;
  private reservationUrl = `${environment.reservationApiUrl}`;

  private get headers(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      Authorization: `Bearer ${token || ''}`,
      'Content-Type': 'application/json',
    });
  }

  // ─── BUS ───────────────────────────────────────────────────────────────────

  getBuses(): Observable<Bus[]> {
    return this.http.get<Bus[]>(`${this.catalogUrl}/buses`, { headers: this.headers });
  }

  createBus(bus: Omit<Bus, 'id'>): Observable<Bus> {
    return this.http.post<Bus>(`${this.catalogUrl}/buses`, bus, { headers: this.headers });
  }

  updateBus(id: number, bus: Omit<Bus, 'id'>): Observable<Bus> {
    return this.http.put<Bus>(`${this.catalogUrl}/buses/${id}`, bus, { headers: this.headers });
  }

  deleteBus(id: number): Observable<void> {
    return this.http.delete<void>(`${this.catalogUrl}/buses/${id}`, { headers: this.headers });
  }

  // ─── TRIPS ─────────────────────────────────────────────────────────────────

  getTrips(): Observable<Trip[]> {
    return this.http.get<Trip[]>(`${this.catalogUrl}/trips`, { headers: this.headers });
  }

  createTrip(trip: CreateTripRequest): Observable<Trip> {
    return this.http.post<Trip>(`${this.catalogUrl}/trips`, trip, { headers: this.headers });
  }

  updateTrip(id: number, trip: CreateTripRequest): Observable<Trip> {
    return this.http.put<Trip>(`${this.catalogUrl}/trips/${id}`, trip, { headers: this.headers });
  }

  deleteTrip(id: number): Observable<void> {
    return this.http.delete<void>(`${this.catalogUrl}/trips/${id}`, { headers: this.headers });
  }

  // ─── RÉSERVATIONS ──────────────────────────────────────────────────────────

  getAllReservations(): Observable<AdminReservation[]> {
    return this.http.get<AdminReservation[]>(`${this.reservationUrl}/reservations`, { headers: this.headers });
  }

  cancelReservation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.reservationUrl}/reservations/${id}`, { headers: this.headers });
  }
}
