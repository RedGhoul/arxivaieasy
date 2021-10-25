import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISubject, Subject } from '../subject.model';
import { SubjectService } from '../service/subject.service';

@Injectable({ providedIn: 'root' })
export class SubjectRoutingResolveService implements Resolve<ISubject> {
  constructor(protected service: SubjectService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISubject> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((subject: HttpResponse<Subject>) => {
          if (subject.body) {
            return of(subject.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Subject());
  }
}
