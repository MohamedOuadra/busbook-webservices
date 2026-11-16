import { ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';

import { Trip } from '../../../trips/models/trip';
import { TripService } from '../../../../core/services/trip';
import { BookingService, TravelerInfo } from '../../../../core/services/booking';
import { ReservationResponse } from '../../../../core/models/reservation.model';
import { TicketResponse } from '../../../../core/models/ticket.model';
import { PaymentMethod } from '../../../../core/models/payment.model';

import { BusIllustration } from '../../components/bus-illustration/bus-illustration';
import { SeatMap } from '../../components/seat-map/seat-map';
import { BookingPanel } from '../../components/booking-panel/booking-panel';
import { PaymentSectionComponent } from '../../components/payment-section/payment-section.component';
import { TicketViewComponent } from '../../components/ticket-view/ticket-view.component';

export type BookingStep = 'SELECTION' | 'PAYMENT' | 'TICKET';

@Component({
  selector: 'app-reservation',
  imports: [
    CommonModule,
    BusIllustration,
    SeatMap,
    BookingPanel,
    PaymentSectionComponent,
    TicketViewComponent,
  ],
  templateUrl: './reservation.html',
  styleUrl: './reservation.css',
})
export class Reservation implements OnInit, OnDestroy {
  @ViewChild(SeatMap) seatMapComponent?: SeatMap;

  currentStep: BookingStep = 'SELECTION';
  selectedSeat: number | null = null;
  trip: Trip | undefined;

  isCreatingReservation = false;
  isProcessingPayment = false;
  errorMessage: string | null = null;
  paymentErrorMessage: string | null = null;

  currentReservation: ReservationResponse | null = null;
  currentTicket: TicketResponse | null = null;
  remainingSeconds = 600;
  isExpired = false;

  private subscriptions = new Subscription();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private tripService: TripService,
    private bookingService: BookingService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const tripId = Number(this.route.snapshot.paramMap.get('tripId'));

    this.tripService.getTripById(tripId).subscribe((trip) => {
      this.trip = trip;
      if (trip) {
        this.bookingService.setTrip(trip);
      }
      this.cdr.markForCheck();
    });

    // Écoute du minuteur et de l'état d'expiration
    this.subscriptions.add(
      this.bookingService.remainingSeconds$.subscribe((sec) => {
        this.remainingSeconds = sec;
        this.cdr.markForCheck();
      })
    );

    this.subscriptions.add(
      this.bookingService.isExpired$.subscribe((expired) => {
        this.isExpired = expired;
        this.cdr.markForCheck();
      })
    );

    this.subscriptions.add(
      this.bookingService.reservation$.subscribe((res) => {
        this.currentReservation = res;
        this.cdr.markForCheck();
      })
    );

    this.subscriptions.add(
      this.bookingService.ticket$.subscribe((tkt) => {
        this.currentTicket = tkt;
        this.cdr.markForCheck();
      })
    );
  }

  onSeatSelected(seatNumber: number): void {
    this.selectedSeat = seatNumber;
    this.bookingService.setSelectedSeat(seatNumber);
    this.errorMessage = null;
    this.cdr.markForCheck();
  }

  onStartBooking(traveler: TravelerInfo): void {
    if (!this.trip || this.selectedSeat === null) {
      this.errorMessage = 'Veuillez sélectionner un siège avant de continuer.';
      return;
    }

    this.isCreatingReservation = true;
    this.errorMessage = null;
    this.bookingService.setTravelerInfo(traveler);
    this.cdr.markForCheck();

    this.bookingService.startReservation(this.trip.id, this.selectedSeat, 1).subscribe({
      next: (reservation) => {
        this.isCreatingReservation = false;
        this.currentReservation = reservation;
        this.currentStep = 'PAYMENT';
        this.cdr.markForCheck();
        this.router.navigate(['/payment', reservation.id]);
      },
      error: (err) => {
        this.isCreatingReservation = false;
        if (err.status === 409) {
          this.errorMessage =
            'Ce siège est déjà réservé ou bloqué temporairement. Veuillez choisir un autre siège.';
        } else {
          this.errorMessage =
            err.error?.message ||
            'Impossible de créer la réservation. Veuillez vérifier la disponibilité.';
        }
        this.cdr.markForCheck();
        // Rafraîchir les sièges occupés
        if (this.seatMapComponent) {
          this.seatMapComponent.loadOccupiedSeats();
        }
      },
    });
  }

  onPay(event: { paymentMethod: PaymentMethod; simulateFailure: boolean }): void {
    this.isProcessingPayment = true;
    this.paymentErrorMessage = null;
    this.cdr.markForCheck();

    this.bookingService
      .payAndGenerateTicket(event.paymentMethod, event.simulateFailure)
      .subscribe({
        next: ({ ticket }) => {
          this.isProcessingPayment = false;
          this.currentTicket = ticket;
          this.currentStep = 'TICKET';
          this.cdr.markForCheck();
          this.router.navigate(['/ticket', ticket.ticketNumber]);
        },
        error: (err) => {
          this.isProcessingPayment = false;
          this.paymentErrorMessage =
            err.error?.message ||
            err.message ||
            'Le paiement a échoué. Veuillez vérifier les informations et réessayer.';
          this.cdr.markForCheck();
        },
      });
  }

  onCancelBooking(): void {
    if (this.currentReservation) {
      this.bookingService.resetBooking();
    }
    this.currentStep = 'SELECTION';
    this.selectedSeat = null;
    this.paymentErrorMessage = null;
    this.errorMessage = null;
    if (this.seatMapComponent) {
      this.seatMapComponent.loadOccupiedSeats();
    }
  }

  onResetAll(): void {
    this.bookingService.resetBooking();
    this.router.navigate(['/']);
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
