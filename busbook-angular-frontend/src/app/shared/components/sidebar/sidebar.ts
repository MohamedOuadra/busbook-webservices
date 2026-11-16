import { Component, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, NavigationEnd } from '@angular/router';
import { Location } from '@angular/common';
import { filter } from 'rxjs';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar {
  readonly showBackButton = signal(false);

  constructor(
    private router: Router,
    private location: Location
  ) {
    this.updateBackVisibility(this.router.url);

    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe((event) => {
        this.updateBackVisibility(event.urlAfterRedirects || event.url);
      });
  }

  private updateBackVisibility(url: string): void {
    const cleanUrl = url.split('?')[0].split('#')[0];
    // Only show back button on internal pages, never on the home/trips search page
    const isRoot = cleanUrl === '' || cleanUrl === '/' || cleanUrl === '/trips';
    this.showBackButton.set(!isRoot);
  }

  goBack(): void {
    if (typeof window !== 'undefined' && window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/']);
    }
  }
}
