import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-seat',
  imports: [],
  templateUrl: './seat.html',
  styleUrl: './seat.css',
})
export class Seat {
  @Input({ required: true }) number!: number;
  @Input() status: 'available' | 'occupied' | 'selected' = 'available';

  @Output() seatSelected = new EventEmitter<number>();

  selectSeat(): void {
    if (this.status === 'available') {
      this.seatSelected.emit(this.number);
    }
  }
}