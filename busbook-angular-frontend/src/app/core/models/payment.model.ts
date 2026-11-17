export type PaymentMethod = 'CARTE_BANCAIRE' | 'PORTEFEUILLE_MOBILE' | 'ESPECES';
export type PaymentStatus = 'PENDING' | 'SUCCESS' | 'FAILED';

export interface PaymentRequest {
  reservationId: number;
  amount: number;
  paymentMethod: PaymentMethod;
  simulateFailure?: boolean;
}

export interface PaymentResponse {
  id: number;
  reservationId: number;
  amount: number;
  paymentMethod: PaymentMethod;
  status: PaymentStatus;
  transactionId: string;
  createdAt: string;
  completedAt: string | null;
}
