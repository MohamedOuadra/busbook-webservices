export interface ReservationRequest {
  tripId: number;
  travelerId: number;
  seatId: number;
}

export interface ReservationResponse {
  id: number;
  tripId: number;
  travelerId: number;
  seatId: number;
  seatNumber?: string;
  status: 'EN_ATTENTE' | 'CONFIRMEE' | 'ANNULEE' | 'EXPIREE';
  reservationDate?: string;
  createdAt?: string;
  expiresAt: string;
  confirmedAt: string | null;
}
