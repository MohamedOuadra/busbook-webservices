export interface TicketRequest {
  reservationId: number;
  tripId: number;
  seatNumber: string | number;
  travelerName: string;
  travelerEmail: string;
}

export interface TicketResponse {
  id: number;
  ticketNumber: string;
  reservationId: number;
  tripId: number;
  seatNumber: string | number;
  travelerName: string;
  travelerEmail: string;
  qrCode: string;
  issuedAt: string;
  valid: boolean;
}
