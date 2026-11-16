
import { Component, EventEmitter, Output } from '@angular/core';
import { Seat } from '../seat/seat';
import { SeatLegend } from '../seat-legend/seat-legend';

interface SeatData {
  number: number;
  occupied: boolean;
}

@Component({
  selector: 'app-seat-map',
  imports: [Seat, SeatLegend],
  templateUrl: './seat-map.html',
  styleUrl: './seat-map.css',
})
export class SeatMap {
  @Output() seatSelected = new EventEmitter<number>();

  selectedSeat: number | null = null;

  // Données temporaires pour tester l'interface
  seats: SeatData[] = Array.from({ length: 20 }, (_, index) => ({
    number: index + 1,
    occupied: [4, 12].includes(index + 1),
  }));

  get leftSeats(): SeatData[] {
    return this.seats.filter(seat => seat.number <= 10);
  }

  get rightSeats(): SeatData[] {
    return this.seats.filter(seat => seat.number > 10);
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
    this.selectedSeat = number;
    this.seatSelected.emit(number);
  }
}
