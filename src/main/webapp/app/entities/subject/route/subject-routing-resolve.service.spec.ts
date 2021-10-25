jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ISubject, Subject } from '../subject.model';
import { SubjectService } from '../service/subject.service';

import { SubjectRoutingResolveService } from './subject-routing-resolve.service';

describe('Service Tests', () => {
  describe('Subject routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: SubjectRoutingResolveService;
    let service: SubjectService;
    let resultSubject: ISubject | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(SubjectRoutingResolveService);
      service = TestBed.inject(SubjectService);
      resultSubject = undefined;
    });

    describe('resolve', () => {
      it('should return ISubject returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSubject = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSubject).toEqual({ id: 123 });
      });

      it('should return new ISubject if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSubject = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultSubject).toEqual(new Subject());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Subject })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSubject = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSubject).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
