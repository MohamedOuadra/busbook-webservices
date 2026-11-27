import { DEFAULT_TRAVEL_PREFERENCES, TravelPreferencesService } from './travel-preferences.service';

describe('TravelPreferencesService', () => {
  beforeEach(() => localStorage.clear());
  afterEach(() => vi.restoreAllMocks());

  it('restores saved choices and keeps each account and guest separate', () => {
    const service = new TravelPreferencesService();
    const preferences = { ...DEFAULT_TRAVEL_PREFERENCES, defaultCity: 'Agadir', preferredSeat: 'aisle', smsNotifications: false };
    expect(service.save(1, preferences)).toBe(true);
    expect(new TravelPreferencesService().load(1)).toEqual(preferences);
    expect(service.load(2)).toEqual(DEFAULT_TRAVEL_PREFERENCES);
    expect(service.load(null)).toEqual(DEFAULT_TRAVEL_PREFERENCES);
  });

  it('validates corrupted or partial storage and ignores unknown fields', () => {
    localStorage.setItem('busbook_travel_preferences_v1_1', '{broken');
    const service = new TravelPreferencesService();
    expect(service.load(1)).toEqual(DEFAULT_TRAVEL_PREFERENCES);
    localStorage.setItem('busbook_travel_preferences_v1_1', JSON.stringify({ defaultCity: 'Agadir', language: 'invalid', smsNotifications: 'false', password: 'ignored' }));
    expect(service.load(1)).toEqual({ ...DEFAULT_TRAVEL_PREFERENCES, defaultCity: 'Agadir' });
  });

  it('returns defaults for blocked reads and reports failed saves', () => {
    vi.spyOn(Storage.prototype, 'getItem').mockImplementation(() => { throw new Error('blocked'); });
    const service = new TravelPreferencesService();
    expect(service.load(1)).toEqual(DEFAULT_TRAVEL_PREFERENCES);
    vi.spyOn(Storage.prototype, 'setItem').mockImplementation(() => { throw new Error('full'); });
    expect(service.save(1, DEFAULT_TRAVEL_PREFERENCES)).toBe(false);
  });
});
