import { TestBed } from '@angular/core/testing';

import { ClaimsApiService } from './claims-api.service';

describe('ClaimsApiService', () => {
  let service: ClaimsApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClaimsApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
