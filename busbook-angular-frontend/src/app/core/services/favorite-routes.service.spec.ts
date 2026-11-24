import { FavoriteRoutesService } from './favorite-routes.service';

describe('FavoriteRoutesService', () => {
  const route = { departure: 'Agadir', destination: 'Rabat' };
  beforeEach(() => localStorage.clear());
  afterEach(() => vi.restoreAllMocks());

  it('persists routes without dates and deduplicates case and whitespace', () => {
    const service = new FavoriteRoutesService();
    service.add(route);
    service.add({ departure: ' agadir ', destination: 'RABAT' });
    expect(service.routes()).toEqual([route]);
    expect(new FavoriteRoutesService().routes()).toEqual([route]);
    service.remove(route);
    expect(new FavoriteRoutesService().routes()).toEqual([]);
  });

  it('caps favorites without evicting existing routes', () => {
    const service = new FavoriteRoutesService();
    for (let i = 0; i < 20; i++) expect(service.add({ ...route, destination: `City ${i}` })).toBe(true);
    expect(service.add(route)).toBe(false);
    expect(service.routes()).toHaveLength(20);
  });

  it('handles malformed storage and reports failed writes without losing routes', () => {
    localStorage.setItem('busbook_favorite_routes_v1', '[null,{},false]');
    const service = new FavoriteRoutesService();
    expect(service.routes()).toEqual([]);
    service.add(route);
    vi.spyOn(Storage.prototype, 'setItem').mockImplementation(() => { throw new Error('full'); });
    expect(service.remove(route)).toBe(false);
    expect(service.routes()).toEqual([route]);
  });
});
