import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPaper, Paper } from '../paper.model';
import { PaperService } from '../service/paper.service';
import { AccountService } from '../../../core/auth/account.service';

@Injectable({ providedIn: 'root' })
export class PaperRoutingResolveService implements Resolve<IPaper> {
  constructor(protected service: PaperService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPaper> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.findPublic(id).pipe(
        mergeMap((paper: HttpResponse<Paper>) => {
          if (paper.body) {
            return of(paper.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Paper());
  }
}
