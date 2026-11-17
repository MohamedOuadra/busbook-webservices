import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject, Observable, Subscription, interval } from 'rxjs';
import { Trip } from '../../features/trips/models/trip';
import { ReservationResponse } from '../models/reservation.model';
import { PaymentResponse, PaymentMethod } from '../models/payment.model';
import { TicketResponse } from '../models/ticket.model';
import { ReservationApiService } from './reservation.service';
import { PaymentApiService } from './payment.service';
import { TicketApiService } from './ticket.service';

export interface TravelerInfo {
  fullName: string;
  phoneNumber: string;
  email: string;
}

@Injectable({
  providedIn: 'root'
})
export class BookingService implements OnDestroy {
  private readonly tripSubject = new BehaviorSubject<Trip | null>(null);
  private readonly seatSubject = new BehaviorSubject<number | null>(null);
  private readonly travelerSubject = new BehaviorSubject<TravelerInfo>({
    fullName: '',
    phoneNumber: '',
    email: ''
  });
  private readonly reservationSubject = new BehaviorSubject<ReservationResponse | null>(null);
  private readonly ticketSubject = new BehaviorSubject<TicketResponse | null>(null);
  private readonly remainingSecondsSubject = new BehaviorSubject<number>(0);
  private readonly expiredSubject = new BehaviorSubject<boolean>(false);

  readonly trip$ = this.tripSubject.asObservable();
  readonly selectedSeat$ = this.seatSubject.asObservable();
  readonly traveler$ = this.travelerSubject.asObservable();
  readonly reservation$ = this.reservationSubject.asObservable();
  readonly ticket$ = this.ticketSubject.asObservable();
  readonly remainingSeconds$ = this.remainingSecondsSubject.asObservable();
  readonly isExpired$ = this.expiredSubject.asObservable();

  private timerSubscription?: Subscription;

  constructor(
    private reservationApi: ReservationApiService,
    private paymentApi: PaymentApiService,
    private ticketApi: TicketApiService
  ) {}

  setTrip(trip: Trip): void {
    this.tripSubject.next(trip);
  }

  setSelectedSeat(seatNumber: number | null): void {
    this.seatSubject.next(seatNumber);
  }

  setTravelerInfo(info: TravelerInfo): void {
    this.travelerSubject.next(info);
  }

  get currentTrip(): Trip | null {
    return this.tripSubject.value;
  }

  get currentSeat(): number | null {
    return this.seatSubject.value;
  }

  get currentTraveler(): TravelerInfo {
    return this.travelerSubject.value;
  }

  get currentReservation(): ReservationResponse | null {
    return this.reservationSubject.value;
  }

  get currentTicket(): TicketResponse | null {
    return this.ticketSubject.value;
  }

  /**
   * Crée une réservation temporaire de 10 minutes et démarre le minuteur.
   */
  startReservation(tripId: number, seatId: number, travelerId: number = 1): Observable<ReservationResponse> {
    return new Observable<ReservationResponse>((subscriber) => {
      this.reservationApi.createReservation({ tripId, seatId, travelerId }).subscribe({
        next: (reservation) => {
          this.reservationSubject.next(reservation);
          this.startCountdown(reservation.expiresAt);
          subscriber.next(reservation);
          subscriber.complete();
        },
        error: (err) => {
          subscriber.error(err);
        }
      });
    });
  }

  /**
   * Effectue le paiement de la réservation en cours et génère le billet si succès.
   */
  payAndGenerateTicket(
    paymentMethod: PaymentMethod,
    simulateFailure: boolean = false
  ): Observable<{ payment: PaymentResponse; ticket: TicketResponse }> {
    const reservation = this.reservationSubject.value;
    const trip = this.tripSubject.value;
    const traveler = this.travelerSubject.value;

    if (!reservation) {
      throw new Error('Aucune réservation en cours');
    }
    if (this.expiredSubject.value) {
      throw new Error('La réservation a expiré (délai de 10 minutes dépassé)');
    }

    const amount = trip ? trip.prix : 100;

    return new Observable((subscriber) => {
      this.paymentApi.processPayment({
        reservationId: reservation.id,
        amount,
        paymentMethod,
        simulateFailure
      }).subscribe({
        next: (payment) => {
          if (payment.status === 'SUCCESS') {
            // Paiement validé -> Émettre le billet
            this.ticketApi.createTicket({
              reservationId: reservation.id,
              tripId: reservation.tripId,
              seatNumber: String(reservation.seatNumber || reservation.seatId),
              travelerName: traveler.fullName || 'Voyageur BusBook',
              travelerEmail: traveler.email || 'voyageur@busbook.ma'
            }).subscribe({
              next: (ticket) => {
                this.stopCountdown();
                this.ticketSubject.next(ticket);
                subscriber.next({ payment, ticket });
                subscriber.complete();
              },
              error: (ticketErr) => {
                subscriber.error(ticketErr);
              }
            });
          } else {
            subscriber.error(new Error('Paiement refusé ou échoué'));
          }
        },
        error: (payErr) => {
          subscriber.error(payErr);
        }
      });
    });
  }

  /**
   * Démarre le décompte basé sur la date d'expiration réelle `expiresAt`.
   */
  startCountdown(expiresAtVal: any): void {
    this.stopCountdown();
    this.expiredSubject.next(false);

    const expiresAtMs = this.parseDate(expiresAtVal);

    const updateTimer = () => {
      const nowMs = Date.now();
      let diffSec = Math.floor((expiresAtMs - nowMs) / 1000);
      while (diffSec > 600) {
        diffSec -= 3600;
      }
      diffSec = Math.max(0, Math.min(600, diffSec));

      this.remainingSecondsSubject.next(diffSec);

      if (diffSec <= 0) {
        this.expiredSubject.next(true);
        this.stopCountdown();
      }
    };

    updateTimer();
    this.timerSubscription = interval(1000).subscribe(() => updateTimer());
  }

  private parseDate(val: any): number {
    if (!val) {
      return Date.now() + 10 * 60 * 1000;
    }
    if (Array.isArray(val)) {
      const [y, m, d, h = 0, min = 0, s = 0] = val;
      return new Date(y, m - 1, d, h, min, s).getTime();
    }
    const ms = new Date(val).getTime();
    return isNaN(ms) ? Date.now() + 10 * 60 * 1000 : ms;
  }

  stopCountdown(): void {
    if (this.timerSubscription) {
      this.timerSubscription.unsubscribe();
      this.timerSubscription = undefined;
    }
  }

  resetBooking(): void {
    this.stopCountdown();
    this.seatSubject.next(null);
    this.reservationSubject.next(null);
    this.ticketSubject.next(null);
    this.remainingSecondsSubject.next(0);
    this.expiredSubject.next(false);
  }

  ngOnDestroy(): void {
    this.stopCountdown();
  }
}
