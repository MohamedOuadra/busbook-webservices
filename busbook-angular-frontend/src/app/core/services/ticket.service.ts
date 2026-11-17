import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TicketRequest, TicketResponse } from '../models/ticket.model';

@Injectable({
  providedIn: 'root'
})
export class TicketApiService {
  private readonly baseUrl = `${environment.ticketApiUrl}/tickets`;

  constructor(private http: HttpClient) {}

  createTicket(request: TicketRequest): Observable<TicketResponse> {
    return this.http.post<TicketResponse>(this.baseUrl, request);
  }

  getTicketById(id: number): Observable<TicketResponse> {
    return this.http.get<TicketResponse>(`${this.baseUrl}/${id}`);
  }

  getTicketByReservation(reservationId: number): Observable<TicketResponse> {
    return this.http.get<TicketResponse>(`${this.baseUrl}/reservation/${reservationId}`);
  }

  verifyTicket(ticketNumber: string): Observable<TicketResponse> {
    return this.http.get<TicketResponse>(`${this.baseUrl}/verify/${ticketNumber}`);
  }
}
