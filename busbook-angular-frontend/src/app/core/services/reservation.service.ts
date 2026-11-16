import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ReservationRequest, ReservationResponse } from '../models/reservation.model';

@Injectable({
  providedIn: 'root'
})
export class ReservationApiService {
  private readonly baseUrl = `${environment.reservationApiUrl}/reservations`;

  constructor(private http: HttpClient) {}

  createReservation(request: ReservationRequest): Observable<ReservationResponse> {
    return this.http.post<ReservationResponse>(this.baseUrl, request);
  }

  getReservationById(id: number): Observable<ReservationResponse> {
    return this.http.get<ReservationResponse>(`${this.baseUrl}/${id}`);
  }

  getReservationsByTripId(tripId: number): Observable<ReservationResponse[]> {
    return this.http.get<ReservationResponse[]>(`${this.baseUrl}/trip/${tripId}`);
  }

  getReservationsByTravelerId(travelerId: number): Observable<ReservationResponse[]> {
    return this.http.get<ReservationResponse[]>(`${this.baseUrl}/traveler/${travelerId}`);
  }

  checkSeatAvailability(tripId: number, seatId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/availability`, {
      params: { tripId: tripId.toString(), seatId: seatId.toString() }
    });
  }

  confirmReservation(id: number): Observable<ReservationResponse> {
    return this.http.patch<ReservationResponse>(`${this.baseUrl}/${id}/confirm`, {});
  }

  cancelReservation(id: number): Observable<ReservationResponse> {
    return this.http.patch<ReservationResponse>(`${this.baseUrl}/${id}/cancel`, {});
  }
}
