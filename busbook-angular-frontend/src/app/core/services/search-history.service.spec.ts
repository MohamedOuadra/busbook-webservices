import { SearchHistoryService } from './search-history.service';

describe('SearchHistoryService', () => {
  const search = { departure: 'Agadir', destination: 'Casablanca', date: '2099-12-02' };
  beforeEach(() => localStorage.clear());
  afterEach(() => vi.restoreAllMocks());

  it('deduplicates routes and dates and keeps only the five newest searches', () => {
    const service = new SearchHistoryService();
    for (let i = 1; i <= 6; i++) service.remember({ ...search, date: `2099-12-0${i}` });
    service.remember({ ...search, departure: ' agadir ', date: '2099-12-03' });
    expect(service.recent()).toHaveLength(5);
    expect(service.recent()[0].date).toBe('2099-12-03');
    expect(new SearchHistoryService().recent()).toEqual(service.recent());
    expect(service.clear()).toBe(true);
    expect(new SearchHistoryService().recent()).toEqual([]);
  });

  it('ignores incomplete searches and malformed stored data', () => {
    localStorage.setItem('busbook_recent_searches_v1', '[{"date":false},null]');
    const service = new SearchHistoryService();
    service.remember({ ...search, destination: ' ' });
    expect(service.recent()).toEqual([]);
    localStorage.setItem('busbook_recent_searches_v1', '{broken');
    expect(new SearchHistoryService().recent()).toEqual([]);
  });

  it('handles unavailable storage without preventing searches', () => {
    vi.spyOn(Storage.prototype, 'getItem').mockImplementation(() => { throw new Error('blocked'); });
    const service = new SearchHistoryService();
    vi.spyOn(Storage.prototype, 'setItem').mockImplementation(() => { throw new Error('full'); });
    expect(service.remember(search)).toBe(false);
    expect(service.recent()).toEqual([]);
  });
});
