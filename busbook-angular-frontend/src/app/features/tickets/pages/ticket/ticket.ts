import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

import { TicketApiService } from '../../../../core/services/ticket.service';
import { TripService } from '../../../../core/services/trip';
import { TicketResponse } from '../../../../core/models/ticket.model';
import { Trip } from '../../../trips/models/trip';
import { TicketViewComponent } from '../../../reservations/components/ticket-view/ticket-view.component';

@Component({
  selector: 'app-ticket-page',
  imports: [CommonModule, RouterLink, TicketViewComponent],
  templateUrl: './ticket.html',
  styleUrl: './ticket.css',
})
export class TicketPage implements OnInit {
  ticketNumber = '';
  ticket?: TicketResponse;
  trip?: Trip;

  isLoading = true;
  errorMessage: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private ticketApi: TicketApiService,
    private tripService: TripService
  ) {}

  ngOnInit(): void {
    this.ticketNumber = this.route.snapshot.paramMap.get('ticketNumber') || '';

    if (!this.ticketNumber) {
      this.isLoading = false;
      this.errorMessage = 'No ticket number provided.';
      return;
    }

    this.loadTicket();
  }

  loadTicket(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.ticketApi.verifyTicket(this.ticketNumber).subscribe({
      next: (tkt) => {
        this.ticket = tkt;
        this.isLoading = false;

        this.tripService.getTripById(tkt.tripId).subscribe((trip) => {
          this.trip = trip || {
            id: tkt.tripId,
            villeDepart: 'Agadir',
            villeArrivee: 'Casablanca',
            dateDepart: '2026-11-20T08:00:00',
            dateArrivee: '2026-11-20T14:00:00',
            prix: 120,
            bus: { id: 1, immatriculation: '12345-A-6', capacite: 40, type: 'Confort' }
          };
        });
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = `Ticket "${this.ticketNumber}" could not be verified or was not found.`;
      }
    });
  }

  onNewBooking(): void {
    this.router.navigate(['/']);
  }
}
