import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LikeEntryComponent } from '../list/like-entry.component';
import { LikeEntryDetailComponent } from '../detail/like-entry-detail.component';
import { LikeEntryUpdateComponent } from '../update/like-entry-update.component';
import { LikeEntryRoutingResolveService } from './like-entry-routing-resolve.service';

const likeEntryRoute: Routes = [
  {
    path: '',
    component: LikeEntryComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LikeEntryDetailComponent,
    resolve: {
      likeEntry: LikeEntryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LikeEntryUpdateComponent,
    resolve: {
      likeEntry: LikeEntryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LikeEntryUpdateComponent,
    resolve: {
      likeEntry: LikeEntryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(likeEntryRoute)],
  exports: [RouterModule],
})
export class LikeEntryRoutingModule {}
