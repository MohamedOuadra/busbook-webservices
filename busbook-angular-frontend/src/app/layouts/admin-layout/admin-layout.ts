import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

interface AdminNavItem {
  label: string;
  route: string;
  icon: string;
}

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './admin-layout.html',
  styleUrl: './admin-layout.css',
})
export class AdminLayout {
  private authService = inject(AuthService);
  private router = inject(Router);

  currentUser = this.authService.currentUser;
  sidebarOpen = signal(true);

  readonly navItems: AdminNavItem[] = [
    { label: 'Tableau de bord', route: '/admin', icon: 'dashboard' },
    { label: 'Trajets', route: '/admin/trips', icon: 'trips' },
    { label: 'Bus', route: '/admin/buses', icon: 'bus' },
    { label: 'Réservations', route: '/admin/reservations', icon: 'reservations' },
  ];

  toggleSidebar(): void {
    this.sidebarOpen.update(v => !v);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
