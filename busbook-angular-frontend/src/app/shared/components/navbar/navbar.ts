import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  readonly navItems = [
    { label: 'Book Tickets', route: '/', exact: true },
    { label: 'Schedules', route: '/trips', exact: false },
    { label: 'My Bookings', route: '/my-reservations', exact: false },
    { label: 'Verify Ticket', route: '/verify-ticket', exact: false },
  ];
}
