import { TestBed } from '@angular/core/testing';

import { ApiSerivce } from './api.serivce';

describe('ApiSerivce', () => {
  let service: ApiSerivce;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiSerivce);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
