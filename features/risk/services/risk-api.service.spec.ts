import { TestBed } from '@angular/core/testing';

import { RiskApiService } from './risk-api.service';

describe('RiskApiService', () => {
  let service: RiskApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RiskApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
