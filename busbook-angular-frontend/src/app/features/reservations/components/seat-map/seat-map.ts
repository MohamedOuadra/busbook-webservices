import { ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { Seat } from '../seat/seat';
import { ReservationApiService } from '../../../../core/services/reservation.service';

export interface SeatData {
  number: number;
  occupied: boolean;
}

@Component({
  selector: 'app-seat-map',
  imports: [Seat],
  templateUrl: './seat-map.html',
  styleUrl: './seat-map.css',
})
export class SeatMap implements OnInit, OnChanges {
  @Input() tripId?: number;
  @Input() capacity: number = 40;
  @Output() seatSelected = new EventEmitter<number>();

  selectedSeat: number | null = null;
  seats: SeatData[] = [];

  constructor(
    private reservationApi: ReservationApiService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.initSeats();
    this.loadOccupiedSeats();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['capacity'] || changes['tripId']) {
      this.initSeats();
      this.loadOccupiedSeats();
    }
  }

  initSeats(): void {
    const total = this.capacity > 0 ? this.capacity : 40;
    this.seats = Array.from({ length: total }, (_, index) => ({
      number: index + 1,
      occupied: false,
    }));
  }

  loadOccupiedSeats(): void {
    if (!this.tripId) {
      return;
    }

    this.reservationApi.getReservationsByTripId(this.tripId).subscribe({
      next: (reservations) => {
        const now = new Date();
        const occupiedSeatIds = new Set<number>();

        for (const res of reservations) {
          const seatNum = res.seatNumber ? Number(res.seatNumber) : res.seatId;
          if (res.status === 'CONFIRMEE') {
            occupiedSeatIds.add(seatNum);
          } else if (res.status === 'EN_ATTENTE' && res.expiresAt) {
            const expires = new Date(res.expiresAt);
            if (expires > now) {
              occupiedSeatIds.add(seatNum);
            }
          }
        }

        this.seats = this.seats.map((seat) => ({
          ...seat,
          occupied: occupiedSeatIds.has(seat.number),
        }));

        if (this.selectedSeat && occupiedSeatIds.has(this.selectedSeat)) {
          this.selectedSeat = null;
          this.seatSelected.emit(0);
        }
        this.cdr.markForCheck();
      },
      error: () => {
        // Fallback en cas d'erreur de connexion au microservice
        this.cdr.markForCheck();
      }
    });
  }

  get leftSeats(): SeatData[] {
    const half = Math.ceil(this.seats.length / 2);
    return this.seats.slice(0, half);
  }

  get rightSeats(): SeatData[] {
    const half = Math.ceil(this.seats.length / 2);
    return this.seats.slice(half);
  }

  get seatRows(): { seat1?: SeatData; seat2?: SeatData; seat3?: SeatData; seat4?: SeatData }[] {
    const rows = [];
    for (let i = 0; i < this.seats.length; i += 4) {
      rows.push({
        seat1: this.seats[i],
        seat2: this.seats[i + 1],
        seat3: this.seats[i + 2],
        seat4: this.seats[i + 3],
      });
    }
    return rows;
  }

  getSeatStatus(seat: SeatData): 'available' | 'occupied' | 'selected' {
    if (seat.occupied) {
      return 'occupied';
    }
    if (seat.number === this.selectedSeat) {
      return 'selected';
    }
    return 'available';
  }

  selectSeat(number: number): void {
    const targetSeat = this.seats.find(s => s.number === number);
    if (targetSeat && targetSeat.occupied) {
      return;
    }
    this.selectedSeat = number;
    this.seatSelected.emit(number);
  }
}
