import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  readonly navItems = [
    { label: 'Book Tickets', route: '/', exact: true },
    { label: 'My Bookings', route: '/my-reservations', exact: false },
    { label: 'Verify Ticket', route: '/verify-ticket', exact: false },
  ];

  private authService = inject(AuthService);
  currentUser = this.authService.currentUser;

  logout(): void {
    this.authService.logout();
  }
}
