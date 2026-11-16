import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-navbar',
  imports: [],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  @Input() active = 'Buy Tickets';

  readonly tabs = [
    'Buy Tickets',
    'Schedules',
    'Trips',
    'Vehicles',
  ];
}
