import { Component, inject, OnInit, signal } from '@angular/core';
import { AdminService } from '../../../../core/services/admin.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class AdminDashboard implements OnInit {
  private adminService = inject(AdminService);

  totalTrips = signal(0);
  totalBuses = signal(0);
  totalReservations = signal(0);
  confirmedReservations = signal(0);
  loading = signal(true);

  ngOnInit(): void {
    // Charge les stats en parallèle depuis les APIs
    this.adminService.getTrips().subscribe({
      next: trips => this.totalTrips.set(trips.length),
      error: () => this.totalTrips.set(0),
    });

    this.adminService.getBuses().subscribe({
      next: buses => this.totalBuses.set(buses.length),
      error: () => this.totalBuses.set(0),
    });

    this.adminService.getAllReservations().subscribe({
      next: reservations => {
        this.totalReservations.set(reservations.length);
        this.confirmedReservations.set(
          reservations.filter(r => r.status === 'CONFIRMED' || (r.status as string) === 'CONFIRMEE').length
        );
        this.loading.set(false);
      },
      error: () => {
        this.totalReservations.set(0);
        this.loading.set(false);
      },
    });
  }
}
