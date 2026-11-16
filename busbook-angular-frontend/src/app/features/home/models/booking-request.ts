export interface BookingRequest {
  passenger: {
    fullName: string;
    phoneNumber: string;
    email: string;
  };

  journey: {
    from: string;
    to: string;
    date: string;
    seatNumber: number;
    price: number;
  };
}