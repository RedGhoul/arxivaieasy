import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILikeEntry, LikeEntry } from '../like-entry.model';
import { LikeEntryService } from '../service/like-entry.service';

@Injectable({ providedIn: 'root' })
export class LikeEntryRoutingResolveService implements Resolve<ILikeEntry> {
  constructor(protected service: LikeEntryService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILikeEntry> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((likeEntry: HttpResponse<LikeEntry>) => {
          if (likeEntry.body) {
            return of(likeEntry.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new LikeEntry());
  }
}
