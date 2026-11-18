import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { Navbar } from '../../shared/components/navbar/navbar';
import { AuthService } from '../../core/services/auth.service';

interface AdminTab {
  label: string;
  route: string;
  icon: string;
  exact: boolean;
}

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, Navbar],
  templateUrl: './admin-layout.html',
  styleUrl: './admin-layout.css',
})
export class AdminLayout {
  private authService = inject(AuthService);
  currentUser = this.authService.currentUser;

  readonly tabs: AdminTab[] = [
    { label: 'Tableau de bord', route: '/admin', icon: 'dashboard', exact: true },
    { label: 'Trajets', route: '/admin/trips', icon: 'trips', exact: false },
    { label: 'Flotte de Bus', route: '/admin/buses', icon: 'bus', exact: false },
    { label: 'Réservations', route: '/admin/reservations', icon: 'reservations', exact: false },
  ];
}
