import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

import { TicketApiService } from '../../../../core/services/ticket.service';
import { TripService } from '../../../../core/services/trip';
import { TicketResponse } from '../../../../core/models/ticket.model';
import { Trip } from '../../../trips/models/trip';
import { TicketViewComponent } from '../../../reservations/components/ticket-view/ticket-view.component';

@Component({
  selector: 'app-ticket-page',
  standalone: true,
  imports: [CommonModule, RouterLink, TicketViewComponent],
  templateUrl: './ticket.html',
  styleUrl: './ticket.css',
})
export class TicketPage implements OnInit {
  ticketNumber = '';

  private readonly _ticket = signal<TicketResponse | undefined>(undefined);
  private readonly _trip = signal<Trip | undefined>(undefined);
  private readonly _isLoading = signal(true);
  private readonly _errorMessage = signal<string | null>(null);

  get ticket(): TicketResponse | undefined {
    return this._ticket();
  }
  set ticket(val: TicketResponse | undefined) {
    this._ticket.set(val);
  }

  get trip(): Trip | undefined {
    return this._trip();
  }
  set trip(val: Trip | undefined) {
    this._trip.set(val);
  }

  get isLoading(): boolean {
    return this._isLoading();
  }
  set isLoading(val: boolean) {
    this._isLoading.set(val);
  }

  get errorMessage(): string | null {
    return this._errorMessage();
  }
  set errorMessage(val: string | null) {
    this._errorMessage.set(val);
  }

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private ticketApi = inject(TicketApiService);
  private tripService = inject(TripService);

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const num = params.get('ticketNumber');
      this.ticketNumber = num ? num.trim() : '';

      if (!this.ticketNumber) {
        this.isLoading = false;
        this.errorMessage = 'No ticket number provided.';
        return;
      }

      this.loadTicket();
    });
  }

  loadTicket(): void {
    this.isLoading = true;
    this.errorMessage = null;
    this.ticket = undefined;
    this.trip = undefined;

    this.ticketApi.verifyTicket(this.ticketNumber).subscribe({
      next: (tkt) => {
        this.ticket = tkt;

        this.tripService.getTripById(tkt.tripId).subscribe({
          next: (trip) => {
            this.trip = trip || this.buildFallbackTrip(tkt.tripId);
            this.isLoading = false;
          },
          error: () => {
            this.trip = this.buildFallbackTrip(tkt.tripId);
            this.isLoading = false;
          },
        });
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage =
          err.error?.message ||
          `Ticket "${this.ticketNumber}" could not be verified or was not found.`;
      },
    });
  }

  private buildFallbackTrip(tripId: number): Trip {
    return {
      id: tripId,
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
    };
  }

  onNewBooking(): void {
    this.router.navigate(['/']);
  }
}
