import { Component, EventEmitter, Output, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { TripSearch as TripSearchCriteria } from '../../models/trip-search';
import { isTravelDate, todayDate } from '../../../../shared/utils/travel-date';
import { SearchHistoryService } from '../../../../core/services/search-history.service';
import { FavoriteRoute, FavoriteRoutesService } from '../../../../core/services/favorite-routes.service';

@Component({
  selector: 'app-trip-search',
  imports: [FormsModule],
  templateUrl: './trip-search.html',
  styleUrl: './trip-search.css',
})
export class TripSearch {
  readonly history = inject(SearchHistoryService);
  readonly favorites = inject(FavoriteRoutesService);
  readonly storageMessage = signal('');
  readonly favoriteMessage = signal('');

  @Output() search = new EventEmitter<TripSearchCriteria>();

  departure = '';
  destination = '';
  date = '';

  onSearch(): void {
    const criteria: TripSearchCriteria = {
      departure: this.departure.trim(),
      destination: this.destination.trim(),
      date: this.date,
    };

    if (!criteria.departure || !criteria.destination || !isTravelDate(criteria.date)) return;
    this.storageMessage.set(this.history.remember(criteria) ? '' : 'Search history could not be saved in this browser.');
    this.search.emit(criteria);
  }

  repeatSearch(criteria: TripSearchCriteria): void {
    this.departure = criteria.departure;
    this.destination = criteria.destination;
    this.date = criteria.date < todayDate() ? todayDate() : criteria.date;
    this.onSearch();
  }

  clearHistory(): void {
    this.storageMessage.set(this.history.clear() ? '' : 'Search history could not be cleared in this browser.');
  }

  saveFavorite(): void {
    const saved = this.favorites.add({ departure: this.departure, destination: this.destination });
    this.favoriteMessage.set(saved ? 'Route saved in this browser.' :
      'Could not save this route. Check both cities, browser storage, and the limit of 20 favorites.');
  }

  removeFavorite(route: FavoriteRoute): void {
    this.favoriteMessage.set(this.favorites.remove(route) ? 'Favorite removed.' : 'Could not remove this favorite in this browser.');
  }

  onSwap(): void {
    const temp = this.departure;
    this.departure = this.destination;
    this.destination = temp;
    if (this.departure && this.destination && this.date) {
      this.onSearch();
    }
  }

  selectRoute(dep: string, dest: string): void {
    this.departure = dep;
    this.destination = dest;
    if (!isTravelDate(this.date) || this.date < todayDate()) {
      this.date = todayDate();
    }
    this.onSearch();
  }
}
