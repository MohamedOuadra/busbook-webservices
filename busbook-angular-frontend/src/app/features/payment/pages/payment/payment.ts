import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription, interval } from 'rxjs';

import { ReservationApiService } from '../../../../core/services/reservation.service';
import { PaymentApiService } from '../../../../core/services/payment.service';
import { TicketApiService } from '../../../../core/services/ticket.service';
import { TripService } from '../../../../core/services/trip';
import { BookingService } from '../../../../core/services/booking';
import { ReservationResponse } from '../../../../core/models/reservation.model';
import { Trip } from '../../../trips/models/trip';
import { PaymentMethod } from '../../../../core/models/payment.model';
import { CountdownTimerComponent } from '../../../reservations/components/countdown-timer/countdown-timer.component';

@Component({
  selector: 'app-payment-page',
  imports: [CommonModule, FormsModule, RouterLink, CountdownTimerComponent],
  templateUrl: './payment.html',
  styleUrl: './payment.css',
})
export class PaymentPage implements OnInit, OnDestroy {
  reservationId!: number;
  reservation?: ReservationResponse;
  trip?: Trip;

  travelerName = 'Voyageur BusBook';
  travelerEmail = 'voyageur@busbook.ma';

  selectedMethod: PaymentMethod = 'CARTE_BANCAIRE';
  simulateFailure = false;

  remainingSeconds = 600;
  isExpired = false;
  isProcessing = false;
  isLoading = true;
  errorMessage: string | null = null;

  private timerSub?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private reservationApi: ReservationApiService,
    private paymentApi: PaymentApiService,
    private ticketApi: TicketApiService,
    private tripService: TripService,
    private bookingService: BookingService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.reservationId = Number(this.route.snapshot.paramMap.get('reservationId'));

    if (!this.reservationId) {
      this.router.navigate(['/']);
      return;
    }

    const traveler = this.bookingService.currentTraveler;
    if (traveler && traveler.fullName) {
      this.travelerName = traveler.fullName;
    }
    if (traveler && traveler.email) {
      this.travelerEmail = traveler.email;
    }

    this.loadReservation();
  }

  loadReservation(): void {
    this.isLoading = true;
    this.errorMessage = null;
    this.cdr.markForCheck();

    this.reservationApi.getReservationById(this.reservationId).subscribe({
      next: (res) => {
        this.reservation = res;
        this.isLoading = false;

        if (res.status === 'EXPIREE') {
          this.isExpired = true;
          this.remainingSeconds = 0;
        } else if (res.status === 'CONFIRMEE') {
          // Déjà confirmée -> récupérer le billet
          this.ticketApi.getTicketByReservation(res.id).subscribe({
            next: (tkt) => this.router.navigate(['/ticket', tkt.ticketNumber]),
            error: () => {}
          });
        } else if (res.status === 'EN_ATTENTE') {
          this.initCountdown(res.expiresAt);
        }

        // Charger les informations du trajet
        this.tripService.getTripById(res.tripId).subscribe((trip) => {
          this.trip = trip;
          this.cdr.markForCheck();
        });
        this.cdr.markForCheck();
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'Impossible de charger la réservation #' + this.reservationId;
        this.cdr.markForCheck();
      }
    });
  }

  initCountdown(expiresAtVal: any): void {
    if (this.timerSub) {
      this.timerSub.unsubscribe();
    }

    const expiresMs = this.parseDate(expiresAtVal);
    const check = () => {
      let diff = Math.floor((expiresMs - Date.now()) / 1000);
      // Correction du décalage de fuseau horaire (si le serveur Spring Boot renvoie l'heure locale sans offset ISO, ex: 1h de décalage)
      while (diff > 600) {
        diff -= 3600;
      }
      diff = Math.max(0, Math.min(600, diff));
      this.remainingSeconds = diff;

      if (diff <= 0) {
        this.isExpired = true;
        if (this.timerSub) {
          this.timerSub.unsubscribe();
        }
      }
      this.cdr.markForCheck();
    };

    check();
    this.timerSub = interval(1000).subscribe(() => check());
  }

  private parseDate(val: any): number {
    if (!val) return Date.now() + 10 * 60 * 1000;
    if (Array.isArray(val)) {
      const [y, m, d, h = 0, min = 0, s = 0] = val;
      return new Date(y, m - 1, d, h, min, s).getTime();
    }
    const ms = new Date(val).getTime();
    return isNaN(ms) ? Date.now() + 10 * 60 * 1000 : ms;
  }

  onPay(): void {
    if (this.isExpired || this.isProcessing || !this.reservation) return;

    this.isProcessing = true;
    this.errorMessage = null;
    this.cdr.markForCheck();

    const amount = this.trip ? this.trip.prix : 120;

    this.paymentApi.processPayment({
      reservationId: this.reservation.id,
      amount,
      paymentMethod: this.selectedMethod,
      simulateFailure: this.simulateFailure,
    }).subscribe({
      next: (payment) => {
        if (payment.status === 'SUCCESS') {
          // Émettre le billet
          this.ticketApi.createTicket({
            reservationId: this.reservation!.id,
            tripId: this.reservation!.tripId,
            seatNumber: String(this.reservation!.seatNumber || this.reservation!.seatId),
            travelerName: this.travelerName,
            travelerEmail: this.travelerEmail,
          }).subscribe({
            next: (ticket) => {
              this.isProcessing = false;
              this.cdr.markForCheck();
              this.router.navigate(['/ticket', ticket.ticketNumber]);
            },
            error: (tktErr) => {
              this.isProcessing = false;
              this.errorMessage = tktErr.error?.message || 'Erreur lors de la génération du billet.';
              this.cdr.markForCheck();
            }
          });
        } else {
          this.isProcessing = false;
          this.errorMessage = 'Paiement refusé. Veuillez vérifier vos coordonnées bancaires.';
          this.cdr.markForCheck();
        }
      },
      error: (payErr) => {
        this.isProcessing = false;
        this.errorMessage = payErr.error?.message || 'Le paiement a échoué. Veuillez réessayer.';
        this.cdr.markForCheck();
      }
    });
  }

  onCancel(): void {
    if (!this.reservation) return;
    this.reservationApi.cancelReservation(this.reservation.id).subscribe({
      next: () => {
        this.router.navigate(['/reservation', this.reservation!.tripId]);
      },
      error: () => {
        this.router.navigate(['/']);
      }
    });
  }

  ngOnDestroy(): void {
    if (this.timerSub) {
      this.timerSub.unsubscribe();
    }
  }
}
